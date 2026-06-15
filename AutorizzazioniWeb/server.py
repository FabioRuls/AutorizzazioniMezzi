#!/usr/bin/env python3
"""
Server backend per AutorizzazioniMezzi.
Legge e salva il CSV, serve la pagina HTML.

Avvio:
    python3 server.py

Opzioni (variabili d'ambiente):
    CSV_PATH   percorso del file CSV  (default: autorizzazioni.csv nella stessa cartella)
    PORT       porta del server       (default: 8080)
    HOST       indirizzo di ascolto   (default: 0.0.0.0  → accessibile dalla rete)
"""

import os
import json
import csv
import io
import re
from http.server import HTTPServer, BaseHTTPRequestHandler
from datetime import datetime, date
from urllib.parse import urlparse, parse_qs

# ── Configurazione ──────────────────────────────────────────────────────────
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
CSV_PATH = os.environ.get("CSV_PATH", os.path.join(BASE_DIR, "autorizzazioni.csv"))
PORT     = int(os.environ.get("PORT", 8080))
HOST     = os.environ.get("HOST", "0.0.0.0")

HEADER = ["Numero autorizzazione", "Scadenza", "Richiedente",
          "Posizione insegna", "Tipo di mezzo pubblico"]

# ── Pulizia dati (stessa logica del parser Java) ─────────────────────────────

def pulisci(val):
    """Rimuove spazi, virgolette e \r da un campo CSV."""
    if val is None:
        return ""
    return val.strip().strip('"').replace('\r', '')

def normalizza_data(raw):
    """
    Normalizza una stringa data:
    - Sostituisce O (lettera) con 0 (zero)
    - Espande anni a 2 cifre (25 → 2025)
    - Rimuove spazi interni (26.01. 2025 → 26.01.2025)
    Restituisce None se la data non è parsabile.
    """
    s = raw.replace('O', '0').replace('o', '0')
    s = re.sub(r'\s+', '', s)

    # Prova separatori . - /
    for sep in ['.', '-', '/']:
        parti = s.split(sep)
        if len(parti) == 3:
            giorno, mese, anno = parti
            if len(anno) == 2:
                anno = '20' + anno
            elif len(anno) == 3:
                anno = '20' + anno[-2:]
            s = f"{giorno}.{mese}.{anno}"
            try:
                return datetime.strptime(s, "%d.%m.%Y").date()
            except ValueError:
                pass
    return None

STATI_SPECIALI = {
    'ANNUL': 'ANNULLATA',
    'NEGA':  'NEGATA',
    'SOSP':  'SOSPESA',
    'RIGET': 'RIGETTATA',
    'ROTO':  'RIGETTATA',
}

def interpreta_scadenza(raw):
    """
    Restituisce (data_iso_o_None, stato_stringa).
    """
    s = pulisci(raw)
    if not s:
        return None, 'SCONOSCIUTO'

    su = s.upper()
    for chiave, stato in STATI_SPECIALI.items():
        if chiave in su:
            return None, stato
    # Prova come data
    d = normalizza_data(s)
    if d:
        return d.isoformat(), 'ATTIVA' if d >= date.today() else 'SCADUTA'
    return None, 'SCONOSCIUTO'

# ── Lettura / scrittura CSV ───────────────────────────────────────────────────

def leggi_csv():
    """Legge il CSV e restituisce una lista di dizionari arricchiti."""
    if not os.path.exists(CSV_PATH):
        return []
    righe = []
    # Prova UTF-8 prima, poi Latin-1 come fallback (comune nei CSV italiani)
    enc = 'utf-8-sig'
    for e in ('utf-8-sig', 'utf-8', 'latin-1', 'cp1252'):
        try:
            with open(CSV_PATH, encoding=e) as _t: _t.read()
            enc = e; break
        except (UnicodeDecodeError, LookupError):
            continue
    with open(CSV_PATH, encoding=enc, newline='') as f:
        reader = csv.reader(f)
        prima = True
        for row in reader:
            if not any(c.strip() for c in row):
                continue
            if prima:
                prima = False
                # Salta intestazione se non inizia con cifra
                primo = pulisci(row[0]) if row else ''
                if not primo or not primo[0].isdigit():
                    continue
            if len(row) < 5:
                continue
            num_raw = pulisci(row[0])
            try:
                num = int(num_raw)
            except ValueError:
                continue
            richiedente = pulisci(row[2])
            tipo_mezzo  = pulisci(row[4])
            if not richiedente or not tipo_mezzo:
                continue
            scadenza_raw = pulisci(row[1])
            data_iso, stato = interpreta_scadenza(scadenza_raw)
            righe.append({
                'numero':    num,
                'scadenza':  scadenza_raw,
                'scadenza_iso': data_iso,
                'stato':     stato,
                'richiedente':      richiedente,
                'posizione':        pulisci(row[3]),
                'tipo_mezzo':       tipo_mezzo,
            })
    return righe

def salva_csv(righe):
    """Sovrascrive il CSV con le righe fornite."""
    # Backup prima di sovrascrivere
    if os.path.exists(CSV_PATH):
        backup = CSV_PATH + '.bak'
        with open(CSV_PATH, 'rb') as src, open(backup, 'wb') as dst:
            dst.write(src.read())
    with open(CSV_PATH, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(HEADER)
        for r in righe:
            writer.writerow([
                r['numero'],
                r['scadenza'],
                r['richiedente'],
                r['posizione'],
                r['tipo_mezzo'],
            ])

# ── HTTP Handler ──────────────────────────────────────────────────────────────

class Handler(BaseHTTPRequestHandler):

    def log_message(self, fmt, *args):
        print(f"[{self.log_date_time_string()}] {fmt % args}")

    def send_json(self, data, status=200):
        body = json.dumps(data, ensure_ascii=False).encode('utf-8')
        self.send_response(status)
        self.send_header('Content-Type', 'application/json; charset=utf-8')
        self.send_header('Content-Length', len(body))
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        self.wfile.write(body)

    def send_html(self, html):
        body = html.encode('utf-8')
        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.send_header('Content-Length', len(body))
        self.end_headers()
        self.wfile.write(body)

    def read_body_json(self):
        length = int(self.headers.get('Content-Length', 0))
        return json.loads(self.rfile.read(length).decode('utf-8'))

    # ── GET ──────────────────────────────────────────────────────────────────

    def do_GET(self):
        path = urlparse(self.path).path

        if path == '/' or path == '/index.html':
            html_path = os.path.join(BASE_DIR, 'index.html')
            with open(html_path, encoding='utf-8') as f:
                self.send_html(f.read())

        elif path == '/api/autorizzazioni':
            self.send_json(leggi_csv())

        elif path == '/api/info':
            righe = leggi_csv()
            oggi = date.today()
            attive  = sum(1 for r in righe if r['stato'] == 'ATTIVA')
            scadute = sum(1 for r in righe if r['stato'] == 'SCADUTA')
            # tipo mezzo più frequente
            conteggio = {}
            for r in righe:
                t = r['tipo_mezzo']
                conteggio[t] = conteggio.get(t, 0) + 1
            tipo_max = max(conteggio, key=conteggio.get) if conteggio else '—'
            self.send_json({
                'totale':   len(righe),
                'attive':   attive,
                'scadute':  scadute,
                'tipo_max': tipo_max,
            })

        else:
            self.send_response(404)
            self.end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()

    # ── POST ─────────────────────────────────────────────────────────────────

    def do_POST(self):
        path = urlparse(self.path).path

        # Inserimento nuova autorizzazione
        if path == '/api/autorizzazioni':
            try:
                data = self.read_body_json()
                righe = leggi_csv()
                nuovo_num = max((r['numero'] for r in righe), default=1000) + 1
                scadenza_raw = data.get('scadenza', '')
                data_iso, stato = interpreta_scadenza(scadenza_raw)
                nuova = {
                    'numero':      nuovo_num,
                    'scadenza':    scadenza_raw,
                    'scadenza_iso': data_iso,
                    'stato':       stato,
                    'richiedente': data.get('richiedente', '').strip(),
                    'posizione':   data.get('posizione', '').strip(),
                    'tipo_mezzo':  data.get('tipo_mezzo', '').strip(),
                }
                if not nuova['richiedente'] or not nuova['tipo_mezzo']:
                    self.send_json({'errore': 'Richiedente e tipo mezzo sono obbligatori'}, 400)
                    return
                righe.append(nuova)
                salva_csv(righe)
                self.send_json({'ok': True, 'numero': nuovo_num, 'record': nuova})
            except Exception as e:
                self.send_json({'errore': str(e)}, 500)

        # Caricamento CSV completo dal browser
        elif path == '/api/upload':
            try:
                length = int(self.headers.get('Content-Length', 0))
                raw_bytes = self.rfile.read(length)
                # Prova UTF-8 poi Latin-1 (per CSV con caratteri italiani)
                for enc in ('utf-8-sig', 'utf-8', 'latin-1', 'cp1252'):
                    try:
                        raw = raw_bytes.decode(enc)
                        break
                    except (UnicodeDecodeError, LookupError):
                        continue
                else:
                    raw = raw_bytes.decode('latin-1')
                # Salva sempre in UTF-8
                with open(CSV_PATH, 'w', encoding='utf-8', newline='') as f:
                    f.write(raw)
                self.send_json({'ok': True, 'righe': len(raw.splitlines())})
            except Exception as e:
                self.send_json({'errore': str(e)}, 500)

        else:
            self.send_response(404)
            self.end_headers()

    # ── PUT ──────────────────────────────────────────────────────────────────

    def do_PUT(self):
        path = urlparse(self.path).path
        # /api/autorizzazioni/1234
        if path.startswith('/api/autorizzazioni/'):
            try:
                num = int(path.split('/')[-1])
                data = self.read_body_json()
                righe = leggi_csv()
                trovato = False
                for i, r in enumerate(righe):
                    if r['numero'] == num:
                        scadenza_raw = data.get('scadenza', r['scadenza'])
                        data_iso, stato = interpreta_scadenza(scadenza_raw)
                        righe[i] = {
                            'numero':       num,
                            'scadenza':     scadenza_raw,
                            'scadenza_iso': data_iso,
                            'stato':        stato,
                            'richiedente':  data.get('richiedente', r['richiedente']).strip(),
                            'posizione':    data.get('posizione',   r['posizione']).strip(),
                            'tipo_mezzo':   data.get('tipo_mezzo',  r['tipo_mezzo']).strip(),
                        }
                        trovato = True
                        break
                if not trovato:
                    self.send_json({'errore': f'Numero {num} non trovato'}, 404)
                    return
                salva_csv(righe)
                self.send_json({'ok': True, 'record': righe[i]})
            except Exception as e:
                self.send_json({'errore': str(e)}, 500)
        else:
            self.send_response(404)
            self.end_headers()

    # ── DELETE ────────────────────────────────────────────────────────────────

    def do_DELETE(self):
        path = urlparse(self.path).path
        if path.startswith('/api/autorizzazioni/'):
            try:
                num = int(path.split('/')[-1])
                righe = leggi_csv()
                nuove = [r for r in righe if r['numero'] != num]
                if len(nuove) == len(righe):
                    self.send_json({'errore': f'Numero {num} non trovato'}, 404)
                    return
                salva_csv(nuove)
                self.send_json({'ok': True, 'eliminato': num})
            except Exception as e:
                self.send_json({'errore': str(e)}, 500)
        else:
            self.send_response(404)
            self.end_headers()


# ── Avvio ─────────────────────────────────────────────────────────────────────

if __name__ == '__main__':
    if not os.path.exists(CSV_PATH):
        print(f"ATTENZIONE: CSV non trovato in {CSV_PATH}")
        print("Verrà creato al primo salvataggio.")
    print(f"Server avviato su http://{HOST}:{PORT}")
    print(f"CSV: {CSV_PATH}")
    print("Premi Ctrl+C per fermare.")
    server = HTTPServer((HOST, PORT), Handler)
    server.serve_forever()
