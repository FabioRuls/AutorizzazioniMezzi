package gui;

import model.Autorizzazione;
import model.AutorizzazioniManager;

/**
 * Finestra per modificare un'autorizzazione esistente.
 * L'utente inserisce il numero autorizzazione, poi compila i nuovi valori.
 */
public class Modifica extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Modifica.class.getName());

    private AutorizzazioniManager manager;

    public Modifica(AutorizzazioniManager manager) {
        this.manager = manager;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel1      = new javax.swing.JPanel();
        titolo       = new javax.swing.JLabel();
        lNumero      = new javax.swing.JLabel();
        lHint        = new javax.swing.JLabel();
        numero       = new javax.swing.JTextField();
        lScadenza    = new javax.swing.JLabel();
        lRichiedente = new javax.swing.JLabel();
        lPosizione   = new javax.swing.JLabel();
        lTipo        = new javax.swing.JLabel();
        scadenza     = new javax.swing.JTextField();
        richiedente  = new javax.swing.JTextField();
        posizione    = new javax.swing.JTextField();
        tipo         = new javax.swing.JTextField();
        ModificaB    = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Output       = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Modifica autorizzazione");

        jPanel1.setBackground(new java.awt.Color(188, 210, 243));

        titolo.setFont(new java.awt.Font("Verdana", 1, 14));
        titolo.setText("Modifica");

        lHint.setFont(new java.awt.Font("Verdana", 0, 12));
        lHint.setText("Numero autorizzazione da modificare:");

        for (javax.swing.JLabel l : new javax.swing.JLabel[]{lNumero, lScadenza, lRichiedente, lPosizione, lTipo}) {
            l.setFont(new java.awt.Font("Verdana", 0, 12));
        }
        lNumero.setText("Numero");
        lScadenza.setText("Scadenza (gg.mm.aaaa)");
        lRichiedente.setText("Richiedente");
        lPosizione.setText("Posizione insegna");
        lTipo.setText("Tipo mezzo");

        ModificaB.setBackground(new java.awt.Color(51, 51, 255));
        ModificaB.setFont(new java.awt.Font("Verdana", 1, 14));
        ModificaB.setForeground(new java.awt.Color(255, 255, 255));
        ModificaB.setText("Modifica");
        ModificaB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { modificaMouseClicked(evt); }
        });

        Output.setColumns(20);
        Output.setRows(5);
        Output.setEditable(false);
        jScrollPane1.setViewportView(Output);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(titolo)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lHint)
                .addComponent(numero, 80, 80, 80))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(lScadenza).addComponent(scadenza, 120, 120, 120))
                .addGroup(layout.createParallelGroup().addComponent(lRichiedente).addComponent(richiedente, 160, 160, 160))
                .addGroup(layout.createParallelGroup().addComponent(lPosizione).addComponent(posizione, 200, 200, 200))
                .addGroup(layout.createParallelGroup().addComponent(lTipo).addComponent(tipo, 140, 140, 140)))
            .addComponent(ModificaB, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(titolo)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lHint).addComponent(numero))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lScadenza).addComponent(lRichiedente)
                .addComponent(lPosizione).addComponent(lTipo))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(scadenza).addComponent(richiedente)
                .addComponent(posizione).addComponent(tipo))
            .addComponent(ModificaB)
            .addComponent(jScrollPane1, 120, 160, 160)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void modificaMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager == null || manager.getElenco().isEmpty()) {
            Output.setText("Errore: nessun dato caricato.");
            return;
        }

        String numeroStr = numero.getText().trim();
        int num;
        try {
            num = Integer.parseInt(numeroStr);
        } catch (NumberFormatException e) {
            Output.setText("Errore: il numero autorizzazione deve essere un intero.");
            return;
        }

        Autorizzazione vecchia = manager.cercaPerNumero(num);
        if (vecchia == null) {
            Output.setText("Nessuna autorizzazione trovata con numero: " + num);
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

        try {
            Autorizzazione aggiornata = new Autorizzazione(num,
                    scadenzaStr.isEmpty() ? vecchia.getScadenzaRaw() : scadenzaStr,
                    richiedenteTx, posizioneTx, tipoTx);
            manager.modifica(aggiornata);

            Output.setText(
                "Modifica effettuata con successo!\n" +
                "─────────────────────────────────────\n" +
                "PRIMA:  " + vecchia.getRichiedente() + " | " + vecchia.getTipoMezzo() +
                            " | " + vecchia.getScadenzaFormattata() + "\n" +
                "DOPO:   " + aggiornata.getRichiedente() + " | " + aggiornata.getTipoMezzo() +
                            " | " + aggiornata.getScadenzaFormattata() + "\n" +
                "─────────────────────────────────────\n" +
                "Ricorda di salvare il CSV dalla Home."
            );

        } catch (IllegalArgumentException | java.util.NoSuchElementException e) {
            Output.setText("Errore: " + e.getMessage());
        }
    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel titolo, lHint, lNumero, lScadenza, lRichiedente, lPosizione, lTipo;
    private javax.swing.JTextField numero, scadenza, richiedente, posizione, tipo;
    private javax.swing.JButton ModificaB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea Output;
}
