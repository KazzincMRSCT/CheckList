package com.kazzinc.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class improvement_step2 extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    String Result, Id = "";

    EditText etImproveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improvement_step2);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Шаг 2 из 2");

        etImproveResult = (EditText) findViewById(R.id.etImproveResult);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null)
        {
            Id= arguments.getString("improveId");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);

        /*for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#4682B4")), 0,     spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;

            case R.id.Next:
                SaveStep2();

                Intent intent = new Intent(improvement_step2.this, improvement_step3.class);
                intent.putExtra("improveId", Id);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveStep2()
    {

        Result = etImproveResult.getText().toString();

        sqlLiteDatabase.open(improvement_step2.this);

        String updateQuery = "UPDATE Improvement SET Result = '" + Result + "', IsSendToServer='0' WHERE Id='" + Id + "'" ;

        sqlLiteDatabase.database.execSQL(updateQuery);

        sqlLiteDatabase.close();
    }
}