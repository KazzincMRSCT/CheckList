package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskDetailActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    String UserId;
    private String UserRole;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);



    }

    public void onResume() {
        super.onResume();
        DoDetail();
    }

    public void DoDetail () {
        Bundle arguments = getIntent().getExtras();

        String taskId = "";
        String taskShift ="";
        String taskdate = "";
        String master = "";
        String stateStatus = "";
        String singStatus = "";

        try {
            taskId = arguments.get("TaskId").toString();;
            taskShift =arguments.get("Shift").toString();;
            taskdate = arguments.get("Date").toString();;
            master = arguments.get("Master").toString();;
            stateStatus = arguments.get("StateStatus").toString();;
            singStatus = arguments.get("SingStatuc").toString();;
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        final String taskIdTD = taskId;
        final String taskShiftTD =taskShift;
        final String taskdateTD = taskdate;
        final String masterTD = master;
        final String stateStatusTD = stateStatus;
        final String singStatusTD = singStatus;

        LinearLayout container = (LinearLayout) findViewById(R.id.TDContainer);

        container.removeAllViews();

        int id = 1;

        //ПОЛУЧЕНИЕ СПИСКА РАБОТ ИЗ НАРЯДА
        try {

            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM TaskDetail WHERE TDTaskId='" + taskIdTD +"'";
            Log.d("Alexey","1 " + selectQuery);
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            String bgColor ="#444446";
            if (cursor.moveToFirst()) {
                do {

                    String TDid = cursor.getString(0);
                    String workType = cursor.getString(2);
                    String workplace1 = cursor.getString(4);
                    String value = cursor.getString(7);
                    String unit = cursor.getString(3);
                    String equipment = cursor.getString(6);

                    int modifyCount = 0;
                    String selectModify = "SELECT * FROM TaskDetailModify WHERE Id ='" + TDid + "'";
                    Cursor cursorModify = sqlLiteDatabase.database.rawQuery(selectModify, null);
                    if (null != cursorModify) {
                        if (cursorModify.moveToFirst()) {
                            do {
                                workType = cursorModify.getString(2);
                                workplace1 = cursorModify.getString(4);
                                value = cursorModify.getString(7);
                                unit = cursorModify.getString(3);
                                equipment = cursorModify.getString(6);

                                modifyCount++;
                            } while (cursorModify.moveToNext());
                        }
                    }

                    final CardView cw = new CardView(this);
                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20, 20, 20, 20);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 20, 0, 0);

                    if (modifyCount > 0) {
                        TextView twModify = new TextView(this);
                        twModify.setTextColor(Color.parseColor("#28B463"));
                        twModify.setText("ИЗМЕНЕНИЕ");
                        twModify.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twModify.setTextSize(18);
                        ll.addView(twModify);
                    }

                    if ( Integer.parseInt(TDid) == -1) {
                        TextView twModify = new TextView(this);
                        twModify.setTextColor(Color.parseColor("#E74C3C"));
                        twModify.setText("УДАЛЕНО");
                        twModify.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twModify.setTextSize(18);
                        ll.addView(twModify);
                    }

                    TextView twWT = new TextView(this);
                    twWT.setTextColor(Color.parseColor("#E1E2E5"));

                    if (Integer.parseInt(TDid) == -1)
                        twWT.setText(Html.fromHtml("<s>" + id + ". " + workType  + "</s>"));
                    else
                        twWT.setText(id + ". " + workType);

                    twWT.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twWT.setTextSize(18);
                    ll.addView(twWT);

                    TextView twWP = new TextView(this);
                    twWP.setTextColor(Color.parseColor("#979797"));
                    twWP.setText("Место работ: " + workplace1);
                    twWP.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twWP.setTextSize(18);
                    ll.addView(twWP);

                    if(equipment.length()>0)
                    {
                        TextView twEq = new TextView(this);
                        twEq.setTextColor(Color.parseColor("#979797"));
                        twEq.setText("Оборудование: " + equipment);
                        twEq.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twEq.setTextSize(18);
                        ll.addView(twEq);
                    }

                    TextView twValue = new TextView(this);
                    twValue.setTextColor(Color.parseColor("#979797"));
                    twValue.setText("Объем работ: " + value + " " + unit);
                    twValue.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twValue.setTextSize(18);
                    ll.addView(twValue);

                    cw.addView(ll);
                    container.addView(cw, layoutParams);

                    id++;


                } while (cursor.moveToNext());


            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        //Изменение наряда ДОБАВЛЕННЫЕ ЗАДАЧИ
        try {

            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT * FROM TaskDetailModify WHERE Id ='0'";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            String bgColor ="#444446";
            if (cursor.moveToFirst()) {
                do {

                    String TDid = cursor.getString(0);
                    String workType = cursor.getString(2);
                    String workplace1 = cursor.getString(4);
                    String value = cursor.getString(7);
                    String unit = cursor.getString(3);
                    String equipment = cursor.getString(6);

                    final CardView cw = new CardView(this);
                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20, 20, 20, 20);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 20, 0, 0);

                    TextView twModify = new TextView(this);
                    twModify.setTextColor(Color.parseColor("#28B463"));
                    twModify.setText("ДОБАВЛЕНО");
                    twModify.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twModify.setTextSize(18);
                    ll.addView(twModify);

                    TextView twWT = new TextView(this);
                    twWT.setTextColor(Color.parseColor("#E1E2E5"));
                    twWT.setText(id + ". " + workType);
                    twWT.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twWT.setTextSize(18);
                    ll.addView(twWT);

                    TextView twWP = new TextView(this);
                    twWP.setTextColor(Color.parseColor("#979797"));
                    twWP.setText("Место работ: " + workplace1);
                    twWP.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twWP.setTextSize(18);
                    ll.addView(twWP);

                    if(equipment.length()>0)
                    {
                        TextView twEq = new TextView(this);
                        twEq.setTextColor(Color.parseColor("#979797"));
                        twEq.setText("Оборудование: " + equipment);
                        twEq.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                        twEq.setTextSize(18);
                        ll.addView(twEq);
                    }

                    TextView twValue = new TextView(this);
                    twValue.setTextColor(Color.parseColor("#979797"));
                    twValue.setText("Объем работ: " + value + " " + unit);
                    twValue.setTypeface(ResourcesCompat.getFont(this, R.font.cuprum));
                    twValue.setTextSize(18);
                    ll.addView(twValue);

                    cw.addView(ll);
                    container.addView(cw, layoutParams);

                    id++;


                } while (cursor.moveToNext());

            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        if (id>1) {
            Button btn = new Button(new ContextThemeWrapper(this, R.style.AppTheme_Button));
            btn.setBackgroundColor(getResources().getColor(R.color.colorDark));
            btn.setTextColor(getResources().getColor(R.color.colorDarkLight3));
            btn.setText("ПОДПИСАТЬ");
            btn.setHapticFeedbackEnabled(true);
            //btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contract, 0, 0);
            btn.setCompoundDrawablePadding(12);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(240, 50, 240, 20);
            btn.setLayoutParams(params);

            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        sqlLiteDatabase.open(TaskDetailActivity.this);
                        String updateQuery = "UPDATE Task SET TaskSignId='2', TaskSignName='Подписан' WHERE TaskId='" + taskIdTD + "'";
                        sqlLiteDatabase.database.execSQL(updateQuery);
                        sqlLiteDatabase.close();

                        saveNeedLoadParam(1);

                        showAddItemDialog(TaskDetailActivity.this);
                    } catch (Exception e) {
                        Log.d("Alexey", e.getMessage());
                    }
                }

            });

            container.addView(btn);
        }
    }

    private void showAddItemDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                //.setTitle("Наряд-задание подписано")
                .setMessage("Наряд-задание подписано")
                //.setView(taskEditText)

                .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //возвращаемся в главную активность
                        Intent intent = new Intent(TaskDetailActivity.this, MenuActivity.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }
}
