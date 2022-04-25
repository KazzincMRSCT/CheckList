package com.kazzinc.checklist.Chat;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;

public class ChatNewDialog extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new_dialog);
        getSupportActionBar().setTitle("Новый диалог");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayout rContainer = (LinearLayout) findViewById(R.id.llCont);


        try {
            String selectQuery;

            sqlLiteDatabase.open(this);
            selectQuery = "SELECT (SELECT DISTINCT UserName FROM Chat) as UserName, (SELECT DISTINCT Message FROM Chat ORDER BY Id DESC) as Message, (SELECT DISTINCT DateTime FROM Chat ORDER BY DateTime DESC) as DateTime, (SELECT DISTINCT UserTabNum FROM Chat) as UserTabNum, (SELECT DISTINCT Status FROM Chat ORDER BY Status DESC) as Status, (SELECT DISTINCT Deleted FROM Chat ORDER BY Deleted DESC) as Deleted";
            //selectQuery = "SELECT * FROM Chat";

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    //int chatId = cursor.getInt(0);

                    int chatUserTabNum = cursor.getInt(3);

                    String chatUserName = cursor.getString(0);

                    String chatDateTime = cursor.getString(2);

                    String chatMsg = cursor.getString(1);

                    int chatStatus = cursor.getInt(4);


                    final CardView cw = new CardView(this);
                    LinearLayout linearLayout2 = new LinearLayout(this);
                    //cw.setId(chatUserTabNum);
                    //cw.setTransitionName(date + ";" + workplace);
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
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);

                    linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout2.setLayoutParams(layoutParams1);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20,20,20,20);

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

                    ll.addView(linearLayout2 );

                    TextView tvEndMsg = new TextView(this);
                    tvEndMsg.setText(Html.fromHtml("<font color='#979797'> " + chatMsg + "</font>"), TextView.BufferType.SPANNABLE);
                    tvEndMsg.setTextSize(18);

                    ll.addView(tvEndMsg);


                    cw.addView(ll);

                    rContainer.addView(cw, layoutParams);

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), ChatMain.class);
                finish();
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}