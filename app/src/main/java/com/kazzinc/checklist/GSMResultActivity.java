package com.kazzinc.checklist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GSMResultActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_result);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Учёт ГСМ");
        getSupportActionBar().setTitle("Учёт ГСМ");
        /*LinearLayout btnNew = (LinearLayout) findViewById(R.id.btnNew);

        btnNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentPSO = new Intent (GSMResultActivity.this, GSMArea.class);
                startActivity (intentPSO);
            }

        });*/

        LinearLayout rContainer = (LinearLayout) findViewById(R.id.resultContainer);
        rContainer.removeAllViews();

        try {
            int id = 1;

            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT Date, EquipIn FROM GSM WHERE Deleted IS NULL or Deleted=0";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    final String date = cursor.getString(0);
                    String[] splitedHour = date.split("\\s+");
                    String[] splitedDate = splitedHour[0].split("-");
                    final String Date = splitedDate[2]+"."+splitedDate[1]+"."+splitedDate[0]+" " + splitedHour[1];

                    String Equipment = cursor.getString(1);
                    String ansColor = "#E1E2E5";
                    String textColor = "#E1E2E5";//"#979797";
                    String bgColor = "#444446";

                    final CardView cw = new CardView(this);
                    cw.setId(id);

                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    cw.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GSMResultActivity.this, GSMReffilEdit.class);
                            intent.putExtra("Date", date);
                            finish();
                            startActivity(intent);
                        }
                    });

                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20,20,20,20);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(10, 20, 10, 0);

                    TextView twDate = new TextView(this);

                    twDate.setText(Html.fromHtml("<font color='" + textColor + "'> "+ Date + "</font>"), TextView.BufferType.SPANNABLE);
                    twDate.setTypeface(ResourcesCompat.getFont(this, R.font.exo_2_light));
                    twDate.setTextSize(16);
                    ll.addView(twDate);

                    TextView twEqioment = new TextView(this);
                    twEqioment.setText(Html.fromHtml("<font color='#979797'>Оборудование:</font><font color='" + ansColor + "'> " + Equipment + "</font>"), TextView.BufferType.SPANNABLE);
                    twEqioment.setTypeface(ResourcesCompat.getFont(GSMResultActivity.this, R.font.cuprum));
                    twEqioment.setTextSize(16);
                    ll.addView(twEqioment);

                    cw.addView(ll);

                    rContainer.addView(cw, layoutParams);

                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(GSMResultActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_add_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GSMAdd:
                Intent intentPSO = new Intent (GSMResultActivity.this, GSMArea.class);
                startActivity (intentPSO);
                return true;
            case android.R.id.home:
                Intent intent = new Intent (GSMResultActivity.this, MenuActivity.class);
                startActivity (intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
