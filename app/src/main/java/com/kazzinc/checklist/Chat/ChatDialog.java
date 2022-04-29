package com.kazzinc.checklist.Chat;

import android.content.Intent;
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

import com.kazzinc.checklist.MenuActivity;
import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatDialog extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    Handler timerHandler = new Handler();

    private String userName = "";
    private int chatUserTabNum;
    //private int chatStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);


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

            inboxMg(chatUserTabNum);
            SendMsg();

        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        getSupportActionBar().setTitle(userName);

        doTask();

    }

    void doTask()
    {
        timerHandler.postDelayed(timerRunnable, 0);
    }


    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            EditText etSendMsg = findViewById(R.id.etSendMsg);
            View b = findViewById(R.id.btnSendMsg);

            if (etSendMsg.getText().toString().trim().length() > 0) {
                b.setVisibility(View.VISIBLE);

            }else {
                b.setVisibility(View.GONE);
            }

            timerHandler.postDelayed(this, 1000);
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

    //это расположение для входящих
    private void inboxMg(int chatUserTabNum){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "";

            selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

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


                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(llp);

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(llp);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.parseColor("#E1E2E5"));
                    tv.setText(cursor.getString(4));

                    ll.addView(tv);

                    cw.addView(ll);

                    LinearLayout ll1 = new LinearLayout(this);

                    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    //для входящих сообщений
                    if(cursor.getInt(5)==1){
                        layoutParams.gravity=Gravity.LEFT;
                        cw.setBackgroundResource(R.drawable.layout_bg_gray);
                        //для исходящих сообщений
                    }else if(cursor.getInt(5)==2){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;
                    }

                    ll1.addView(cw, llp1);

                    llCont.addView(ll1,layoutParams);

                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //это расположение для исходящих
    private void SendMg(int chatUserTabNum){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum+" AND Status=2";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    final CardView cw = new CardView(this);
                    cw.setId(id);
                    cw.setBackgroundResource(R.drawable.layout_bg_blue);
                    cw.setClipToOutline(true);

                    LinearLayout llCont = findViewById(R.id.llCont);

                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 15, 0, 0);


                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(llp);

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(llp);
                    tv.setTextSize(20);
                    tv.setTextColor(Color.parseColor("#E1E2E5"));
                    tv.setText(cursor.getString(4));

                    ll.addView(tv);

                    cw.addView(ll);

                    llCont.setGravity(Gravity.RIGHT);

                    llCont.addView(cw,layoutParams);

                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
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


    private void setMsgChat(String msg, int tubnum){
        LinearLayout linearLayout = findViewById(R.id.llCont);
        try {
            sqlLiteDatabase.open(this);
            String updateQuery = "INSERT INTO Chat (UserTabNum, UserName, DateTime, Message, Status, Deleted) VALUES ("+tubnum+", '"+userName+"', '"+simpleDateFormat.format(new Date())+"', '"+msg+"', 2, 0)";
            sqlLiteDatabase.database.execSQL(updateQuery);
            linearLayout.removeAllViews();
            inboxMg(chatUserTabNum);
        } catch (SQLException e) {
        }finally {
            sqlLiteDatabase.close();
        }
    }
}