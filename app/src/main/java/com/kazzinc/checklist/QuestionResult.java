package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class QuestionResult extends AppCompatActivity {

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_result);

        loadResult();
        loadUseriNFO();
    }

    public void goToMain(View view) {

        try {
            Intent intent = new Intent (QuestionResult.this, MenuActivity.class);
            finish ();
            startActivity (intent);
        }
        catch (Exception e)
        {
            Log.d("Alexey", e.getMessage ());
        }
    }
    private void loadUseriNFO()
    {
        SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String UserRole = sPref.getString("UserRole","");
        String userName = sPref.getString("UserName","");
        getSupportActionBar().setTitle("Чек-лист / " + userName);

    }
    private void loadResult()
    {
        TextView tvResult = (TextView) findViewById(R.id.textViewResult);
        TextView tvResultComment = (TextView) findViewById(R.id.textViewResultComment);

        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        int TotalQues = sPref.getInt("TotalQues",0);
        int Critical = sPref.getInt("Critical",0);
        int NonCritical = sPref.getInt("NonCritical",0);

        int Percent = NonCritical*100/TotalQues;

        String text = "";

        if ((Critical==0)&&(Percent<=25)) {
            text = "Замечаний минимум. Риски под контролем. Требования в основном выполняются";
            tvResult.setText(text);
            tvResult.setTextColor(Color.parseColor("#28B463"));
        }
        if ((Critical==0)&&(Percent>25)) {
            text = "Работа по практическому внедрению проводится, но есть существенные замечания";
            tvResult.setText(text);
            tvResult.setTextColor(Color.parseColor("#F4D03F"));
        }
        if ((Critical==1)&&(Percent<=50)) {
            text = "Работа по практическому внедрению проводится, но есть существенные замечания";
            tvResult.setText(text);
            tvResult.setTextColor(Color.parseColor("#F4D03F"));
        }
        if ((Critical==1)&&(Percent>50)) {
            text = "Наличие большого количества  критических замечаний. Риски велики";
            tvResult.setText(text);
            tvResult.setTextColor(Color.parseColor("#E74C3C"));
        }
        //if ((Critical>1)&&(Percent>50)) {
        if (Critical>1) {
            text = "Наличие большого количества  критических замечаний. Риски велики";
            tvResult.setText(text);
            tvResult.setTextColor(Color.parseColor("#E74C3C"));
        }

        tvResultComment.setText("Вопросов всего: " + TotalQues + System.getProperty ("line.separator") + "Критичных: " + Critical + System.getProperty ("line.separator") + "Некритичных: " + NonCritical);

        saveCalcResult(text);
    }

    private void saveCalcResult(String text)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("PSOResult", text);
        ed.commit();
    }
}
