#!/usr/bin/env python3
import os
import json
import csv
import io
import re
from http.server import HTTPServer, BaseHTTPRequestHandler
from datetime import datetime, date
from urllib.parse import urlparse

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CSV_PATH = os.environ.get("CSV_PATH", os.path.join(BASE_DIR, "autorizzazioni.csv"))
PORT     = int(os.environ.get("PORT", 8080))
HOST     = os.environ.get("HOST", "0.0.0.0")

HEADER = ["Numero autorizzazione", "Scadenza", "Richiedente", "Posizione insegna", "Tipo di mezzo pubblico"]

def pulisci(val):
    if val is None: return ""
    return val.strip().strip('"').replace('\r', '')

def estrai_macro_categoria(testo_originale):
    t = testo_originale.upper()
    if "CARTELL" in t: return "CARTELLO"
    if "ROTOND" in t:  return "ROTONDA"
    if "TOTEM" in t:   return "TOTEM"
    if "INSEGNA" in t: return "INSEGNA"
    if "VETROFAN" in t: return "VETROFAN"
    return "ALTRO"

def parse_data(stringa_data):
    s = stringa_data.strip().upper()
    if s in ["ANNULLATA", "NEGATA", "SOSPESA", "RIGETTATA"]:
        return s, None
    m = re.search(r'(\d{1,2})[-./](\d{1,2})[-./](\d{4})', s)
    if m:
        giorno, mese, anno = int(m.group(1)), int(m.group(2)), int(m.group(3))
        try:
            d = date(anno, mese, giorno)
            return ("SCADUTA" if d < date.today() else "ATTIVA"), d.isoformat()
        except ValueError: pass
    return "SCONOSCIUTO", None

def leggi_csv():
    if not os.path.exists(CSV_PATH): return []
    records = []
    try:
        with open(CSV_PATH, mode='r', encoding='utf-8-sig') as f: testo = f.read()
    except UnicodeDecodeError:
        with open(CSV_PATH, mode='r', encoding='latin-1') as f: testo = f.read()
    reader = csv.reader(io.StringIO(testo))
    rows = list(reader)
    if not rows: return []
    inizio = 1 if "numero" in rows[0][0].lower() or "autorizz" in rows[0][0].lower() else 0
    for r in rows[inizio:]:
        if not r or len(r) < 3: continue
        try: num = int(pulisci(r[0]))
        except ValueError: continue
        scadenza_str = pulisci(r[1])
        stato, scadenza_iso = parse_data(scadenza_str)
        records.append({
            "numero": num, "scadenza": scadenza_str, "scadenza_iso": scadenza_iso, "stato": stato,
            "richiedente": pulisci(r[2]), "posizione": pulisci(r[3]) if len(r) > 3 else "",
            "tipo_mezzo": pulisci(r[4]) if len(r) > 4 else ""
        })
    return sorted(records, key=lambda x: x['numero'])

def salva_csv(records):
    with open(CSV_PATH, mode='w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(HEADER)
        for r in sorted(records, key=lambda x: x['numero']):
            writer.writerow([r['numero'], r['scadenza'], r['richiedente'], r['posizione'], r['tipo_mezzo']])

class Handler(BaseHTTPRequestHandler):
    def send_json(self, obj, status=200):
        self.send_response(status)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.end_headers()
        self.wfile.write(json.dumps(obj, ensure_ascii=False).encode('utf-8'))

    def do_GET(self):
        path = urlparse(self.path).path
        if path in ['/', '/index.html']:
            self.send_response(200)
            self.send_header('Content-Type', 'text/html; charset=utf-8')
            self.end_headers()
            with open(os.path.join(BASE_DIR, 'index.html'), 'r', encoding='utf-8') as f:
                self.wfile.write(f.read().encode('utf-8'))
            return
        if path == '/api/autorizzazioni': self.send_json(leggi_csv()); return
        if path == '/api/info':
            righe = leggi_csv()
            conteggio = {}
            for r in righe:
                macro = estrai_macro_categoria(r['tipo_mezzo'])
                if macro != "ALTRO": conteggio[macro] = conteggio.get(macro, 0) + 1
            self.send_json({
                "totale": len(righe), "attive": sum(1 for r in righe if r['stato'] == 'ATTIVA'),
                "scadute": sum(1 for r in righe if r['stato'] == 'SCADUTA'),
                "tipo_max": max(conteggio, key=conteggio.get) if conteggio else "NESSUNO"
            })
            return
        self.send_response(404); self.end_headers()

    def do_POST(self):
        path = urlparse(self.path).path
        if path == '/api/upload':
            try:
                corpo = self.rfile.read(int(self.headers['Content-Length'])).decode('utf-8')
                with open(CSV_PATH, 'w', encoding='utf-8') as f: f.write(corpo)
                self.send_json({'ok': True})
            except Exception as e: self.send_json({'errore': str(e)}, 500)
            return
        if path == '/api/autorizzazioni':
            try:
                payload = json.loads(self.rfile.read(int(self.headers['Content-Length'])).decode('utf-8'))
                righe = leggi_csv()
                nuovo_num = max([r['numero'] for r in righe], default=0) + 1
                stato, scadenza_iso = parse_data(payload['scadenza'])
                nuovo = {
                    "numero": nuovo_num, "scadenza": payload['scadenza'], "scadenza_iso": scadenza_iso,
                    "stato": stato, "richiedente": payload['richiedente'], "posizione": payload['posizione'], "tipo_mezzo": payload['tipo_mezzo']
                }
                righe.append(nuovo); salva_csv(righe); self.send_json(nuovo)
            except Exception as e: self.send_json({'errore': str(e)}, 500)
            return

    def do_PUT(self):
        path = urlparse(self.path).path
        if path.startswith('/api/autorizzazioni/'):
            try:
                num = int(path.split('/')[-1])
                payload = json.loads(self.rfile.read(int(self.headers['Content-Length'])).decode('utf-8'))
                righe = leggi_csv()
                for i, r in enumerate(righe):
                    if r['numero'] == num:
                        stato, scadenza_iso = parse_data(payload['scadenza'])
                        righe[i].update({
                            "scadenza": payload['scadenza'], "scadenza_iso": scadenza_iso, "stato": stato,
                            "richiedente": payload['richiedente'], "posizione": payload['posizione'], "tipo_mezzo": payload['tipo_mezzo']
                        })
                        break
                salva_csv(righe); self.send_json({'ok': True})
            except Exception as e: self.send_json({'errore': str(e)}, 500)

    def do_DELETE(self):
        path = urlparse(self.path).path
        if path.startswith('/api/autorizzazioni/'):
            try:
                num = int(path.split('/')[-1])
                righe = leggi_csv()
                salva_csv([r for r in righe if r['numero'] != num]); self.send_json({'ok': True})
            except Exception as e: self.send_json({'errore': str(e)}, 500)

if __name__ == '__main__':
    print(f"Server avviato su http://{HOST}:{PORT}")
    HTTPServer((HOST, PORT), Handler).serve_forever()
