import java.util.ArrayList;
import java.util.List;

/**
 * PointQuadTree ağacı, içerisinde
 * AĞACIN
 * dikdörtgensel olarak x1, x2, y1, y2 noktaları
 * bölgesel olarak yine alt ağaçarın alınabileceği
 * ağaca nokta ekleme
 * nokta sayısını veren
 * bütün noktaları liste olarak veren
 * seçilen bir alanda kapsanan noktaları bulan fonksiyonlar bulunmakta
 */
public class PointQuadtree {
    private int x1, y1, x2, y2;
    private int bolgeNo;

    private ArrayList<Nokta> noktalar;
    private PointQuadtree    birinciAlt, ikinciAlt, ucuncuAlt, dorduncuAlt, parent; // Alt ağaçlar
    private Nokta nokta; // Bu ağacı tutan nokta bilgisi

    public PointQuadtree(Nokta nokta, int x1, int y1, int x2, int y2) {
        this.nokta = nokta;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        noktalar = new ArrayList<>();
    }

    public int getBolgeNo() {
        return bolgeNo;
    }

    public void setBolgeNo(int bolgeNo) {
        this.bolgeNo = bolgeNo;
    }

    public void setBirinciAlt(PointQuadtree birinciAlt) {
        birinciAlt.setParent(this);
        birinciAlt.setBolgeNo(1);
        this.birinciAlt = birinciAlt;
    }

    public void setIkinciAlt(PointQuadtree ikinciAlt) {
        ikinciAlt.setParent(this);
        ikinciAlt.setBolgeNo(2);
        this.ikinciAlt = ikinciAlt;
    }

    public void setUcuncuAlt(PointQuadtree ucuncuAlt) {
        ucuncuAlt.setParent(this);
        ucuncuAlt.setBolgeNo(3);
        this.ucuncuAlt = ucuncuAlt;
    }

    public void setDorduncuAlt(PointQuadtree dorduncuAlt) {
        dorduncuAlt.setParent(this);
        dorduncuAlt.setBolgeNo(4);
        this.dorduncuAlt = dorduncuAlt;
    }

    public PointQuadtree getParent() {
        return parent;
    }

    public void setParent(PointQuadtree parent) {
        this.parent = parent;
    }

    public Nokta getNokta() {
        return nokta;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    /**
     * 1 - 4 arası dörtlüklerden (ağaçlardan) birini verir
     */
    public PointQuadtree altAgac(int dortluk) {
        switch (dortluk) {
            case 1:
                return birinciAlt;
            case 2:
                return ikinciAlt;
            case 3:
                return ucuncuAlt;
            case 4:
                return dorduncuAlt;
            default:
                return null;
        }
    }

    private boolean arasinda(int sayi, int baslangic, int son) {
        return sayi < son && sayi > baslangic;
    }

    private boolean bolgede(int x, int y, PointQuadtree bolge) {
        return arasinda(x, bolge.x1, bolge.x2) && arasinda(y, bolge.y1, bolge.y2);
    }

    public PointQuadtree altAgacBul(int x, int y) {
        if (bolgede(x, y, birinciAlt)) return birinciAlt;
        if (bolgede(x, y, ikinciAlt)) return ikinciAlt;
        if (bolgede(x, y, ucuncuAlt)) return ucuncuAlt;
        return dorduncuAlt;
    }

    public void agacaEkle(Nokta p2) {
        PointQuadtree quadtree;
        final int     noktaX = p2.getX(), noktaY = p2.getY();

        if (birinciAlt == null) {
            setBirinciAlt(new PointQuadtree(p2, noktaX, y1, x2, noktaY));
            setIkinciAlt(new PointQuadtree(p2, x1, y1, noktaX, noktaY));
            setUcuncuAlt(new PointQuadtree(p2, x1, noktaY, noktaX, y2));
            setDorduncuAlt(new PointQuadtree(p2, noktaX, noktaY, x2, y2));
            noktalar.add(p2);
            p2.setTree(this);
        }
        else {
            quadtree = altAgacBul(noktaX, noktaY);
            quadtree.agacaEkle(p2);
        }
    }

    public List<Nokta> butunNoktalar() {
        return noktalar;
    }

    @Override
    public String toString() {
        return String.format("[%d..%d x %d..%d]", x1, x2, y1, y2);
    }
}
