package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class improvement_step1 extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    String EmplId, EmplName, EmplArea, EmplProff, DateTime, Title, Offer, Id = "";

    EditText etImproveTitle;
    EditText etOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improvement_step1);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Шаг 1 из 2");

        etImproveTitle = (EditText) findViewById(R.id.etImproveTitle);
        etOffer = (EditText) findViewById(R.id.etOffer);

        loadUserInfo();
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        EmplId = sPref.getString("UserId","");
        EmplName = sPref.getString("UserName","");
        EmplArea = sPref.getString("UserAreaId","");
        EmplProff = sPref.getString("UserRole","");
        DateTime = sPref.getString("UserDateNewFormat","");
    }

    private void SaveStep1()
    {
        Date c = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = df.format(c);

        SimpleDateFormat df2 = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String formattedDate2 = df2.format(c);

        SimpleDateFormat df3 = new SimpleDateFormat("ddMMmmss");
        String formattedDate3 = df3.format(c);

        SimpleDateFormat df4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate4 = df4.format(c);


        Id=formattedDate3;//formattedDate.replace(".","").replace(":","").replace(" ", "");

        Title = etImproveTitle.getText().toString();
        if (Title.length()==0)
            Title = "(Без темы)";

        Offer = etOffer.getText().toString();

        sqlLiteDatabase.open(improvement_step1.this);

        String insertQuery = "INSERT INTO Improvement (Id, EmplId, EmplName, EmplArea, EmplProff, DateTime, DateTime2, DateTime3, Title, Offer, IsDeleted) VALUES ('"+ Id + "','" + EmplId + "','" + EmplName + "','" + EmplArea + "','" + EmplProff + "','" + formattedDate + "','" + formattedDate2 + "','" + formattedDate4 + "','" + Title + "','" + Offer+ "','0')";

        sqlLiteDatabase.database.execSQL(insertQuery);

        sqlLiteDatabase.close();
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
                SaveStep1();
                Intent intent = new Intent(improvement_step1.this, improvement_step2.class);
                intent.putExtra("improveId", Id);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}