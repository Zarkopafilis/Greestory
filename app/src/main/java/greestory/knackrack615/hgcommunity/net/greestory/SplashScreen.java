package greestory.knackrack615.hgcommunity.net.greestory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import greestory.knackrack615.hgcommunity.net.greestory.util.DataFlow;
import greestory.knackrack615.hgcommunity.net.greestory.util.QuestionGrabber;

public class SplashScreen extends Activity {

    String path = "";
    public static File groups;
    public static File version;
    public static File db;

    String ip = "128.199.34.71";

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        final Context cx = this;

        Thread a = new Thread() {
            @Override
            public void run() {
                path = cx.getFilesDir().getAbsolutePath();

                final Intent menu = new Intent(cx, Menu.class);

                t = new

                        Thread() {
                            public void run() {
                                try {
                                    DataFlow.loadDatabase();
                                    sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(menu);
                            }
                        }

                ;

                groups = new

                        File(path + "/groups.txt");

                version = new

                        File(path + "/version.txt");

                QuestionGrabber.db =

                        openOrCreateDatabase("questions.db", Context.MODE_PRIVATE, null);

                QuestionGrabber.db.close();

                db = new

                        File("/data/data/" + getPackageName()

                        + "/databases/questions.db");
                Log.d("tag", "pkg name : " +

                                getPackageName()

                );

                if (version.exists() && groups.exists() && db.exists() && !isNetworkAvailable())

                {
                    t.start();
                } else if ( isNetworkAvailable() )

                {
                    Thread p = new Thread() {

                        @Override
                        public void run() {
                            Log.d("tag", "thread P started....");
                            pullFromServer();
                            t.start();
                        }

                    };

                    p.start();
                    //  }
                } else {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(cx);
                    builder1.setMessage("Πρέπει να είστε συνδεδεμένος στο διαδίκτυο την πρώτη φορά που τρέχετε την εφαρμογή");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Κατάλαβα",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            }
        };

        a.start();

    }

    private boolean okToLaunch() {

        Log.d("tag", "groups.exist " + groups.exists());
        Log.d("tag", "version.exist " + version.exists());
        Log.d("tag", "db.exist " + db.exists());
        Log.d("tag", "checkver " + checkVersion());

        if (readVersion() == null) {
            Log.d("tag", "readversion null ");
        } else {
            Log.d("tag", "readversion not null ");
        }

        Log.d("tag", "isnetworkingavailable: " + isNetworkAvailable());


        if (groups.exists() && version.exists() && db.exists() && readVersion() != null && isNetworkAvailable() && checkVersion()) {
            Log.d("tag", "OK TO LAUNCH");
            return true;
        } else {


            return false;

        }
    }

    private boolean checkVersion() {
        int versionN = Integer.valueOf(readVersion());
        int serverVersion = grabVersion();
        if (version.exists()) {
            versionN = Integer.parseInt(readVersion());

            if (versionN < serverVersion) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void pullFromServer() {

        String readVersion_ = readVersion();
        int versionN = 0;
        try{

            versionN =  Integer.valueOf(readVersion_);

        }catch(Exception e){
            e.printStackTrace();
            versionN = 0;
        }finally{

        }
        int serverVersion = grabVersion();

        if (version.exists()) {
            versionN = Integer.parseInt(readVersion());

            if (versionN <= serverVersion) {
                startPulling();
            }
        } else {
            startPulling();
        }
    }

    private void startPulling() {
        try {
            if (!groups.exists()) {
                groups.createNewFile();
            } else {
                groups.delete();
                groups.createNewFile();
            }
            pullAndWriteGroups();
            if (!db.exists()) {
                Log.d("tag", "db file not exists , creating....");
                db.createNewFile();
            } else {
                Log.d("tag", "db file exists , deleting + creating....");
                db.delete();
                db.createNewFile();
            }
            Log.d("tag", "pullingg db FFS");
            try {
                pullDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!version.exists()) {
                version.createNewFile();
            } else {
                version.delete();
                version.createNewFile();
            }
            writeVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pullDatabase() throws Exception {
        URLConnection conn;
        Log.d("tag", "onDownload....path is: " + path);
        URL url = new URL("http://128.199.34.71/greestory/questions.db");

        conn = url.openConnection();

        int contentLength = conn.getContentLength();
        DataInputStream in = new DataInputStream(conn.getInputStream());

        Log.d("tag", "Buffering the received stream(size=" + contentLength);
        byte[] buffer;
        if (contentLength != -1) {
            buffer = new byte[contentLength];
            in.readFully(buffer);
            in.close();
        } else {
            return;
        }

        if (buffer.length > 0) {
            Log.d("tag", "onDownload. Writing file to files dir,");
            DataOutputStream out;
            FileOutputStream fos = new FileOutputStream(db);

            Log.d("tag", "Writing from buffer to the new file.." + db.getName());
            out = new DataOutputStream(fos);
            out.write(buffer);
            out.flush();
            out.close();

            QuestionGrabber.db.close();
            QuestionGrabber.db = openOrCreateDatabase("questions.db", Context.MODE_PRIVATE, null);
        }
    }

    private void pullAndWriteGroups() throws Exception {
        URLConnection conn;
        Log.d("tag", "groups onDownload....path is: " + path);
        URL url = new URL("http://128.199.34.71/greestory/groups.txt");

        conn = url.openConnection();

        int contentLength = conn.getContentLength();
        DataInputStream in = new DataInputStream(conn.getInputStream());

        Log.d("tag", "Buffering the received stream(size=" + contentLength);
        byte[] buffer;
        if (contentLength != -1) {
            buffer = new byte[contentLength];
            in.readFully(buffer);
            in.close();
        } else {
            return;
        }

        if (buffer.length > 0) {
            Log.d("tag", "onDownload. Writing groups to group.txt,");
            DataOutputStream out;
            FileOutputStream fos = new FileOutputStream(groups);

            Log.d("tag", "Writing from buffer to the new file.." + db.getName());
            out = new DataOutputStream(fos);
            out.write(buffer);
            out.flush();
            out.close();
        }
    }

    private void writeVersion() {
        Log.d("tag", "WRITING VERSION");
        try {
            FileWriter fw = new FileWriter(version);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(grabVersion()) + "\n");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readVersion() {
        BufferedReader br = null;
        Log.d("tag", "READING VERSION");

        if(!version.exists()){
            return "0";
        }

        try {
            br = new BufferedReader(new FileReader(version));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            String everything = sb.toString();
            br.close();
            Log.d("tag", "reading version :" + everything);
            return everything;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {

        }
    }

    private int grabVersion() {
        try {
            Log.d("tag", "DOWNLOADING VERSION");
            Socket clientSocket = new Socket("128.199.34.71", 1235);
            InputStream is = clientSocket.getInputStream();
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
            pw.print("get-version");
            pw.flush();
            String ret = "";
            String read;
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            while ((read = bis.readLine()) != null) {
                Log.d("tag", "Reading from get-version:" + read);
                ret = read;
            }
            clientSocket.close();
            Log.d("tag", "version grabbed:" + ret);
            return Integer.valueOf(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
