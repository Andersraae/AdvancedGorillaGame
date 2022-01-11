package ADVANCEDGORILLA;

public class Guess {
   protected int angle;
   protected double velocity;
   protected double length;

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

    public void setLength(double n){
       this.length = n;
    }

    public double getLength() {
       return this.length;
    }
}
