package com.kazzinc.checklist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotifyActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        getSupportActionBar().setTitle("Уведомление");

        TextView tvKeyCaption = (TextView) findViewById(R.id.tvKeyCaption);
        TextView tvText = (TextView) findViewById(R.id.tvNotifyText);
        TextView tvDate = (TextView) findViewById(R.id.tvNotifyDate);

        sqlLiteDatabase.open(getApplicationContext());

        String selectQuery = "SELECT * FROM Notification WHERE NotifyIsAlarm ISNULL";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        String Text ="";
        String Date = "";
        String Type ="";

        if (cursor.moveToFirst()) {
            do {
                Text = cursor.getString(1);
                Type = cursor.getString(4);
                Date = cursor.getString(2);
                String updateQuery = "UPDATE Notification SET NotifyIsAlarm=1 WHERE NotifyId='" + cursor.getString(0) + "'";
                sqlLiteDatabase.database.execSQL(updateQuery);

            } while (cursor.moveToNext());
        }

        tvKeyCaption.setText(Type);
        tvDate.setText(Date);
        tvText.setText(Text.replace(";","\n\n"));

        Button btnClose = (Button) findViewById(R.id.btnNotifyClose);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }
}