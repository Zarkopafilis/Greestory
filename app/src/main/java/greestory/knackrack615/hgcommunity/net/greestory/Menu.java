package greestory.knackrack615.hgcommunity.net.greestory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import greestory.knackrack615.hgcommunity.net.greestory.util.DataFlow;
import greestory.knackrack615.hgcommunity.net.greestory.util.QuestionGrabber;
import greestory.knackrack615.hgcommunity.net.greestory.util.Team;

public class Menu extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceStace){
        super.onCreate(savedInstanceStace);
        setContentView(R.layout.menu_activity);

        Button solo = (Button) findViewById(R.id.soloButton);
        Button two = (Button) findViewById(R.id.twoTeamsButton);
        Button four = (Button) findViewById(R.id.fourTeamsButton);

        View root = solo.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(0);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(1);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(3);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void startGame(int teamNumber){

       DataFlow.getQuestionsAnswered().clear();
       DataFlow.getTeams().clear();

        for (int i = 0; i <= teamNumber; i++) {
            DataFlow.getTeams().add(new Team(QuestionGrabber.getRandomGroup()));
        }

        for(Team t : DataFlow.getTeams()){
            t.groups.add(t.getGroup());
        }


        DataFlow.setTeamRotation(teamNumber);

        DataFlow.rotateTeams();

        Intent bridge = new Intent(getApplicationContext() , Bridge.class);
        DataFlow.setGameStarted(true);
        startActivity(bridge);

    }

}
