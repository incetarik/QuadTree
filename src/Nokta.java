public class Nokta {
    private int x, y;
    private int           size;
    private PointQuadtree tree;

    public Nokta(int x, int y, int size) {
        this(x, y);
        setSize(size);
    }

    public Nokta(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PointQuadtree getTree() {
        return tree;
    }

    public void setTree(PointQuadtree tree) {
        this.tree = tree;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean dikdortgeniKesiyor(PointQuadtree tree) {
        return dikdortgeniKesiyor(tree.getX1(), tree.getY1(), tree.getX2(), tree.getY2());
    }

    public boolean dikdortgeniKesiyor(int x1, int y1, int x2, int y2) {
        double closestX = Math.min(Math.max(x, x1), x2);
        double closestY = Math.min(Math.max(y, y1), y2);
        return (x - closestX) * (x - closestX) + (y - closestY) * (y - closestY) <= size * size;
    }

    public boolean daireIcinde(Nokta nokta) {
        return daireIcinde(nokta.x, nokta.y, nokta.size);
    }

    public boolean daireIcinde(int daireMerkezX, int daireMerkezY, int daireYaricap) {
        return (x - daireMerkezX) * (x - daireMerkezX) + (y - daireMerkezY) * (y - daireMerkezY) <= daireYaricap * daireYaricap;
    }
}
