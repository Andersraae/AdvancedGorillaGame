package ADVANCEDGORILLA;

import javafx.animation.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import java.net.URL;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    //Variable der ikke skal ændres
    public static int CANVAS_X,CANVAS_Y;
    public static Projectile proj;
    public static Player winner;
    public static boolean winnerFound,player1HasTurn;

    //Variable fra startScreen
    public static int FirstTo;
    public static Player player1, player2;
    public static double g;
    public static boolean manuelKast;

    //AI
    public Computer computer1, computer2;

    //variable fra game-view ift point og navne
    public Label namePlayer1, namePlayer2,player1point, player2point;

    //Visuelt/Animation
    public ImageView abe1, abe2, BA;
    public Timeline gorillathrow = new Timeline();
    public RotateTransition rotationBanan = new RotateTransition();
    public Image kast = new Image(String.valueOf(GameApplication.class.getResource("Kast.png")));
    public Image normal = new Image(String.valueOf(GameApplication.class.getResource("gorilla.png")));
    public Image banan = new Image(String.valueOf(GameApplication.class.getResource("BA - Kopi.PNG")));

    //Manuel kast
    public TextField angle, velocity;
    public Label anglelabel, velocitylabel;
    public Button throwbtn;

    //Visuel kast
    public Line indicatorp1, indicatorp2; //Hver spiller har en indikator, da farve gradient ikke kunne passes til ordentligt
    public Label visualangle, visualvelocity; //Tekst til at vise vinkel og hastighed i spil-vinduet
    public double xdiff,ydiff,throwvelocity,throwangledeg,displayangle;
    public boolean hasthrown = false;

    //Vind
    public static double winddirection, windforce;
    public Label visualwinddir,visualwindforce;

    //Terræn
    public AnchorPane anchorPane;
    public int blockHeight = 16;
    public int blockWidth = 72;
    public int maxHeight;
    public int minHeight;
    public Color[] buildingColors = {Color.DARKTURQUOISE, Color.INDIANRED, Color.LIGHTGREY};
    public static Building[] buildings;
    public ArrayList<Object> blokke = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CANVAS_X = StartController.sizeX;
        CANVAS_Y = StartController.sizeY;
        maxHeight = CANVAS_Y/50;
        minHeight = CANVAS_Y/200;
        int num = CANVAS_X / 2 - 20;

        //setup labels placeringer
        namePlayer2.setLayoutX(CANVAS_X-50);
        player2point.setLayoutX(CANVAS_X-20);
        visualwinddir.setLayoutX(num);
        visualwindforce.setLayoutX(num);
        visualangle.setLayoutX(num);
        visualvelocity.setLayoutX(num);
        angle.setLayoutX(num);
        anglelabel.setLayoutX(angle.getLayoutX()-40);
        velocity.setLayoutX(num);
        velocitylabel.setLayoutX(velocity.getLayoutX()-60);
        throwbtn.setLayoutX(angle.getLayoutX()+175);

        //setup tur
        player1HasTurn = true;
        proj = new Projectile(0,0);
        winner = new Player(-1,-1,"null");
        winnerFound = false;
        FirstTo = StartController.PlayingTo;
        player1 = new Player(0, 0, StartController.namePlayer1);
        player2 = new Player(CANVAS_X - 1, 0, StartController.namePlayer2);
        g = StartController.gravity;

        //navne
        namePlayer1.setText(player1.getName());
        namePlayer2.setText(player2.getName());

        //manuel kast
        manuelKast = StartController.manuelKast;

        //tyngdekraft
         g = StartController.gravity;

        //vind og terræn
        updateWind();
        generateTerrain();

        resetImage();
        abe1.setImage(normal);
        abe2.setImage(normal);
        BA.setImage(banan);

        // Andreas
        // setup computer
        computer1 = new Computer(player1,player2);
        computer2 = new Computer(player2,player1);
        player1.setComputer(StartController.player1AI > 0); // 0 = computer ikke aktiveret
        player2.setComputer(StartController.player2AI > 0);
        try {
            computer1.setup(StartController.player1AI); //hvis computer er slået fra, gør de to kald ikke noget
            computer2.setup(StartController.player2AI);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (manuelKast){
            throwbtn.setOpacity(1);
            anglelabel.setOpacity(1);
            velocitylabel.setOpacity(1);
            angle.setOpacity(1);
            velocity.setOpacity(1);
        } else {
            visualvelocity.setOpacity(1);
            visualangle.setOpacity(1);
        }

        //Christian
        //Rotation af banan
        rotationBanan.setCycleCount(rotationBanan.INDEFINITE); //Fortsæt rotation
        rotationBanan.setByAngle(360);
        rotationBanan.setInterpolator(Interpolator.LINEAR); //Konstant rotation
        rotationBanan.setAutoReverse(false);
        rotationBanan.setNode(BA);
    }

    //Anders
    //Opdaterer vinden
    public void updateWind(){
        windforce = Wind.changeWindForce();
        winddirection = Wind.changeWindDirection();
        visualwinddir.setText("Vindretning i grader: " + (int) winddirection);
        visualwindforce.setText("Vindstyrke: " + (int) windforce);
    }

    // Markus
    // Generer bygninger
    public void generateTerrain() {
        Random r = new Random();
        int arrLength = CANVAS_X / blockWidth;
        if (CANVAS_X % blockWidth != 0) arrLength++;
        buildings = new Building[arrLength];

        // Tegner alle bygningerne, en ad gangen
        for (int i = 0; (i * blockWidth) < CANVAS_X; i++) {
            int height = r.nextInt(maxHeight - minHeight) + minHeight;
            int color = r.nextInt(buildingColors.length);

            // Tegner en bygning ved at tegne flere rektangler ovenpå hinanden
            for (int j = 0; j <= height; j++) {
                Rectangle block = new Rectangle();
                block.setLayoutX(i * blockWidth);
                block.setLayoutY(CANVAS_Y - j * blockHeight);
                block.setWidth(blockWidth);
                block.setHeight(blockHeight);
                block.setFill(buildingColors[color]);
                anchorPane.getChildren().add(block);
                blokke.add(block);

                // Tegner vinduer på den nuværende blok
                for (int k = 0; k < 6; k++) {
                    Rectangle window = new Rectangle();
                    window.setLayoutX(i * blockWidth + (blockWidth / 6) * k + (blockWidth / 6) / 3);
                    window.setLayoutY(CANVAS_Y - j * blockHeight + (blockHeight / 4));
                    window.setWidth(blockWidth / 6 / 3);
                    window.setHeight(blockHeight / 2);
                    if (r.nextInt(2) == 1) {
                        window.setFill(Color.BEIGE);
                    } else {
                        window.setFill(Color.SLATEGRAY);
                    }
                    anchorPane.getChildren().add(window);
                    blokke.add(window);
                }
            }
            // Opret array med bygninger
            Building building = new Building(i * blockWidth, blockWidth, height * blockHeight);
            buildings[i] = building;
        }
        // Sætter positioner af spiller sprites
        int p1 = r.nextInt(buildings.length/2);
        int p2 = r.nextInt(buildings.length - 1 - buildings.length/2 ) + buildings.length/2;

        abe1.setLayoutX(buildings[p1].getX() + buildings[p1].getWidth() / 2 - abe1.getFitWidth() / 2);
        abe1.setLayoutY(CANVAS_Y - (buildings[p1].getHeight() + abe1.getFitHeight()));
        abe2.setLayoutX(buildings[p2].getX() + buildings[p2].getWidth() / 2 - abe2.getFitWidth() / 2);
        abe2.setLayoutY(CANVAS_Y - (buildings[p2].getHeight() + abe2.getFitHeight()));
        // Sætter positioner af spillere
        player1.setX(abe1.getLayoutX() + abe1.getFitWidth() / 2);
        player1.setY(buildings[p1].getHeight() + abe1.getFitHeight() / 2);
        player2.setX(abe2.getLayoutX() + abe2.getFitWidth() / 2);
        player2.setY(buildings[p2].getHeight() + abe2.getFitHeight() / 2);
        // Sætter position af projektil
        proj.setX(player1.getX());
        proj.setY(player1.getY());
        // Sætter position af projektil sprite
        BA.setLayoutX(player1.getX());
        BA.setLayoutY(CANVAS_Y - player1.getY());
    }

    //Andreas
    //Regenererer banen hver gang en spiller rammes
    public void regenTerrain(){
        for (Object o: blokke){
            anchorPane.getChildren().remove(o);
        }
        blokke.clear();
        generateTerrain();
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseMove(MouseEvent event){
        if (!manuelKast){ //Hvis brugeren har valgt 'det visuelle kast'
            xdiff = event.getX() - BA.getLayoutX(); //Forskellen fra bananens position til musen i x aksen
            ydiff = BA.getLayoutY() - event.getY(); //Og y aksen
            throwvelocity = Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)); //Pixels mellem bananen og musen
            throwangledeg = Math.toDegrees(Math.acos(xdiff/throwvelocity)); //Vinklen mellem banan og mus
            displayangle = throwangledeg;

            if (ydiff < 0){ //Hvis man peger musen under bananens position på y aksen
                displayangle = - throwangledeg;
            }

            if (!player1HasTurn){ //Gør så vinklen vises mod spiller 1 fra spiller 2's perspektiv
                if (ydiff < 0) {
                    displayangle = throwangledeg - 180;
                } else {
                    displayangle = 180 - throwangledeg;
                }
            }

            throwvelocity /= 2; //Gør det nemmere at styre hastigheden

            if (!hasthrown){ //Så længe brugeren ikke har kastet
                visualangle.setText("Vinkel: " + round(displayangle)); //Skriv vinklen og hastigheden i spil-vinduet
                visualvelocity.setText("Hastighed: " + round(throwvelocity));
            }

            if (player1HasTurn){
                indicatorp1.setEndX(xdiff/2); //Lad spiller 1's indikator følge musen
                indicatorp1.setEndY(-ydiff/2);
            } else {
                indicatorp2.setEndX(xdiff/2); //Samme for spiller 2
                indicatorp2.setEndY(-ydiff/2);
            }
        }
    }

    //Anders
    //Til visuelt kast
    @FXML
    private void onMouseClick(MouseEvent event) throws IOException, InterruptedException {
        if (!manuelKast){ //Hvis brugeren har valgt 'det visuelle kast'
            if(player1HasTurn){ //Spiller 1 har tur
                if(player1.isComputer()){ //Tjekker om spilleren er sat til at være computer
                    Guess guess = computer1.getNextMove();  //Computeren gætter
                    animateProjectile(player1, guess.getAngle(), guess.getVelocity()); //Og kaster
                } else {
                    animateProjectile(player1, displayangle, throwvelocity); //Ellers bruges brugerens kast
                }
            } else { //Spiller 2 har tur (Samme forløb som for spiller 1)
                if(player2.isComputer()){
                    Guess guess = computer2.getNextMove();
                    animateProjectile(player2, -guess.getAngle(), -guess.getVelocity()); //Bruger negative kast og hastigheder, da kastet er den modsatte vej
                } else {
                    animateProjectile(player2, -displayangle, -throwvelocity);
                }
            }
        }
    }

    //Anders
    //Til kast knappen
    public void kast() throws IOException, InterruptedException {
        try {
            double numangle, numvelocity;
            //Tjekker tur på samme måde som i 'onMouseClick()'
            if(player1HasTurn){
                if(player1.isComputer()){
                    Guess guess = computer1.getNextMove();
                    animateProjectile(player1, guess.getAngle(), guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText()); //Bruger værdierne fra tekst felterne
                    numvelocity = Double.parseDouble(velocity.getText());
                    animateProjectile(player1, numangle, numvelocity);
                }
            } else {
                if(player2.isComputer()){
                    Guess guess = computer2.getNextMove();
                    animateProjectile(player2, -guess.getAngle(), -guess.getVelocity());
                } else {
                    numangle = Double.parseDouble(angle.getText());
                    numvelocity = Double.parseDouble(velocity.getText());
                    animateProjectile(player2, -numangle, -numvelocity);
                }
            }

            //Fjerner indtastede værdier fra sidste spiller
            angle.clear();
            velocity.clear();

            resetImage();
        } catch (Exception e){
            System.out.println(e); //Hvis fejl forekommer, print fejlen
        }
    }

    //Christian
    //Metode for kast arm animation
    public void animationKast() throws InterruptedException {
        KeyFrame resetframe = new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            if(player1HasTurn){
                abe1.setImage(normal);
            } else {
                abe2.setImage(normal);
            }
        }
    });
        gorillathrow.setCycleCount(1);
        if (player1HasTurn) {
            abe1.setImage(kast);
        }else {
            abe2.setImage(kast);
        }
        gorillathrow.getKeyFrames().add(resetframe);
    }

    //Andreas (Udregningen)
    //Christian (Animation)
    //Anders (Omskrevet udregning til animation og implementeret eksisterende animationer)
    public void animateProjectile(Player shootingPlayer, double ANGLE_IN_DEGREES, double VELOCITY) throws IOException, InterruptedException{
        //Anders (Omskrivning) Andreas (Udregning)
        //Kurve animation
        Timeline throwanimation = new Timeline();
        throwanimation.setCycleCount(Timeline.INDEFINITE); //Fortsætter animationen til den stopper
        KeyFrame bananakeyrframe = new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {
            final double angle = Math.toRadians(ANGLE_IN_DEGREES);
            double x = shootingPlayer.getX() - 40;
            double y = shootingPlayer.getY() - 10;
            final double startX = shootingPlayer.getX();
            final double startY = shootingPlayer.getY() - 10;
            double realtime;
            boolean buildingHit = false;
            boolean playerHit = false;

            @Override
            public void handle(ActionEvent event) {
                realtime += throwanimation.getCurrentTime().toSeconds() * 10; //Hvor langt tid der er gået af animationen (Ganges for at gøre animationen hurtigere)

                x = startX + VELOCITY * realtime * Math.cos(angle); //Regner x værdi
                y = startY + (VELOCITY * realtime * Math.sin(angle) - 0.5 * g * realtime * realtime); //Og y værdi

                //bananen påvirkes af vinden, hvis spilleren ikke er styret af computeren
                if (!shootingPlayer.isComputer()){
                    x-= realtime * windforce * Math.cos(Math.toRadians(winddirection));
                    y-= realtime * windforce * Math.sin(Math.toRadians(winddirection));
                }

                proj.setX(x); //Flytter projektil-objekt
                proj.setY(y);

                //Christian
                BA.setLayoutX(x - BA.getFitWidth() / 2); // Ændret så center af så proj's koordinater er centrum af billede
                BA.setLayoutY(CANVAS_Y - y - BA.getFitHeight() / 2); // Ændret så center af så proj's koordinater er centrum af billede

                // Markus
                // Anden version af kollision med spiller
                if ((player1HasTurn && playerIsHit(abe2)) || (!player1HasTurn && playerIsHit(abe1))) {
                    playerHit = true;
                    System.out.println("player hit");
                } else {
                    // Kollision med bygning
                    for (int i = 0; i < buildings.length; i++) {
                        if (buildings[i].collision(proj)) {
                            buildingHit = true;
                            System.out.println("Ramt bygning " + i);
                        }
                    }
                }
                // Checker om banan har ramt noget eller er uden for vinduet (banan kan godt være over vinduet)
                if (y < 0 || x > CANVAS_X || x < 0 || playerHit || buildingHit){
                    throwanimation.stop(); //Stopper alle animationerne til kastet
                    rotationBanan.stop();
                    gorillathrow.stop();

                    if(player1HasTurn){ //Resetter gorilla billedet
                        abe1.setImage(normal);
                    } else {
                        abe2.setImage(normal);
                    }

                    // Check om spiller blev ramt
                    if (playerHit){
                        shootingPlayer.addPoint(1);
                        regenTerrain();
                        computer1.resetAndCalculate();
                        computer2.resetAndCalculate();
                        updateWind();
                    }
                    player1point.setText(Integer.toString(player1.getPoint()));
                    player2point.setText(Integer.toString(player2.getPoint()));

                    // Eksplosion ved kollision med bygning
                    if (buildingHit) explosion(x, y);

                    //status på point
                    pointStatus(player1);
                    pointStatus(player2);

                    try {
                        turnStatus();
                        resetImage();
                        hasthrown = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        throwanimation.getKeyFrames().add(bananakeyrframe);

        if (!hasthrown){ //Hvis ikke spilleren har kastet allerede
            hasthrown = true;

            throwanimation.play(); //Start kaste animation
            indicatorp1.setOpacity(0); //Gør indikatorene gennemsigtige
            indicatorp2.setOpacity(0);

            animationKast();
            gorillathrow.play(); //Start animation af abe der kaster

            if (player1HasTurn) {
                rotationBanan.setRate(1); //Roter banan i positiv omløbsretning
                rotationBanan.play();
            } else {
                rotationBanan.setRate(-1); //Roter banan i negativ omløbsretning
                rotationBanan.play();
            }
        }

        //Andreas - Computer laver nye gæt når alle gæt er brugt
//        if(player1HasTurn){
//            computer1.calculateBetterMoves();
//        } else {
//            computer2.calculateBetterMoves();
//        }
    }

    //Andreas
    //Kaldes efter et kast er sket
    //Tager en Player og printer point til konsollen, samt tjekker om spilleren har vundet
    public void pointStatus(Player player){
        System.out.println(player.getName() + ":" + player.getPoint());

        if (player.getPoint() == FirstTo){
            winner = player;
            winnerFound = true;
        }
    }

    //Andreas
    //Kaldes når koordinater skal printes til konsollen
    //Tager en double og laver den om til 2 decimaler og retunerer den som streng
    public static String round(double a){
        return String.format("%.2f",a);
    }

    //Andreas
    // Modificeret playerIsHit
    public static boolean playerIsHit(ImageView abe){
        double bananX = proj.getX();
        double bananY = proj.getY();
        // Checker om banans centrum rammer aben
        if (bananX > abe.getBoundsInParent().getMinX() && bananX < abe.getBoundsInParent().getMaxX() && bananY < (CANVAS_Y - abe.getBoundsInParent().getMinY()) && bananY > (CANVAS_Y - abe.getBoundsInParent().getMaxY())) {
            return true;
        }
        return false;
    }

    //Andreas
    public void turnStatus() throws IOException {
        //tjekker om vinder er fundet
        if (winnerFound) {
            System.out.println(winner.getName() + " har vundet!");
            GameApplication.setStage("gameover-screen.fxml");
        } else {
            //skifte tur
            player1HasTurn = !player1HasTurn;
        }
    }

    //Christian
    //Reset banan til original pos
    public void resetImage (){
        if(player1HasTurn){
            BA.setLayoutX(player1.getX() - BA.getFitWidth() / 2);
            BA.setLayoutY(CANVAS_Y - player1.getY() - BA.getFitHeight() / 2);
        }else{
            BA.setLayoutX(player2.getX() - BA.getFitWidth() / 2);
            BA.setLayoutY(CANVAS_Y - player2.getY() - BA.getFitHeight() / 2);
        }
        resetIndicators();
    }

    //Anders
    //Stiller indicatorene til at være
    public void resetIndicators(){

        if (player1HasTurn){ //Gør spillerens indikator synlig
            indicatorp1.setOpacity(1);
        } else {
            indicatorp2.setOpacity(1);
        }

        indicatorp1.setEndY(0); //Nulstil indikatorer
        indicatorp1.setEndX(0);
        indicatorp2.setEndY(0);
        indicatorp2.setEndX(0);

        indicatorp1.setLayoutX(BA.getLayoutX() + 20); //Flyt indikatorer til bananens nuværende position
        indicatorp1.setLayoutY(BA.getLayoutY() + 25);
        indicatorp2.setLayoutX(BA.getLayoutX() + 20);
        indicatorp2.setLayoutY(BA.getLayoutY() + 25);
    }

    // Markus
    // Animation af eksplosion af bygning (ændrer ikke bygnings hitbox)
    public void explosion(double x, double y) {

        double explosionRadius = 10;
        int cycles = 40;

        /*
        // TODO: Lav eventuelt en liste med alle 'c' objekter og tjek om de er kollision med både et 'c' objekt og bygning samtidig - hvis sandt, så ignorer. (Til efter rapport)
        // Tegn hul i bygning (skal være samme farve som baggrund)
        Circle c = new Circle();
        c.setCenterX(x);
        c.setCenterY(CANVAS_Y - y);
        c.setRadius(explosionRadius);
        c.setFill(Color.rgb(87, 182, 255));
        anchorPane.getChildren().add(c);
        // ^^ Kan fjernes hvis det ser mærkeligt ud at bygning bliver destrueret uden deres hitbox ændrer sig
        */

        // Eksplosion animation cirkel
        Timeline explosionAnimation = new Timeline();
        explosionAnimation.setCycleCount(cycles);

        Circle blast = new Circle();
        blast.setCenterX(x);
        blast.setCenterY(CANVAS_Y - y);
        blast.setRadius(0);
        blast.setFill(Color.RED);
        blast.setId("blast");
        anchorPane.getChildren().add(blast);

        // Generere keyframes
        KeyFrame explosionKeyframe = new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            Circle blast = (Circle) anchorPane.lookup("#blast");
            int cycleCount = 0;

            // Cirkel vokser i 10 gentagelser -> er statisk i næste 20 -> krymper i 10 gentagelser
            @Override
            public void handle(ActionEvent actionEvent) {
                cycleCount++;
                if (cycleCount <= 10) {
                    blast.setRadius(blast.getRadius() + explosionRadius / 10);
                } else if (cycleCount > 30 && cycleCount < cycles) {
                    blast.setRadius(blast.getRadius() - explosionRadius / 10);
                } else  if (cycleCount == cycles){
                    anchorPane.getChildren().remove(blast);
                }
            }
        });
        // Tilføj keyframes og spil animation
        explosionAnimation.getKeyFrames().add(explosionKeyframe);
        explosionAnimation.play();
    }
}
