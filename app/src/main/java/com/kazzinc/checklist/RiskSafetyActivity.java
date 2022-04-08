package com.kazzinc.checklist;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RiskSafetyActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(RiskSafetyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_safety);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title="";
        Bundle arguments = getIntent().getExtras();
        try {
            title = arguments.get("Title").toString();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        getSupportActionBar().setTitle(title);
        //setTitle(title);

        TextView tvText = (TextView) findViewById(R.id.tvText);
        tvText.setMovementMethod(LinkMovementMethod.getInstance());

        sqlLiteDatabase.open(RiskSafetyActivity.this);

        String selectRS = "SELECT Risk, Safety, Instruction FROM RiskSafety";
        try {
            Cursor cursorRS = sqlLiteDatabase.database.rawQuery(selectRS, null);
            if (null != cursorRS) {
                if (cursorRS.moveToFirst()) {
                    do {
                        switch (title) {
                            case "Риски":
                                tvText.setText(cursorRS.getString(0));
                                break;
                            case "Меры безопасности":
                                tvText.setText(cursorRS.getString(1));
                                break;
                            case "Инструкции":
                                tvText.setText(Html.fromHtml(cursorRS.getString(2)));
                                break;
                        }
                    } while (cursorRS.moveToNext());
                }
            }
        }
        catch (Exception e)
        {
            Log.d("Alexey",e.getMessage());
        }

        sqlLiteDatabase.close();

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

    public void setTitle(String title){
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorDarkLight4));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(textView);
    }
}
