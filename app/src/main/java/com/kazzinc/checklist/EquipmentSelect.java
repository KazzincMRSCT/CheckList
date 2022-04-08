package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EquipmentSelect extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    SharedPreferences sPref;

    private ArrayList dataList;
    private ArrayAdapter arrayAdapter;
    private String selectedEquipment;
    private String selectedEquipmentType;
    private String selectedEquipmentTypeId;
    private String UserId;
    String formattedDate;
    String UserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_equipment_select);

            UserRole = loadUserRole();

            ListView list = (ListView) findViewById(R.id.lwEquipment);
            list.setTextFilterEnabled(true);

            dataList = new ArrayList<String>();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Cast the list view each item as text view
                    TextView item = (TextView) super.getView(position, convertView, parent);

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

            fillEquipmentList();

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

                    TextView textView = (TextView) itemClicked;
                    selectedEquipment = textView.getText().toString(); // получаем текст нажатого элемента
                    selectedEquipmentType = selectedEquipment;//selectedEquipment.substring(0,4);
                    showAddItemDialog(EquipmentSelect.this);
                }
            });
        }
        catch(Exception e)
        {
            Log.d("Alexey", e.getMessage());
        }
    }

    private void fillEquipmentList (){

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
        formattedDate = df.format(c);

        arrayAdapter.clear();

        //String selectQuery = "SELECT DISTINCT TaskEquipName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " AND TaskEquipId>0 ORDER BY TaskEquipName";
        String selectQuery = "SELECT DISTINCT TDEquipment FROM TaskDetail WHERE TDEquipment NOT IN (SELECT Equipment FROM TaskDetailModify) AND TDEquipment NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") UNION ALL SELECT DISTINCT Equipment FROM TaskDetailModify WHERE Equipment NOT IN (SELECT DISTINCT AnswerWorkPlaceName FROM Answer WHERE AnswerUserId=" + UserId + " AND AnswerDate='" + formattedDate + "' AND AnswerShift=" + Shift + ") AND Equipment NOT IN (SELECT DISTINCT TDEquipment FROM TaskDetail)";

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
            }

            sqlLiteDatabase.close();
        } catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }
    }

    private void showAddItemDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Выбрать")
                .setMessage(selectedEquipment + " ?")
                //.setView(taskEditText)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedEquipmentTypeId="3";
                        String selectQuery = "SELECT DISTINCT TaskEquipTypeId FROM Task WHERE TaskEquipName='" + selectedEquipment + "'";
                        sqlLiteDatabase.open(getApplicationContext());

                        try {
                            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
                            if (cursor.getCount() > 0) {
                                if (cursor.moveToFirst()) {
                                    do {
                                        selectedEquipmentTypeId = cursor.getString(0);
                                        Log.d("Alexey", "Quesr 0 " + selectedEquipmentTypeId);
                                    } while (cursor.moveToNext());
                                }
                            }
                            else
                            {
                                selectQuery = "SELECT DISTINCT TaskEquipTypeId FROM Task";
                                cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
                                if (cursor.getCount() > 0) {
                                    if (cursor.moveToFirst()) {
                                        do {
                                            selectedEquipmentTypeId = cursor.getString(0);
                                            Log.d("Alexey", "Quesr 0 " + selectedEquipmentTypeId);
                                        } while (cursor.moveToNext());
                                    }
                                }
                            }

                            sqlLiteDatabase.close();
                        } catch (Exception e) {
                            Log.d("Alexey", "Quesr err " + e.getMessage());
                        }


                        saveEquipmentInfo(selectedEquipment, selectedEquipmentType, selectedEquipmentTypeId);
                        Intent intent = new Intent(EquipmentSelect.this, MotoActivity.class);
                        intent.putExtra("EquipmentName", selectedEquipment);
                        intent.putExtra("Date", formattedDate);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fillEquipmentList();
                    }
                })
                .create();
        dialog.show();
    }

    private void saveEquipmentInfo(String equipmentName, String equipmentType, String equipmentTypeId)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("EquipmentName", equipmentName);
        ed.putString("EquipmentType", equipmentType);
        ed.putString("EquipmentTypeId", equipmentTypeId);
        Log.d("Alexey", "Quesr 1" + equipmentTypeId);
        ed.putString("WorkPlaceName", "");
        ed.commit();
    }

    private String loadUserRole()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","0");
        String UserRole = sPref.getString("UserRole","");
        String userName = sPref.getString("UserName","");
        getSupportActionBar().setTitle("Чек-лист / " + userName);
        return UserRole;

    }
}
