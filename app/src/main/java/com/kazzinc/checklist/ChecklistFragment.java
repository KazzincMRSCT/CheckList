package com.kazzinc.checklist;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.kazzinc.checklist.Model.Answer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChecklistFragment extends Fragment {

    private ChecklistViewModel mViewModel;

    Intent intentService;
    private TableLayout mChklWorkPlace;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    SharedPreferences sPref;
    Handler timerHandler = new Handler();

    private MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    private ArrayList<Answer> answers = new ArrayList<>();

    private LinearLayout mLayoutShift;

    private String UserRole;
    private String QuesType;

    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    private String Shift;
    String formattedDate;
    String UserId;


    public static ChecklistFragment newInstance() {
        return new ChecklistFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checklist_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChecklistViewModel.class);
        // TODO: Use the ViewModel

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

        loadUserInfo();

        //(new SyncDatabasesTask()).execute();

        /*stopService(
                new Intent(MainActivity.this, SyncService.class));*/

        LinearLayout llEquipment = (LinearLayout) getView().findViewById(R.id.llEquipment);
        LinearLayout llWorkPlace = (LinearLayout) getView().findViewById(R.id.llWorkPlace);

        CardView cardEquipment = (CardView) getView().findViewById(R.id.cardEquipment);
        CardView cardWorkPlace = (CardView) getView().findViewById(R.id.cardWorkPlace);
        CardView cardPSO1 = (CardView) getView().findViewById(R.id.cardPSO1);
        CardView cardPSO6 = (CardView) getView().findViewById(R.id.cardPSO6);


        TextView tw = (TextView) getView().findViewById(R.id.textView);

        tw.setText("Чек-лист");

        if (UserRole.equals("ПСО"))
        {
            cardPSO1.setVisibility(View.VISIBLE);
            cardPSO6.setVisibility(View.VISIBLE);

            cardEquipment.setVisibility(View.INVISIBLE);
            cardWorkPlace.setVisibility(View.INVISIBLE);

            ViewGroup.LayoutParams params = llEquipment.getLayoutParams();
            params.height = 50;
            llEquipment.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = llWorkPlace.getLayoutParams();
            params1.height = 50;
            llWorkPlace.setLayoutParams(params1);
            llWorkPlace.setPadding(0,0,0,0);
        }
        else
        {
            cardPSO1.setVisibility(View.INVISIBLE);
            cardPSO6.setVisibility(View.INVISIBLE);

            if (IsHaveEquipment())
                cardEquipment.setVisibility(View.VISIBLE);
            else
                cardEquipment.setVisibility(View.INVISIBLE);

            if (IsHaveWorkPlace()) {
                cardWorkPlace.setVisibility(View.VISIBLE);
                tw.setText("Чек-лист");
            }
            else {
                cardWorkPlace.setVisibility(View.INVISIBLE);
                tw.setText("Нет чек-листов для заполнения");
            }

            llWorkPlace.setPadding(0,150,0,0);
        }

        // button on click listener
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                Vibrator vibro = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibro.vibrate(30);

                switch (v.getId()) {

                    case R.id.cardEquipment:
                        QuesType="2";
                        saveUserInfo();
                        try {
                            Intent intentEquipment = new Intent (getActivity(), EquipmentSelect.class);
                            //finish ();
                            startActivity (intentEquipment);
                        }
                        catch (Exception e)
                        {
                            Log.d("Alexey", e.getMessage ());
                        }
                        break;
                    case R.id.cardPSO1:
                        QuesType="3";
                        saveUserInfo();
                        try {
                            Intent intentPSO = new Intent (getActivity(), WorkPlaceSelect.class);
                            startActivity (intentPSO);
                        }
                        catch (Exception e)
                        {
                            Log.d("Alexey", e.getMessage ());
                        }
                        break;
                    case R.id.cardPSO6:
                        QuesType="4";
                        saveUserInfo();
                        try {
                            Intent intentPSO = new Intent (getActivity(), WorkPlaceSelect.class);
                            startActivity (intentPSO);
                        }
                        catch (Exception e)
                        {
                            Log.d("Alexey", e.getMessage ());
                        }
                        break;

                    case R.id.cardWorkPlace:
                        QuesType="1";
                        saveUserInfo();
                        try {
                            Intent intentWorkPlace = new Intent (getActivity(), WorkPlaceSelect.class);
                            //finish ();
                            startActivity (intentWorkPlace);
                        }
                        catch (Exception e)
                        {
                            Log.d("Maxim", e.getMessage ());
                        }
                        break;
                }
            }
        };

        cardEquipment.setOnClickListener(handler);
        cardWorkPlace.setOnClickListener(handler);
        cardPSO1.setOnClickListener(handler);
        cardPSO6.setOnClickListener(handler);

        //timerHandler.postDelayed(timerRunnable, 0);



    }

    private void saveUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("UserDate", formattedDate);
        ed.putInt("UserShift", Integer.valueOf(Shift));
        ed.putString("QuesType", QuesType);
        ed.commit();
    }

    private void loadUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }

    private boolean IsHaveEquipment(){

        boolean result = false;

        String selectQuery = "SELECT DISTINCT TaskEquipName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " AND TaskEquipId>0 ORDER BY TaskEquipName";

        sqlLiteDatabase.open(getActivity());

        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if(null != cursor) {
                if (cursor.getCount() > 0) {
                    result = true;
                }
            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        return result;
    }

    private boolean IsHaveWorkPlace(){

        boolean result = false;

        String selectQuery = "SELECT DISTINCT TaskWorkPlaceName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " ORDER BY TaskWorkPlaceName";

        Log.d("Alexey", "Чек-листы " + selectQuery);

        sqlLiteDatabase.open(getActivity());

        try {
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if(null != cursor) {
                if (cursor.getCount() > 0) {
                    result = true;
                }
            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        return result;
    }

}
