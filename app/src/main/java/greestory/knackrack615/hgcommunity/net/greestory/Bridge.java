package greestory.knackrack615.hgcommunity.net.greestory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import greestory.knackrack615.hgcommunity.net.greestory.common.AnswerQuestion;
import greestory.knackrack615.hgcommunity.net.greestory.util.DataFlow;
import greestory.knackrack615.hgcommunity.net.greestory.util.DatabaseFFS;
import greestory.knackrack615.hgcommunity.net.greestory.util.QuestionGrabber;
import greestory.knackrack615.hgcommunity.net.greestory.util.Team;

public class Bridge extends Activity {

    TextView team1, team2 , team3 , team4 , team1Points , team2Points , team3Points , team4Points;
    TextView teamxwon , questionsAnswered;
    Button backToMenu;

    boolean isBackPressedWithinTimes = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge_activity);

        team1 = (TextView) findViewById(R.id.team1Label);
        team2 = (TextView) findViewById(R.id.team2Label);
        team3 = (TextView) findViewById(R.id.team3Label);
        team4 = (TextView) findViewById(R.id.team4Label);

        View root = team1.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        team1Points = (TextView) findViewById(R.id.team1Points);
        team2Points = (TextView) findViewById(R.id.team2Points);
        team3Points = (TextView) findViewById(R.id.team3Points);
        team4Points = (TextView) findViewById(R.id.team4Points);

        teamxwon = (TextView) findViewById(R.id.teamXwon);
        questionsAnswered = (TextView) findViewById(R.id.questionsX);

        teamxwon.setVisibility(View.INVISIBLE);
        questionsAnswered.setVisibility(View.INVISIBLE);

        backToMenu = (Button) findViewById(R.id.backToMenu);
        backToMenu.setVisibility(View.INVISIBLE);

        final Intent menu = new Intent(this , Menu.class);

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataFlow.getTeams().clear();
                DataFlow.getQuestionsAnswered().clear();
                startActivity(menu);

            }
        });

        int teamNum = DataFlow.getTeamRotation();

        if(teamNum == 0){

            team1.setText("Εσείς");
            team2.setVisibility(View.INVISIBLE);
            team3.setVisibility(View.INVISIBLE);
            team4.setVisibility(View.INVISIBLE);

            team2Points.setVisibility(View.INVISIBLE);
            team3Points.setVisibility(View.INVISIBLE);
            team4Points.setVisibility(View.INVISIBLE);
        }else if(teamNum == 1){

            team3.setVisibility(View.INVISIBLE);
            team4.setVisibility(View.INVISIBLE);

            team3Points.setVisibility(View.INVISIBLE);
            team4Points.setVisibility(View.INVISIBLE);
        }

        DatabaseFFS dbffs = new DatabaseFFS(this , "questions.db" , null ,1);
        QuestionGrabber.db = dbffs.getReadableDatabase();

        continuePlaying();
    }

    @Override
    public void onBackPressed() {

        if(isBackPressedWithinTimes){
            Intent i = new Intent(this , Menu.class);
            startActivity(i);
            return;
        }

        Context context = getApplicationContext();
        CharSequence text = "Πατήστε ξανά για να πάτε στο αρχικό menu";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        isBackPressedWithinTimes = true;

        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                toggleButtonPressed(false);
            }
        };

        t.start();
    }

    private void toggleButtonPressed(boolean a){
        isBackPressedWithinTimes = a;
    }

    private void continuePlaying(){

        boolean end = false;
        int num = 0;

        for(Team t : DataFlow.getTeams()){
            if(t.getPoints() >= 30){
                end = true;
                num++;
            }
        }

        if(end){
            if(DataFlow.getTeamRotation() == 0){
                teamxwon.setText("Νικήσατε!");
            }else{
                teamxwon.setText("Η ομάδα" + num + " Νίκησε!");
            }

            questionsAnswered.setText("Ερωτήσεις που απαντήθηκαν: " + (DataFlow.getQuestionsAnswered().size() + 1));

            backToMenu.setVisibility(View.VISIBLE);
            teamxwon.setVisibility(View.VISIBLE);
            questionsAnswered.setVisibility(View.VISIBLE);

            return;
        }


        if(!DataFlow.isFirstRun()){
            DataFlow.rotateTeams();
        }else{
            DataFlow.setFirstRun(false);
        }

        DataFlow.setQuestionSelected(false);

       final Activity self = this;

        Thread pickRandomQuestion = new Thread(){

            @Override
            public void run(){
                try {
                    QuestionGrabber.grabRandomQuestion(DataFlow.getCurrentTeam().getGroup() , self);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DataFlow.setQuestionSelected(true);
            }
        };


        Thread waiter = new Thread(){
            @Override
            public void run(){
                waitForQuestionToBePicked();
            }

            private void waitForQuestionToBePicked(){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!DataFlow.isQuestionSelected()){
                    waitForQuestionToBePicked();
                }else{
                    launchToAnswerQuestion();
                }
            }
        };
        pickRandomQuestion.start();
        waiter.start();
    }

    private void launchToAnswerQuestion(){
        Intent answer = new Intent(getApplicationContext() , AnswerQuestion.class);
        startActivityForResult(answer , AnswerQuestion.REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateScoreView();
        continuePlaying();
    }

    private void updateScoreView(){

        //normalize Scores
        for(Team t : DataFlow.getTeams()){
            if (t.getPoints() < 0){
                t.setPoints(0);
            }
        }

        if(DataFlow.getTeamRotation() == 0){
            team1Points.setText(DataFlow.getTeams().get(0).getPoints() + "");
        }else if(DataFlow.getTeamRotation() == 1){
            team1Points.setText(DataFlow.getTeams().get(0).getPoints() + "");
            team2Points.setText(DataFlow.getTeams().get(1).getPoints() + "");
        }else if(DataFlow.getTeamRotation() == 3){
            team1Points.setText(DataFlow.getTeams().get(0).getPoints() + "");
            team2Points.setText(DataFlow.getTeams().get(1).getPoints() + "");
            team3Points.setText(DataFlow.getTeams().get(2).getPoints() + "");
            team4Points.setText(DataFlow.getTeams().get(3).getPoints() + "");
        }
    }
}
