import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Ekran extends JFrame {
    private int cizimX, cizimY;

    /**
     * Başlıksız ekran oluşturur
     */
    public Ekran() {
        super("");
    }

    /**
     * Başlıklı ekran
     * @param title başlık
     */
    public Ekran(String title) {
        super(title);
    }

    /**
     * Boyutları ile başlıklı ekran
     *
     * @param title		Başlık
     * @param genislik		Genişlik
     * @param yukseklik	Yükseklik
     */
    public Ekran(String title, int genislik, int yukseklik) {
        super(title);
        hazirla(genislik, yukseklik);
    }

    /**
     * Ekrani hazirlama fonksiyonu
     *
     * @param width		Genislik
     * @param height	Yukseklik
     */
    private void hazirla(int width, int height) {
        JComponent cizim = new JComponent() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                ciz(g);
            }
        };

        cizim.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                int x = event.getPoint().x, y = event.getPoint().y;
                cizimY = y;
                cizimX = x;

                mouseBasildi(cizimX, cizimY);
            }
        });

        cizim.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent event) {
                mouseHareketi(event.getPoint().x, event.getPoint().y);
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent event) {
                tusaBasildi(event.getKeyChar());
            }
        });

        setSize(width, height);
        cizim.setPreferredSize(new Dimension(width, height));
        getContentPane().add(cizim);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Alt siniflar tarafindan override edilecek
     */
    public void ciz(Graphics g) {}
    public void mouseBasildi(int x, int y) {}
    public void mouseHareketi(int x, int y) {}
    public void tusaBasildi(char key) {}
}