package com.kazzinc.checklist;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskFragment extends Fragment {

    private TaskViewModel mViewModel;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    String UserId;
//    String IsLongShift;
    private String UserRole;
    SharedPreferences sPref;
    Handler timerHandler = new Handler();
    ScrollView scrollView;

    int taskId;
    String date;
    String shift;
    String master;
    String stateStatus;
    String singStatus;
    int eхpandStatus;
    int eхpandStatusSafety;
    String formattedDate;
    private String Shift;
    int needSignCount;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    protected String getTaskEmplId(){
        int EmliId=0;
        sqlLiteDatabase.open(getContext());
        String selectQuery = "SELECT  * FROM Task";
        Cursor cursorSelect = sqlLiteDatabase.database.rawQuery(selectQuery, null);
        if (cursorSelect.moveToFirst()) {
            do {
                EmliId=cursorSelect.getInt(5);
                Log.d("Alexey", "EmliId= "+EmliId);
            } while (cursorSelect.moveToNext());
        }
        return String.valueOf(EmliId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        // TODO: Use the ViewModel


        Log.d("Test", "(1): ");

        scrollView = getView().findViewById(R.id.scroll);
        eхpandStatus=0;
        eхpandStatusSafety=0;

        Shift = "1";

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

        Do();
        if (taskId>0)
            DoDetail();

        //Вывод уведомления о просрочке ключа ЭЦП
//        if (new TaskFragment().getUserId().equals(new TaskFragment().getTaskEmplId()))
//            new TaskFragment().showDialogECP(getApplicationContext());
        //

        timerHandler.postDelayed(timerRunnable, 15000);

        final Button btnRisk = (Button) getView().findViewById(R.id.btnRisk);
        final Button btnSafety = (Button) getView().findViewById(R.id.btnSafety);
        final Button btnInstruction = (Button) getView().findViewById(R.id.btnInstruction);
        final Button btnMark = (Button) getView().findViewById(R.id.btnMark);

        if (taskId>0)
        {
            btnRisk.setVisibility(View.VISIBLE);
            btnSafety.setVisibility(View.VISIBLE);
            btnInstruction.setVisibility(View.VISIBLE);
            btnMark.setVisibility(View.VISIBLE);
        } else {
            btnRisk.setVisibility(View.GONE);
            btnSafety.setVisibility(View.GONE);
            btnInstruction.setVisibility(View.GONE);
            btnMark.setVisibility(View.GONE);
        }

        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), RiskSafetyActivity.class);

                switch (v.getId()) {

                    case R.id.btnRisk:

                        intent.putExtra("Title", "Риски");
                        startActivity(intent);

                        break;

                    case R.id.btnSafety:

                        intent.putExtra("Title", "Меры безопасности");
                        startActivity(intent);

                        break;

                    case R.id.btnInstruction:

                        intent.putExtra("Title", "Инструкции");
                        startActivity(intent);

                        break;

                    case R.id.btnMark:

//                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath("/storage/emulated/0/me1.jpg").build();
//                        startActivity(new Intent(Intent.ACTION_VIEW, uri));

                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("/storage/emulated/0/me1.jpg"), "image/*");
                        startActivity(intent);

//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/external/images/media/16"))); /** replace with your own uri */

                        break;


                }
            }
        };

        btnRisk.setOnClickListener(handler);
        btnSafety.setOnClickListener(handler);
        btnInstruction.setOnClickListener(handler);

        verifyStoragePeremissions(this.getActivity());
    }



    //Вывод сообщения о истечении срока действия ключа ЭЦП
    protected String showDialogECP(Context c) {

        String str = null;
        sqlLiteDatabase.open(getContext());
        String selectQuery = "SELECT  * FROM EmlpECPKey";
        Cursor cursorSelect = sqlLiteDatabase.database.rawQuery(selectQuery, null);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        if (cursorSelect.moveToFirst()) {
            do {
                try{
                    String[] dateArr = cursorSelect.getString(1).split("-");
                    String date = dateArr[2].split("T")[0]+"."+dateArr[1]+"."+dateArr[0];
                    Date date1 = df.parse(df.format(new Date()));
                    Date date2 = df.parse(date);

                    /////////
                    long diff = (date2.getTime() - date1.getTime()) / 1000;
                    long days = diff/(24 * 60 * 60);
                    if(days<=30) {
                        str= "<font color='#F44336'>Срок действия ключа ЭЦП заканчивается " +date+"</font>";
                    }else {
                        str= "<font color='#8BC34A'>Срок действия ключа ЭЦП заканчивается " +date+"</font>";
                    }
                    ////////
                } catch (Exception e) {;
                    String msgError1 = cursorSelect.getString(2).split(";")[1];
                    String msgError0 = cursorSelect.getString(2).split(";")[0];
                    Log.d("Alexey","Error msg ecp"+msgError1);
                    if(msgError1.equals(" Status: 0")||msgError0.equals("Status: 0")){
                        str= "<font color='#F44336'>Срок действия ключа ЭЦП закончился</font>";
                    }else if(msgError1.equals(" Status: 3")||msgError0.equals("Status: 3")){
                        str= "<font color='#F44336'>Не верный пароль ключа ЭЦП<font>";
                    }else if(msgError0.equals("Status: 5")){
                        str= "<font color='#F44336'>Срок действия ключа ЭЦП закончился</font>";
                    }
                    return str;
                }

            } while (cursorSelect.moveToNext());
        }

        return str;
    }

    private static void verifyStoragePeremissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.results);
        if(item!=null)
            item.setVisible(false);
    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            try {
                Do();
                DoDetail();
            }
            catch (Exception e)
            {
                Log.d("Alexey", "Ошибка заполнения " + e.getMessage());
            }
            timerHandler.postDelayed(this, 5000);
        }
    };

    public void Do () {

        TextView tw = (TextView) getView().findViewById(R.id.textView);

        tw.setText("Нет наряд-заданий");

        loadUserInfo();

        LinearLayout rContainer = (LinearLayout) getView().findViewById(R.id.taskContainer);

        rContainer.removeAllViews();

        String selectQuery = "SELECT DISTINCT TaskId, TaskDate, taskShift, taskUserName, taskStateName, taskSignName FROM TASK WHERE TaskEmplId ='" + UserId + "' AND TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " ORDER BY TaskDate DESC";

        sqlLiteDatabase.open(getActivity());

        try {
            int id = 1;

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if(null != cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        //Меры и риски
                        String selectRS = "SELECT Risk, Safety FROM RiskSafety";
                        sqlLiteDatabase.open(getActivity());

                        try {
                            Cursor cursorRS = sqlLiteDatabase.database.rawQuery(selectRS, null);
                            if (null != cursorRS) {
                                if (cursorRS.moveToFirst()) {
                                    do {
                                        //twRisk.setText(cursorRS.getString(0));
                                        //twSafety.setText(cursorRS.getString(1));
                                    } while (cursorRS.moveToNext());
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.d("Alexey",e.getMessage());
                        }
                        //

                        tw.setText("Наряд-задание");
                        taskId = cursor.getInt(0);
                        date = cursor.getString(1);
                        shift = cursor.getString(2);
                        master = cursor.getString(3);
                        stateStatus = cursor.getString(4);

                        if (loadSignState()==2)
                            singStatus = "Да";//cursor.getString(5);
                        else
                            singStatus = "Нет";

                        final CardView cw = new CardView(getActivity());

                        cw.setId( taskId);
                        cw.setTransitionName(String.valueOf(taskId));

                        TableLayout tl = new TableLayout(getActivity());

                        TableRow tableRow = new TableRow(getActivity());
                        tableRow.setGravity(Gravity.TOP);

                        LinearLayout ll = new LinearLayout(getActivity());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setPadding(10,20,10,20);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 0, 0, 20);

                        TextView twCaption = new TextView(getActivity());

                        twCaption.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        twCaption.setText(Html.fromHtml("<font color='#979797'>Дата:</font><font color='#E1E2E5'> " + date + "</font>"));
                        twCaption.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twCaption.setTextSize(18);

                        TextView twShift = new TextView(getActivity());
                        twShift.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        twShift.setText(Html.fromHtml("<font color='#979797'>Смена:</font><font color='#E1E2E5'> Смена " + shift + "</font>"));
                        twShift.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twShift.setTextSize(18);


                        TextView twMaster = new TextView(getActivity());
                        twMaster.setText(Html.fromHtml("<font color='#979797'>Мастер:</font><font color='#E1E2E5'> " + master + "</font>"));
                        twMaster.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twMaster.setTextSize(18);

                        ll.addView(twCaption);
                        ll.addView(twShift);
                        ll.addView(twMaster);
                        tableRow.addView(ll, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));

                        /////////////////////////////////

                        LinearLayout ll1 = new LinearLayout(getActivity());
                        ll1.setOrientation(LinearLayout.VERTICAL);
                        ll1.setPadding(10,20,10,20);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams1.setMargins(0, 0, 0, 20);

                        TextView twStatus = new TextView(getActivity());
                        twStatus.setText(Html.fromHtml("<font color='#979797'>Статус: </font><font color='#E1E2E5'> " + stateStatus + "</font>"));
                        twStatus.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twStatus.setTextSize(18);

                        String color = "#E1E2E5";
                        if (singStatus.equals("Нет"))
                            color = "#E74C3C";

                        TextView twSing = new TextView(getActivity());
                        twSing.setText(Html.fromHtml("<font color='#979797'>Подпись:</font><font color='" + color + "'> " + singStatus + "</font>"));
                        twSing.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twSing.setTextSize(18);

                        int modifyCount =0;
                        needSignCount = 0;

                        String selectModify = "SELECT Count(Id) FROM TaskDetailModify WHERE TaskId ='" + taskId + "'";

                        Cursor cursorModify = sqlLiteDatabase.database.rawQuery(selectModify, null);
                        if(null != cursorModify) {
                            if (cursorModify.moveToFirst()) {
                                do {
                                    modifyCount = cursorModify.getInt(0);
                                } while (cursorModify.moveToNext());
                            }
                        }

                        cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight1));

                        ll1.addView(twStatus);
                        ll1.addView(twSing);

                        if (modifyCount>0)
                        {
                            /*String selectModify1 = "SELECT Count(Id) FROM TaskDetailModify WHERE SingedEmpl=0";

                            Cursor cursorModify1 = sqlLiteDatabase.database.rawQuery(selectModify1, null);
                            if(null != cursorModify1) {
                                if (cursorModify1.moveToFirst()) {
                                    do {
                                        needSignCount = cursorModify1.getInt(0);
                                    } while (cursorModify1.moveToNext());
                                }
                            }*/

                            needSignCount = loadModifySignState();

                            Log.d("Alexey", "ECP needSignCount - " + needSignCount);

                            TextView twModify = new TextView(getActivity());
                            twModify.setText(Html.fromHtml("<font color='#979797'>Изменение:</font><font color='#28B463'> Да</font>"));
                            twModify.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                            twModify.setTextSize(18);

                            ll1.addView(twModify);
                        }
                        else
                        {
                            TextView twModify = new TextView(getActivity());
                            twModify.setText(Html.fromHtml("<font color='#979797'>Изменение:</font><font color='#E1E2E5'> Нет</font>"));
                            twModify.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                            twModify.setTextSize(18);

                            ll1.addView(twModify);
                        }

                        tableRow.addView(ll1, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                        tl.addView(tableRow, 0);
                        cw.addView(tl);
                        rContainer.addView(cw, layoutParams);

                        id++;

                    } while (cursor.moveToNext());
                }
            }

            sqlLiteDatabase.close();

        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

    }

    public void DoDetail () {

        LinearLayout container = (LinearLayout) getView().findViewById(R.id.TDContainer);
        LinearLayout btnContainer = (LinearLayout) getView().findViewById(R.id.TDButtonContainer);

        container.removeAllViews();
        btnContainer.removeAllViews();

        int id = 1;

        //ПОЛУЧЕНИЕ СПИСКА РАБОТ ИЗ НАРЯДА
        try {

            sqlLiteDatabase.open(getActivity());
            String selectQuery = "SELECT * FROM TaskDetail WHERE TDTaskId='" + taskId + "'";

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            String bgColor = "#222226";

            if(taskId > 0)
            {
                if (cursor.moveToFirst()) {
                    do {
                        String TDid = cursor.getString(0);
                        String workType = cursor.getString(2);
                        String workplace1 = cursor.getString(4);
                        String value = cursor.getString(7);
                        String unit = cursor.getString(3);
                        String equipment = cursor.getString(6);
                        String fact = cursor.getString(8);


                        int modifyCount = 0;
                        int singed = 0;
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
                                    fact = cursorModify.getString(8);
                                    singed = cursorModify.getInt(10);

                                    modifyCount++;
                                } while (cursorModify.moveToNext());
                            }
                        }


                        final CardView cw = new CardView(getActivity());
                        cw.setCardBackgroundColor(Color.parseColor(bgColor));

                        LinearLayout ll = new LinearLayout(getActivity());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setPadding(0, 0, 0, 20);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 20, 0, 0);

                        if (modifyCount > 0) {
                            TextView twModify = new TextView(getActivity());
                            twModify.setTextColor(Color.parseColor("#28B463"));

                            if (singed==0) {
                                twModify.setText(Html.fromHtml("<font color='#28B463'>Изменение </font><font color='#E74C3C'> (не подписано)</font>"));
                                //twModify.setText("Изменение не подписано");
                            }
                            else
                                twModify.setText("Изменение");

                            twModify.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                            twModify.setTextSize(14);
                            ll.addView(twModify);
                        }

                        if (Integer.parseInt(TDid) == -1) {
                            TextView twModify = new TextView(getActivity());
                            twModify.setTextColor(Color.parseColor("#E74C3C"));
                            twModify.setText("Удалено");
                            twModify.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                            twModify.setTextSize(14);
                            ll.addView(twModify);
                        }

                        TextView twWT = new TextView(getActivity());
                        twWT.setTextColor(Color.parseColor("#E1E2E5"));

                        if (Integer.parseInt(TDid) == -1)
                            twWT.setText(Html.fromHtml("<s>" + id + ". " + workType + "</s>"));
                        else
                            twWT.setText(id + ". " + workType);

                        twWT.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twWT.setTextSize(18);
                        ll.addView(twWT);

                        TextView twWP = new TextView(getActivity());
                        twWP.setTextColor(Color.parseColor("#979797"));
                        twWP.setText("Место работ: " + workplace1);
                        twWP.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twWP.setTextSize(18);
                        ll.addView(twWP);

                        if (equipment.length() > 0) {
                            TextView twEq = new TextView(getActivity());
                            twEq.setTextColor(Color.parseColor("#979797"));
                            twEq.setText("Оборудование: " + equipment);
                            twEq.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                            twEq.setTextSize(18);
                            ll.addView(twEq);
                        }

                        TextView twValue = new TextView(getActivity());
                        twValue.setTextColor(Color.parseColor("#979797"));
                        twValue.setText("Объем работ: " + value + " " + unit);
                        twValue.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twValue.setTextSize(18);
                        ll.addView(twValue);


                        //прогрессбар

                        if ((equipment.length() > 0) && (Integer.parseInt(TDid) > -1)) {

                            int percent = (int) Math.round(Double.valueOf(fact) / Double.valueOf(value) * 100);

                            RelativeLayout rl = new RelativeLayout(getActivity());

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.progressbarId);
                            rl.setLayoutParams(params);

                            ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
                            progressBar.setId(R.id.progressbarId);
                            progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    55));
                            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress));
                            progressBar.setProgress(percent);
                            progressBar.setPadding(0, 20, 0, 0);

                            rl.addView(progressBar);

                            TextView twProgress = new TextView(getActivity());

                            twProgress.setPadding(0, 18, 0, 0);
                            twProgress.setText(String.valueOf(percent) + "% (" + fact + " " + unit + ")");
                            //twProgress.setTextColor(Color.parseColor("#909497"));
                            //twProgress.setTextColor(Color.parseColor("#FDFEFE"));
                            twProgress.setTextColor(Color.parseColor("#17202A"));

                            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params1.addRule(RelativeLayout.CENTER_IN_PARENT);
                            params1.addRule(RelativeLayout.CENTER_VERTICAL);

                            rl.addView(twProgress, params1);
                            ll.addView(rl);
                        }

                        cw.addView(ll);
                        container.addView(cw, layoutParams);

                        id++;

                    } while (cursor.moveToNext());

                }
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        //Изменение наряда ДОБАВЛЕННЫЕ ЗАДАЧИ
        try {

            sqlLiteDatabase.open(getActivity());
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

                    final CardView cw = new CardView(getActivity());
                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(10, 20, 10, 20);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 20, 0, 0);

                    TextView twModify = new TextView(getActivity());
                    twModify.setTextColor(Color.parseColor("#28B463"));
                    twModify.setText("Добавлено");
                    twModify.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twModify.setTextSize(18);
                    ll.addView(twModify);

                    TextView twWT = new TextView(getActivity());
                    twWT.setTextColor(Color.parseColor("#E1E2E5"));
                    twWT.setText(id + ". " + workType);
                    twWT.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twWT.setTextSize(14);
                    ll.addView(twWT);

                    TextView twWP = new TextView(getActivity());
                    twWP.setTextColor(Color.parseColor("#979797"));
                    twWP.setText("Место работ: " + workplace1);
                    twWP.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twWP.setTextSize(18);
                    ll.addView(twWP);

                    if(equipment.length()>0)
                    {
                        TextView twEq = new TextView(getActivity());
                        twEq.setTextColor(Color.parseColor("#979797"));
                        twEq.setText("Оборудование: " + equipment);
                        twEq.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twEq.setTextSize(18);
                        ll.addView(twEq);
                    }

                    TextView twValue = new TextView(getActivity());
                    twValue.setTextColor(Color.parseColor("#979797"));
                    twValue.setText("Объем работ: " + value + " " + unit);
                    twValue.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twValue.setTextSize(18);
                    ll.addView(twValue);

                    ImageView imageView = new ImageView(getActivity());
                    imageView.setImageResource(R.drawable.loader1);

                    ll.addView(imageView);

                    cw.addView(ll);
                    container.addView(cw, layoutParams);

                    id++;


                } while (cursor.moveToNext());

            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey", e.getMessage());
        }

        try {
            LinearLayout llRisk = (LinearLayout) getView().findViewById(R.id.llRisk);
            llRisk.setVisibility(View.INVISIBLE);
            if (id > 1)
                llRisk.setVisibility(View.VISIBLE);

            if ((id > 1) && ((singStatus.equals("Нет"))||(needSignCount==1))) {
                final Button btn = new Button(new ContextThemeWrapper(getActivity(), R.style.AppTheme_Button));
                btn.setBackgroundColor(getResources().getColor(R.color.colorDark));
                btn.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                btn.setText("ПОДПИСАТЬ");
                btn.setHapticFeedbackEnabled(true);
                //btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bank_check, 0, 0);
                btn.setCompoundDrawablePadding(12);

                btn.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(240, 20, 240, 20);
                btn.setLayoutParams(params);

                btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {
                            sqlLiteDatabase.open(getActivity());
                            String updateQuery = "";
                            if (needSignCount == 0)
                            {
                                updateQuery = "UPDATE Task SET TaskSignId='2', TaskSignName='Да' WHERE TaskId='" + taskId + "'";
                                saveSignState(2); //сохраняем признак подписания наряда
                                saveNeedLoadParam(2);
                                Log.d("Alexey", "ECP: нажали ПОДПИСАТЬ основной наряд");
                            }
                            else
                            {
                                updateQuery = "UPDATE TaskDetailModify SET SingedEmpl='1' WHERE TaskId='" + taskId + "'";
                                saveModifySignState(2);
                                saveNeedLoadParam(7);
                                Log.d("Alexey", "ECP: нажали ПОДПИСАТЬ изменение в наряде");
                            }

                            sqlLiteDatabase.database.execSQL(updateQuery);
                            sqlLiteDatabase.close();

                            btn.setVisibility(View.INVISIBLE);

                            Do();

                            scrollView = getActivity().findViewById(R.id.scroll);
                            scrollView.fullScroll(ScrollView.FOCUS_UP);

                            showAddItemDialog(getActivity());
                        } catch (Exception e) {

                        }
                    }

                });

                btnContainer.addView(btn);
            }
        }
        catch (Exception e){
            Log.d("Alexey", e.getMessage());
        }
    }

    private void saveSignState(int Value)
    {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("SignState", String.valueOf(Value));
        ed.commit();

        Log.d("Alexey", "ECP статус подписи при подписании основного наряда - " + Value);
    }

    private int loadSignState()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        String ss = sPref.getString("SignState","");
        return Integer.valueOf(ss);

    }

    private void saveModifySignState(int Value)
    {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("ModifySignState", String.valueOf(Value));
        ed.commit();

        Log.d("Alexey", "ECP статус подписи при подписании основного наряда - " + Value);
    }

    private int loadModifySignState()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        String ss = sPref.getString("ModifySignState","1");
        return Integer.valueOf(ss);

    }

    private void showAddItemDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyAlertDialogTheme))
                .setTitle("Наряд-задание подписано")
                .setMessage(Html.fromHtml(showDialogECP(getContext())))
                //.setView(taskEditText)

                .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*Fragment fragment = new ChecklistFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frameContainer, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/

                        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.task, fragment).commit();
                    }
                })
                .create();
        dialog.show();
    }

    private void saveNeedLoadParam(int needLoad)
    {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    private void loadUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
//        IsLongShift = sPref.getString("GSMLongShift","0");
    }

    public static void expand(final TextView v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();

                /*int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);*/
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();

        //valueAnimator.setDuration(duration);
        //valueAnimator.start();
    }

    public static void collapse(final TextView v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public String getUserId(){
        return UserId;
    }
}
