package greestory.knackrack615.hgcommunity.net.greestory.util;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Random;

public class QuestionGrabber {

    public static SQLiteDatabase db;

    /*private final String createDatabase = "CREATE TABLE IF NOT EXISTS QUESTIONS" +
            "(UID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "QUESTION CHAR(80)," +
            "ANSWER_1 CHAR(40)," +
            "ANSWER_2 CHAR(40)" +
            "ANSWER_3 CHAR(40)" +
            "ANSWER_4 CHAR(40)" +
            "CORRECT_ANSWER INT," +
            "TEAM CHAR(40))";

    public QuestionGrabber(){

    }*/

    public static String getRandomGroup() {

        Random r = new Random();
        return DataFlow.groups.get(r.nextInt(DataFlow.groups.size()));
    }

    /*private void addQuestionToDb(String question, String answer1, String answer2, String answer3, String answer4, int correctAnswer, int uid , String group){
        String query = "INSERT INTO QUESTIONS (UID , QUESTION , ANSWER_1 , ANSWER_2 , ANSWER_3 , ANSWER_4 , CORRECT_ANSWER , GROUP) VALUES"+
                "(" + uid +")";
    }*/

    public static void grabRandomQuestion(String group , Activity activity) throws Exception{
        String query = "SELECT * FROM QUESTIONS WHERE TEAM=\"" + group +"\"";
        if(!DataFlow.getQuestionsAnswered().isEmpty()) {
            for (Integer i : DataFlow.getCurrentTeam().uids) {
                query += " AND UID!=" + i;
            }
        }

        query  += " LIMIT 1";

        Log.d("tag" , "select query :" + query);

        Cursor c = db.rawQuery(query, null);
        Log.d("tag" , "columnCount:" + c.getColumnCount());

        int rowCount = c.getCount();

        Log.d("tag" , "rowCount: " + rowCount);

        boolean mtf = c.moveToFirst();
        Log.d("tag" , "movetofirst :" + c.moveToFirst());

        Integer uid = null , correct = null;
        String question = null, answer1 = null, answer2 = null, answer3 = null, answer4 = null, groupz = null;

        if(mtf) {
             Log.d("tag" , "grabbing stuff from cursor");
             uid = Integer.valueOf(c.getString(0));
             question = c.getString(1);
             answer1 = c.getString(2).replaceAll("A. ", "").replaceAll("B. ", "").replaceAll("C. ", "").replaceAll("D. ", "");
             answer2 = c.getString(3).replaceAll("A. ", "").replaceAll("B. ", "").replaceAll("C. ", "").replaceAll("D. ", "");
             answer3 = c.getString(4).replaceAll("A. ", "").replaceAll("B. ", "").replaceAll("C. ", "").replaceAll("D. ", "");
             answer4 = c.getString(5).replaceAll("A. ", "").replaceAll("B. ", "").replaceAll("C. ", "").replaceAll("D. ", "");
             correct = Integer.valueOf(c.getString(6));
             groupz = c.getString(7);
        }
        c.close();

        if(!mtf || DataFlow.getCurrentTeam().uids.contains(uid)){
            Log.d("tag" , "mtf = false && uids contain uid....changing group");
            String group_;
            while(true){
                group_ = getRandomGroup();
                Log.d("tag" , "picked new group :" + group_);
                if(!DataFlow.getCurrentTeam().groups.contains(group_)){
                    Log.d("tag" , "will settle with group :" + group_);
                    break;
                }else{
                    Log.d("tag" , "already got this question group answered....trying again");
                }
            }

            DataFlow.getCurrentTeam().group = group_;
            Log.d("tag" , "grabbing question recursively with new group:" + group_);
            grabRandomQuestion(group_ , activity);
        }else{
            Log.d("tag" , "uid -> uids: " + uid);
            DataFlow.getCurrentTeam().uids.add(uid);
        }

        Log.d("tag" , "submitting new question to bus");
        Question que = new Question(question,answer1,answer2,answer3,answer4,correct,uid,groupz);

        DataFlow.addQuestionAnswered(que.getUid());
        DataFlow.setQuestion(que);
    }
}
