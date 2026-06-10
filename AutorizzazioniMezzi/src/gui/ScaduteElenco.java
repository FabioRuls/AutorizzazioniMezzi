package gui;

import model.Autorizzazione;
import model.AutorizzazioniManager;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Finestra che mostra l'elenco delle autorizzazioni con licenza scaduta,
 * ordinate per data di scadenza (più vecchia prima).
 */
public class ScaduteElenco extends javax.swing.JFrame {

    private AutorizzazioniManager manager;

    private static final String[] COLONNE = {
        "Numero", "Scadenza", "Richiedente", "Posizione insegna", "Tipo mezzo", "Giorni scaduta"
    };

    public ScaduteElenco(AutorizzazioniManager manager) {
        this.manager = manager;
        initComponents();
        setLocationRelativeTo(null);
        popolaTabella();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPanel1      = new javax.swing.JPanel();
        titoloLabel  = new javax.swing.JLabel();
        conteggioLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabella      = new javax.swing.JTable();
        esportaBtn   = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Licenze scadute");

        jPanel1.setBackground(new java.awt.Color(188, 210, 243));

        titoloLabel.setFont(new java.awt.Font("Verdana", 1, 16));
        titoloLabel.setForeground(new java.awt.Color(180, 0, 0));
        titoloLabel.setText("Autorizzazioni con licenza scaduta");

        conteggioLabel.setFont(new java.awt.Font("Verdana", 0, 12));
        conteggioLabel.setText("Caricamento...");

        tabella.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(tabella);

        esportaBtn.setBackground(new java.awt.Color(51, 51, 255));
        esportaBtn.setFont(new java.awt.Font("Verdana", 1, 12));
        esportaBtn.setForeground(new java.awt.Color(255, 255, 255));
        esportaBtn.setText("Esporta lista in CSV");
        esportaBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { esportaMouseClicked(evt); }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(titoloLabel)
            .addComponent(conteggioLabel)
            .addComponent(jScrollPane1)
            .addComponent(esportaBtn, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(titoloLabel)
            .addComponent(conteggioLabel)
            .addComponent(jScrollPane1, 400, 500, Short.MAX_VALUE)
            .addComponent(esportaBtn)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, 900, 1000, Short.MAX_VALUE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }
    // </editor-fold>

    private void popolaTabella() {
        ArrayList<Autorizzazione> scadute = manager.getScadute();

        // Ordina per data di scadenza: la più vecchia prima
        scadute.sort((a, b) -> {
            if (a.getScadenza() == null && b.getScadenza() == null) return 0;
            if (a.getScadenza() == null) return 1;
            if (b.getScadenza() == null) return -1;
            return a.getScadenza().compareTo(b.getScadenza());
        });

        javax.swing.table.DefaultTableModel modello = new javax.swing.table.DefaultTableModel(COLONNE, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 5 ? Long.class : String.class;
            }
        };

        LocalDate oggi = LocalDate.now();
        for (Autorizzazione a : scadute) {
            long giorniScaduta = a.getScadenza() != null
                    ? java.time.temporal.ChronoUnit.DAYS.between(a.getScadenza(), oggi)
                    : 0;
            modello.addRow(new Object[]{
                String.valueOf(a.getNumero()),
                a.getScadenzaFormattata(),
                a.getRichiedente(),
                a.getPosizioneInsegna(),
                a.getTipoMezzo(),
                giorniScaduta
            });
        }
        tabella.setModel(modello);

        // Colora le righe in base ai giorni scaduta
        tabella.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel) {
                    long giorni = (Long) t.getModel().getValueAt(
                            t.convertRowIndexToModel(row), 5);
                    if (giorni > 365) {
                        setBackground(new java.awt.Color(255, 180, 180)); // rosso: oltre 1 anno
                    } else if (giorni > 90) {
                        setBackground(new java.awt.Color(255, 220, 150)); // arancione: oltre 3 mesi
                    } else {
                        setBackground(new java.awt.Color(255, 255, 200)); // giallo: meno di 3 mesi
                    }
                } else {
                    setBackground(new java.awt.Color(100, 149, 237));
                }
                return this;
            }
        });

        // Aggiusta larghezza colonne
        tabella.getColumnModel().getColumn(0).setPreferredWidth(60);   // Numero
        tabella.getColumnModel().getColumn(1).setPreferredWidth(90);   // Scadenza
        tabella.getColumnModel().getColumn(2).setPreferredWidth(200);  // Richiedente
        tabella.getColumnModel().getColumn(3).setPreferredWidth(220);  // Posizione
        tabella.getColumnModel().getColumn(4).setPreferredWidth(120);  // Tipo
        tabella.getColumnModel().getColumn(5).setPreferredWidth(100);  // Giorni scaduta

        conteggioLabel.setText("Totale: " + scadute.size() + " autorizzazioni scadute");
        if (scadute.isEmpty()) {
            conteggioLabel.setForeground(new java.awt.Color(0, 150, 0));
            conteggioLabel.setText("Nessuna autorizzazione scaduta ✓");
        } else {
            conteggioLabel.setForeground(new java.awt.Color(180, 0, 0));
        }
    }

    private void esportaMouseClicked(java.awt.event.MouseEvent evt) {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File("scadute.csv"));
        if (fc.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fc.getSelectedFile();
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv")) path += ".csv";

        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(
                new java.io.OutputStreamWriter(new java.io.FileOutputStream(path), "UTF-8"))) {
            bw.write("Numero,Scadenza,Richiedente,Posizione insegna,Tipo mezzo,Giorni scaduta");
            bw.newLine();
            javax.swing.table.TableModel m = tabella.getModel();
            for (int r = 0; r < m.getRowCount(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < m.getColumnCount(); c++) {
                    if (c > 0) sb.append(",");
                    Object v = m.getValueAt(r, c);
                    sb.append(v != null ? v.toString() : "");
                }
                bw.write(sb.toString());
                bw.newLine();
            }
            javax.swing.JOptionPane.showMessageDialog(this, "File esportato con successo:\n" + path);
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Errore nell'esportazione:\n" + e.getMessage(),
                    "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel titoloLabel, conteggioLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabella;
    private javax.swing.JButton esportaBtn;
}
