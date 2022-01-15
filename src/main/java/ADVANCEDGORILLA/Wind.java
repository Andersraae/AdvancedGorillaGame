package ADVANCEDGORILLA;

import java.util.Random;

//Anders
/*
    vindens styrke ift sværhedsgrad
    1 -> 10-20
    2 -> 20-40
    3 -> 30-60
    4 -> 40-80
    5 -> 50-100
*/
public class Wind {

    private static Random randi = new Random();

    public static double changeWindForce(){
        int difficulty = StartController.windDifficulty;
        int num = randi.nextInt(20 * difficulty - 10 * difficulty + 1) + 10 * difficulty;
        return num;
    }

    public static double changeWindDirection(){
        return randi.nextDouble(360);
    }
}
