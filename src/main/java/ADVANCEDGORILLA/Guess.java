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
}
