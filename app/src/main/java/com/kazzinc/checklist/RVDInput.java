package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RVDInput extends AppCompatActivity {

    SharedPreferences sPref;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    String Equipment = "";
    EditText etOldRVD;
    EditText etNewRVD;
    EditText etMotoHours;
    EditText etSpecHours;
    EditText etPVDPlace;
    EditText etPVDWhy;

    int valueOldRVD, valueNewRVD, valueMotoHours, valueSpecHours;
    String valuePVDPlace = "";
    String valuePVDWhy = "";

    SimpleDateFormat df;
    Date c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r_v_d_input);

        setTitle("Новая запись");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etOldRVD = (EditText)findViewById(R.id.etOldRVD);
        etNewRVD = (EditText)findViewById(R.id.etNewRVD);
        etMotoHours = (EditText)findViewById(R.id.etMotoHours);
        etSpecHours = (EditText)findViewById(R.id.etSpecHours);
        etPVDPlace = (EditText)findViewById(R.id.etPVDPlace);
        etPVDWhy = (EditText)findViewById(R.id.etPVDWhy);

        etPVDPlace.setHorizontallyScrolling(false);
        etPVDWhy.setHorizontallyScrolling(false);
        etPVDPlace.setMaxLines(4);
        etPVDWhy.setMaxLines(4);

        valueOldRVD=valueNewRVD=valueMotoHours=valueSpecHours = 0;

        c = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        //df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


        sqlLiteDatabase.open(RVDInput.this);
        String selectQuery = "SELECT TaskEquipName FROM Task";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Equipment  = cursor.getString(0);
            }
            while (cursor.moveToNext());
        }
        sqlLiteDatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GSMSave:

                try {
                    if (etOldRVD.getText().toString().trim().length() > 0)
                        valueOldRVD = Integer.parseInt(etOldRVD.getText().toString());
                    if (etNewRVD.getText().toString().trim().length() > 0)
                        valueNewRVD = Integer.parseInt(etNewRVD.getText().toString());
                    if (etMotoHours.getText().toString().trim().length() > 0)
                        valueMotoHours = Integer.parseInt(etMotoHours.getText().toString());
                    if (etSpecHours.getText().toString().trim().length() > 0)
                        valueSpecHours = Integer.parseInt(etSpecHours.getText().toString());
                    if (etPVDPlace.getText().toString().trim().length() > 0)
                        valuePVDPlace = etPVDPlace.getText().toString();
                    if (etPVDWhy.getText().toString().trim().length() > 0)
                        valuePVDWhy = etPVDWhy.getText().toString();

                    sqlLiteDatabase.open(RVDInput.this);

                    String insertQuery = "INSERT INTO RVD (DateEvent, Date, Shift, Equipment, OldNumber, NewNumber, MotoHours, SpecialHours, Place, Reason, Deleted, SendToServer) VALUES ('" + df.format(c) + "','" + GetUserDate() + "','" + GetUserShift() + "','" + Equipment + "','" + valueOldRVD +"','" + valueNewRVD  + "','" + valueMotoHours + "','" + valueSpecHours + "','" + valuePVDPlace + "','" + valuePVDWhy + "','0','0')";
                    //String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer) VALUES ('" + df.format(c) +"','"  + GetUserDate() +  "','" + GetUserShift() + "','" + equipOut + "','" + equipIn +"','" + GetUserName()  + "','','" + valueDT + "','" + valueSAE15W40 + "','" + valueSAE50 + "','" + valueSAE10W40+ "','" + value46 +"','0','0')";

                    Log.d("Alexey", "RVD: " + insertQuery);

                    sqlLiteDatabase.database.execSQL(insertQuery);

                    sqlLiteDatabase.close();

                    saveNeedLoadParam(9);

                    Intent intent = new Intent (getApplication(), MenuActivity.class);
                    intent.putExtra("inputPage", "rvd");
                    startActivity (intent);
                    finish();
                    showBottomToastShort("Сохранено");

                }
                catch (Exception e){
                    Log.d("QweReq", e.getMessage());
                }

                return true;
            case android.R.id.home:
                Intent intent = new Intent (RVDInput.this, MenuActivity.class);
                intent.putExtra("inputPage", "rvd");
                startActivity (intent);
                finish();
                return true;
            /*case R.id.help:
                showHelp();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showBottomToastShort(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(RVDInput.this, msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
            }
        });
    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    private String GetUserName()
    {
        SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        return sPref.getString("UserName","");
    }

    private int GetUserShift()
    {
        SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        return sPref.getInt("UserShift",0);
    }
    private String GetUserDate()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userDate = sPref.getString("UserDateNewFormat","");
        return userDate;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent (RVDInput.this, MenuActivity.class);
        intent.putExtra("inputPage", "rvd");
        startActivity (intent);
        finish();
    }
}