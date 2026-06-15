# AutorizzazioniMezzi — App Web

Applicazione web per la gestione delle autorizzazioni mezzi pubblicitari.
Nessuna dipendenza esterna: richiede solo Python 3 (già installato su quasi tutti i server Linux).

## File inclusi

```
AutorizzazioniWeb/
├── server.py          ← backend Python (avvia questo)
├── index.html         ← frontend (servito automaticamente dal server)
├── autorizzazioni.csv ← il tuo file CSV (mettilo qui)
└── README.md
```

## Installazione

1. Copia la cartella `AutorizzazioniWeb/` sul server Linux
2. Metti il file CSV nella stessa cartella con il nome `autorizzazioni.csv`
   (oppure specifica il percorso con la variabile CSV_PATH — vedi sotto)

## Avvio

```bash
cd AutorizzazioniWeb
python3 server.py
```

Il server parte sulla porta **8080**.
Aprire il browser su: `http://INDIRIZZO_SERVER:8080`

## Avvio automatico (consigliato per produzione)

Per farlo partire automaticamente con il sistema, crea un servizio systemd:

```bash
sudo nano /etc/systemd/system/autorizzazioni.service
```

Incolla questo contenuto (modifica i percorsi):

```ini
[Unit]
Description=AutorizzazioniMezzi Web App
After=network.target

[Service]
User=www-data
WorkingDirectory=/percorso/AutorizzazioniWeb
ExecStart=python3 /percorso/AutorizzazioniWeb/server.py
Restart=always
Environment=PORT=8080

[Install]
WantedBy=multi-user.target
```

Poi:
```bash
sudo systemctl daemon-reload
sudo systemctl enable autorizzazioni
sudo systemctl start autorizzazioni
```

## Variabili d'ambiente

| Variabile  | Default                        | Descrizione                    |
|------------|--------------------------------|--------------------------------|
| `CSV_PATH` | `./autorizzazioni.csv`         | Percorso del file CSV          |
| `PORT`     | `8080`                         | Porta del server               |
| `HOST`     | `0.0.0.0`                      | Indirizzo di ascolto           |

Esempio con percorso CSV personalizzato:
```bash
CSV_PATH=/dati/comune/autorizzazioni.csv PORT=80 python3 server.py
```

## Funzionalità

- **Dashboard** — totale, attive, scadute, tipo mezzo più frequente
- **Tabella** con ricerca testo libero, filtro per stato e tipo mezzo
- **Ordinamento** per qualsiasi colonna (click sull'intestazione)
- **Inserimento** nuova autorizzazione (numero assegnato automaticamente)
- **Modifica** autorizzazione esistente
- **Eliminazione** con conferma
- **Sezione Scadute** — elenco colorato (giallo/arancione/rosso) con giorni scaduta
- **Carica CSV** — sostituisce il CSV sul server con uno da file locale
- **Scarica CSV** — scarica il CSV aggiornato

## Backup

Ad ogni salvataggio il server crea automaticamente `autorizzazioni.csv.bak`
con il contenuto precedente, nella stessa cartella.

## Porta 80 (senza sudo)

Se vuoi usare la porta 80 standard senza privilegi root:
```bash
# Opzione 1: redirect con iptables
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

# Opzione 2: usa authbind
sudo apt install authbind
sudo touch /etc/authbind/byport/80
sudo chmod 500 /etc/authbind/byport/80
sudo chown www-data /etc/authbind/byport/80
PORT=80 authbind python3 server.py
```
