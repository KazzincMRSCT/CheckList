package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DangerActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    String UserId;
    String UserDate;
    String WorkPlace;
    int UserShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger);

        final EditText dangerText = (EditText) findViewById(R.id.dangerText);
        dangerText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dangerText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final EditText controlToolsText = (EditText) findViewById(R.id.controlToolsText);
        controlToolsText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        controlToolsText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final EditText improvementText = (EditText) findViewById(R.id.improvementText);
        improvementText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        improvementText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        loadUserInfo();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat nDF = new SimpleDateFormat("MM.dd.yyyy hh:mm:ss");
        final String newFormattedDate = nDF.format(c);

        //нажимаем ГОТОВО
        ((Button)findViewById(R.id.btn_nextDanger)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    CheckBox cbIsSafety = (CheckBox) findViewById(R.id.cbIsSafety);
                    String SafetyResult = "Не безопасно";

                    if (cbIsSafety.isChecked())
                        SafetyResult = "Безопасно";

                    sqlLiteDatabase.open(DangerActivity.this);

                    //String selectQuery = "UPDATE Answer SET AnswerText='" + ansText + "', AnswerComment='" + comment + "', AnswerPhotos='"+ Photos +"' WHERE AnswerId='" + ansId+"'";
                    String insertQuery = "INSERT INTO CheckList (UserId, Date, Shift, Object, UserDateTime, Danger, ControlTools, Improvement, UserIsSafety) VALUES ('"+ UserId + "','" + UserDate + "','" + UserShift + "','" + WorkPlace +"','" + newFormattedDate  + "','" + dangerText.getText() + "','" + controlToolsText.getText() + "','" + improvementText.getText() + "','" + SafetyResult +"')";
                    Log.d("Alexey",insertQuery);
                    sqlLiteDatabase.database.execSQL(insertQuery);
                    sqlLiteDatabase.close();

                    //возвращаемся в главную активность
                    Intent intent = new Intent(DangerActivity.this, MenuActivity.class);
                    finish();
                    startActivity(intent);
                    saveNeedLoadParam(6);
                }
                catch (Exception e){
                    Log.d("Alexey", e.getMessage());
                }

            }
        });
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserDate = sPref.getString("UserDateNewFormat","");
        UserShift = sPref.getInt("UserShift",2);
        WorkPlace = loadWorkPlaceInfo();
        getSupportActionBar().setTitle(WorkPlace);
    }

    private String loadWorkPlaceInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String workPlaceName = sPref.getString("WorkPlaceName","");
        if (workPlaceName.length()==0)
            workPlaceName = sPref.getString("EquipmentName","");

        return workPlaceName;
    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }


}
