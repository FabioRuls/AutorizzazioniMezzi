package gui;

import javax.swing.Timer;

/**
 * Finestra informativa sull'applicazione.
 * Mantiene l'animazione typewriter dell'originale.
 */
public class Info extends javax.swing.JFrame {

    private String testoCompleto;
    private int indice = 0;

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Info.class.getName());

    public Info() {
        initComponents();
        setLocationRelativeTo(null);
        jTextArea1.setEditable(false);

        testoCompleto =
            "AutorizzaMezzi è un'applicazione per la gestione delle " +
            "autorizzazioni per mezzi pubblicitari.\n\n" +
            "Permette di caricare file CSV, visualizzare le autorizzazioni, " +
            "effettuare ricerche per richiedente, tipo di mezzo e stato, " +
            "e gestire inserimenti, modifiche ed eliminazioni.\n\n" +
            "Gestisce automaticamente le anomalie presenti nel file:\n" +
            "date malformate, stati speciali (ANNULLATA, NEGATA, SOSPESA),\n" +
            "anni abbreviati e righe vuote.\n\n" +
            "Adattato da BudgetSight.";

        avviaAnimazione();
    }

    private void avviaAnimazione() {
        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            if (indice < testoCompleto.length()) {
                jTextArea1.append(String.valueOf(testoCompleto.charAt(indice)));
                indice++;
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel1      = new javax.swing.JPanel();
        jPanel2      = new javax.swing.JPanel();
        nomeApp      = new java.awt.Label();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1   = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Info");

        jPanel1.setBackground(new java.awt.Color(188, 210, 243));
        jPanel2.setBackground(new java.awt.Color(51, 51, 255));

        nomeApp.setFont(new java.awt.Font("Verdana", 1, 32));
        nomeApp.setForeground(new java.awt.Color(255, 255, 255));
        nomeApp.setText("AutorizzaMezzi");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nomeApp)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(nomeApp)
            .addContainerGap(20, Short.MAX_VALUE)
        );

        jTextArea1.setBackground(new java.awt.Color(188, 210, 243));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Verdana", 0, 14));
        jTextArea1.setRows(5);
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1).addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
            .addContainerGap()
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    // Variables declaration
    private javax.swing.JPanel jPanel1, jPanel2;
    private java.awt.Label nomeApp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
}
