package ADVANCEDGORILLA;

//*****************************************************
// Player
// En udvidelse af Entity der også har et navn og point
// samt en bool isComputer, der afgør om denne Player
// skal styres af computeren
//
// Lavet af Andreas
//*****************************************************

public class Player extends Entity{
    protected String name;
    protected int point;
    protected boolean isComputer;

    public Player(int x, int y, String name){
        super(x,y);
        this.name = name;
        this.point = 0; //point starter på 0
        this.isComputer = false; // som udgangspunkt er spilleren ikke styret af computeren
    }

    //sætter computer, hvis true beregner computeren de gæt der udføres
    public void setComputer(boolean bool){
        this.isComputer = bool;
    }

    //retunerer true hvis denne spiller styres af computeren
    public boolean isComputer(){
        return this.isComputer;
    }

    //retunerer spillerens navn
    public String getName(){
        return this.name;
    }

    //adderer point til denne spiller
    public void addPoint(int n){
        this.point += n;
    }

    //retunerer spillerens point
    public int getPoint(){
        return this.point;
    }
}
