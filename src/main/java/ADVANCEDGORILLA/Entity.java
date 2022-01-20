package ADVANCEDGORILLA;


//***********************************************************
// Entity
// En overordnet klasse for de to statiske spillere
// og projektilen (banan)
// En entity har et punkt (x,y), der kan sættes og retuneres
//
// Lavet af Andreas
//***********************************************************
public class Entity {

    private double x; // entity x-koordinat
    private double y; // entity y-koordinat
    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //retunerer x-koordinatet for entity
    public double getX() {
        return this.x;
    }

    //retunerer y-koordinatet for entity
    public double getY() {
        return this.y;
    }

    //sætter x-koordinaten for entity
    public void setX(double x) {
        this.x = x;
    }

    //sætter y-koordinaten for entity
    public void setY(double y) {
        this.y = y;
    }

    //tager en double og retunerer strengen afrundet til 2 decimaler
    public String round(double a){
        return String.format("%.2f",a);
    }

    //tostring
    public String toString(){
        return "x:"+ round(this.x) + " y:" + round(this.y);
    }
}

