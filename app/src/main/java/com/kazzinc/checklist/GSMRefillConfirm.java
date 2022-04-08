package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GSMRefillConfirm extends AppCompatActivity {
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_refill_confirm);

        final String[] spisok = getResources().getStringArray(R.array.prem);
        spinner = (Spinner) findViewById(R.id.spReason);

        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown_list, R.id.list_content1, spisok));

        Button btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    /*sqlLiteDatabase.open(getApplicationContext());

                    String updateQuery = "UPDATE GSM SET Confirmed=1";

                    Log.d("Alexey", "GSMвбазу: updateQuery - " + updateQuery);

                    sqlLiteDatabase.database.execSQL(updateQuery);
                    sqlLiteDatabase.close();*/

                    saveParam(0);

                    Intent intent = new Intent(GSMRefillConfirm.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e){
                    Log.d("QweReq", e.getMessage());
                }

            }
        });
    }
    SharedPreferences sPref;

    private void saveParam(int n)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("Notify", n);
        ed.commit();
    }
}
