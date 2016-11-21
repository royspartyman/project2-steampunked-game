package edu.msu.perrym23.project1;


import java.io.Serializable;

public class Player implements Serializable {

    PipeBank pipeBank = null;
    String name = null;
    boolean turn = false;
    boolean winner = false;
    Integer color = 0;

    Player(String name, int color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Integer getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

}
