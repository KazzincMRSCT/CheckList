package com.kazzinc.checklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class VideoLesson extends AppCompatActivity {
    private Button btnLessonMSTems;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    LinearLayout cont;

    private List<String> getAllHelpBtn() {
        List<String> list = new ArrayList<>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM HelpInUseApps";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(1)+"@"+cursor.getString(2));
                    Log.d("Alexey", "lesson array cursor " + cursor.getString(1)+"  "+cursor.getString(2));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lesson);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Обучающие видео ролики");

        ScrollView scrollCont = findViewById(R.id.scrollCont);
        LinearLayout llCont = findViewById(R.id.llCont);


        List list = getAllHelpBtn();

        //генерируем кнопки
        for(int i=0; i<list.size();i++){

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            Log.d("Alexey", "lesson array btn " +list.get(i));

            Button btn = new Button(this);
            btn.setLayoutParams(layoutParams);
            btn.setText(String.valueOf(list.get(i)).split("@")[0]);
            btn.setTextColor(Color.WHITE);
            btn.setClickable(true);
            btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            btn.setBackgroundResource(R.drawable.btn_border_bottom);
            btn.setFocusable(true);
            btn.setHapticFeedbackEnabled(true);
            btn.setAllCaps(true);

            ll.addView(btn);

            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(String.valueOf(list.get(finalI)).split("@")[1]));
                    startActivity(intent);
                }
            });

            llCont.addView(ll);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}