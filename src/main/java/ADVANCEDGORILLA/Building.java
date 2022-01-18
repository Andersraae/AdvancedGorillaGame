package ADVANCEDGORILLA;
//******************************************************
// Building
// Klasse repræsenterer en af bygningerne i spillet.
// Bruges hovedsageligt til at tjekke hvornår en banan
// rammer en bygning.
// Lavet af Markus
//******************************************************
public class Building {

    private double x;
    private double width;
    private double height;

    // Konstruktør
    public Building(double x, double width, double height) {
        this.x = x;
        this.width = width;
        this.height = height;
    }

    // Returnerer bygningens x-koordinat, som er x-værdien for bygningens venstre side
    public double getX() {
        return x;
    }

    // Returnerer bygningens højde
    public double getHeight() {
        return height;
    }

    // Returnerer bygningens bredde
    public double getWidth() {
        return width;
    }

    // Checker om projektils x- og y-koordinat er inden for bygningen
    public boolean collision(Projectile other) {
        if (other.getX() > this.x && other.getX() < (this.x + this.width) && other.getY() < this.height) {
            return true;
        }
        return false;
    }
}
