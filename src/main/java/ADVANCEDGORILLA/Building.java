package ADVANCEDGORILLA;

public class Building {

    private double x;
    private double width;
    private double height;

    public Building(double x, double width, double height) {
        this.x = x;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // Check kollision mellem bygning og projektil
    public boolean collision(Projectile other) {
        if (other.getX() > this.x && other.getX() < (this.x + this.width) && other.getY() < this.height) {
            return true;
        }
        return false;
    }
}
