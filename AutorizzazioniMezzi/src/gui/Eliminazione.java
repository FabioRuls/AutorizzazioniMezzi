package gui;

import model.AutorizzazioniManager;

/**
 * Finestra per eliminare una o più autorizzazioni per numero.
 * Mantiene la progress bar animata dell'originale.
 */
public class Eliminazione extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Eliminazione.class.getName());

    private AutorizzazioniManager manager;

    public Eliminazione(AutorizzazioniManager manager) {
        initComponents();
        this.manager = manager;
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel1      = new javax.swing.JPanel();
        OutputLabel  = new javax.swing.JLabel();
        EliminaB     = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1   = new javax.swing.JTextArea();
        ProgressBar  = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Eliminazione autorizzazione");

        jPanel1.setBackground(new java.awt.Color(188, 210, 243));

        OutputLabel.setFont(new java.awt.Font("Verdana", 1, 14));
        OutputLabel.setText("Eliminazione");

        EliminaB.setBackground(new java.awt.Color(51, 51, 255));
        EliminaB.setFont(new java.awt.Font("Verdana", 1, 14));
        EliminaB.setForeground(new java.awt.Color(255, 255, 255));
        EliminaB.setText("Clicca qui per eliminare uno o più elementi");
        EliminaB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { eliminaMouseClicked(evt); }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(OutputLabel)
            .addComponent(EliminaB, javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(jScrollPane1)
            .addComponent(ProgressBar)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(OutputLabel)
            .addGap(20)
            .addComponent(EliminaB)
            .addComponent(jScrollPane1, 150, 173, 173)
            .addComponent(ProgressBar, 19, 19, 19)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void eliminaMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager == null || manager.getElenco().isEmpty()) {
            OutputLabel.setText("Errore: nessun dato caricato.");
            return;
        }

        String quantiStr = javax.swing.JOptionPane.showInputDialog(this,
                "Quante autorizzazioni vuoi eliminare?");
        if (quantiStr == null) return;

        int quanti;
        try {
            quanti = Integer.parseInt(quantiStr.trim());
            if (quanti <= 0) { OutputLabel.setText("Inserisci un numero > 0."); return; }
        } catch (NumberFormatException e) {
            OutputLabel.setText("Errore: numero non valido.");
            return;
        }

        java.util.ArrayList<Integer> numeriDaEliminare = new java.util.ArrayList<>();
        for (int i = 1; i <= quanti; i++) {
            String input = javax.swing.JOptionPane.showInputDialog(this,
                    "Inserisci il numero autorizzazione " + i + " di " + quanti + ":");
            if (input == null) return;
            try {
                int n = Integer.parseInt(input.trim());
                if (manager.cercaPerNumero(n) == null) {
                    OutputLabel.setText("Numero " + n + " non trovato. Operazione annullata.");
                    return;
                }
                numeriDaEliminare.add(n);
            } catch (NumberFormatException e) {
                OutputLabel.setText("\"" + input + "\" non è un numero valido. Operazione annullata.");
                return;
            }
        }

        EliminaB.setEnabled(false);
        OutputLabel.setText("Eliminazione in corso...");
        ProgressBar.setValue(0);

        final int[] step = {0};
        javax.swing.Timer timer = new javax.swing.Timer(50, null);
        timer.addActionListener(e -> {
            step[0]++;
            ProgressBar.setValue(step[0]);
            if (step[0] >= 100) {
                timer.stop();

                java.util.ArrayList<Integer> eliminati   = new java.util.ArrayList<>();
                java.util.ArrayList<Integer> nonTrovati  = new java.util.ArrayList<>();

                for (int n : numeriDaEliminare) {
                    try {
                        manager.elimina(n);
                        eliminati.add(n);
                    } catch (java.util.NoSuchElementException ex) {
                        nonTrovati.add(n);
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append("Operazione completata!\n");
                sb.append("═══════════════════════════════════════\n");
                sb.append("Autorizzazioni eliminate (").append(eliminati.size()).append("):\n");
                for (int n : eliminati) sb.append("  - Numero: ").append(n).append("\n");
                if (!nonTrovati.isEmpty()) {
                    sb.append("───────────────────────────────────────\n");
                    sb.append("Non trovati (").append(nonTrovati.size()).append("):\n");
                    for (int n : nonTrovati) sb.append("  - Numero: ").append(n).append("\n");
                }
                sb.append("═══════════════════════════════════════\n");
                sb.append("Ricorda di salvare il CSV dalla Home.");

                jTextArea1.setText(sb.toString());
                OutputLabel.setText("Eliminazione completata: " + eliminati.size() + " voci rimosse.");
                EliminaB.setEnabled(true);
            }
        });
        timer.start();
    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel OutputLabel;
    private javax.swing.JButton EliminaB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JProgressBar ProgressBar;
}
