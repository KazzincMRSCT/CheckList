package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GSMReffilEdit extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    String date="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_reffil_edit);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnDel = (Button) findViewById(R.id.btnDelete);

        EditText edDT = (EditText) findViewById(R.id.etDT);
        EditText etSAE15W40 = (EditText) findViewById(R.id.etSAE15W40);
        EditText etSAE50 = (EditText) findViewById(R.id.etSAE50);
        EditText etSAE10W40 = (EditText) findViewById(R.id.etSAE10W40);
        EditText et46 = (EditText) findViewById(R.id.et46);
        EditText et86 = (EditText) findViewById(R.id.et86);


        Bundle arguments = getIntent().getExtras();
        try {
            date = arguments.get("Date").toString();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        Log.d("Alexey","Del date " + date);

        sqlLiteDatabase.open(getApplicationContext());
        String selectQuery = "SELECT Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, T86 FROM GSM WHERE Date='" + date + "'";
        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        Log.d("Alexey","Del date " + selectQuery);
        //Обновляем мнформацию по наряд-заданиям (подпись)
        if (cursor.moveToFirst()) {
            do {
                edDT.setText(cursor.getString(6));
                etSAE15W40.setText(cursor.getString(7));
                etSAE50.setText(cursor.getString(8));
                etSAE10W40.setText(cursor.getString(9));
                et46.setText(cursor.getString(10));
                et86.setText(cursor.getString(15));
            } while (cursor.moveToNext());
        }
        sqlLiteDatabase.close();

        btnDel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showAddItemDialog(GSMReffilEdit.this);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent (GSMReffilEdit.this, GSMResultActivity.class);
                startActivity (intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddItemDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Подтвердите")
                .setMessage("Удалить запись?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlLiteDatabase.open(getApplicationContext());
                        String updateQuery = "UPDATE GSM SET Deleted=1 WHERE Date='" + date + "'";
                        sqlLiteDatabase.database.execSQL(updateQuery);
                        sqlLiteDatabase.close();

                        Intent intent = new Intent (GSMReffilEdit.this, GSMResultActivity.class);
                        startActivity (intent);
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (GSMReffilEdit.this, GSMResultActivity.class);
                        startActivity (intent);
                        finish();
                    }
                })
                .create();
        dialog.show();
    }
}
