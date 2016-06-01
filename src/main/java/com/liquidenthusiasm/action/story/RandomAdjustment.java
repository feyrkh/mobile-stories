package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

public class RandomAdjustment {

    private static final Logger log = LoggerFactory.getLogger(RandomAdjustment.class);

    /**
     * The name of the variable to output to. If not provided, defaults to "si_random"
     */
    private String outputVar = "si_random";

    /**
     * The base chance out of 1000. If a random number from 0-999 is less than this number, then the outputVar value will be positive.
     * The greater the success, the higher the number will be, so this can be used for scaling successes.
     */
    private int baseChance = 500;

    /**
     * The stat that can be used to modify the baseChance. If not provided, this is treated as 'luck' and the baseChance is used by
     * itself.
     */
    private String stat = null;

    /**
     * The value of the adjustment stat which is treated as +/- 0. If the player's stat value is higher, this increases their chances
     * by 'statAdjustment' * 0.1% per point, rounded to the nearest 0.1%. If the player's stat value is lower, it decreases their
     * chances by the same amount.
     */
    private int baseStat = 0;

    /**
     * The amount (in 0.1% increments) that each point of 'stat' is worth.
     */
    private float statAdjustment = 100;

    @JsonIgnore
    public String getChanceDescription(Coven coven, Person person, StoryInstance story) {
        int chance = calculateSuccessChance(coven, person, story);
        String stat = this.stat == null ? "pi_luck" : this.stat;
        String statDesc = Daos.varRepo.getVarLabel(stat);
        return "Your '" + statDesc + "' quality gives you a " + (chance / 10f) + "% chance of success.";
    }

    private int calculateSuccessChance(Coven coven, Person person, StoryInstance story) {
        int successChance = baseChance;
        if (stat != null) {
            int statDiff = Daos.varRepo.getIntProperty(stat, coven, person, story.getState()) - baseStat;
            successChance += (int) (statDiff * statAdjustment);
        }
        return successChance;
    }

    public void applyAdjustment(Coven coven, Person person, StoryInstance story) {
        int result = calculateSuccessChance(coven, person, story) - ((int) (Math.random() * 1000));
        Daos.varRepo.setProperty(this.outputVar, result, coven, person, story);
    }

    public String getOutputVar() {
        return outputVar;
    }

    public void setOutputVar(String outputVar) {
        this.outputVar = outputVar;
    }

    public int getBaseChance() {
        return baseChance;
    }

    public void setBaseChance(int baseChance) {
        this.baseChance = baseChance;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public int getBaseStat() {
        return baseStat;
    }

    public void setBaseStat(int baseStat) {
        this.baseStat = baseStat;
    }

    public float getStatAdjustment() {
        return statAdjustment;
    }

    public void setStatAdjustment(float statAdjustment) {
        this.statAdjustment = statAdjustment;
    }

}
