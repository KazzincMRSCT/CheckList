package com.kazzinc.checklist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class improvement extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improvement);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Список предложений");

        GetData();
    }

    private void GetData(){
        LinearLayout rContainer = (LinearLayout) findViewById(R.id.resultContainerImprove);

        rContainer.removeAllViews();

        String selectQuery = "SELECT Id, DateTime, Title FROM Improvement WHERE IsDeleted=0 ORDER BY DateTime3 DESC";

        sqlLiteDatabase.open(improvement.this);

        try
        {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if(null != cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        int Id = cursor.getInt(0);
                        String DateTime = cursor.getString(1);
                        String Title = cursor.getString(2);

                        CardView cw = new CardView(this);
                        cw.setId(Id);
                        cw.setTransitionName(String.valueOf(Id));

                        /*cw.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), AnswerResultActivity.class);

                                String[] separated = cw.getTransitionName().split(";");

                                intent.putExtra("UserId", UserId);
                                intent.putExtra("Date", separated[0]);
                                intent.putExtra("WorkPlace", separated[1]);

                                startActivity(intent);
                                //intent.setDataAndType(Uri.parse(path + iv.getTransitionName()), "image/*");
                            }

                        });*/

                        LinearLayout ll = new LinearLayout(this);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setPadding(20,20,20,20);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(10, 0, 10, 20);

                        TextView twDate = new TextView(this);

                        twDate.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        twDate.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twDate.setText(Html.fromHtml("<strong>Дата/время: </strong><font color='#FFFFFF'>" + DateTime + "</font>"));
                        twDate.setTextSize(18);

                        TextView twTitle = new TextView(this);
                        twTitle.setText(Html.fromHtml("<strong>Тема: </strong><font color='#FFFFFF'>" + Title + "</font>"));
                        twTitle.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twTitle.setTextSize(18);

                        cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight1));
                        twDate.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        twTitle.setTextColor(getResources().getColor(R.color.colorDarkLight3));

                        ll.addView(twDate);
                        ll.addView(twTitle);
                        cw.addView(ll);

                        rContainer.addView(cw, layoutParams);

                    } while (cursor.moveToNext());
                }
            }
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);

        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;

            case R.id.addImprovement:
                Intent intent = new Intent(improvement.this, improvement_step1.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}