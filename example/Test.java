package example;

public class Test implements Cloneable {
    public int x = 1, y = 2;

    public Test(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Test clone() throws CloneNotSupportedException {
        Test b = (Test) super.clone();
        return b;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
}
