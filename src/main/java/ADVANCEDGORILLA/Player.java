package ADVANCEDGORILLA;

import javafx.beans.value.ObservableValue;

public class Player extends Entity{
    protected String name;
    protected int point;
    protected boolean isComputer;

    public Player(int x, int y, String name){
        super(x,y);
        this.name = name;
        this.point = 0;
        this.isComputer = false;
    }

    public void setComputer(boolean bool){
        this.isComputer = bool;
    }

    public boolean isComputer(){
        return this.isComputer;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;

    }

    public void addPoint(int n){
        this.point += n;
    }

    public int getPoint(){
        return this.point;
    }

    public double distanceToProjectile(Projectile proj){
        return Math.abs(Math.sqrt(Math.pow(proj.getX()-this.getX(),2))+Math.pow(proj.getY()-this.getY(),2));
    }
}
