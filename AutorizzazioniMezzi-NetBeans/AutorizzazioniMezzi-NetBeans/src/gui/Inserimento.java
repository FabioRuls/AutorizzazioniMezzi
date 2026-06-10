package gui;

import model.Autorizzazione;
import model.AutorizzazioniManager;

/**
 * Finestra per l'inserimento di una nuova autorizzazione.
 * Campi: Scadenza (dd.MM.yyyy), Richiedente, Posizione insegna, Tipo mezzo.
 * Il numero viene generato automaticamente (max esistente + 1).
 */
public class Inserimento extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Inserimento.class.getName());

    private AutorizzazioniManager manager;

    public Inserimento(AutorizzazioniManager manager) {
        this.manager = manager;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel2         = new javax.swing.JPanel();
        titolo          = new javax.swing.JLabel();
        lScadenza       = new javax.swing.JLabel();
        lRichiedente    = new javax.swing.JLabel();
        lPosizione      = new javax.swing.JLabel();
        lTipo           = new javax.swing.JLabel();
        scadenza        = new javax.swing.JTextField();
        richiedente     = new javax.swing.JTextField();
        posizione       = new javax.swing.JTextField();
        tipo            = new javax.swing.JTextField();
        InserisciB      = new javax.swing.JButton();
        jScrollPane1    = new javax.swing.JScrollPane();
        Output          = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Inserimento autorizzazione");

        jPanel2.setBackground(new java.awt.Color(188, 210, 243));

        titolo.setFont(new java.awt.Font("Verdana", 1, 14));
        titolo.setText("Inserimento");

        for (javax.swing.JLabel l : new javax.swing.JLabel[]{lScadenza, lRichiedente, lPosizione, lTipo}) {
            l.setFont(new java.awt.Font("Verdana", 0, 12));
        }
        lScadenza.setText("Scadenza (gg.mm.aaaa)");
        lRichiedente.setText("Richiedente");
        lPosizione.setText("Posizione insegna");
        lTipo.setText("Tipo mezzo");

        InserisciB.setBackground(new java.awt.Color(51, 51, 255));
        InserisciB.setFont(new java.awt.Font("Verdana", 1, 14));
        InserisciB.setForeground(new java.awt.Color(255, 255, 255));
        InserisciB.setText("Inserisci");
        InserisciB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { inserisciMouseClicked(evt); }
        });

        Output.setColumns(20);
        Output.setRows(5);
        Output.setEditable(false);
        jScrollPane1.setViewportView(Output);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(titolo)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(lScadenza).addComponent(scadenza, 120, 120, 120))
                .addGroup(layout.createParallelGroup().addComponent(lRichiedente).addComponent(richiedente, 160, 160, 160))
                .addGroup(layout.createParallelGroup().addComponent(lPosizione).addComponent(posizione, 200, 200, 200))
                .addGroup(layout.createParallelGroup().addComponent(lTipo).addComponent(tipo, 140, 140, 140)))
            .addComponent(InserisciB, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(titolo)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lScadenza).addComponent(lRichiedente)
                .addComponent(lPosizione).addComponent(lTipo))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(scadenza).addComponent(richiedente)
                .addComponent(posizione).addComponent(tipo))
            .addComponent(InserisciB)
            .addComponent(jScrollPane1, 120, 160, 160)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void inserisciMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager == null || manager.getElenco().isEmpty()) {
            Output.setText("Errore: nessun dato caricato. Carica prima un CSV dalla Home.");
            return;
        }

        String scadenzaStr   = scadenza.getText().trim();
        String richiedenteTx = richiedente.getText().trim();
        String posizioneTx   = posizione.getText().trim();
        String tipoTx        = tipo.getText().trim();

        if (richiedenteTx.isEmpty() || tipoTx.isEmpty()) {
            Output.setText("Errore: Richiedente e Tipo mezzo sono obbligatori.");
            return;
        }

        int nuovoNumero = manager.generaNuovoNumero();

        try {
            Autorizzazione nuova = new Autorizzazione(nuovoNumero, scadenzaStr,
                                                       richiedenteTx, posizioneTx, tipoTx);
            manager.inserisci(nuova);

            Output.setText(
                "Inserimento effettuato con successo!\n" +
                "─────────────────────────────────────\n" +
                "Numero assegnato: " + nuovoNumero + "\n" +
                "Scadenza:         " + nuova.getScadenzaFormattata() + "\n" +
                "Stato:            " + nuova.getStato().name() + "\n" +
                "Richiedente:      " + richiedenteTx + "\n" +
                "Posizione:        " + (posizioneTx.isEmpty() ? "—" : posizioneTx) + "\n" +
                "Tipo mezzo:       " + tipoTx + "\n" +
                "─────────────────────────────────────\n" +
                "Ricorda di salvare il CSV dalla Home."
            );

            // Pulisce i campi
            scadenza.setText("");
            richiedente.setText("");
            posizione.setText("");
            tipo.setText("");

        } catch (IllegalArgumentException | IllegalStateException e) {
            Output.setText("Errore: " + e.getMessage());
        }
    }

    // Variables declaration
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel titolo, lScadenza, lRichiedente, lPosizione, lTipo;
    private javax.swing.JTextField scadenza, richiedente, posizione, tipo;
    private javax.swing.JButton InserisciB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea Output;
}
