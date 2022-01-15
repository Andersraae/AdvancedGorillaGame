package ADVANCEDGORILLA;

//******************************************************
// Guess
// Klassen bruges til at opbevare gæt i et enkelt objekt
// Et gæt består af en vinkel og hastighed
//
// Lavet af Andreas
//******************************************************

public class Guess {
    protected int angle;
    protected double velocity;

    //konstruktør
    public Guess(int a, double v){
        this.angle = a;
        this.velocity = v;
    }

    //retunerer gættets vinkel
    public int getAngle(){
        return this.angle;
    }

    //retunerer gættes hastighed
    public double getVelocity(){
        return this.velocity;
    }

    //toString metode, eg: "a:45 v:70"
    public String toString(){
        return "a:" + this.angle + " v:"+ this.velocity;
    }
}
