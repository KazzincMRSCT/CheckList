package com.kazzinc.checklist.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.MenuActivity;
import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;

public class ChatMain extends AppCompatActivity {
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    String UserId;
    String UserRole;
    String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        loadUserInfo();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout rContainer = (LinearLayout) findViewById(R.id.llCont);


        try {
            String selectQuery,selectQuery1;

            sqlLiteDatabase.open(this);
            selectQuery = "SELECT DISTINCT UserTabNum FROM Chat";

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);


            if (cursor.moveToFirst()) {
                do {

                    selectQuery1 = "SELECT UserTabNum, UserName,DateTime, Message, Status From Chat WHERE UserTabNum=" + cursor.getInt(0) +" ORDER BY DateTime DESC LIMIT 1";

                    Log.d("Alexey", "12345 создан " + selectQuery1);

                    Cursor cursor1 = sqlLiteDatabase.database.rawQuery(selectQuery1, null);

                    if (cursor1.moveToFirst()) {
                        do {

                            //int chatId = cursor.getInt(0);

                            int chatUserTabNum = cursor1.getInt(0);

                            String chatUserName = cursor1.getString(1);

                            String chatDateTime = cursor1.getString(2);

                            String chatMsg = cursor1.getString(3);

                            int chatStatus = cursor1.getInt(4);


                            final CardView cw = new CardView(this);
                            LinearLayout linearLayout2 = new LinearLayout(this);
                            cw.setCardBackgroundColor(Color.parseColor("#444446"));

                            cw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), ChatDialog.class);
                                    intent.putExtra("userName", chatUserName);
                                    intent.putExtra("chatUserTabNum", chatUserTabNum);
                                    intent.putExtra("chatMsg", chatMsg);
                                    intent.putExtra("chatStatus", chatStatus);
                                    finish();
                                    startActivity(intent);
                                }
                            });

                            LinearLayout ll = new LinearLayout(this);


                            LinearLayout.LayoutParams layoutParamsTV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);

                            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout2.setLayoutParams(layoutParams1);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            ll.setOrientation(LinearLayout.VERTICAL);
                            ll.setPadding(20, 20, 20, 20);

                            layoutParams.setMargins(0, 10, 0, 0);

                            TextView tvName = new TextView(this);
                            tvName.setText(Html.fromHtml("<font color='#E1E2E5'> " + chatUserName + "</font>"), TextView.BufferType.SPANNABLE);
                            tvName.setTypeface(ResourcesCompat.getFont(this, R.font.exo_2_light));
                            tvName.setTextSize(21);
                            tvName.setLayoutParams(layoutParamsTV);
                            linearLayout2.addView(tvName);

                            TextView tvDate = new TextView(this);
                            tvDate.setText(Html.fromHtml("<font color='#E1E2E5'> " + chatDateTime + "</font>"), TextView.BufferType.SPANNABLE);
                            tvDate.setTextSize(15);
                            tvDate.setLayoutParams(layoutParamsTV);
                            tvDate.setGravity(Gravity.RIGHT);
                            linearLayout2.addView(tvDate);

                            ll.addView(linearLayout2);

                            TextView tvEndMsg = new TextView(this);
                            tvEndMsg.setText(Html.fromHtml("<font color='#979797'> " + chatMsg + "</font>"), TextView.BufferType.SPANNABLE);
                            tvEndMsg.setTextSize(18);

                            ll.addView(tvEndMsg);


                            cw.addView(ll);

                            rContainer.addView(cw, layoutParams);

                        } while (cursor1.moveToNext());
                    }
                } while (cursor.moveToNext());
            }


            sqlLiteDatabase.close();
        } catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }
        Button btnNewChat = findViewById(R.id.btnNewChat);

        btnNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatNewDialog.class);
                startActivity(intent);
            }
        });
    }


    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        getSupportActionBar().setTitle("Сообщения");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                intent.putExtra("inputPage", "task");
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}