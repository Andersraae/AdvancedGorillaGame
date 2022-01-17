package ADVANCEDGORILLA;
import java.util.ArrayList;
import java.util.Random;

//******************************************************************
//                         Computerklasse
// Klassen bruges når der spilles mod computeren.
// Computeren genererer først en tilfældig vinkel
// og beregner den hastighed der skal til for at ramme
// med den vinkel.
// Derefter gætter computeren på en vinkel og hastighed
// indtil det korrekte svar er gættet.
// Antallet af gæt føjes til en liste og er dermed
// det totale antal gæt det tager computeren at ramme modspilleren
// Computerens sværhedsgrad påvirker antallet af gæt der
// skal til.
//                      Lavet af Andreas
//******************************************************************

public class Computer{

    //klassevariable
    private Player shooter;
    private Player target;
    private ArrayList<Guess> moves;
    private int currentGuessCounter;
    private int difficulty;
    private static double g = GameController.g;
    private int val;
    public static double dy;
    private int lowAngle;
    private static int highestAngle = 89;

    //konstruktør
    public Computer(Player shooter, Player target){
        this.shooter = shooter;
        this.target = target;
        this.moves = new ArrayList<>();
        this.currentGuessCounter = 0;
        this.difficulty = 0;
        this.lowAngle = 1;

        //setup side
        this.val = 1; //1=venstre, -1=højre
        if (this.shooter.equals(GameController.player2)) {
            this.val = -1;
        }
    }

    //kaldes hver gang spillet startes
    public void setup(int difficulty) throws InterruptedException {

        //fjerner tidligere gæt
        this.moves.clear();

        //beregner alle gæt, hvis computeren er aktiveret
        if(difficulty > 0){
            this.difficulty = difficulty;
            calulateMoves();
        }
    }

    //retunerer det næste gæt i listen
    public Guess getNextMove(){
        Guess move= this.moves.get(this.currentGuessCounter);
         if (this.currentGuessCounter + 1 < this.moves.size()){
            this.currentGuessCounter++;
         } else {
             //tøm tidligere liste og nulstil antallet af gæt og beregn nye gæt
             this.calculateBetterMoves();
         }
         return move;
    }

    //computer har brugt alle sine gæt og laver nye gæt
    //kaldes når computeren rammer en bygning med sit sidste gæt
    public void calculateBetterMoves(){
        this.lowAngle = 45; // højere vinkel gør det mindre sandsynligt at ramme en bygning
        this.resetAndCalculate();
    }

    //tøm tidligere liste og nulstil antallet af gæt og beregn nye gæt
    public void resetAndCalculate(){
        this.moves.clear();
        this.currentGuessCounter = 0;
        this.calulateMoves();
    }

    //beregner alle de gæt der skal til for at ramme modspilleren
    //og føjer dem til listen moves
    public void calulateMoves(){
        //variabler
        Random random = new Random();
        int currentGuess = 1;

        //genererer tilfældigt korrekt svar
        Guess correct;
        int angleInDegrees = random.nextInt(highestAngle - this.lowAngle) / 2 * 2 + this.lowAngle;
        double a = Math.toRadians(angleInDegrees);
        double dx = Math.abs(target.getX() - shooter.getX());
        dy = shooter.getY() - target.getY();
        double udtryk = 2*(dx*Math.pow(Math.sin(a),3)-dy*Math.pow(Math.cos(a),3)-dx*Math.sin(a));
        double v0 = Math.abs((Math.sqrt(-udtryk*Math.cos(a)*g)*dx/udtryk));
        correct = new Guess(angleInDegrees, v0);

        //skaler antallet af muligheder med sværhedsgraden
        int totalAngles = 15 - 2 * this.difficulty;
        int totalVelocity = 15 - 2 * this.difficulty;

        //sæt øvre og nedre grænse for vinkel
        int angleLower, angleUpper;
        double velcocityLower, velocityUpper;
        int tal = random.nextInt(totalAngles);
        angleLower = correct.getAngle() - tal;
        angleUpper = correct.getAngle() + totalAngles - tal;
        if (angleLower < this.lowAngle) {
            angleUpper += Math.abs(angleLower) + 1;
            angleLower = this.lowAngle;
        }

        //sæt øvre og nedre grænse for hastighed
        tal = random.nextInt(totalVelocity);
        velcocityLower = (int) (correct.getVelocity() - tal);
        velocityUpper = (int) (correct.getVelocity() + totalVelocity - tal);
        if (velcocityLower < 1) {
            velocityUpper += Math.abs(velcocityLower) + 1;
            velcocityLower = 1;
        }

        //gæt på en tilfældig hastighed og vinkel
        int guessedAngle = random.nextInt(angleUpper - angleLower) + angleLower;
        double guessedVelocity = random.nextInt((int) (velocityUpper - velcocityLower)) + velcocityLower;
        this.moves.add(new Guess(guessedAngle, guessedVelocity));

        //gætter indtil modspilleren rammes
        while (!playerIsHit(this.moves.get(currentGuess - 1))) {

            //evaluer sidste gæt
            //hvis vinklen var for høj, lowerBound<correct<prevAnswer
            if (correct.getAngle() < guessedAngle) {
                angleUpper = guessedAngle - 1;
            }

            //hvis vinkel var for lav, answer<correct<upperbound
            if (correct.getAngle() > guessedAngle) {
                angleLower = guessedAngle + 1;
            }

            //evaluer sidste gæt
            //hvis hastigheden var for høj, lowerBound<correct<prevAnswer
            if (correct.getVelocity() < guessedVelocity) {
                velocityUpper = guessedVelocity - 1;
            }

            //hvis hastigheden var for lav, answer<correct<upperbound
            if (correct.getVelocity() > guessedVelocity) {
                velcocityLower = guessedVelocity + 1;
            }

            //gæt på den vinkel og hastighed der ligger i midten af den øvre og nedre grænse
            guessedVelocity = velocityUpper - (velocityUpper - velcocityLower) / 2;
            guessedAngle = angleUpper - (angleUpper - angleLower) / 2 * 2;
            this.moves.add(new Guess(guessedAngle, guessedVelocity));

            //tilføj en til antal gæt
            currentGuess++;
        }
    }

    //retunerer true hvis en spiller er ramt
    public boolean playerIsHit(Guess guess){
        Guess gues = new Guess(guess.getAngle()*this.val,guess.getVelocity()*this.val);
        double v0 = gues.velocity;
        double a = Math.toRadians(gues.angle);
        int x = (int) (this.target.getX() - this.shooter.getX());
        int y = (int) (-g/(2*v0*v0*Math.cos(a)*Math.cos(a))*x*x+Math.tan(a)*x);
        double l = Math.sqrt(y*y) - Math.abs(dy);
        return l <= 12;
    }
}