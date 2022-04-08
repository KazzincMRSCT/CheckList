package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MotoActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;

    String UserDate;
    int UserShift;
    String MsgText;
    String FinalEquipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto);

        loadUserInfo();

        Bundle arguments = getIntent().getExtras();

        String equipment = "";
        final String fEquipment;
        String hour = "";
        String date = "";

        try {
            equipment = arguments.get("EquipmentName").toString();
            date = arguments.get("Date").toString();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        fEquipment = equipment.toUpperCase();

        TextView tw = (TextView) findViewById(R.id.textView);
        tw.setText(equipment);

        LinearLayout llAnkerSolo = (LinearLayout) findViewById(R.id.AnkerSolo);
        if ((fEquipment.contains("АНКЕР"))||(fEquipment.contains("SOLO"))||(fEquipment.contains("СОЛО")))
            llAnkerSolo.setVisibility(View.VISIBLE);
        else
            llAnkerSolo.setVisibility(View.GONE);

        LinearLayout llBoomer = (LinearLayout) findViewById(R.id.Boomer);
        if ((fEquipment.contains("BOOMER"))||(fEquipment.contains("БУМЕР")))
            llBoomer.setVisibility(View.VISIBLE);
        else
            llBoomer.setVisibility(View.GONE);

        Button btnOK = (Button) findViewById(R.id.btnOK);
        final String finalEquipment = equipment;
        FinalEquipment = equipment;

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText motoDVS = (EditText) findViewById(R.id.etDVS);
                String MotoDVS = motoDVS.getText().toString();

                EditText motoCompress = (EditText) findViewById(R.id.etCompressor);
                String MotoCompress = motoCompress.getText().toString();

                EditText motoPerfor = (EditText) findViewById(R.id.etPedforator);
                String MotoPerfor = motoPerfor.getText().toString();

                EditText motoMaslo = (EditText) findViewById(R.id.etMaslo);
                String MotoMaslo = motoMaslo.getText().toString();

                EditText motoLeft = (EditText) findViewById(R.id.etLeftMaslo);
                String MotoLeft = motoLeft.getText().toString();

                EditText motoRight = (EditText) findViewById(R.id.etRightMaslo);
                String MotoRight = motoRight.getText().toString();

                boolean check = true;


                if (!isNumber(MotoDVS) || MotoDVS == null || MotoDVS.isEmpty())
                    check = false;

                if ((fEquipment.contains("АНКЕР"))||(fEquipment.contains("SOLO"))||(fEquipment.contains("СОЛО")))
                {
                    if (!isNumber(MotoCompress) || MotoCompress == null || MotoCompress.isEmpty())
                        check = false;
                    if (!isNumber(MotoPerfor) || MotoPerfor == null || MotoPerfor.isEmpty())
                        check = false;
                    if (!isNumber(MotoMaslo) || MotoMaslo == null || MotoMaslo.isEmpty())
                        check = false;
                }
                if ((fEquipment.contains("BOOMER"))||(fEquipment.contains("БУМЕР")))
                {
                    if (!isNumber(MotoLeft) || MotoLeft == null || MotoLeft.isEmpty())
                        check = false;
                    if (!isNumber(MotoRight) || MotoRight == null || MotoRight.isEmpty())
                        check = false;
                }

                if (!check) {
                    MsgText = "Введите числовое значение";
                    showDialog(MotoActivity.this);
                }
                else {

                    /*if (Float.valueOf(hour)<=lastHour())
                    {
                        MsgText = "Некорректное значение моточасов";
                        showDialog(MotoActivity.this);
                    }
                    else {*/

                        /*if (Float.valueOf(hour)> lastHour() + 100) {
                            MsgText = "Некорректное значение моточасов";
                            showDialog(MotoActivity.this);
                        }
                        else {*/
                            sqlLiteDatabase.open(MotoActivity.this);

                            //String deleteQuery = "DELETE FROM Moto WHERE (MotoDate='" + UserDate + "' AND MotoShift='" + UserShift + "' AND MotoEquipName='" + finalEquipment + "');";
                            String deleteQuery = "DELETE FROM Moto";
                            sqlLiteDatabase.database.execSQL(deleteQuery);
                            String updateQuery = "INSERT INTO Moto (MotoEquipName, MotoDate, MotoShift, MotoDVS, MotoCompress, MotoPerfor, MotoMaslo, MotoLeft, MotoRight) VALUES ('" + finalEquipment + "','" + UserDate + "','" + UserShift + "','" + MotoDVS+ "','" + MotoCompress + "','" + MotoPerfor + "','" + MotoMaslo + "','" + MotoLeft + "','" + MotoRight + "' )";
                            sqlLiteDatabase.database.execSQL(updateQuery);
                            sqlLiteDatabase.close();

                            saveNeedLoadParam(3);

                            Intent intent = new Intent(MotoActivity.this, QuestionsActivity.class);
                            startActivity(intent);
                            finish();
                        //}
                    //}
                }
            }
        });
    }

    private float lastHour()
    {
        float result = 0;

        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT  MAX(MotoHours) FROM Moto WHERE (MotoDate='" + UserDate + "' AND MotoShift='" + UserShift + "' AND MotoEquipName='" + FinalEquipment + "')";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    result = Float.valueOf(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }
        return result;
    }

    private boolean isNumber(String value)
    {
        boolean result = false;

        String regexStr = "^[0-9]*$";

        if(value.trim().matches(regexStr))
        {
            result = true;
        }

        return result;
    }

    private void showDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle(MsgText)
                //.setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        String userId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserDate = sPref.getString("UserDateNewFormat","");
        UserShift = sPref.getInt("UserShift",2);
    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

}
