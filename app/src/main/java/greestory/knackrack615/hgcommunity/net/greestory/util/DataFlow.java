package greestory.knackrack615.hgcommunity.net.greestory.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import greestory.knackrack615.hgcommunity.net.greestory.SplashScreen;

public class DataFlow {

    private DataFlow(){
        throw new AssertionError();
    }

    private static boolean gameStarted = false, firstRun = true , questionSelected = false;

    private static List<Integer> questionsAnswered = new ArrayList<Integer>();

    public static void clearQuestionsAnswered(){
        questionsAnswered.clear();
    }

    public static void addQuestionAnswered(int uid){
        questionsAnswered.add(uid);
    }

    public static boolean isQuestionSelected() {
        return questionSelected;
    }

    public static void setQuestionSelected(boolean questionSelected) {
        DataFlow.questionSelected = questionSelected;
    }

    public static boolean isFirstRun() {
        return firstRun;
    }

    public static void setFirstRun(boolean firstRun) {
        DataFlow.firstRun = firstRun;
    }

    private static Question question;
    private static Team currentTeam;

    private static int teamRotation = 0;
    private static List<Team> teams = new ArrayList<Team>();

    public static boolean isGameStarted() {
        return gameStarted;
    }

    public static void setGameStarted(boolean gameStarted) {
        DataFlow.gameStarted = gameStarted;
    }

    public static List<Integer> getQuestionsAnswered() {
        return questionsAnswered;
    }

    public static Question getQuestion() {
        return question;
    }

    public static void setQuestion(Question question) {
        DataFlow.question = question;
    }

    public static Team getCurrentTeam() {
        return currentTeam;
    }

    public static void setCurrentTeam(Team currentTeam) {
        DataFlow.currentTeam = currentTeam;
    }

    public static List<Team> getTeams() {
        return teams;
    }

    public static void setTeams(List<Team> teams) {
        DataFlow.teams = teams;
    }

    public static int getTeamRotation() {
        return teams.size() - 1;
    }

    public static void setTeamRotation(int teamRotation) {
        DataFlow.teamRotation = teamRotation;
    }

    public static List<String> groups = new ArrayList<String>();

    public static void rotateTeams(){

        if(teamRotation >= teams.size()){
            teamRotation = 0;
        }

        currentTeam = teams.get(teamRotation);

        teamRotation++;
    }

    private static String getGroups(){
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(SplashScreen.groups));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line + "-");
                    line = br.readLine();
                }
                String everything = sb.toString();
                br.close();
                return everything;
            }catch(Exception e){
                e.printStackTrace();
            } finally {

            }
            return "0";
    }

    public static void loadDatabase() {

        String[] groupz = getGroups().split("-");
        groups.clear();

        for(String s : groupz){
            groups.add(s);
            Log.d("tag" , "Load group > " + s);
        }

    }
}
