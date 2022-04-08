package com.kazzinc.checklist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AuditFragment extends Fragment {

    private AuditViewModel mViewModel;

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    SharedPreferences sPref;
    Handler timerHandler = new Handler();

    String UserId;
    String UserRole;
    String UserName;
    String Area;

    public static AuditFragment newInstance() {
        return new AuditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.audit_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AuditViewModel.class);
        // TODO: Use the ViewModel

        loadUserInfo();

        Do();

        timerHandler.postDelayed(timerRunnable, 15000);
    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            try {
                Do();
            }
            catch (Exception e)
            {
                Log.d("Alexey", "Ошибка заполнения " + e.getMessage());
            }
            timerHandler.postDelayed(this, 5000);
        }
    };

    public void Do () {
        int userRole = 1;

        if (UserRole.equals("Мастер смены"))
            userRole = 1;
        if (UserRole.equals("Начальник участка")) {
            userRole = 2;
        }

        final int userRoleInt = userRole;

        TableLayout tableLayout = (TableLayout)getActivity().findViewById(R.id.tl);
        tableLayout.removeAllViews();


        try {

            int id = 1;

            sqlLiteDatabase.open(getActivity());

            String selectQuery = "SELECT DISTINCT TaskEmplName, TaskSignId, HaveCkWP, HaveCkEq, TaskEquipName, TaskEmplId, TaskDate, TaskUserName, TaskId, TaskShift, (SELECT TaskUserAreaName FROM TaskUser WHERE TaskUser.TaskUserId=Task.TaskUserId) as TaskUserAreaName FROM Task ORDER BY TaskEmplName";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            Log.d("Alexey", "аполнения " + selectQuery);

            if (cursor.moveToFirst()) {
                do {

                    String TaskEmplName = cursor.getString(0);
                    int TaskSignId = cursor.getInt(1);
                    int haveCkWP = cursor.getInt(2);
                    int haveCkEq = cursor.getInt(3);
                    String taskEquipName = cursor.getString(4);
                    final int TaskEmplId = cursor.getInt(5);
                    final String TaskDate = cursor.getString(6);
                    final String TaskUserName = cursor.getString(7);
                    final String TaskId = cursor.getString(8);
                    final String TaskShift = cursor.getString(9);
                    final String TaskArea = cursor.getString(10);


                    //////////////////////////////////////

                    LinearLayout rContainer = (LinearLayout) getActivity().findViewById(R.id.conn);

                    rContainer.removeAllViews();

                    CardView cw = new CardView(getActivity());

                    cw.setId(Integer.parseInt (TaskId));
                    cw.setTransitionName(TaskId);

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
                    //twCaption.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    //twCaption.setText("Дата: " + date + " | Смена: " + shift + " | №" +  taskId);
                    //twCaption.setText(Html.fromHtml("<span style='float:left;display:block;'><font color='#979797'>Дата:</font><font color='#E1E2E5'> " + date + "</font></span><span style='float:right;display:block;'><font color='#E1E2E5'> № "+ taskId + "</font></span>"));
                    twCaption.setText(Html.fromHtml("<font color='#979797'>Дата:</font><font color='#E1E2E5'> " + TaskDate + "</font>"));
                    twCaption.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twCaption.setTextSize(18);

                    TextView twShift = new TextView(getActivity());
                    twShift.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                    twShift.setText(Html.fromHtml("<font color='#979797'>Смена:</font><font color='#E1E2E5'> Смена " + TaskShift + "</font>"));
                    twShift.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twShift.setTextSize(18);

                    ll.addView(twCaption);
                    ll.addView(twShift);
                    tableRow.addView(ll, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                    /////////////////////////////////

                    LinearLayout ll1 = new LinearLayout(getActivity());
                    ll1.setOrientation(LinearLayout.VERTICAL);
                    ll1.setPadding(10,20,10,20);

                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams1.setMargins(0, 0, 0, 20);



                    TextView twStatus = new TextView(getActivity());
                    //twStatus.setTextColor(getResources().getColor(R.color.colorDarkLight4));
                    //twStatus.setText("Статус: " + stateStatus);
                    twStatus.setText(Html.fromHtml("<font color='#979797'>Мастер:</font><font color='#E1E2E5'> " + TaskUserName + "</font>"));
                    twStatus.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twStatus.setTextSize(18);

                    TextView twSing = new TextView(getActivity());
                    //twSing.setTextColor(getResources().getColor(R.color.colorDarkLight4));
                    //twSing.setText("Подпись: " + singStatus);
                    twSing.setText(Html.fromHtml("<font color='#979797'>Участок:</font><font color='#E1E2E5'> " + TaskArea + "</font>"));
                    twSing.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                    twSing.setTextSize(18);


                        /*if (isHaveEmpty>0) {
                            cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight3));
                            twDate.setText(Html.fromHtml("<strong><font color='#88271D'>НЕ ЗАПОЛНЕН!</font></strong><font color='#444446'> Дата: " + date + " | Чек-лист: " + qType +"</font>"));
                        }
                        else {
                            cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight1));
                            twWorkplace.setText(Html.fromHtml("<font color='#E1E2E5'>Подстанция " + workplace + "</font>"));
                            twDate.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        }*/

                    cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight1));


                    ll1.addView(twStatus);
                    ll1.addView(twSing);

                    tableRow.addView(ll1, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));

                    tl.addView(tableRow, 0);
                    cw.addView(tl);
                    rContainer.addView(cw, layoutParams);
                    id++;
                    /////////////////////////////////////////////////////////////



                    TableRow tr = new TableRow(getActivity());

                    TableRow.LayoutParams layoutParams3 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6f);
                    layoutParams3.height=80;
                    TableRow.LayoutParams layoutParams2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    layoutParams2.height=80;
                    layoutParams2.setMargins(5,5, 5,0);

                    TextView tw = new TextView(getActivity());

                    tw.setLayoutParams(layoutParams3);
                    tw.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                    //tw.setTypeface(ResourcesCompat.getFont(MasterActivity.this, R.font.cuprum));
                    tw.setText(TaskEmplName);
                    tw.setTextSize(18);
                    tr.addView(tw, 0);


                    TextView twEQ = new TextView(getActivity());
                    twEQ.setLayoutParams(layoutParams2);
                    twEQ.setText("О");
                    twEQ.setTextColor(getResources().getColor(R.color.colorDark));
                    twEQ.setGravity(Gravity.CENTER | Gravity.CENTER);

                    tr.addView(twEQ, 1);

                    if (taskEquipName.length()>0)
                    {
                        if (haveCkEq==0) {
                            twEQ.setBackgroundResource(R.color.colorRed);
                            twEQ.setTextColor(getResources().getColor(R.color.colorBtnText));

                            twEQ.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showToast("Чек-лист не заполнен");
                                }
                            });

                        }
                        else
                        {
                            twEQ.setTextColor(getResources().getColor(R.color.colorDark));
                            twEQ.setBackgroundResource(R.color.colorGreen);

                            twEQ.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String ii="?EmplId="+TaskEmplId+"&Date="+TaskDate+"&User="+TaskUserName+"&UserRole=" + userRoleInt;;

                                    String url = "http://192.168.164.5:819/Equipment.aspx"+ii;

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                        }
                    }
                    else
                    {
                        twEQ.setBackgroundResource(R.color.colorDark);
                        twEQ.setTextColor(getResources().getColor(R.color.colorDark));
                    }

                    TextView twWP = new TextView(getActivity());
                    twWP.setLayoutParams(layoutParams2);
                    twWP.setText("РМ");
                    twWP.setTextColor(getResources().getColor(R.color.colorDark));
                    twWP.setGravity(Gravity.CENTER | Gravity.CENTER);

                    Log.d("Alexey", "333");

                    tr.addView(twWP, 1);

                    if (haveCkWP==0) {
                        twWP.setBackgroundResource(R.color.colorRed);
                        twWP.setTextColor(getResources().getColor(R.color.colorBtnText));

                        twWP.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showToast("Чек-лист не заполнен");
                            }
                        });

                    }
                    else {
                        twWP.setTextColor(getResources().getColor(R.color.colorDark));
                        twWP.setBackgroundResource(R.color.colorGreen);

                        twWP.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String ii="?EmplId="+TaskEmplId+"&Date="+TaskDate+"&User="+TaskUserName+"&UserRole=" + userRoleInt;

                                String url = "http://192.168.164.5:819/WorkPlace.aspx"+ii;

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                Log.d("Alexey",url);
                            }
                        });

                    }

                    TextView twNZ = new TextView(getActivity());
                    twNZ.setLayoutParams(layoutParams2);
                    twNZ.setGravity(Gravity.CENTER | Gravity.CENTER);
                    twNZ.setText("НЗ");
                    twNZ.setTextColor(getResources().getColor(R.color.colorDark));

                    twNZ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String url = "http://192.168.159.136/JTM/TaskView.aspx?TaskId="+TaskId;

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            Log.d("Alexey",url);
                        }
                    });


                    tr.addView(twNZ, 1);

                    if (TaskSignId==1) {
                        twNZ.setBackgroundResource(R.color.colorRed);
                        twNZ.setTextColor(getResources().getColor(R.color.colorBtnText));
                    }
                    else {
                        twNZ.setTextColor(getResources().getColor(R.color.colorDark));
                        twNZ.setBackgroundResource(R.color.colorGreen);
                    }

                    tableLayout.addView(tr);

                    //////////////////////////////////////
                    try {
                        cursor.moveToNext();
                        int TaskEmplIdNext = cursor.getInt(5);
                        if (TaskEmplIdNext != TaskEmplId) {
                            cursor.moveToPrevious();
                        }
                    }
                    catch (Exception e){
                        Log.d("Alexey", e.getMessage());
                    }

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }
    }

    private void loadUserInfo()
    {
        sPref = this.getActivity().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }

    public void showToast(final String msg)
    {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 150);
                toast1.show();
            }
        });
    }

}
