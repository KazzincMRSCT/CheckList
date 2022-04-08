package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WorkPlaceSelect extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    SharedPreferences sPref;

    private ArrayList dataList;
    private ListView list;
    private ArrayAdapter arrayAdapter;
    private String selectedWorkPlace;
    private String UserId;

    private String QuesType;


    private Integer WorkPlaceSelecCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_work_place_select);

            WorkPlaceSelecCounter = 1;

            list = (ListView) findViewById(R.id.wpGroupList);
            list.setTextFilterEnabled(true);

            dataList = new ArrayList<String>();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Cast the list view each item as text view
                    TextView item = (TextView) super.getView(position,convertView,parent);

                    // Set the typeface/font for the current item
                    //item.setTypeface(mTypeface);

                    // Set the list view item's text color
                    item.setTextColor(Color.parseColor("#979797"));

                    // Set the item text style to bold
                    //item.setTypeface(item.getTypeface(), Typeface.BOLD);

                    // Change the item text size
                    //item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

                    // return the view
                    return item;
                }
            };

            list.setAdapter(arrayAdapter);

            if (loadUserRole().equals("Мастер смены")||loadUserRole().equals("Начальник участка")||loadUserRole().equals("Начальник рудника"))
                fillWorkPlaceListAll("");
            else
                if (loadUserRole().equals("ПСО"))
                    fillWorkPlaceList(WorkPlaceSelecCounter,"");
                else
                    fillWorkPlaceFromSUENZ();

            SearchView sv = (SearchView) findViewById(R.id.search);

            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String text) {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String text) {

                    arrayAdapter.getFilter().filter(text);

                    return false;
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                        long id) {
                    WorkPlaceSelecCounter++;
                    TextView textView = (TextView) itemClicked;
                    String strText = textView.getText().toString(); // получаем текст нажатого элемента

                    selectedWorkPlace = strText;

                    if (loadUserRole().equals("Мастер смены")||loadUserRole().equals("Начальник участка")||loadUserRole().equals("Начальник рудника")) {
                                fillWorkPlaceListAll(strText);
                    } else
                        showAddItemDialog(WorkPlaceSelect.this);
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Alexey",e.getMessage());
        }
    }

    private void fillWorkPlaceListAll(String workPlaceGroupCode){

        arrayAdapter.clear();

        String whereCondition = "";

        if (workPlaceGroupCode=="")
            whereCondition = " IS NULL ";
        else {
            whereCondition = "='" + GroupCode(workPlaceGroupCode) + "'";
        }

        String selectQuery = "SELECT DISTINCT WorkPlaceName FROM Workplace WHERE WorkPlaceGroupCode" + whereCondition + " ORDER BY WorkPlaceName";

        sqlLiteDatabase.open(this);

        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.getCount()>0) {
                if (cursor.moveToFirst()) {
                    do {
                        String workPlaceName = cursor.getString(0);

                        if (workPlaceName != null) {
                            dataList.add(workPlaceName);
                        }
                    } while (cursor.moveToNext());
                }
                //arrayAdapter.addAll(dataList);
                //arrayAdapter.notifyDataSetChanged();
            }
            else
            {
                showAddItemDialog(WorkPlaceSelect.this);
            }

            sqlLiteDatabase.close();

            list.smoothScrollToPosition(0);
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

    }

    private void fillWorkPlaceFromSUENZ (){

        Log.d("Alexey","Ку-ку 2");

        String Shift = "1";

        Date c = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(c);


        int h = c.getHours();
        if ((h >= 6) && (h < 18))
            Shift = "2";
        if (h < 6) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            c = cal.getTime();
        }

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = df.format(c);


        arrayAdapter.clear();

        //String selectQuery = "SELECT DISTINCT TaskWorkPlaceName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " ORDER BY TaskWorkPlaceName";
        //String selectQuery = "SELECT DISTINCT TaskWorkPlaceName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " AND TaskWorkPlaceName NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") ORDER BY TaskWorkPlaceName";
        //String selectQuery = "SELECT DISTINCT TDWP1 FROM TaskDetail WHERE Tdid NOT IN (SELECT Id FROM TaskDetailModify) AND TDWP1 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") UNION ALL SELECT DISTINCT WP1 FROM TaskDetailModify WHERE WP1 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ")";
        String selectQuery = "SELECT DISTINCT TDWP1 FROM TaskDetail WHERE Tdid NOT IN (SELECT Id FROM TaskDetailModify) AND TDWP1 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") UNION ALL SELECT DISTINCT WP1 FROM TaskDetailModify WHERE WP1 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") UNION ALL SELECT DISTINCT TDWP2 FROM TaskDetail WHERE Tdid NOT IN (SELECT Id FROM TaskDetailModify) AND TDWP2 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") UNION ALL SELECT DISTINCT WP2 FROM TaskDetailModify WHERE WP2 NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ")";
        sqlLiteDatabase.open(this);

        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String workPlaceName = cursor.getString(0);

                        if (workPlaceName != null)
                            dataList.add(workPlaceName);
                    } while (cursor.moveToNext());
                }
            } else {
                showDialog(WorkPlaceSelect.this);
            }

            sqlLiteDatabase.close();
        } catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }
    }

    private void fillWorkPlaceList(Integer level, String itemName){

        Log.d("Alexey","Ку-ку 1");

        arrayAdapter.clear();

        String selectQuery = "";
        String whereCondition = "";

        if (level==1)
            selectQuery = "SELECT DISTINCT WorkPlacePSOGroupLevel1 FROM WorkPlacePSO ORDER BY WorkPlacePSOGroupLevel1";

        if (level==2) {
            if (itemName!="")
                whereCondition = "WHERE WorkPlacePSOGroupLevel1 = '" + itemName + "'";
            selectQuery = "SELECT DISTINCT WorkPlacePSOGroupLevel2 FROM WorkPlacePSO " + whereCondition + " ORDER BY WorkPlacePSOGroupLevel2";
        }

        if (level==3) {
            if (itemName!="")
                whereCondition = "WHERE WorkPlacePSOGroupLevel2 = '" + itemName  + "'";
            selectQuery = "SELECT WorkPlacePSOName FROM WorkPlacePSO " + whereCondition + "  ORDER BY WorkPlacePSOName";
        }

        sqlLiteDatabase.open(this);

        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.getCount()>0) {
                if (cursor.moveToFirst()) {
                    do {
                        String workPlaceName = cursor.getString(0);

                        if (workPlaceName != null)
                            dataList.add(workPlaceName);
                    } while (cursor.moveToNext());
                }
            }
            else
            {
                showAddItemDialog(WorkPlaceSelect.this);
            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }
    }

    private String GroupCode(String workPlaceName)
    {
        String groupCode = "";
        String selectQuery = "SELECT WorkPlaceCode FROM Workplace WHERE WorkPlaceName='" + workPlaceName + "' ORDER BY WorkPlaceName LIMIT 1";
        sqlLiteDatabase.open(this);
        //проверяем соединение с удаленной бд и обновялем вопросы в удаленной бд
        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    groupCode = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        sqlLiteDatabase.close();

        return groupCode;
    }

    class SelectWorkPlaceListGroup extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

        return true;
        }

        protected void onPostExecute(boolean preverse){
        }
    }

    private void saveWorkPlaceInfo(String workPlaceName)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("WorkPlaceName", workPlaceName);
        ed.commit();
    }

    public void showBottomToast(final String msg)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(WorkPlaceSelect.this,msg, Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 20);
                toast1.show();
            }
        });
    }
    private void showDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setMessage("Нет доступных рабочих мест")
                .setCancelable(false)
                //.setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(WorkPlaceSelect.this, MenuActivity.class);
                            startActivity(intent);
                        finish();
                    }
                })
                .create();
        dialog.show();
    }
    private void showAddItemDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Выбрать")
                .setMessage(selectedWorkPlace + " ?")
                //.setView(taskEditText)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveWorkPlaceInfo(selectedWorkPlace);
                        if (loadUserRole().equals("ПСО")) {
                            Intent intent = new Intent(WorkPlaceSelect.this, QuestionsActivityPSO.class);
                            startActivity(intent);
                        }
                        else
                            if (loadUserRole().equals("Мастер смены")||loadUserRole().equals("Начальник участка")||loadUserRole().equals("Начальник рудника"))
                            {
                                if (QuesType.equals("5")) {
                                    Intent intent = new Intent(WorkPlaceSelect.this, QuestionsMasterActivity.class);
                                    startActivity(intent);
                                }
                                if (QuesType.equals("6")) {
                                    Intent intent = new Intent(WorkPlaceSelect.this, QuestionGCOMActivity.class);
                                    startActivity(intent);
                                }

                            }
                            else
                            {
                                Intent intent = new Intent(WorkPlaceSelect.this, QuestionsActivity.class);
                                startActivity(intent);
                            }
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (loadUserRole().equals("Мастер смены")||loadUserRole().equals("Начальник участка")||loadUserRole().equals("Начальник рудника"))
                        {
                            fillWorkPlaceListAll("");
                        }
                        else
                        fillWorkPlaceFromSUENZ();
                    }
                })
                .create();
        dialog.show();
    }
    private String loadUserRole()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","0");
        String UserRole = sPref.getString("UserRole","");
        String userName = sPref.getString("UserName","");
        QuesType = sPref.getString("QuesType","");
        getSupportActionBar().setTitle("Чек-лист / " + userName);
        return UserRole;

    }
}
