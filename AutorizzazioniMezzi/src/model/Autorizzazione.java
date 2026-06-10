package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Rappresenta una singola autorizzazione per mezzo pubblicitario.
 * Corrisponde a una riga del file CSV/ODS con i campi:
 *   numero, scadenza, richiedente, posizioneInsegna, tipoMezzo.
 *
 * La colonna Scadenza può contenere:
 *   - una data valida (es. "12.07.2024")
 *   - una data malformata (es. "01.O8.2024", "27.05.26") — gestita con best-effort
 *   - uno stato speciale (es. "ANNULLATA", "NEGATA", "SOSPESA") — gestito come enum
 *   - un campo vuoto — trattato come SCONOSCIUTO
 */
public class Autorizzazione {

    private int    numero;
    private String scadenzaRaw;         // valore grezzo dal file (mai modificato)
    private LocalDate scadenza;          // null se lo stato non è ATTIVA
    private StatoAutorizzazione stato;
    private String richiedente;
    private String posizioneInsegna;
    private String tipoMezzo;

    /** Formati data accettati nel file, tentati in ordine. */
    private static final DateTimeFormatter[] FORMATI = {
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    };

    /** Formato usato per l'output (normalizzato). */
    private static final DateTimeFormatter FORMATO_OUTPUT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String SEPARATORE = ",";

    // ─────────────────────────────────────────────────────────────────────
    // COSTRUTTORI
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Costruttore completo usato dal parser CSV/ODS.
     *
     * @param numero           numero autorizzazione (> 0)
     * @param scadenzaRaw      valore grezzo della colonna Scadenza
     * @param richiedente      nome del richiedente (non null/vuoto)
     * @param posizioneInsegna indirizzo/posizione dell'insegna (può essere vuota)
     * @param tipoMezzo        tipo di mezzo pubblicitario (non null/vuoto)
     * @throws IllegalArgumentException se un campo obbligatorio è invalido
     */
    public Autorizzazione(int numero, String scadenzaRaw,
                          String richiedente, String posizioneInsegna, String tipoMezzo) {
        this.setNumero(numero);
        this.setRichiedente(richiedente);
        this.setPosizioneInsegna(posizioneInsegna);
        this.setTipoMezzo(tipoMezzo);
        this.parseScadenza(scadenzaRaw);
    }

    /** Costruttore vuoto con valori di default. Usato per form di inserimento. */
    public Autorizzazione() {
        this.numero           = 0;
        this.scadenzaRaw      = "";
        this.scadenza         = null;
        this.stato            = StatoAutorizzazione.SCONOSCIUTO;
        this.richiedente      = "";
        this.posizioneInsegna = "";
        this.tipoMezzo        = "";
    }

    /** Costruttore di copia (copia difensiva). */
    public Autorizzazione(Autorizzazione a) {
        this.numero           = a.numero;
        this.scadenzaRaw      = a.scadenzaRaw;
        this.scadenza         = a.scadenza;
        this.stato            = a.stato;
        this.richiedente      = a.richiedente;
        this.posizioneInsegna = a.posizioneInsegna;
        this.tipoMezzo        = a.tipoMezzo;
    }

    // ─────────────────────────────────────────────────────────────────────
    // PARSING SCADENZA  (logica centrale di gestione anomalie)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Interpreta la stringa grezza della colonna Scadenza.
     *
     * Strategia:
     * 1. Se null/vuota  → SCONOSCIUTO, scadenza=null
     * 2. Se stato speciale (ANNULLATA, NEGATA, ...) → stato relativo, scadenza=null
     * 3. Altrimenti tenta il parsing come data (con normalizzazione):
     *    - corregge 'O' (lettera) → '0' (zero)
     *    - espande anni a 2 cifre  ("25"  → "2025")
     *    - tenta 3 separatori (. - /)
     *    Se tutti i tentativi falliscono → SCONOSCIUTO, log su stderr, non crasha.
     *
     * @param raw il valore grezzo dal file
     */
    private void parseScadenza(String raw) {
        // Rimuove spazi esterni, virgolette (es. "12.07.2024"), spazi interni (es. "26.01. 2025")
        String pulito = (raw != null) ? raw.trim() : "";
        pulito = pulito.replaceAll("^\"|\"$", "");  // rimuove virgolette iniziali/finali
        pulito = pulito.replaceAll("\\s+", "");      // rimuove tutti gli spazi interni
        this.scadenzaRaw = pulito;

        if (this.scadenzaRaw.isEmpty()) {
            this.stato    = StatoAutorizzazione.SCONOSCIUTO;
            this.scadenza = null;
            return;
        }

        // Controlla se è uno stato speciale (testo non-data)
        StatoAutorizzazione statoRilevato = StatoAutorizzazione.fromStringa(this.scadenzaRaw);
        if (statoRilevato != StatoAutorizzazione.ATTIVA) {
            this.stato    = statoRilevato;
            this.scadenza = null;
            return;
        }

        // Prova a parsare come data, con normalizzazione best-effort
        String dataNorm = normalizzaData(this.scadenzaRaw);
        for (DateTimeFormatter fmt : FORMATI) {
            try {
                this.scadenza = LocalDate.parse(dataNorm, fmt);
                this.stato    = StatoAutorizzazione.ATTIVA;
                return;
            } catch (DateTimeParseException ignored) {
                // prova il formato successivo
            }
        }

        // Tutti i formati hanno fallito: dato malformato irrecuperabile
        System.err.println("[Autorizzazione " + this.numero + "] Data malformata ignorata: \"" + raw + "\"");
        this.stato    = StatoAutorizzazione.SCONOSCIUTO;
        this.scadenza = null;
    }

    /**
     * Normalizza una stringa data correggendo gli errori tipici del file:
     * <ul>
     *   <li>Sostituisce la lettera O maiuscola/minuscola con lo zero ('0'):
     *       {@code "01.O8.2024"} → {@code "01.08.2024"}</li>
     *   <li>Espande anni a 2 cifre aggiungendo "20":
     *       {@code "27.05.26"} → {@code "27.05.2026"}</li>
     *   <li>Segnala anni a 3 cifre su stderr e tenta un'espansione parziale.</li>
     * </ul>
     *
     * @param raw la stringa grezza (non null)
     * @return la stringa normalizzata, pronta per il parsing
     */
    private String normalizzaData(String raw) {
        String norm = raw.replace('O', '0').replace('o', '0');

        // Determina il separatore presente
        char sep = '.';
        if (norm.contains("-")) sep = '-';
        else if (norm.contains("/")) sep = '/';

        String[] parti = norm.split("\\" + sep);
        if (parti.length == 3) {
            String anno = parti[2];
            if (anno.length() == 2) {
                // Anno abbreviato: "25" → "2025"
                parti[2] = "20" + anno;
            } else if (anno.length() == 3) {
                // Anno troncato (es. "202"): tenta "2024" come fallback e segnala
                String annoExpanso = "20" + anno.charAt(anno.length() - 2) + anno.charAt(anno.length() - 1);
                System.err.println("[Autorizzazione " + this.numero + "] Anno troncato \"" + anno
                        + "\" → interpretato come \"" + annoExpanso + "\"");
                parti[2] = annoExpanso;
            }
            norm = parti[0] + sep + parti[1] + sep + parti[2];
        }

        return norm;
    }

    // ─────────────────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────────────────

    /** @return il numero autorizzazione */
    public int getNumero() { return numero; }

    /** @return il valore grezzo originale della colonna Scadenza */
    public String getScadenzaRaw() { return scadenzaRaw; }

    /** @return la data di scadenza come LocalDate, o null se non disponibile */
    public LocalDate getScadenza() { return scadenza; }

    /** @return lo stato dell'autorizzazione */
    public StatoAutorizzazione getStato() { return stato; }

    /** @return il nome del richiedente */
    public String getRichiedente() { return richiedente; }

    /** @return la posizione/indirizzo dell'insegna */
    public String getPosizioneInsegna() { return posizioneInsegna; }

    /** @return il tipo di mezzo pubblicitario */
    public String getTipoMezzo() { return tipoMezzo; }

    /**
     * @return la scadenza formattata come "dd.MM.yyyy",
     *         oppure il nome dello stato se la data non è disponibile
     */
    public String getScadenzaFormattata() {
        if (scadenza != null) {
            return scadenza.format(FORMATO_OUTPUT);
        }
        return stato.name();
    }

    /**
     * @return true se la scadenza è nel passato rispetto alla data odierna
     */
    public boolean isScaduta() {
        return scadenza != null && scadenza.isBefore(LocalDate.now());
    }

    /**
     * @return true se l'autorizzazione è ATTIVA e non ancora scaduta
     */
    public boolean isValida() {
        return stato == StatoAutorizzazione.ATTIVA && !isScaduta();
    }

    // ─────────────────────────────────────────────────────────────────────
    // SETTERS
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Imposta il numero autorizzazione.
     *
     * @param numero deve essere > 0
     * @throws IllegalArgumentException se <= 0
     */
    public void setNumero(int numero) {
        if (numero <= 0)
            throw new IllegalArgumentException("Numero autorizzazione non valido: " + numero);
        this.numero = numero;
    }

    /**
     * Aggiorna la scadenza ri-parsando la stringa grezza.
     *
     * @param scadenzaRaw il nuovo valore (data o stato speciale)
     */
    public void setScadenza(String scadenzaRaw) {
        parseScadenza(scadenzaRaw);
    }

    /**
     * Imposta direttamente la scadenza come LocalDate (imposta stato=ATTIVA).
     *
     * @param scadenza la data (non null)
     * @throws IllegalArgumentException se null
     */
    public void setScadenza(LocalDate scadenza) {
        if (scadenza == null)
            throw new IllegalArgumentException("La data di scadenza non può essere null");
        this.scadenza    = scadenza;
        this.scadenzaRaw = scadenza.format(FORMATO_OUTPUT);
        this.stato       = StatoAutorizzazione.ATTIVA;
    }

    /**
     * @param richiedente nome del richiedente (non null/vuoto)
     * @throws IllegalArgumentException se null o vuoto
     */
    public void setRichiedente(String richiedente) {
        if (richiedente == null || richiedente.trim().isEmpty())
            throw new IllegalArgumentException("Il richiedente non può essere vuoto");
        this.richiedente = richiedente.trim();
    }

    /** @param pos posizione dell'insegna (può essere vuota) */
    public void setPosizioneInsegna(String pos) {
        this.posizioneInsegna = (pos != null) ? pos.trim() : "";
    }

    /**
     * @param tipo tipo di mezzo pubblicitario (non null/vuoto)
     * @throws IllegalArgumentException se null o vuoto
     */
    public void setTipoMezzo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty())
            throw new IllegalArgumentException("Il tipo di mezzo non può essere vuoto");
        this.tipoMezzo = tipo.trim();
    }

    // ─────────────────────────────────────────────────────────────────────
    // METODI
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Serializza l'autorizzazione in formato CSV.
     * Formato: numero,scadenzaRaw,richiedente,posizioneInsegna,tipoMezzo
     *
     * @return stringa CSV pronta per la scrittura su file
     */
    public String toCSV() {
        return numero + SEPARATORE +
               scadenzaRaw + SEPARATORE +
               richiedente + SEPARATORE +
               posizioneInsegna + SEPARATORE +
               tipoMezzo;
    }

    @Override
    public String toString() {
        return "[" + numero + "] " + richiedente
                + " | " + tipoMezzo
                + " | " + posizioneInsegna
                + " | Scadenza: " + getScadenzaFormattata()
                + " | Stato: " + stato.name();
    }

    /**
     * Due autorizzazioni sono uguali se hanno lo stesso numero.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Autorizzazione)) return false;
        return this.numero == ((Autorizzazione) obj).numero;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }
}
