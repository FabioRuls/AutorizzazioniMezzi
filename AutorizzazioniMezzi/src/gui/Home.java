package gui;

import exceptions.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.AutorizzazioniManager;

/**
 * Finestra principale dell'applicazione.
 * Gestisce caricamento/salvataggio CSV e mostra le 4 dashboard:
 *   1. Totale autorizzazioni caricate
 *   2. Autorizzazioni attive (non scadute)
 *   3. Autorizzazioni scadute
 *   4. Tipo di mezzo più frequente
 */
public class Home extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Home.class.getName());

    public AutorizzazioniManager manager = new AutorizzazioniManager();
    TabellaVoci tabella;
    File file;

    public Home() {
        initComponents();
        setResizable(false);
    }

    /** Aggiorna le 4 card della dashboard con i dati attuali del manager. */
    public void aggiornaDashboard() {
        totaleLabel.setText(String.valueOf(manager.getTotale()));
        attiveLabel.setText(String.valueOf(manager.getAttive().size()));
        scaduteLabel.setText(String.valueOf(manager.getScadute().size()));
        tipoMaxLabel.setText(manager.getTipoMezzoMaxFrequenza());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sfondoHome         = new javax.swing.JPanel();
        navBar             = new javax.swing.JPanel();
        nomeApp            = new java.awt.Label();
        caricaCSV          = new javax.swing.JButton();
        salva              = new javax.swing.JButton();
        salvaConNome       = new javax.swing.JButton();
        infoBtn            = new javax.swing.JButton();

        // 4 dashboard card panels
        card1              = new javax.swing.JPanel();
        colore1            = new javax.swing.JPanel();
        card1TitoloL       = new java.awt.Label();
        totaleLabel        = new javax.swing.JLabel();

        card2              = new javax.swing.JPanel();
        colore2            = new javax.swing.JPanel();
        card2TitoloL       = new java.awt.Label();
        attiveLabel        = new javax.swing.JLabel();

        card3              = new javax.swing.JPanel();
        colore3            = new javax.swing.JPanel();
        card3TitoloL       = new java.awt.Label();
        scaduteLabel       = new javax.swing.JLabel();

        card4              = new javax.swing.JPanel();
        colore4            = new javax.swing.JPanel();
        card4TitoloL       = new java.awt.Label();
        tipoMaxLabel       = new javax.swing.JLabel();

        // 4 chart placeholder panels
        grafico1           = new javax.swing.JPanel();
        grafico2           = new javax.swing.JPanel();
        grafico3           = new javax.swing.JPanel();
        grafico4           = new javax.swing.JPanel();

        // Bottom action buttons
        RicercaVoci        = new javax.swing.JButton();
        ModificaVoci       = new javax.swing.JButton();
        InserisciVoci      = new javax.swing.JButton();
        EliminaVoci        = new javax.swing.JButton();
        ScaduteVoci        = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // ── sfondo ──────────────────────────────────────────────────────
        sfondoHome.setBackground(new java.awt.Color(188, 210, 243));
        sfondoHome.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 153), 1, true));

        // ── navbar ──────────────────────────────────────────────────────
        navBar.setBackground(new java.awt.Color(51, 51, 255));
        nomeApp.setFont(new java.awt.Font("Verdana", 1, 32));
        nomeApp.setForeground(new java.awt.Color(255, 255, 255));
        nomeApp.setText("AutorizzaMezzi");

        configBtn(caricaCSV, "Carica CSV");
        configBtn(salva, "Salva");
        configBtn(salvaConNome, "Salva con nome");
        configBtn(infoBtn, "Info");

        caricaCSV.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { caricaCSVMouseClicked(evt); }
        });
        salva.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { salvaMouseClicked(evt); }
        });
        salvaConNome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { salvaConNomeMouseClicked(evt); }
        });
        infoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { new Info().setVisible(true); }
        });

        javax.swing.GroupLayout navBarLayout = new javax.swing.GroupLayout(navBar);
        navBar.setLayout(navBarLayout);
        navBarLayout.setHorizontalGroup(
            navBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navBarLayout.createSequentialGroup()
                .addGap(53).addComponent(nomeApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, Short.MAX_VALUE, Short.MAX_VALUE)
                .addComponent(infoBtn, 83, 83, 83)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(caricaCSV, 117, 117, 117)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salva, 83, 83, 83)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salvaConNome, 182, 182, 182))
        );
        navBarLayout.setVerticalGroup(
            navBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(navBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nomeApp)
                    .addComponent(infoBtn, 29, 29, 29)
                    .addComponent(caricaCSV, 29, 29, 29)
                    .addComponent(salva, 29, 29, 29)
                    .addComponent(salvaConNome, 29, 29, 29))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        // ── card 1 — Totale ──────────────────────────────────────────────
        buildCard(card1, colore1, new java.awt.Color(255, 153, 51),
                  card1TitoloL, "Totale autorizzazioni:",
                  totaleLabel, "—");

        // ── card 2 — Attive ──────────────────────────────────────────────
        buildCard(card2, colore2, new java.awt.Color(0, 204, 0),
                  card2TitoloL, "Autorizzazioni attive:",
                  attiveLabel, "—");

        // ── card 3 — Scadute ─────────────────────────────────────────────
        buildCard(card3, colore3, new java.awt.Color(255, 51, 204),
                  card3TitoloL, "Autorizzazioni scadute:",
                  scaduteLabel, "—");

        // ── card 4 — Tipo mezzo max ───────────────────────────────────────
        buildCard(card4, colore4, new java.awt.Color(255, 255, 0),
                  card4TitoloL, "Tipo mezzo più frequente:",
                  tipoMaxLabel, "—");

        // ── grafici placeholder ───────────────────────────────────────────
        for (javax.swing.JPanel g : new javax.swing.JPanel[]{grafico1, grafico2, grafico3, grafico4}) {
            g.setBackground(new java.awt.Color(255, 255, 255));
            g.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(53, 95, 232), 2, true));
            javax.swing.GroupLayout gl = new javax.swing.GroupLayout(g);
            g.setLayout(gl);
            gl.setHorizontalGroup(gl.createParallelGroup().addGap(0, 0, Short.MAX_VALUE));
            gl.setVerticalGroup(gl.createParallelGroup().addGap(0, 336, Short.MAX_VALUE));
        }

        // ── pulsanti azione ───────────────────────────────────────────────
        configBtn(RicercaVoci,   "Ricerca");
        configBtn(ModificaVoci,  "Modifica");
        configBtn(InserisciVoci, "Inserisci");
        configBtn(EliminaVoci,   "Eliminazione");
        configBtn(ScaduteVoci,   "Licenze scadute");
        ScaduteVoci.setBackground(new java.awt.Color(200, 0, 0));

        RicercaVoci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { RicercaVociMouseClicked(evt); }
        });
        ModificaVoci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { ModificaVociMouseClicked(evt); }
        });
        InserisciVoci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { InserisciVociMouseClicked(evt); }
        });
        EliminaVoci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { EliminaVociMouseClicked(evt); }
        });
        ScaduteVoci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { ScaduteVociMouseClicked(evt); }
        });

        // ── layout principale ─────────────────────────────────────────────
        javax.swing.GroupLayout sfondoLayout = new javax.swing.GroupLayout(sfondoHome);
        sfondoHome.setLayout(sfondoLayout);
        sfondoLayout.setHorizontalGroup(
            sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(sfondoLayout.createSequentialGroup()
                .addGap(52)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grafico1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grafico2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grafico3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(card4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grafico4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(77, Short.MAX_VALUE))
            .addGroup(sfondoLayout.createSequentialGroup()
                .addGap(30)
                .addComponent(RicercaVoci, 160, 160, 160)
                .addGap(18)
                .addComponent(ModificaVoci, 160, 160, 160)
                .addGap(18)
                .addComponent(InserisciVoci, 160, 160, 160)
                .addGap(18)
                .addComponent(EliminaVoci, 160, 160, 160)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ScaduteVoci, 180, 180, 180)
                .addGap(30))
        );
        sfondoLayout.setVerticalGroup(
            sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sfondoLayout.createSequentialGroup()
                .addComponent(navBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grafico1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grafico2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grafico3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grafico4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(sfondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RicercaVoci, 40, 40, 40)
                    .addComponent(ModificaVoci, 40, 40, 40)
                    .addComponent(InserisciVoci, 40, 40, 40)
                    .addComponent(EliminaVoci, 40, 40, 40)
                    .addComponent(ScaduteVoci, 40, 40, 40))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(sfondoHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(sfondoHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ── helper per la costruzione dei componenti ──────────────────────────

    private void configBtn(javax.swing.JButton btn, String testo) {
        btn.setBackground(new java.awt.Color(51, 51, 255));
        btn.setFont(new java.awt.Font("Verdana", 1, 18));
        btn.setForeground(new java.awt.Color(255, 255, 255));
        btn.setText(testo);
        btn.setBorder(null);
    }

    private void buildCard(javax.swing.JPanel card, javax.swing.JPanel colore,
                           java.awt.Color coloreBar,
                           java.awt.Label titoloLabel, String titoloTesto,
                           javax.swing.JLabel valoreLabel, String valoreDefault) {
        card.setBackground(new java.awt.Color(53, 95, 232));
        card.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        colore.setBackground(coloreBar);
        javax.swing.GroupLayout cl = new javax.swing.GroupLayout(colore);
        colore.setLayout(cl);
        cl.setHorizontalGroup(cl.createParallelGroup().addGap(0, 11, Short.MAX_VALUE));
        cl.setVerticalGroup(cl.createParallelGroup().addGap(0, 100, Short.MAX_VALUE));

        titoloLabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16));
        titoloLabel.setForeground(new java.awt.Color(255, 255, 255));
        titoloLabel.setText(titoloTesto);

        valoreLabel.setFont(new java.awt.Font("Verdana", 1, 24));
        valoreLabel.setForeground(new java.awt.Color(255, 255, 255));
        valoreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valoreLabel.setText(valoreDefault);

        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(card);
        card.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardLayout.createSequentialGroup()
                .addComponent(colore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titoloLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cardLayout.createSequentialGroup()
                        .addGap(20)
                        .addComponent(valoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 40, Short.MAX_VALUE))
        );
        cardLayout.setVerticalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardLayout.createSequentialGroup()
                        .addComponent(titoloLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valoreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(colore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }

    // ── event handlers ────────────────────────────────────────────────────

    private void caricaCSVMouseClicked(java.awt.event.MouseEvent evt) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("File CSV (*.csv)", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            try {
                manager.caricaCSV(file);
                aggiornaDashboard();
                tabella = new TabellaVoci(manager);
            } catch (IOException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Errore nella lettura del file:\n" + e.getMessage(),
                        "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvaMouseClicked(java.awt.event.MouseEvent evt) {
        if (file == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nessun file aperto. Usa 'Salva con nome'.");
            return;
        }
        try {
            manager.salvaCSV(file);
            javax.swing.JOptionPane.showMessageDialog(this, "File salvato con successo.");
        } catch (IOException | FileException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Errore nel salvataggio:\n" + e.getMessage(),
                    "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvaConNomeMouseClicked(java.awt.event.MouseEvent evt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            try {
                manager.salvaCSV(path);
                javax.swing.JOptionPane.showMessageDialog(this, "File salvato con successo.");
            } catch (IOException | FileException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Errore nel salvataggio:\n" + e.getMessage(),
                        "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void RicercaVociMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager.getElenco().isEmpty()) { avvisa(); return; }
        new Ricerca(manager).setVisible(true);
    }

    private void ModificaVociMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager.getElenco().isEmpty()) { avvisa(); return; }
        new Modifica(manager).setVisible(true);
    }

    private void InserisciVociMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager.getElenco().isEmpty()) { avvisa(); return; }
        new Inserimento(manager).setVisible(true);
    }

    private void EliminaVociMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager.getElenco().isEmpty()) { avvisa(); return; }
        new Eliminazione(manager).setVisible(true);
    }

    private void ScaduteVociMouseClicked(java.awt.event.MouseEvent evt) {
        if (manager.getElenco().isEmpty()) { avvisa(); return; }
        new ScaduteElenco(manager).setVisible(true);
    }

    private void avvisa() {
        javax.swing.JOptionPane.showMessageDialog(this, "Carica prima il CSV.");
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Home().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton EliminaVoci, InserisciVoci, ModificaVoci, RicercaVoci, ScaduteVoci;
    private javax.swing.JButton caricaCSV, salva, salvaConNome, infoBtn;
    private javax.swing.JPanel sfondoHome, navBar;
    private java.awt.Label nomeApp;
    private javax.swing.JPanel card1, card2, card3, card4;
    private javax.swing.JPanel colore1, colore2, colore3, colore4;
    private java.awt.Label card1TitoloL, card2TitoloL, card3TitoloL, card4TitoloL;
    private javax.swing.JLabel totaleLabel, attiveLabel, scaduteLabel, tipoMaxLabel;
    private javax.swing.JPanel grafico1, grafico2, grafico3, grafico4;
}
