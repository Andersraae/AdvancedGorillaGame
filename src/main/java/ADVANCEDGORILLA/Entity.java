package ADVANCEDGORILLA;

//Andreas
public class Entity {

    private double x;
    private double y;
    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    public String toString(){
        return "x:"+ round(this.x) + " y:" + round(this.y);
    }

    public String round(double a){
        return String.format("%.2f",a);
    }
}

