package com.kazzinc.checklist;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kazzinc.checklist.Chat.ChatMain;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    ChecklistFragment checklistFragment = new ChecklistFragment();
    TaskFragment taskFragment = new TaskFragment();
    ResultFragment resultFragment = new ResultFragment();
    GSMFragment gsmFragment = new GSMFragment();
    RVDFragment rvdFragment = new RVDFragment();
    ScrollView scrollView;
    SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private String srt = "";
    SharedPreferences sPref;
    String UserId;
    private String UserRole;
    String selectedPage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //проверка работы сервиса
        boolean result= isMyServiceRunning(SyncService.class);

        if (!result)
        {
            Intent serviceIntent = new Intent(this, SyncService.class);
            serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

            ContextCompat.startForegroundService(this, serviceIntent);
        }

        setContentView(R.layout.activity_menu);
        scrollView = findViewById(R.id.sclollView);
        bottomNavigationView = findViewById(R.id.bottomMenu);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        selectedPage = "task";

        String page = "task";
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            page = "task";
        } else {
            page = extras.getString("inputPage");
        }

        switch (page)
        {
            case "task":
                bottomNavigationView.setSelectedItemId(R.id.task);
                break;
            case "gsm":
                bottomNavigationView.setSelectedItemId(R.id.gsm);
                break;
            case "checkList":
                bottomNavigationView.setSelectedItemId(R.id.checkList);
                break;
            case "rvd":
                bottomNavigationView.setSelectedItemId(R.id.rvd);
                break;
            case "chat":
                bottomNavigationView.setSelectedItemId(R.id.chat);
                break;
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.user_6);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUserInfo();

        if(UserRole.equals("ПСО")) {
            bottomNavigationView.getMenu().removeItem(R.id.task);
            bottomNavigationView.setSelectedItemId(R.id.checkList);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void hideBottomNavigationItem(int id, BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.findViewById(id).setVisibility(View.GONE);
    }

    public void showBottomNavigationItem(int id, BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.findViewById(id).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkList:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, checklistFragment).commit();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                selectedPage = "checkList";
                invalidateOptionsMenu();
                return true;
            case R.id.task:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, taskFragment).commit();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                selectedPage = "task";
                invalidateOptionsMenu();
                return true;
            case R.id.gsm:
                final ProgressDialog progress = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
                progress.setIndeterminate(true);
                progress.setTitle("Загрузка");
                progress.setMessage("Пожалуйста, подождите...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, gsmFragment).commit();
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        selectedPage = "gsm";
                        invalidateOptionsMenu();
                        SystemClock.sleep(500);
                        progress.dismiss();
                    }
                };
                mThread.start();
                return true;
            case R.id.rvd:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, rvdFragment).commit();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                selectedPage = "rvd";
                invalidateOptionsMenu();
                return true;
            case R.id.chat:
                Intent intent = new Intent(this, ChatMain.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        //getSupportActionBar().setTitle(userName);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            View customView = getLayoutInflater().inflate(R.layout.titleview, null);
            ((TextView)customView.findViewById(R.id.action_bar_title)).setText(userName);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(customView);
            Toolbar parent =(Toolbar) customView.getParent();
            parent.setContentInsetsAbsolute(0,0);
            //actionBar.setTitle(userName);
        }
    }


    //получаем оборудование из наряд задания в локальной бд sqllite
    public List<String> getTDEquipment(){
        sqlLiteDatabase.open(getApplicationContext());
        String selectQuery = "SELECT  * FROM TaskDetail";
        Cursor cursorSelect = sqlLiteDatabase.database.rawQuery(selectQuery, null);
        List<String> TDEquipment= new ArrayList<>();

        if (cursorSelect.moveToFirst()) {
            do {
                TDEquipment.add(cursorSelect.getString(6));
                Log.d("Alexey", "TDEquipment1 "+TDEquipment);
            } while (cursorSelect.moveToNext());
        }
        return TDEquipment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);

        List<String> arr = getTDEquipment();

            for (int i=0;i<arr.size();i++) {
                try {
                    if (!arr.get(i).equals("")) {
//                        arr.remove(i);
                        srt = arr.get(i);
                    }
                } catch (Exception e) {
                    srt = arr.get(i);
                    Log.d("Alexey", "TDEquipment222 " + e);
                    e.printStackTrace();
                }
            }

        switch (selectedPage)
        {
            case "gsm":
                //if (srt.equals("UTIMEC-4 (ТЗМ)") || srt.equals("UTIMEC-3 (ТЗМ)")) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(true);
                    menu.getItem(2).setVisible(false);
                    menu.getItem(3).setVisible(false);
                    break;
//                } else {
//                    menu.getItem(0).setVisible(true);
//                    menu.getItem(1).setVisible(false);
//                    menu.getItem(2).setVisible(false);
//                    menu.getItem(3).setVisible(false);
//                    break;
//                }

            case "rvd":
                if (!srt.equals("")) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                    menu.getItem(3).setVisible(false);
                    break;
                }

            default:
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.results:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, resultFragment).commit();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(MenuActivity.this, AccountActivity.class);
                startActivity(intent);
                return true;
            case R.id.addGSM:
                Intent intentPSO = new Intent (MenuActivity.this, GSMArea.class);
                startActivity (intentPSO);
                return true;
            case R.id.addRVD:
                Intent intentRVD = new Intent (MenuActivity.this, RVDInput.class);
                startActivity (intentRVD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        showDialog(this);
    }

    private void showDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Вернуться на главную страницу?")

                //.setView(taskEditText)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bottomNavigationView.setSelectedItemId(R.id.task);
                    }
                })
                .create();

        dialog.show();
    }
}
