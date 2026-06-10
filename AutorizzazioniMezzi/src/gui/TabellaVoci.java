package gui;

import java.util.ArrayList;
import model.Autorizzazione;
import model.AutorizzazioniManager;

/**
 * Finestra che mostra tutte le autorizzazioni in una tabella scrollabile.
 * Si apre automaticamente dopo il caricamento del CSV dalla Home.
 */
public class TabellaVoci extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(TabellaVoci.class.getName());

    private AutorizzazioniManager manager;

    private static final String[] COLONNE = {
        "Numero", "Scadenza", "Stato", "Richiedente", "Posizione insegna", "Tipo mezzo"
    };

    public TabellaVoci(AutorizzazioniManager manager) {
        this.manager = manager;
        initComponents();
        popolaTabella();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tabella = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Elenco Autorizzazioni");

        tabella.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, COLONNE));
        tabella.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(tabella);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1198, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void popolaTabella() {
        javax.swing.table.DefaultTableModel modello = new javax.swing.table.DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Autorizzazione a : manager.getElenco()) {
            modello.addRow(new Object[]{
                a.getNumero(),
                a.getScadenzaFormattata(),
                a.getStato().name(),
                a.getRichiedente(),
                a.getPosizioneInsegna(),
                a.getTipoMezzo()
            });
        }
        tabella.setModel(modello);
    }

    // Variables declaration
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabella;
}
