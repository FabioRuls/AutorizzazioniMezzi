package model;

import exceptions.AutorizzazioneException;
import exceptions.FileException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe manager principale per la gestione delle autorizzazioni mezzi pubblicitari.
 * Punto di accesso unico per tutte le operazioni da GUI.
 *
 * Gestisce:
 * - Lettura/scrittura CSV (con intestazione)
 * - Struttura dati in memoria (ArrayList + indici per ricerca)
 * - Operazioni CRUD (inserisci, modifica, elimina)
 * - Ricerche multi-campo (case-insensitive)
 * - Statistiche e report (scadute, attive, per tipo mezzo)
 *
 * Il formato CSV atteso è:
 * {@code Numero autorizzazione,Scadenza,Richiedente,Posizione insegna,Tipo di mezzo pubblico}
 */
public class AutorizzazioniManager {

    private ArrayList<Autorizzazione> elenco;

    private static final String SEPARATORE = ",";

    /** Intestazione del CSV di output. */
    private static final String HEADER =
            "Numero autorizzazione,Scadenza,Richiedente,Posizione insegna,Tipo di mezzo pubblico";

    // ─────────────────────────────────────────────────────────────────────
    // COSTRUTTORE
    // ─────────────────────────────────────────────────────────────────────

    public AutorizzazioniManager() {
        this.elenco = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────
    // LETTURA CSV
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Carica le autorizzazioni da un file CSV in memoria.
     *
     * Comportamento sulle anomalie:
     * - Salta la riga di intestazione automaticamente (prima riga non vuota).
     * - Righe vuote o con campi insufficienti → saltate con log su stderr.
     * - Date malformate → parsate best-effort (vedi {@link Autorizzazione#parseScadenza}).
     * - Numeri autorizzazione non numerici → riga saltata con log.
     * - Non crasha mai per dati sporchi: legge il massimo possibile.
     *
     * @param file il file CSV da leggere
     * @throws IOException   se il file non può essere aperto o letto
     */
    public void caricaCSV(File file) throws IOException {
        elenco.clear();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String riga;
            boolean primaRiga = true;
            int numeroLinea   = 0;

            while ((riga = br.readLine()) != null) {
                numeroLinea++;

                if (riga.trim().isEmpty()) continue;

                if (primaRiga) {
                    primaRiga = false;
                    // Se la prima riga è l'intestazione (non inizia con cifra), la saltiamo
                    if (!Character.isDigit(riga.trim().charAt(0))) continue;
                }

                try {
                    Autorizzazione a = parseRiga(riga);
                    elenco.add(a);
                } catch (IllegalArgumentException e) {
                    System.err.println("[Riga " + numeroLinea + "] Saltata: " + e.getMessage() + " → \"" + riga + "\"");
                }
            }
        }

        System.out.println("Caricate " + elenco.size() + " autorizzazioni.");
    }

    /**
     * Effettua il parsing di una riga CSV in un oggetto {@link Autorizzazione}.
     *
     * Il separatore è la virgola. I campi previsti sono 5:
     * {@code numero, scadenza, richiedente, posizioneInsegna, tipoMezzo}
     * Se ci sono meno di 5 campi → IllegalArgumentException.
     * Se il campo numero non è intero → IllegalArgumentException.
     * Campi richiedente o tipoMezzo vuoti → IllegalArgumentException.
     *
     * @param riga la riga CSV da parsare
     * @return l'oggetto Autorizzazione costruito
     * @throws IllegalArgumentException se la riga è malformata
     */
    /** Rimuove spazi esterni e virgolette da un campo CSV. */
    private static String pulisci(String campo) {
        if (campo == null) return "";
        return campo.trim().replaceAll("^\"|\"$", "");
    }

    private Autorizzazione parseRiga(String riga) {
        String[] campi = riga.split(SEPARATORE, -1);

        if (campi.length < 5) {
            throw new IllegalArgumentException("Campi insufficienti (" + campi.length + "/5)");
        }

        int numero;
        try {
            numero = Integer.parseInt(pulisci(campi[0]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Numero autorizzazione non numerico: \"" + campi[0] + "\"");
        }

        String scadenza    = pulisci(campi[1]);
        String richiedente = pulisci(campi[2]);
        String posizione   = pulisci(campi[3]);
        String tipoMezzo   = pulisci(campi[4]);

        // richiedente e tipoMezzo sono obbligatori
        if (richiedente.isEmpty()) {
            throw new IllegalArgumentException("Richiedente vuoto");
        }
        if (tipoMezzo.isEmpty()) {
            throw new IllegalArgumentException("Tipo mezzo vuoto");
        }

        // Il costruttore Autorizzazione gestisce internamente la scadenza malformata
        return new Autorizzazione(numero, scadenza, richiedente, posizione, tipoMezzo);
    }

    // ─────────────────────────────────────────────────────────────────────
    // SCRITTURA CSV
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Salva tutte le autorizzazioni in un file CSV, sovrascrivendo il contenuto esistente.
     * Include la riga di intestazione.
     *
     * @param file il file di destinazione
     * @throws IOException   se il file non può essere scritto
     * @throws FileException se la struttura dati è vuota
     */
    public void salvaCSV(File file) throws IOException, FileException {
        if (elenco.isEmpty()) {
            throw new FileException("Impossibile scrivere: la struttura dati è vuota.");
        }
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write(HEADER);
            bw.newLine();
            for (Autorizzazione a : elenco) {
                bw.write(a.toCSV());
                bw.newLine();
            }
        }
    }

    /**
     * Salva tutte le autorizzazioni su un percorso stringa.
     * Valida il percorso e crea il file se non esiste.
     *
     * @param percorso il percorso di destinazione
     * @throws IOException   se il file non può essere scritto
     * @throws FileException se la struttura è vuota o il file non può essere creato
     */
    public void salvaCSV(String percorso) throws IOException, FileException {
        if (elenco.isEmpty()) {
            throw new FileException("Impossibile scrivere: la struttura dati è vuota.");
        }
        percorso = validaPercorso(percorso);
        File file = new File(percorso);
        if (!file.createNewFile()) {
            throw new FileException("Impossibile creare il file (potrebbe già esistere): " + percorso);
        }
        salvaCSV(file);
    }

    /**
     * Valida un percorso per il file CSV di output.
     * Se il percorso è null/vuoto → restituisce "outputAutorizzazioni.csv".
     * Se termina con separatore → aggiunge il nome di default.
     * Se non termina con ".csv" → considera che sia una directory.
     *
     * @param percorso il percorso da validare
     * @return il percorso validato
     */
    public static String validaPercorso(String percorso) {
        if (percorso == null || percorso.trim().isEmpty()) {
            return "outputAutorizzazioni.csv";
        }
        percorso = percorso.trim();
        if (percorso.endsWith("/") || percorso.endsWith("\\")) {
            return percorso + "outputAutorizzazioni.csv";
        }
        Path path     = Paths.get(percorso);
        Path fileName = path.getFileName();
        if (fileName == null || !fileName.toString().toLowerCase().endsWith(".csv")) {
            return percorso + File.separator + "outputAutorizzazioni.csv";
        }
        return percorso;
    }

    // ─────────────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Aggiunge una nuova autorizzazione all'elenco.
     *
     * @param a l'autorizzazione da inserire (non null)
     * @throws IllegalArgumentException se null
     * @throws IllegalStateException    se esiste già un'autorizzazione con lo stesso numero
     */
    public void inserisci(Autorizzazione a) {
        if (a == null) throw new IllegalArgumentException("L'autorizzazione non può essere null");
        if (cercaPerNumero(a.getNumero()) != null) {
            throw new IllegalStateException("Esiste già un'autorizzazione con il numero: " + a.getNumero());
        }
        elenco.add(a);
    }

    /**
     * Sostituisce l'autorizzazione con lo stesso numero con i nuovi valori.
     *
     * @param aggiornata l'autorizzazione aggiornata (non null)
     * @throws IllegalArgumentException  se null
     * @throws NoSuchElementException    se il numero non è trovato
     */
    public void modifica(Autorizzazione aggiornata) {
        if (aggiornata == null) throw new IllegalArgumentException("L'autorizzazione non può essere null");
        for (int i = 0; i < elenco.size(); i++) {
            if (elenco.get(i).getNumero() == aggiornata.getNumero()) {
                elenco.set(i, aggiornata);
                return;
            }
        }
        throw new NoSuchElementException("Nessuna autorizzazione con numero: " + aggiornata.getNumero());
    }

    /**
     * Rimuove l'autorizzazione con il numero specificato.
     *
     * @param numero il numero autorizzazione da eliminare
     * @throws IllegalArgumentException se il numero è <= 0
     * @throws NoSuchElementException   se non trovato
     */
    public void elimina(int numero) {
        if (numero <= 0) throw new IllegalArgumentException("Numero non valido: " + numero);
        boolean rimossa = elenco.removeIf(a -> a.getNumero() == numero);
        if (!rimossa) {
            throw new NoSuchElementException("Nessuna autorizzazione con numero: " + numero);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // RICERCHE
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Cerca un'autorizzazione per numero esatto.
     *
     * @param numero il numero da cercare
     * @return l'oggetto trovato, o null se non esiste
     */
    public Autorizzazione cercaPerNumero(int numero) {
        for (Autorizzazione a : elenco) {
            if (a.getNumero() == numero) return a;
        }
        return null;
    }

    /**
     * Cerca autorizzazioni per richiedente (case-insensitive, ricerca parziale).
     *
     * @param query la stringa da cercare nel nome del richiedente
     * @return lista dei risultati (vuota se nessun match)
     */
    public ArrayList<Autorizzazione> cercaPerRichiedente(String query) {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return risultati;
        String q = query.trim().toLowerCase();
        for (Autorizzazione a : elenco) {
            if (a.getRichiedente().toLowerCase().contains(q)) risultati.add(a);
        }
        return risultati;
    }

    /**
     * Cerca autorizzazioni per tipo di mezzo (case-insensitive, ricerca parziale).
     *
     * @param tipo il tipo di mezzo da cercare
     * @return lista dei risultati
     */
    public ArrayList<Autorizzazione> cercaPerTipoMezzo(String tipo) {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        if (tipo == null || tipo.trim().isEmpty()) return risultati;
        String q = tipo.trim().toLowerCase();
        for (Autorizzazione a : elenco) {
            if (a.getTipoMezzo().toLowerCase().contains(q)) risultati.add(a);
        }
        return risultati;
    }

    /**
     * Cerca autorizzazioni per posizione insegna (case-insensitive, ricerca parziale).
     *
     * @param posizione la stringa da cercare nella posizione
     * @return lista dei risultati
     */
    public ArrayList<Autorizzazione> cercaPerPosizione(String posizione) {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        if (posizione == null || posizione.trim().isEmpty()) return risultati;
        String q = posizione.trim().toLowerCase();
        for (Autorizzazione a : elenco) {
            if (a.getPosizioneInsegna().toLowerCase().contains(q)) risultati.add(a);
        }
        return risultati;
    }

    /**
     * Cerca autorizzazioni per stato.
     *
     * @param stato lo stato da filtrare
     * @return lista delle autorizzazioni con quello stato
     */
    public ArrayList<Autorizzazione> cercaPerStato(StatoAutorizzazione stato) {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        if (stato == null) return risultati;
        for (Autorizzazione a : elenco) {
            if (a.getStato() == stato) risultati.add(a);
        }
        return risultati;
    }

    // ─────────────────────────────────────────────────────────────────────
    // STATISTICHE / REPORT
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Restituisce tutte le autorizzazioni attive e non scadute.
     *
     * @return lista delle autorizzazioni valide
     */
    public ArrayList<Autorizzazione> getAttive() {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        for (Autorizzazione a : elenco) {
            if (a.isValida()) risultati.add(a);
        }
        return risultati;
    }

    /**
     * Restituisce tutte le autorizzazioni con scadenza nel passato (stato ATTIVA ma data passata).
     *
     * @return lista delle autorizzazioni scadute
     */
    public ArrayList<Autorizzazione> getScadute() {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        for (Autorizzazione a : elenco) {
            if (a.isScaduta()) risultati.add(a);
        }
        return risultati;
    }

    /**
     * Restituisce le autorizzazioni che scadono entro i prossimi {@code giorni} giorni.
     *
     * @param giorni numero di giorni dalla data odierna
     * @return lista delle autorizzazioni in scadenza imminente
     */
    public ArrayList<Autorizzazione> getInScadenzaEntro(int giorni) {
        ArrayList<Autorizzazione> risultati = new ArrayList<>();
        LocalDate oggi   = LocalDate.now();
        LocalDate limite = oggi.plusDays(giorni);
        for (Autorizzazione a : elenco) {
            LocalDate sc = a.getScadenza();
            if (sc != null && !sc.isBefore(oggi) && !sc.isAfter(limite)) {
                risultati.add(a);
            }
        }
        return risultati;
    }

    /**
     * Conta quante autorizzazioni ci sono per ogni tipo di mezzo.
     *
     * @return mappa tipoMezzo → conteggio (ordinata per conteggio decrescente)
     */
    public LinkedHashMap<String, Integer> getConteggioPerTipoMezzo() {
        HashMap<String, Integer> conteggio = new HashMap<>();
        for (Autorizzazione a : elenco) {
            String tipo = a.getTipoMezzo();
            conteggio.put(tipo, conteggio.getOrDefault(tipo, 0) + 1);
        }
        // Ordina per valore decrescente
        LinkedHashMap<String, Integer> ordinata = new LinkedHashMap<>();
        conteggio.entrySet().stream()
                 .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                 .forEach(e -> ordinata.put(e.getKey(), e.getValue()));
        return ordinata;
    }

    /**
     * Tipo di mezzo più frequente nell'elenco.
     *
     * @return il tipo di mezzo con il maggior numero di autorizzazioni, o "" se elenco vuoto
     */
    public String getTipoMezzoMaxFrequenza() {
        LinkedHashMap<String, Integer> conteggio = getConteggioPerTipoMezzo();
        return conteggio.isEmpty() ? "" : conteggio.entrySet().iterator().next().getKey();
    }

    /**
     * Genera un nuovo numero autorizzazione univoco (max esistente + 1).
     *
     * @return il nuovo numero
     */
    public int generaNuovoNumero() {
        int max = 0;
        for (Autorizzazione a : elenco) {
            if (a.getNumero() > max) max = a.getNumero();
        }
        return max + 1;
    }

    // ─────────────────────────────────────────────────────────────────────
    // GETTERS GENERALI
    // ─────────────────────────────────────────────────────────────────────

    /** @return l'elenco completo di tutte le autorizzazioni */
    public ArrayList<Autorizzazione> getElenco() { return elenco; }

    /** @return il numero totale di autorizzazioni caricate */
    public int getTotale() { return elenco.size(); }
}
