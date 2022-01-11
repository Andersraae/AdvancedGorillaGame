package ADVANCEDGORILLA;

import java.util.Random;

//Anders
public class Wind {

    public static double winddirection;
    public static double windforce;
    private static Random randi = new Random();

    public static double changeWindForce(){
        return randi.nextInt(5);
    }

    public static double changeWindDirection(){
        return randi.nextDouble(360);
    }
}
