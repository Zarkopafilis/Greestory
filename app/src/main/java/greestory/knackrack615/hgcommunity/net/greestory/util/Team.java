package greestory.knackrack615.hgcommunity.net.greestory.util;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private int questionsAnswered = 0, correctAnswers = 0, points = 0;
    public String group;

    public List<String> groups = new ArrayList<String>();

    public List<Integer> uids = new ArrayList<Integer>();

    public String getGroup() {
        return group;
    }

    public Team(String group){ this.group = group; }

    public void incrementQuestionsAnswered(){
        questionsAnswered += 1;
    }

    public void incrementCorrectAnswers(){
        correctAnswers += 1;
    }

    public void addPoints(int amount){
        points += amount;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
