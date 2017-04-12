import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

public class NoktaAgacEkrani extends Ekran {
    private static final int en = 500, boy = 500;

    // Noktaların katmanlarını belirtmek için kullanılan palet
    // nokta herhangi bir alt kümeye ait olup derinleştikçe
    // rengi de o miktarda değişecek
    private static final Color[]       palet        = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA };

    private static       int           noktaYaricap = 10;

    private              PointQuadtree anaAgac      = null;

    // Farenin şimdiki modu, e eklemeyi, a aramayı temsil ediyor
    private char fareModu = 'e';

    // Arama için fare konumları
    private int fareX, fareY;

    // Takip / bulma vs işlemlerinde mouse'nin sahip olacağı yarı çap
    private int mouseCapi = 10;

    private boolean cizgileriGoster      = true;
    private boolean katmanlariRenklendir = true;

    // Arama esnasında mouse tarafından bulunan noktalar listesi, bunu bir
    // fonksiyon doldurmalı, ağaç içerisindeki fonksiyonlara bakın
    private List<Nokta> bulunanNoktalar = null;

    public NoktaAgacEkrani() {
        super("Nokta-Ağaç", en, boy);
        getAnaAgac();
    }

    /**
     * Ekran boyutunda ağaç oluşturur. İlk hal için 500x500 dür ama tekrar yapılandırılarak (s'e basmakla)
     * yeni bir ağaç elde edilebilir.
     *
     * NOT: BU FONKSİYON, AĞAÇ ZATEN VARSA EKLEME YAPMAZ
     *
     * @return Yeni ağaç veya önceki ağaç
     */
    public PointQuadtree getAnaAgac() {
        if (anaAgac == null)
            anaAgac = new PointQuadtree(new Nokta(0, 0), 0, 0, getWidth(), getHeight());
        return anaAgac;
    }

    /**
     * Başlatıcı
     */
    public static void main(String[] args) {
        // SwingUtilities.invokeLater zorunlu değil, direkt olarak new NoktaAgacEkrani()
        // yeterlidir. Bu çağrı, bazı sistemlerde çizim sorunlarını çözmek için kullanıldı
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NoktaAgacEkrani();
            }
        });
    }

    /**
     * Çizdirme fonksiyonu
     * En önemli fonksiyon, ağaç oluşmuş ise ağacı çizdirme fonksiyonu 'agaciCiz' çağırılır.
     * Fare modu a ise (arama), fare içine düşen noktalar bulunursa siyaha boyanır. Detaylar
     * için fonksiyonun içerisine bakın
     */
    @Override
    public void ciz(Graphics g) {
        if (anaAgac != null) agaciCiz(g, anaAgac, 0);

        if (fareModu == 'a') {
            // Farenin çevresini boyayacağımız renk
            g.setColor(Color.BLACK);

            // Farenin kapsadığı alanı boyuyoruz
            g.drawOval(fareX - mouseCapi, fareY - mouseCapi, 2 * mouseCapi, 2 * mouseCapi);

            if (bulunanNoktalar == null) return;


            // Fareye yakın bulunup, 'bulunanNoktalar' listesine eklenen noktaları
            // boyayacağımız renk
            g.setColor(Color.BLACK);

            for (Nokta nokta : bulunanNoktalar) {
                int noktaYaricap = nokta.getSize(), noktaX = nokta.getX(), noktaY = nokta.getY();
                g.fillOval(noktaX - noktaYaricap, noktaY - noktaYaricap, 2 * noktaYaricap, 2 * noktaYaricap);
            }
        }
    }

    /**
     * Yukarıda tanımlanmış bir fonksiyon, tuşa basıldığında çalışır.
     * Eğer e veya a basıldı ise fare modunu değiştir, + veya - ise
     * fare çapını arttır.
     * <p>
     * Ekstra şeyler eklenebilir
     */
    @Override
    public void tusaBasildi(char key) {
        switch (key) {
            case 'e':
            case 'a':
                fareModu = key;
                break;

            case '+':
                mouseCapi += 10;
                if (fareModu == 'a') {
                    bulunanNoktalar = new ArrayList<>();
                    kesisenDaireleriEkle(this.anaAgac, fareX, fareY);
                }

                break;

            case '-':
                mouseCapi -= 10;
                if (mouseCapi < 0) mouseCapi = 0;

                if (fareModu == 'a') {
                    bulunanNoktalar = new ArrayList<>();
                    kesisenDaireleriEkle(this.anaAgac, fareX, fareY);
                }

                break;

            case 'c':
                cizgileriGoster = !cizgileriGoster;
                break;

            case 'r':
                katmanlariRenklendir = !katmanlariRenklendir;
                break;

            case 's':
                noktalariSil();
                break;

            case 'n':
                rastgeleNoktaEkle();
                break;

            case 'b':
                noktaYaricap += 5;
                break;

            case 'k':
                noktaYaricap -= 5;
                if (noktaYaricap < 10) noktaYaricap = 10;
                break;
        }

        repaint();
    }

    /**
     * Ağacı siler ve böylelikle bütün noktalar silinmiş olur.
     * Yeni eklemeye hazır hale getirmek için yeni ağaç alıyoruz.
     */
    private void noktalariSil() {
        anaAgac = null;
        getAnaAgac();
    }

    /**
     * İlk tıklamada buraya, tıklanınca eklenen noktanın kaydını alıyoruz.
     * Böylelikle, ilk noktanın koordinatları tutuluyor ve mouse hareketi olursa,
     * bu yerlere göreceli olarak çap hesaplanıp tekrar boyut veriliyor.
     * İkinci tıklamada, zaten buna referans tutulduğu için, bu nokta üzerindeki
     * oynamayı tamamlıyoruz.
     */
    private Nokta eklenecekNokta;

    /**
     * Fareye basılma fonksiyonu, x ve y de basılmıştır
     * Moda bakacağız ve duruma göre ekleme veya arama yapacağız.
     */
    @Override
    public void mouseBasildi(int x, int y) {
        // Eğer ekleme modundaysak
        if (fareModu == 'e') {
            // ekleyeceğimiz noktayı kayıt etmediysek
            if (eklenecekNokta == null) {
                // onu kayıt ediyoruz
                eklenecekNokta = new Nokta(x, y, noktaYaricap);

                // hemen çizmesi için ağaca ekliyoruz
                anaAgac.agacaEkle(eklenecekNokta);
            }
            else {
                // eğer kayıt ettiysek, bu ikinci tıklamadır.
                // İkinci tıklamaya kadar da boyut ayarlanmıştır.
                // artık ağaca son şekli verilip eklendiğine göre bu noktayı tutmayı
                // bırakabiliriz
                eklenecekNokta = null;
            }
        }

        // Yeniden boya
        repaint();
    }

    /**
     * Alt alta, her bir bölgenin baş elemanı (o bölgeyi oluşturmaya sebep olan nokta) alınıp
     * onun olduğu yerlerden ve onun altlarından ardı ardına kesişim kontrolleri yapılıyor
     * Eğer kesişim olursa, bu kesişimleri bulunanNoktalar listesine ekliyor.
     *
     * Bu liste, bu fonksiyonu çağıranlar tarafından tekrar çizimciye verilip bulunan noktaları
     * siyaha boyatıyor.
     *
     * @param tree Ana ağaç
     * @param x Şimdiki farenin x noktası
     * @param y Şimdiki farenin y noktası
     */
    private void kesisenDaireleriEkle(PointQuadtree tree, int x, int y) {
        // Bu dörtlük başı yok ise geri dön
        if (tree == null) return;

        // Her bir bulunan nokta için
        for (Nokta nokta : tree.butunNoktalar()) {
            // Eğer nokta daire içindeyse (mouse çapındaki)
            if (nokta.daireIcinde(x, y, mouseCapi)) {
                // Bulunan noktalara ekle
                bulunanNoktalar.add(nokta);
            }
        }

        // Diğer dörtlük için aynı işlemi başlat
        kesisenDaireleriEkle(tree.altAgac(1), x, y);
        kesisenDaireleriEkle(tree.altAgac(2), x, y);
        kesisenDaireleriEkle(tree.altAgac(3), x, y);
        kesisenDaireleriEkle(tree.altAgac(4), x, y);
    }

    /**
     * Yukarıdan gelen bir fonksiyon, her fare hareketinde tetiklenecek
     */
    @Override
    public void mouseHareketi(int x, int y) {
        // Arama modundaysak
        if (fareModu == 'a') {
            // Hareket ettiğimiz için, yeni arama başlatmalıyız
            // bulunanNoktaları sil ve x, y noktasını al farenin
            bulunanNoktalar = new ArrayList<>();
            fareX = x;
            fareY = y;

            kesisenDaireleriEkle(this.anaAgac, x, y);
        }
        else if (fareModu == 'e') {
            // Ekleme modundaysak ama eklenecek nokta yoksa geri dön
            if (eklenecekNokta == null) return;

            // Eklenecek noktayı içeren ağacı (bir üstü) al
            PointQuadtree tree = eklenecekNokta.getTree();

            // O ağacın sınırlarını bul ve noktanın x, y sini al
            final int solSinir = tree.getX1(), sagSinir = tree.getX2();
            final int ustSinir = tree.getY1(), altSinir = tree.getY2();
            final int xNokta = eklenecekNokta.getX(), yNokta = eklenecekNokta.getY();

            // Öklid ile uzaklık bul = kök (a2 + b2)
            int yaricap = (int) Math.sqrt(Math.pow(x - xNokta, 2) + Math.pow(y - yNokta, 2));

            // Yarıçap 10'dan küçük olamaz, dön
            if (yaricap < 10) return;

            // Sınırlardan herhangi birini geçiyorsa dön
            if (xNokta - yaricap < solSinir) return;
            if (xNokta + yaricap > sagSinir) return;
            if (yNokta - yaricap < ustSinir) return;
            if (yNokta + yaricap > altSinir) return;

            // Noktaya yeni boyutunu ver
            eklenecekNokta.setSize(yaricap);
        }

        // Yeniden boya
        repaint();
    }

    private void rastgeleNoktaEkle() {
        final Random random = new Random();
        // 30 adet
        for (int i = 0; i < 30; i++) {
            // rastgele x ve y noktaları için
            int x = random.nextInt(getWidth()), y = random.nextInt(getHeight());

            // mouse oraya tıklamış süsü ver
            mouseBasildi(x, y);
        }
    }

    /**
     * Ağacı çizme fonksiyonu
     *
     * @param g        Çizim yapacak eleman
     * @param tree     çizilecek ağaç, alt ağaç olabilir
     * @param derinlik renk seçimi için derinlik seviyesi, 0 ile çağrılır, her bir
     *                 katmanda bir arttırılarak çağrılır.
     */
    public void agaciCiz(Graphics g, PointQuadtree tree, int derinlik) {
        // renk 'palet'inden derinliğe göre renk seçiliyor
        if (katmanlariRenklendir) {
            g.setColor(palet[derinlik % palet.length]);
        }
        else g.setColor(Color.RED);

        for (Nokta nokta : tree.butunNoktalar()) {
            final int noktaYaricap = nokta.getSize(), noktaX = nokta.getX(), noktaY = nokta.getY();

            g.drawOval((noktaX - noktaYaricap), noktaY - noktaYaricap, noktaYaricap * 2, noktaYaricap * 2);
            g.fillOval((noktaX - noktaYaricap), noktaY - noktaYaricap, noktaYaricap * 2, noktaYaricap * 2);
        }

        // Alt ağacı hazırlıyoruz
        PointQuadtree altAgac;

        if (cizgileriGoster) {
            Nokta nokta = tree.getNokta();
            g.setColor(Color.BLUE);
            g.drawLine(tree.getX1(), nokta.getY(), tree.getX2(), nokta.getY());
            g.drawLine(nokta.getX(), tree.getY1(), nokta.getX(), tree.getY2());
        }

        // 4'e kadar
        for (int i = 1; i <= 4; i++) {
            // Her ağacı numara ile alıyoruz ve eğer bu ağaç boş değilse
            if ((altAgac = tree.altAgac(i)) != null) {
                // bu alt ağacı, derinliği 1 arttırarak, bu bütün işlemleri onun üzerinde çağırıyoruz
                agaciCiz(g, altAgac, derinlik + 1);
            }
        }

        // Alt ağaçlar çizildi, noktalar ve doğrular çizildi, grafik hazır
    }
}
