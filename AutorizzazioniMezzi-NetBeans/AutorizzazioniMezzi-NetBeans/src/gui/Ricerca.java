package gui;

import model.Autorizzazione;
import model.AutorizzazioniManager;
import model.StatoAutorizzazione;
import java.util.ArrayList;

/**
 * Finestra di ricerca autorizzazioni.
 * Criteri disponibili: Numero, Richiedente, Tipo mezzo, Posizione, Stato.
 */
public class Ricerca extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Ricerca.class.getName());

    private AutorizzazioniManager manager;

    public Ricerca(AutorizzazioniManager manager) {
        initComponents();
        this.manager = manager;
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        PannelloRicerca = new javax.swing.JPanel();
        jLabel1         = new javax.swing.JLabel();
        Selezione       = new javax.swing.JComboBox<>();
        Inserimento     = new javax.swing.JTextField();
        Cerca           = new javax.swing.JButton();
        jScrollPane1    = new javax.swing.JScrollPane();
        OutputArea      = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ricerca autorizzazioni");

        PannelloRicerca.setBackground(new java.awt.Color(188, 210, 243));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14));
        jLabel1.setText("Ricerca");

        Selezione.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Numero", "Richiedente", "Tipo mezzo", "Posizione", "Stato"}));

        Cerca.setBackground(new java.awt.Color(51, 51, 255));
        Cerca.setFont(new java.awt.Font("Verdana", 1, 14));
        Cerca.setForeground(new java.awt.Color(255, 255, 255));
        Cerca.setText("Cerca");
        Cerca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { cercaMouseClicked(evt); }
        });

        OutputArea.setEditable(false);
        OutputArea.setColumns(20);
        OutputArea.setRows(5);
        jScrollPane1.setViewportView(OutputArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(PannelloRicerca);
        PannelloRicerca.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(jLabel1)
            .addGroup(layout.createSequentialGroup()
                .addGap(80)
                .addComponent(Selezione, 130, 130, 130)
                .addComponent(Inserimento, 155, 155, 155)
                .addComponent(Cerca))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(jLabel1, 33, 33, 33)
            .addGap(12)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(Selezione).addComponent(Inserimento).addComponent(Cerca))
            .addComponent(jScrollPane1, 188, 188, 188)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup()
            .addComponent(PannelloRicerca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup()
            .addComponent(PannelloRicerca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void cercaMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager == null || manager.getElenco().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Carica prima un CSV.");
            return;
        }

        String tipo   = Selezione.getSelectedItem().toString();
        String valore = Inserimento.getText().trim();

        if (valore.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Inserisci un valore di ricerca.");
            return;
        }

        ArrayList<Autorizzazione> risultati = new ArrayList<>();

        switch (tipo) {
            case "Numero":
                try {
                    int n = Integer.parseInt(valore);
                    Autorizzazione a = manager.cercaPerNumero(n);
                    if (a != null) risultati.add(a);
                } catch (NumberFormatException e) {
                    OutputArea.setText("Errore: inserisci un numero intero.");
                    return;
                }
                break;
            case "Richiedente":
                risultati = manager.cercaPerRichiedente(valore);
                break;
            case "Tipo mezzo":
                risultati = manager.cercaPerTipoMezzo(valore);
                break;
            case "Posizione":
                risultati = manager.cercaPerPosizione(valore);
                break;
            case "Stato":
                try {
                    StatoAutorizzazione stato = StatoAutorizzazione.valueOf(valore.toUpperCase());
                    risultati = manager.cercaPerStato(stato);
                } catch (IllegalArgumentException e) {
                    OutputArea.setText("Stato non riconosciuto.\nValori validi: ATTIVA, ANNULLATA, NEGATA, SOSPESA, RIGETTATA, SCONOSCIUTO");
                    return;
                }
                break;
        }

        if (risultati.isEmpty()) {
            OutputArea.setText("NESSUN RISULTATO TROVATO");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Trovati: ").append(risultati.size()).append(" risultati\n");
        sb.append("═══════════════════════════════════════\n");
        for (Autorizzazione a : risultati) {
            sb.append("Numero:    ").append(a.getNumero()).append("\n");
            sb.append("Scadenza:  ").append(a.getScadenzaFormattata()).append("\n");
            sb.append("Stato:     ").append(a.getStato().name()).append("\n");
            sb.append("Richiedente: ").append(a.getRichiedente()).append("\n");
            sb.append("Posizione: ").append(a.getPosizioneInsegna()).append("\n");
            sb.append("Tipo:      ").append(a.getTipoMezzo()).append("\n");
            sb.append("───────────────────────────────────────\n");
        }
        OutputArea.setText(sb.toString());
    }

    // Variables declaration
    private javax.swing.JPanel PannelloRicerca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox<String> Selezione;
    private javax.swing.JTextField Inserimento;
    private javax.swing.JButton Cerca;
    private javax.swing.JTextArea OutputArea;
    private javax.swing.JScrollPane jScrollPane1;
}
