package com.kazzinc.checklist.Chat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatDialog extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private List<String> getAllHelpBtn() {
        List<String> list = new ArrayList<>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM Chat";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(2));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullDialog();

//        ScrollView scrollCont = findViewById(R.id.scrollCont);
//        LinearLayout llCont = findViewById(R.id.llCont);
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

    private void fullDialog(){
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM Chat WhERE id=1";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    getSupportActionBar().setTitle(cursor.getString(2));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}