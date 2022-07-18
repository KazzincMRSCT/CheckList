package com.kazzinc.checklist;

import static android.content.Context.MODE_MULTI_PROCESS;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GSMFragment extends Fragment {

    private GSMViewModel mViewModel;

    public static GSMFragment newInstance() {
        return new GSMFragment();
    }

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    Handler timerHandler = new Handler();

    SharedPreferences sPref;
    String UserDate;
    Integer UserShift;
    Integer GSM;
    String GSMEquipment;

    TextView tvControlDiff;
    EditText etBegin;
    EditText etEnd;

    SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    Date c;
    String equipOut="";



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.g_s_m_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(GSMViewModel.class);

        /*FloatingActionButton bntAdd = (FloatingActionButton) getView().findViewById(R.id.btnAdd);
        bntAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentPSO = new Intent (getActivity(), GSMArea.class);
                startActivity (intentPSO);
            }
        });*/


        sqlLiteDatabase.open(getContext());
        String selectQuery = "SELECT TaskEquipName FROM Task WHERE length(TaskEquipName)>0 AND TaskEquipName IS NOT NULL";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                equipOut = cursor.getString(0);
            }
            while (cursor.moveToNext());
        }
        sqlLiteDatabase.close();

        tvControlDiff = getView().findViewById(R.id.tvControlDiff);


        etBegin = getView().findViewById(R.id.etBeginShift);
        etEnd = getView().findViewById(R.id.etEndShift);

        c = new Date();

        ImageButton ib = (ImageButton) getView().findViewById(R.id.btnRefresh);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getContext(), "Синхронизация выполнена", Toast.LENGTH_SHORT).show();
                Toast toast1 = Toast.makeText(getContext(),"Синхронизация выполнена", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.TOP, 0, 250);
                toast1.show();
            }
        });




        Button btnSave = (Button) getView().findViewById(R.id.btnSaveControlMeasure);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckControlDiff();

                SimpleDateFormat nDF = new SimpleDateFormat("MM-dd-yyyy 00:00:00");
                String newFormattedDate = nDF.format(c);

                sqlLiteDatabase.open(getContext());

                String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer, Confirmed, ReasonOil, T86, DT2) " +
//                        "VALUES ('" + df.format(c) + "','" + newFormattedDate + "','" + GetUserShift() + "','" + equipOut + "','Контрольные показания','" + GetUserName() + "','Заправка','" + etBegin.getText() + "','" + 0 + "','" + 0 + "','" + 0 + "','" + 0 + "','0','0', '0',' ','" + 0 +  "')";
                        "VALUES ('" + df.format(c) + "','" + newFormattedDate + "','" + GetUserShift() + "','" + equipOut + "','Контрольные показания','" + GetUserName() + "','Заправка','" + Double.parseDouble(etBegin.getText().toString()) + "','" + 0.0 + "','" + 0.0 + "','" + 0.0 + "','" + 0.0 + "','0','0', '1',' ','" + 0.0 +  "','" + Double.parseDouble(etEnd.getText().toString()) +  "')";
                //String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer) VALUES ('" + df.format(c) +"','"  + GetUserDate() +  "','" + GetUserShift() + "','" + equipOut + "','" + equipIn +"','" + GetUserName()  + "','','" + valueDT + "','" + valueSAE15W40 + "','" + valueSAE50 + "','" + valueSAE10W40+ "','" + value46 +"','0','0')";

                sqlLiteDatabase.database.execSQL(insertQuery);

                sqlLiteDatabase.close();



                Toast toast2 = Toast.makeText(getContext(),"Показания сохранены", Toast.LENGTH_SHORT);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
            }

            private int GetUserShift()
            {
                SharedPreferences sPref = getActivity().getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                return sPref.getInt("UserShift",0);
            }

            private String GetUserName()
            {
                SharedPreferences sPref = getActivity().getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                return sPref.getString("UserName","");
            }
        });

        final LinearLayout llControl = (LinearLayout) getView().findViewById(R.id.controlMeasureContainer);
        final LinearLayout llGSM = (LinearLayout) getView().findViewById(R.id.gsmMeasureContainer);

        llControl.setVisibility(View.GONE);
        llGSM.setVisibility(View.VISIBLE);

        CheckControlDiff();

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.pages_tabs_gsm);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        llControl.setVisibility(View.GONE);
                        llGSM.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        llControl.setVisibility(View.VISIBLE);
                        llGSM.setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });


        UserDate = "";
        UserShift = 0;
        GSM = 0;

        loadUserInfo();

        GetData();

        timerHandler.postDelayed(timerRunnable, 5000);

    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            try {
                GetData();
            }
            catch (Exception e)
            {
                Log.d("Alexey", "Ошибка заполнения " + e.getMessage());
            }
            timerHandler.postDelayed(this, 5000);
        }
    };

    private void CheckControlDiff ()
    {
        int controlValue = 0;

        String beginValue = etBegin.getText().toString();
        String endValue = etEnd.getText().toString();
        if ((endValue.length()>0)&&(Integer.parseInt(endValue.toString())>0))
        {
            if (endValue.length()>0)
                controlValue = 1050 - (Integer.parseInt(endValue)-Integer.parseInt(beginValue));
        }

        if (controlValue==0) {
            tvControlDiff.setTextColor(Color.parseColor("#32CD32"));
            tvControlDiff.setText("Отклонений нет");
        }
        else
        {
            tvControlDiff.setTextColor(Color.parseColor("#DC143C"));
            tvControlDiff.setText("Отклонение контрольных показаний от данных по выполненным заправкам составляет: " + controlValue + " л.");
        }
    }

    private void GetData()
    {
        LinearLayout rContainer = (LinearLayout) getView().findViewById(R.id.resultContainer);
        rContainer.removeAllViews();

        try {
            int id = 1;

            sqlLiteDatabase.open(this.getActivity());
            String selectQuery = "SELECT DateEvent, EquipIn, EquipOUT, ifnull(Reason,''), ifnull(DT,0), ifnull(SAE15W40,0), ifnull(SAE50,0), ifnull(SAE10W40,0), ifnull(T46,0), ifnull(Confirmed,0), Shift FROM GSM WHERE Deleted IS NULL or Deleted=0 ORDER BY DateEvent DESC";
            Log.d("Alexey", "GetData() selectQuery: " + selectQuery);
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    final String date = cursor.getString(0);
                    String[] splitedHour = date.split("\\s+");
                    String[] splitedDate = splitedHour[0].split("-");
                    final String Date = splitedDate[2]+"."+splitedDate[1]+"."+splitedDate[0];
                    final String DateTime = splitedDate[2]+"."+splitedDate[1]+"."+splitedDate[0]+" " + splitedHour[1].split(":")[0] + ":" + splitedHour[1].split(":")[1];
                    final String EquipIn = cursor.getString(1);
                    final String EquipOut = cursor.getString(2);
                    final String Reason = cursor.getString(3);
                    String Confirmed = "<font color='#F44336'>Нет</font>";
                    final String DT = cursor.getString(4);
                    final String SAE15W40 = cursor.getString(5);
                    final String SAE50 = cursor.getString(6);
                    final String SAE10W40 = cursor.getString(7);
                    final String T46 = cursor.getString(8);
                    final int Shift =  cursor.getInt(10);

                    switch (cursor.getString(9)) {
                        case "1":
                            Confirmed = "да";
                            break;
                        default:
                            Confirmed = "нет";
                    }

                    Log.d("Alexey", "2Notif Confirmed " + Confirmed + ", " + cursor.getString(9));

                    String ansColor = "#E1E2E5";
                    String bgColor = "#444446";

                    final CardView cw = new CardView(this.getActivity());
                    cw.setId(id);

                    cw.setCardBackgroundColor(Color.parseColor(bgColor));
                    //cw.setForeground(getContext().getResources().getDrawable(R.drawable.bg_roundrect_ripple_light_border));
                    ///
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 20, 0, 0);

                    LinearLayout vl = new LinearLayout(this.getActivity());
                    vl.setOrientation(LinearLayout.VERTICAL);

                    TableLayout table = new TableLayout(this.getActivity());
                    table.setStretchAllColumns(true);
                    table.setShrinkAllColumns(true);
                    table.setPadding(20,10,10,0);

                    TableRow row1 = new TableRow(this.getActivity());
                    TableRow row2 = new TableRow(this.getActivity());
                    TableRow row3 = new TableRow(this.getActivity());
                    TableRow row4 = new TableRow(this.getActivity());
                    TableRow row5 = new TableRow(this.getActivity());
                    TableRow row6 = new TableRow(this.getActivity());

                    TextView twR1C1 = new TextView(this.getActivity());
                    twR1C1.setText(Html.fromHtml("<font color='#979797'>Дата/время:</font><font color='" + ansColor + "'> "+ DateTime + "</font>"), TextView.BufferType.SPANNABLE);
                    twR1C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR1C1.setTextSize(18);
                    twR1C1.setHeight(50);

                    TextView twR1C2 = new TextView(this.getActivity());
                    twR1C2.setText(Html.fromHtml("<font color='#979797'>Д/Т:</font><font color='" + ansColor + "'> "+ DT + " л</font>"), TextView.BufferType.SPANNABLE);
                    twR1C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR1C2.setTextSize(16);

                    row1.addView(twR1C1);
                    row1.addView(twR1C2);

                    table.addView(row1);

                    TextView twR2C1 = new TextView(this.getActivity());
                    twR2C1.setText(Html.fromHtml("<font color='#979797'>Заправил:</font><font color='" + ansColor + "'> " + EquipOut + "</font>"), TextView.BufferType.SPANNABLE);
                    twR2C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR2C1.setTextSize(18);
                    twR2C1.setHeight(50);

                    TextView twR2C2 = new TextView(this.getActivity());
                    twR2C2.setText(Html.fromHtml("<font color='#979797'>SAE15W40:</font><font color='" + ansColor + "'> " + SAE15W40 + " л</font>"), TextView.BufferType.SPANNABLE);
                    twR2C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR2C2.setTextSize(16);

                    row2.addView(twR2C1);
                    row2.addView(twR2C2);

                    table.addView(row2);

                    TextView twR3C1 = new TextView(this.getActivity());
                    twR3C1.setText(Html.fromHtml("<font color='#979797'>Оборудование:</font><font color='" + ansColor + "'> " + EquipIn + "</font>"), TextView.BufferType.SPANNABLE);
                    twR3C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR3C1.setTextSize(18);
                    twR3C1.setHeight(50);


                    TextView twR3C2 = new TextView(this.getActivity());
                    twR3C2.setText(Html.fromHtml("<font color='#979797'>SAE50:</font><font color='" + ansColor + "'> " + SAE50 + " л</font>"), TextView.BufferType.SPANNABLE);
                    twR3C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR3C2.setTextSize(18);

                    row3.addView(twR3C1);
                    row3.addView(twR3C2);

                    table.addView(row3);

                    TextView twR4C1 = new TextView(this.getActivity());
                    twR4C1.setText(Html.fromHtml("<font color='#979797'>Основание:</font><font color='" + ansColor + "'> " + Reason + "</font>"), TextView.BufferType.SPANNABLE);
                    twR4C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR4C1.setTextSize(18);
                    twR4C1.setHeight(50);


                    TextView twR4C2 = new TextView(this.getActivity());
                    twR4C2.setText(Html.fromHtml("<font color='#979797'>SAE10W40:</font><font color='" + ansColor + "'> " + SAE10W40 + " л</font>"), TextView.BufferType.SPANNABLE);
                    twR4C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR4C2.setTextSize(18);

                    row4.addView(twR4C1);
                    row4.addView(twR4C2);

                    table.addView(row4);

                    TextView twR5C1 = new TextView(this.getActivity());
                    twR5C1.setText(Html.fromHtml("<font color='#979797'>Подтверждение:</font><font color='" + ansColor + "'> " + Confirmed + "</font>"), TextView.BufferType.SPANNABLE);
                    twR5C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR5C1.setTextSize(18);
                    twR5C1.setHeight(50);


                    TextView twR5C2 = new TextView(this.getActivity());
                    twR5C2.setText(Html.fromHtml("<font color='#979797'>T46:</font><font color='" + ansColor + "'> " + T46 + "  л</font>"), TextView.BufferType.SPANNABLE);
                    twR5C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR5C2.setTextSize(18);

                    row5.addView(twR5C1);
                    row5.addView(twR5C2);

                    table.addView(row5);

                    //STS -- send to server
                    TextView twSTS = new TextView(this.getActivity());
                    twR5C1.setText(Html.fromHtml("<font color='#979797'>Переданно на сервер:</font><font color='" + ansColor + "'> " + Confirmed + "</font>"), TextView.BufferType.SPANNABLE);
                    twR5C1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR5C1.setTextSize(18);
                    twR5C1.setHeight(50);


                    TextView twT86 = new TextView(this.getActivity());
                    twR5C2.setText(Html.fromHtml("<font color='#979797'>T86:</font><font color='" + ansColor + "'> " + T46 + "  л</font>"), TextView.BufferType.SPANNABLE);
                    twR5C2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twR5C2.setTextSize(18);

                    row6.addView(twSTS);
                    row6.addView(twT86);

                    table.addView(row6);

                    vl.addView(table);

                    if (GSM==1) {
                        if ((Date.equals(UserDate)) && (Shift == UserShift)) {

                            LinearLayout.LayoutParams layoutLine = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            layoutLine.setMargins(20, 20, 20, 0);

                            TextView line = new TextView(this.getContext());
                            line.setHeight(2);
                            line.setGravity(Gravity.CENTER_HORIZONTAL);
                            line.setBackgroundColor(Color.parseColor("#909497"));
                            vl.addView(line, layoutLine);

                            LinearLayout hlItog = new LinearLayout(this.getActivity());
                            LinearLayout.LayoutParams lpItog = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            hlItog.setLayoutParams(lpItog);

                            hlItog.setOrientation(LinearLayout.HORIZONTAL);

                            LinearLayout hlIV = new LinearLayout(this.getActivity());
                            hlIV.setOrientation(LinearLayout.HORIZONTAL);
                            hlIV.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);


                            LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            LinearLayout hlDel = new LinearLayout(this.getActivity());

                            ImageView imagebyCode = new ImageView(getContext());
                            imagebyCode.setImageResource(R.drawable.green_point);
                            //imagebyCode.getLayoutParams().height = 10;
                            LinearLayout.LayoutParams ivLP = new LinearLayout.LayoutParams(10, 10);
                            imagebyCode.setLayoutParams(ivLP);
                            hlIV.setPadding(20,35,0,0);
                            hlIV.addView(imagebyCode);


                            hlDel.setOrientation(LinearLayout.HORIZONTAL);
                            hlDel.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                            hlDel.setPadding(450, -10, 20, -10);

                            Button btnDelete = new Button(this.getActivity());

                            btnDelete.setLayoutParams(lpDel);
                            btnDelete.setMinHeight(0);
                            btnDelete.setMinWidth(0);
                            btnDelete.setText("Удалить");
                            btnDelete.setTextSize(16);
                            btnDelete.setAllCaps(false);

                            btnDelete.setClickable(true);
                            btnDelete.setFocusable(true);
                            btnDelete.setHapticFeedbackEnabled(true);
                            btnDelete.setTextColor(Color.parseColor("#FC7E7E"));
                            btnDelete.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.exo_2_light));//setBackgroundColor(Color.TRANSPARENT);//getBackground().setColorFilter(ContextCompat.getColor(this.getActivity(), R.color.colorDarkLight1), PorterDuff.Mode.MULTIPLY);
                            TypedValue outValue = new TypedValue();
                            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                            btnDelete.setBackgroundResource(outValue.resourceId);

                            btnDelete.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Vibrator vibro = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                    // Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibro.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vibro.vibrate(150);
                                    }

                                    showDeleteItemDialog(getContext(), date);
                                }

                            });

                            hlDel.addView(btnDelete);
                            hlItog.addView(hlIV);
                            hlItog.addView(hlDel);

                            vl.addView(hlItog);
                        }
                    }
                    else
                    {
                        //if ((Date.equals(UserDate)) && (Shift == UserShift)) {

                            if (Confirmed.equals("нет")) {

                                LinearLayout.LayoutParams layoutLine = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                layoutLine.setMargins(20, 20, 20, 0);

                                TextView line = new TextView(this.getContext());
                                line.setHeight(2);
                                line.setGravity(Gravity.CENTER_HORIZONTAL);
                                line.setBackgroundColor(Color.parseColor("#909497"));
                                vl.addView(line, layoutLine);

                                LinearLayout.LayoutParams lpConfirm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                LinearLayout hlConfirm = new LinearLayout(this.getActivity());

                                hlConfirm.setOrientation(LinearLayout.HORIZONTAL);
                                hlConfirm.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                                hlConfirm.setPadding(0, -10, 0, -10);

                                final Button btnConfirm = new Button(this.getActivity());
                                btnConfirm.setTransitionName(date);

                                btnConfirm.setLayoutParams(lpConfirm);
                                btnConfirm.setMinHeight(0);
                                btnConfirm.setMinWidth(0);
                                btnConfirm.setText("ПОДТВЕРДИТЬ ЗАПРАВКУ");
                                btnConfirm.setTextSize(16);
                                btnConfirm.setAllCaps(false);

                                btnConfirm.setClickable(true);
                                btnConfirm.setFocusable(true);
                                btnConfirm.setHapticFeedbackEnabled(true);
                                btnConfirm.setTextColor(Color.parseColor("#FC7E7E"));
                                btnConfirm.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.exo_2_light));//setBackgroundColor(Color.TRANSPARENT);//getBackground().setColorFilter(ContextCompat.getColor(this.getActivity(), R.color.colorDarkLight1), PorterDuff.Mode.MULTIPLY);
                                TypedValue outValue = new TypedValue();
                                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                                btnConfirm.setBackgroundResource(outValue.resourceId);

                                btnConfirm.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Vibrator vibro = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                        // Vibrate for 500 milliseconds
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibro.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));


                                        } else {
                                            //deprecated in API 26
                                            vibro.vibrate(150);
                                        }

                                        sqlLiteDatabase.open(getContext());

                                        String insertQuery = "UPDATE GSM SET Confirmed = 1, SendToServer = 0 WHERE DateEvent='" + btnConfirm.getTransitionName() + "'";
                                        //String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer) VALUES ('" + df.format(c) +"','"  + GetUserDate() +  "','" + GetUserShift() + "','" + equipOut + "','" + equipIn +"','" + GetUserName()  + "','','" + valueDT + "','" + valueSAE15W40 + "','" + valueSAE50 + "','" + valueSAE10W40+ "','" + value46 +"','0','0')";

                                        sqlLiteDatabase.database.execSQL(insertQuery);

                                        sqlLiteDatabase.close();

                                        Toast.makeText(getActivity(), "Заправка подтверждена", Toast.LENGTH_SHORT).show();

                                        saveNeedLoadParam(8);

                                        GetData();
                                    }

                                });

                                hlConfirm.addView(btnConfirm);
                                vl.addView(hlConfirm);
                            }
                        //}
                    }

                    cw.addView(vl);

                    rContainer.addView(cw, layoutParams);
                    ///
                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();

        }
        catch (Exception e){
            Log.d("Alexey", "GetData() Error: " + e.getMessage());
        }
    }


    private void showDeleteItemDialog(Context c, final String date) {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.MyAlertDialogTheme))
                .setTitle("Подтвердите")
                .setMessage("Удалить запись?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlLiteDatabase.open(getContext());
                        String updateQuery = "UPDATE GSM SET Deleted=1, SendToServer=0 WHERE DateEvent='" + date + "'";
                        sqlLiteDatabase.database.execSQL(updateQuery);
                        sqlLiteDatabase.close();
                        GetData();
                        saveNeedLoadParam(8);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void saveNeedLoadParam(int needLoad)
    {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    private void loadUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserDate = sPref.getString("UserDate","");
        UserShift = sPref.getInt("UserShift",0);
        GSM = sPref.getInt("GSM",0);
        GSMEquipment = sPref.getString("GSMEquipment","");
    }

}