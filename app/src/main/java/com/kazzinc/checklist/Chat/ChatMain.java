package com.kazzinc.checklist.Chat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.kazzinc.checklist.SyncService;

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


        boolean result= isMyServiceRunning(SyncService.class);

        if (!result)
        {
            Intent serviceIntent = new Intent(this, SyncService.class);
            serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

            ContextCompat.startForegroundService(this, serviceIntent);
        }


        Log.d("Alexey", "ChatTest -10 ");

        try {
            String selectQuery,selectQuery1;

            sqlLiteDatabase.open(this);

            selectQuery = "SELECT DISTINCT  UserTNFrom as Contact From Chat WHERE UserTNFrom<>" + getTubNum() + " UNION SELECT DISTINCT UserTNTo as Contact From Chat WHERE UserTNTo<>" + getTubNum();

            Log.d("Alexey", "ChatTest 0 " + selectQuery);

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    int tabNum = cursor.getInt(0);

                    selectQuery1 = "SELECT UserTNFrom, UserNameFrom, UserTNTo, UserNameTo, DateTime, Message, Status From Chat WHERE UserTNFrom=" + tabNum + " union SELECT UserTNFrom, UserNameFrom, UserTNTo, UserNameTo, DateTime, Message, Status From Chat WHERE UserTNTo=" + tabNum + " ORDER BY DateTime DESC LIMIT 1";

                    Log.d("Alexey", "ChatTest 1 " + selectQuery1);

                    Cursor cursor1 = sqlLiteDatabase.database.rawQuery(selectQuery1, null);

                    if (cursor1.moveToFirst()) {
                        do {

                            //int chatId = cursor.getInt(0);

                            String chatUserTabNum1 = cursor1.getString(0);
                            String chatUserTabNum2 = cursor1.getString(2);
                            String chatUserTabNum;

                            if (chatUserTabNum1.equals(getTubNum()))
                                chatUserTabNum = chatUserTabNum2;
                            else
                                chatUserTabNum = chatUserTabNum1;

                            String chatUserName1 = cursor1.getString(1);

                            String chatUserName2 = cursor1.getString(3);

                            String chatUserName;

                            String chatDateTime = cursor1.getString(4);

                            String chatMsg = cursor1.getString(5);

                            int chatStatus = cursor1.getInt(6);

                            if (chatUserName1.equals(UserName))
                                chatUserName = chatUserName2;
                            else
                                chatUserName = chatUserName1;

                            final CardView cw = new CardView(this);
                            LinearLayout linearLayout2 = new LinearLayout(this);
                            cw.setCardBackgroundColor(Color.parseColor("#444446"));

                            String finalChatMsg = chatMsg;
                            cw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), ChatDialog.class);
                                    intent.putExtra("userName", chatUserName);
                                    intent.putExtra("chatUserTabNum", chatUserTabNum);
                                    intent.putExtra("chatMsg", finalChatMsg);
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
                            tvName.setText(Html.fromHtml("<font color='#E1E2E5'>"+ chatUserName +"</font>"), TextView.BufferType.SPANNABLE);
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

                            if (chatMsg.contains("img$"))
                                chatMsg = "Фото";

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
//                Intent myService = new Intent(ChatMain.this, SyncService.class);
//                stopService(myService);

                //проверка работы сервиса


                //Toast.makeText(ChatMain.this, "Service ... " + result, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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