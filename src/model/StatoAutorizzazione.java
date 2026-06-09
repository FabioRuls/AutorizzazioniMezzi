package model;

/**
 * Enum che rappresenta i possibili stati di un'autorizzazione.
 * Gestisce i valori speciali trovati nel file ODS (ANNULLATA, NEGATA, ecc.).
 */
public enum StatoAutorizzazione {
    ATTIVA,
    ANNULLATA,
    NEGATA,
    SOSPESA,
    RIGETTATA,
    SCONOSCIUTO;

    /**
     * Interpreta una stringa grezza dalla colonna Scadenza.
     * Restituisce ATTIVA se sembra una data (verrà parsata separatamente),
     * altrimenti restituisce lo stato speciale corrispondente.
     * Gestisce anche i typo presenti nel file (es. "ANNUUATA", "RIGETTTATO PER ROTONDA").
     *
     * @param valore il valore grezzo dalla colonna Scadenza
     * @return lo StatoAutorizzazione corrispondente
     */
    public static StatoAutorizzazione fromStringa(String valore) {
        if (valore == null || valore.trim().isEmpty()) {
            return SCONOSCIUTO;
        }
        String v = valore.trim().toUpperCase();
        if (v.contains("ANNUL")) return ANNULLATA;  // copre "ANNULLATA" e "ANNUUATA" (typo)
        if (v.contains("NEGA"))  return NEGATA;
        if (v.contains("SOSP"))  return SOSPESA;
        if (v.contains("RIGET") || v.contains("ROTO")) return RIGETTATA;
        return ATTIVA; // probabile data, verrà parsata separatamente
    }
}
