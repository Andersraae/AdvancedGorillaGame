package ADVANCEDGORILLA;

public class Guess {
    protected int angle;
    protected double velocity;

    public Guess(int a, double v){
        this.angle = a;
        this.velocity = v;
    }

    public int getAngle(){
        return this.angle;
    }

    public double getVelocity(){
        return this.velocity;
    }

    public String toString(){
        return "a:" + this.angle + " v:"+ this.velocity;
    }

    public void setAngle(int a){
        this.angle = a;
    }

    public void setVelocity(double v){
        this.velocity = v;
    }



}
