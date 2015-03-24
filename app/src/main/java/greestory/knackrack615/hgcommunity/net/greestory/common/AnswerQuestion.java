package greestory.knackrack615.hgcommunity.net.greestory.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import greestory.knackrack615.hgcommunity.net.greestory.Menu;
import greestory.knackrack615.hgcommunity.net.greestory.R;
import greestory.knackrack615.hgcommunity.net.greestory.util.DataFlow;
import greestory.knackrack615.hgcommunity.net.greestory.util.Question;
import greestory.knackrack615.hgcommunity.net.greestory.util.Team;


public class AnswerQuestion extends Activity {

    public static final int REQUEST_CODE = 25565;

    public static final String ANSWER_TRUE = "true" , ANSWER_FALSE = "false";

    private Question question;

    boolean isBackPressedWithinTimes = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.answer_question_activity);

        question = DataFlow.getQuestion();

        TextView questionView = (TextView) findViewById(R.id.questionView);

        View root = questionView.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        questionView.setText(question.getQuestion());

        TextView whois = (TextView) findViewById(R.id.whois);

        if(DataFlow.getTeamRotation() != 0) {
            int num = 0;
            int i = 0;
            for (Team t : DataFlow.getTeams()) {
                num++;
                if (t.equals(DataFlow.getCurrentTeam())) {
                    i = num;
                    break;
                }
                Log.d("tag" , "Team " + num + " is not playing");
            }

            whois.setText("Τώρα παίζει η ομάδα " + num);
        }else {
            whois.setText("Εσείς παίζετε");
        }

        TextView x50 = (TextView) findViewById(R.id.x50);

        x50.setText(DataFlow.getCurrentTeam().getPoints() + "/30");


        Button answer1 = (Button) findViewById(R.id.answer1Button);
        Button answer2 = (Button) findViewById(R.id.answer2Button);
        Button answer3 = (Button) findViewById(R.id.answer3Button);
        Button answer4 = (Button) findViewById(R.id.answer4Button);

        answer1.setText(question.getAnswer1());
        answer2.setText(question.getAnswer2());
        answer3.setText(question.getAnswer3());
        answer4.setText(question.getAnswer4());

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered(1);
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered(2);
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered(3);
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionAnswered(4);
            }
        });
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

    private void questionAnswered(int answerId){
        boolean isCorrect = question.isCorrect(answerId);

        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;

        if(isCorrect){

            Random r = new Random();

            int pts = r.nextInt(3) + 1;

            DataFlow.getCurrentTeam().incrementCorrectAnswers();
            DataFlow.getCurrentTeam().addPoints(pts);

            text = "Σωστή απάντηση! +" + pts + " Πόντοι";
        }else{
            DataFlow.getCurrentTeam().addPoints(-1);

            text = "Λάθος απάντηση! -1 Πόντοι!";
        }

        DataFlow.getCurrentTeam().incrementQuestionsAnswered();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        finish();
    }

    private String boolToString(boolean a){
        if(a) {
            return ANSWER_TRUE;
        }else {
            return ANSWER_FALSE;
        }
    }

}
