package com.kazzinc.checklist.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.MenuActivity;
import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ChatDialog extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Handler timerHandler = new Handler();

    private String userName = "";
    private int chatUserTabNum;

    SharedPreferences sPref;
    String UserId;
    String UserRole;
    String UserName;
    //private int chatStatus;

    Timer timer = new Timer();

    private int ErrorCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_dialog);

            EditText etSendMsg = findViewById(R.id.etSendMsg);
            Button btnSendMsg = findViewById(R.id.btnSendMsg);

            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (etSendMsg.getText().toString().trim().length() > 0) {
                        setMsgChat(String.valueOf(etSendMsg.getText()), chatUserTabNum);
                        etSendMsg.getText().clear();

                        ScrollView svCont = findViewById(R.id.svCont);
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
            });

            loadUserInfo();

            ErrorCount = 1;

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Bundle arguments = getIntent().getExtras();

            ScrollView svCont = findViewById(R.id.svCont);

            svCont.post(new Runnable() {
                @Override
                public void run() {
                    svCont.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            try {
                userName = arguments.get("userName").toString();
                chatUserTabNum = Integer.parseInt(String.valueOf(arguments.get("chatUserTabNum")));
                //chatStatus = Integer.parseInt(String.valueOf(arguments.get("chatStatus")));


            } catch (Exception e) {
                Log.d("Alexey", e.getMessage());
            }

            getSupportActionBar().setTitle(userName);


            doTask();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Alexey", "Chat Dialog (Global Error 1): " + e.getMessage());
        }


    }



    void doTask()
    {
        try {
            Log.d("Alexey", "Chat Dialog (Search Error 1)");
            timerHandler.postDelayed(timerRunnable, 0);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            try {
                Log.d("Alexey", "Chat Dialog (Search Error 2)");
                LinearLayout linearLayout = findViewById(R.id.llCont);
                linearLayout.removeAllViews();

                inboxMg(chatUserTabNum);

                ScrollView svCont = findViewById(R.id.svCont);

                svCont.post(new Runnable() {
                    @Override
                    public void run() {
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                Log.d("Alexey", "Chat Dialog (Search Error End)");

                timerHandler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Alexey", "Chat Dialog (Global Error): " + e.getMessage());
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                intent.putExtra("inputPage", "chat");
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void inboxMg(int chatUserTabNum){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "";

            ErrorCount++;
            Log.d("Alexey", "Chat Dialog (Search Error 3) " + ErrorCount);

            selectQuery = "SELECT * FROM Chat WhERE UserTNFrom="+chatUserTabNum + " OR UserTNTo="+chatUserTabNum;
            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("Alexey", "Chat Dialog (Search Error 31)");
                    final CardView cw = new CardView(this);
                    cw.setId(id);
                    cw.setClipToOutline(true);

                    LinearLayout llCont = findViewById(R.id.llCont);

                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    //layoutParams.gravity=Gravity.RIGHT;
                    //llp.gravity=Gravity.RIGHT;

                    layoutParams.setMargins(15, 15, 15, 0);

                    Log.d("Alexey", "Chat Dialog (Search Error 32)");

                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(llp);

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(llp);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.parseColor("#E1E2E5"));
                    tv.setText(cursor.getString(6));

                    ll.addView(tv);

                    cw.addView(ll);

                    Log.d("Alexey", "Chat Dialog (Search Error 33)");

                    LinearLayout ll1 = new LinearLayout(this);

                    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    //для входящих сообщений
                    /*if(cursor.getInt(7)==1){
                        layoutParams.gravity=Gravity.LEFT;
                        cw.setBackgroundResource(R.drawable.layout_bg_gray);
                        //для исходящих сообщений
                    }else if(cursor.getInt(7)==2){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;
                    }
*/
                    Log.d("Alexey", "Chat Dialog (Search Error 34)");
                    Log.d("Sergey", "Sergey " + cursor.getString(4));

                    cw.setBackgroundResource(R.drawable.layout_bg_gray);

                    if(cursor.getString(2).equals(UserName)){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;
                    }

                    ll1.addView(cw, llp1);

                    llCont.addView(ll1,layoutParams);
                    Log.d("Alexey", "Chat Dialog (Search Error 35)");
                    id++;

                } while (cursor.moveToNext());
            }
            Log.d("Alexey", "Chat Dialog (Search Error 36)");
            sqlLiteDatabase.close();
            Log.d("Alexey", "Chat Dialog (Search Error 4)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendMsg(){
        EditText etSendMsg = findViewById(R.id.etSendMsg);
        Button btnSendMsg = findViewById(R.id.btnSendMsg);
        ScrollView svCont = findViewById(R.id.svCont);

        etSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView svCont = findViewById(R.id.svCont);

                svCont.post(new Runnable() {
                    @Override
                    public void run() {
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etSendMsg.getText().toString().trim().length() > 0) {
                    setMsgChat(String.valueOf(etSendMsg.getText()), chatUserTabNum);
                    etSendMsg.getText().clear();
                }
            }
        });
    }

    private String getTubNum(){
        try {
            sqlLiteDatabase.open(getApplicationContext());

            String selectQuery1 = "SELECT * FROM VpnConnection WhERE Id=2";

            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery1, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("Alexey", "LoginActivity8765434 "+cursor.getString(1));
                    return cursor.getString(1);
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setMsgChat(String msg, int tubnum){
        try {
            LinearLayout linearLayout = findViewById(R.id.llCont);
            sqlLiteDatabase.open(this);
            String updateQuery = "INSERT INTO Chat (UserTNFrom, UserNameFrom, UserTNTo, UserNameTo, DateTime, Message, Status, Deleted) VALUES (" + getTubNum() + ",'" + UserName + "'," + chatUserTabNum + ",'"+ userName +"','"+simpleDateFormat.format(new Date())+"', '"+msg+"', 2, 0)";
            sqlLiteDatabase.database.execSQL(updateQuery);
            linearLayout.removeAllViews();
            inboxMg(chatUserTabNum);
        } catch (SQLException e) {
            Log.d("Alexey", "Chat Dialog (Global Error 3): " + e.getMessage());
        }finally {
            sqlLiteDatabase.close();
        }
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }
}