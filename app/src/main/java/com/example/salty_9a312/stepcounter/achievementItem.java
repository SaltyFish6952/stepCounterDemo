package com.example.salty_9a312.stepcounter;

public class achievementItem {

    private int achievement_steps;
    private int current_steps;

    achievementItem(int achievement_steps,int current_steps){

        this.achievement_steps = achievement_steps;
        this.current_steps = current_steps;
    }

    public int getAchievement_steps() {
        return achievement_steps;
    }

    public int getCurrent_steps() {
        return current_steps;
    }

}
