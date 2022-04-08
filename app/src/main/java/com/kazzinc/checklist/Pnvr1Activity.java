package com.kazzinc.checklist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Pnvr1Activity extends AppCompatActivity {
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    SharedPreferences sPref;

    Button btnPeople;
    TextView tvPeople;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mPeopleItems = new ArrayList<>();

    Button btnWorkPlace;
    TextView tvWorkPlace;
    String[] listItemsWP;
    boolean[] checkedItemsWP;
    ArrayList<Integer> mWorkplaceItems = new ArrayList<>();

    Button btnDangers;
    TextView tvDangers;
    String[] listItemsDangers;
    boolean[] checkedItemsDangers;
    ArrayList<Integer> mDangersItems = new ArrayList<>();

    public static final int REQUEST_IMAGE = 100;
    private LinearLayout mPhotos;
    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";
    private int photosCounter;
    String imageName;
    private String imageFilePath = "";
    private String Photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnvr1);

        setTitle("ПНВР");

        Date c = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        final String formattedDate = df.format(c);

        TextView datePNVR = (TextView) findViewById(R.id.DatePNVR);
        datePNVR.setText(formattedDate);



        //Обработка нажатия кнопки ОТМЕНА
        Button btnCancel = (Button) findViewById(R.id.btnPNVRCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(Pnvr1Activity.this, MenuMasterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Обработка нажатия кнопки СОХРАНИТЬ
        Button btnSave = (Button) findViewById(R.id.btnPNVRSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                String userName = sPref.getString("UserName","");
                TextView userDate = (TextView) findViewById(R.id.DatePNVR);//Проверяемый персонал
                int userShift = sPref.getInt("UserShift",2);

                TextView tvCheckedPersonal = (TextView) findViewById(R.id.tvPeople);//Проверяемый персонал
                TextView tvTask = (TextView)findViewById(R.id.tvTaskReview);//Задание
                TextView tvWorkplace = (TextView) findViewById(R.id.tvWorkPlace);//Выбор места работ
                TextView tvResponsible = (TextView) findViewById(R.id.tvResponsible);//Выбор ответственного
                TextView tvTeam = (TextView) findViewById(R.id.tvTeam);//Выбор участников команды
                TextView tvPodrjadOrg = (TextView) findViewById(R.id.tvPodrjad);//Подрядная организация
                TextView tvRiskTool = (TextView) findViewById(R.id.tvTool);//Инструмент оценки рисков
                TextView tvDangers = (TextView) findViewById(R.id.tvDangers);
                TextView tvInstruction  = (TextView) findViewById(R.id.tvInstruction);//ИНСТРУКЦИИ
                SwitchCompat swСonformity = (SwitchCompat) findViewById(R.id.swСonformity);//Соответствие инстркуци
                TextView tvAssessment = (TextView) findViewById(R.id.tvAssessment); //ОБЩАЯ ОЦЕНКА ДЕЯТЕЛЬНОСТИ
                TextView tvShortReview = (TextView)findViewById(R.id.tvShortReview);//Краткий обзор по наблюдению или взаимодействию
                String txtConfirmity = "Да";

                if (swСonformity.isChecked())
                    txtConfirmity = "Да";
                else
                    txtConfirmity = "Нет";
                //

                TextView tvClassBehavior = (TextView) findViewById(R.id.tvClassBehavior); //Нарушение/положительное действие относится к
                TextView tvBehaviorReview = (TextView)findViewById(R.id.tvBehaviorReview);//Описание нарушения/положительного действия
                TextView tvPSO = (TextView) findViewById(R.id.tvPSO);//Класс ПСО
                TextView tvActionReview = (TextView)findViewById(R.id.tvActionReview);//Меры, предпринятые незамедлительно
                TextView tvEvent = (TextView)findViewById(R.id.tvEvent);//Мероприятия

                try {
                    tvDangers = (TextView) findViewById(R.id.tvDangers);

                    sqlLiteDatabase.open(Pnvr1Activity.this);

                    String selectQuery = "INSERT INTO PNVR (User, Date, Shift, CheckedPersonal, Task, Workplace, Responsible, Team, PodrjadOrg, RiskTool, Dangers, Instruction, Сonformity, Assessment, ShortReview, ClassBehavior, BehaviorReview, PSO, ActionReview, Event) VALUES ('"+ userName + "','" + userDate.getText() + "','" + userShift + "','" + tvCheckedPersonal.getText() +"','" + tvTask.getText()  + "','" + tvWorkplace.getText() + "','" + tvResponsible.getText() + "','" + tvTeam.getText() + "','" + tvPodrjadOrg.getText() + "','" + tvRiskTool.getText() + "','" + tvDangers.getText() + "','" + tvInstruction.getText() + "','" + txtConfirmity + "','" + tvAssessment.getText() + "','" + tvShortReview.getText() + "','" + tvClassBehavior.getText() + "','" + tvBehaviorReview.getText() + "','" + tvPSO.getText() + "','" + tvActionReview.getText() + "','" + tvEvent.getText() + "')";
                    Log.d("Alexey",selectQuery);
                    sqlLiteDatabase.database.execSQL(selectQuery);
                    sqlLiteDatabase.close();

                    saveNeedLoadParam(5);
                }
                catch (Exception e){
                    Log.d("Alexey", e.getMessage());
                }

                Intent intent;
                intent = new Intent(Pnvr1Activity.this, MenuMasterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //Выбор опасностей
        btnDangers = (Button) findViewById(R.id.btnDangers);
        tvDangers = (TextView) findViewById(R.id.tvDangers);

        listItemsDangers = getResources().getStringArray(R.array.pnvr_dangers);
        checkedItemsDangers = new boolean[listItemsDangers.length];

        btnDangers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ХАРАКТЕРНЫЕ СМЕРТЕЛЬНЫЕ ОПАСНОСТИ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItemsDangers, checkedItemsDangers, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mDangersItems.add(position);
                        }else{
                            mDangersItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mDangersItems.size(); i++) {
                            item = item + listItemsDangers[mDangersItems.get(i)];
                            if (i != mDangersItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tvDangers.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsDangers.length; i++) {
                            checkedItemsDangers[i] = false;
                            mDangersItems.clear();
                            tvDangers.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        FillAuditPersonal();
        FillResponsible();
        FillTask();
        FillWorkplace();
        FillPodrjad();
        FillTeam();
        FillInstruction();
        FillRiskTool();
        FillAssessment();
        FillShortReview();
        FillClassBehavior();
        FillBehaviorReview();
        FillPSO();
        FillActionReview();
        FillEvent();
    }

    void FillPodrjad() //Подрядная организация
    {
        Button btn;
        final TextView tv;
        final String[] listItems;
        final boolean[] checkedItems;
        final ArrayList<Integer> mItems = new ArrayList<>();


        btn = (Button) findViewById(R.id.btnPodrjad);
        tv = (TextView) findViewById(R.id.tvPodrjad);

        listItems = getResources().getStringArray(R.array.pnvr_podrjad);
        checkedItems = new boolean[listItems.length];

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР ОРГАНИЗАЦИИ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mItems.add(position);
                        }else{
                            mItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mItems.size(); i++) {
                            item = item + listItems[mItems.get(i)];
                            if (i != mItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tv.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItems.clear();
                            tv.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    void FillRiskTool() //Инструмент оценки рисков
    {
        Button btn;
        final TextView tv;
        final String[] listItems;
        final boolean[] checkedItems;
        final ArrayList<Integer> mItems = new ArrayList<>();


        btn = (Button) findViewById(R.id.btnTool);
        tv = (TextView) findViewById(R.id.tvTool);

        listItems = getResources().getStringArray(R.array.pnvr_tool);
        checkedItems = new boolean[listItems.length];

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ИНСТРУМЕНТ ОЦЕНКИ РИСКОВ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mItems.add(position);
                        }else{
                            mItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mItems.size(); i++) {
                            item = item + listItems[mItems.get(i)];
                            if (i != mItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tv.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItems.clear();
                            tv.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    void FillAssessment() //ОБЩАЯ ОЦЕНКА ДЕЯТЕЛЬНОСТИ
    {
        Button btn;
        final TextView tv;
        final String[] listItems;
        final boolean[] checkedItems;
        final ArrayList<Integer> mItems = new ArrayList<>();


        btn = (Button) findViewById(R.id.btnAssessment);
        tv = (TextView) findViewById(R.id.tvAssessment);

        listItems = getResources().getStringArray(R.array.pnvr_assessment);
        checkedItems = new boolean[listItems.length];

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ОБЩАЯ ОЦЕНКА ДЕЯТЕЛЬНОСТИ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mItems.add(position);
                        }else{
                            mItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mItems.size(); i++) {
                            item = item + listItems[mItems.get(i)];
                            if (i != mItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tv.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItems.clear();
                            tv.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    Button btnOpenDialog;
    TextView tvShortReview;

    void FillShortReview() //Краткий обзор по наблюдению или взаимодействию
    {
        btnOpenDialog = (Button)findViewById(R.id.btnShortReview);
        tvShortReview = (TextView)findViewById(R.id.tvShortReview);

        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_text, null);
                final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

                if (!tvShortReview.getText().equals("Не заполнено"))
                    subEditText.setText(tvShortReview.getText());

                subEditText.requestFocus();

                //final ImageView subImageView = (ImageView)subView.findViewById(R.id.image);
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                //subImageView.setImageDrawable(drawable);

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);

                        if (subEditText.getText().length()==0)
                            tvShortReview.setText("Не заполнено");
                        else
                            tvShortReview.setText(subEditText.getText().toString());
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);
                    }
                });

                mBuilder.show();

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

    }

    void FillClassBehavior() //Нарушение/положительное действие относится к
    {
        Button btn;
        final TextView tv;
        final String[] listItems;
        final boolean[] checkedItems;
        final ArrayList<Integer> mItems = new ArrayList<>();


        btn = (Button) findViewById(R.id.btnClassBehavior);
        tv = (TextView) findViewById(R.id.tvClassBehavior);

        listItems = getResources().getStringArray(R.array.pnvr_ClassBehavior);
        checkedItems = new boolean[listItems.length];

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mItems.add(position);
                        }else{
                            mItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mItems.size(); i++) {
                            item = item + listItems[mItems.get(i)];
                            if (i != mItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tv.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItems.clear();
                            tv.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    Button btnTask;
    TextView tvTask;

    void FillTask() //Задание
    {
        btnTask = (Button)findViewById(R.id.btnTaskReview);
        tvTask = (TextView)findViewById(R.id.tvTaskReview);

        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_text, null);
                final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

                if (!tvTask.getText().equals("Не заполнено"))
                    subEditText.setText(tvTask.getText());

                subEditText.requestFocus();

                //final ImageView subImageView = (ImageView)subView.findViewById(R.id.image);
                //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                //subImageView.setImageDrawable(drawable);

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ЗАДАНИЕ:");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);

                        if (subEditText.getText().length()==0)
                            tvTask.setText("Не заполнено");
                        else
                            tvTask.setText(subEditText.getText().toString());
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);
                    }
                });

                mBuilder.show();

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

    }

    Button btnBehaviorReview;
    TextView tvBehaviorReview;

    void FillBehaviorReview() //Описание нарушения/положительного действия
    {
        btnBehaviorReview = (Button)findViewById(R.id.btnBehaviorReview);
        tvBehaviorReview = (TextView)findViewById(R.id.tvBehaviorReview);

        btnBehaviorReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_text, null);
                final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

                if (!tvBehaviorReview.getText().equals("Не заполнено"))
                    subEditText.setText(tvBehaviorReview.getText());

                subEditText.requestFocus();

                //final ImageView subImageView = (ImageView)subView.findViewById(R.id.image);
                //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                //subImageView.setImageDrawable(drawable);

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ОПИСАНИЕ:");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);

                        if (subEditText.getText().length()==0)
                            tvBehaviorReview.setText("Не заполнено");
                        else
                            tvBehaviorReview.setText(subEditText.getText().toString());
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);
                    }
                });

                mBuilder.show();

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

    }

    private void openGalleryIntent() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void openCameraIntent() {
        try {
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);

            /*Intent pictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);

            if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_IMAGE);
            }*/
        }
        catch(Exception e)
        {
            Log.d("Alexey","Запуск камеры  " + e.getMessage ());
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageName = image.getName();
        imageFilePath = image.getAbsolutePath();
        Photos = Photos + image.getName() + ";";
        photosCounter++;

        //ImageView Setup
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.image_attach_3);
        iv.setTransitionName(imageName);
        iv.setId(photosCounter);
        iv.setPadding(0,0,5,0);

        //create a button

        /*final Button btnPhoto = new Button(this);
        btnPhoto.setText("Фото " + photosCounter);
        btnPhoto.setTransitionName(imageName);
        btnPhoto.setId(photosCounter);*/


        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setType("image/jpg");
                intent.setAction (Intent.ACTION_VIEW);
                Uri hacked_uri = Uri.parse(path + iv.getTransitionName());
                intent.setDataAndType(hacked_uri, "image/jpg");
                startActivity (intent);
            }

        });

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                /*Toast.makeText(getApplicationContext(), "Long Clicked " ,
                        Toast.LENGTH_SHORT).show();*/
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                showAddItemDialog(Pnvr1Activity.this, iv.getTransitionName(), iv.getId());
                return true;    // <- set to true
            }
        });

        //mPhotos.addView(btnPhoto);
        mPhotos.addView(iv);

        return image;
    }

    private void showAddItemDialog(Context c, final String photoName, final int photoId) {

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Подтвердите")
                .setMessage("Удалить фото?")
                //.setView(taskEditText)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Photos = Photos.replaceAll(photoName + ";","");
                        ImageView iv = (ImageView) findViewById(photoId);
                        mPhotos.removeView(iv);
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

    void FillPSO() //Класс ПСО
    {
        Button btn;
        final TextView tv;
        final String[] listItems;
        final boolean[] checkedItems;
        final ArrayList<Integer> mItems = new ArrayList<>();


        btn = (Button) findViewById(R.id.btnPSO);
        tv = (TextView) findViewById(R.id.tvPSO);

        listItems = getResources().getStringArray(R.array.pnvr_PSO);
        checkedItems = new boolean[listItems.length];

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("Выбор");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mItems.add(position);
                        }else{
                            mItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mItems.size(); i++) {
                            item = item + listItems[mItems.get(i)];
                            if (i != mItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tv.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Очистить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mItems.clear();
                            tv.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    Button btnActionReview;
    TextView tvActionReview;

    void FillActionReview() //Меры, предпринятые незамедлительно
    {
        btnActionReview = (Button)findViewById(R.id.btnActionReview);
        tvActionReview = (TextView)findViewById(R.id.tvActionReview);

        btnActionReview .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_text, null);
                final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

                if (!tvActionReview.getText().equals("Не заполнено"))
                    subEditText.setText(tvActionReview.getText());

                subEditText.requestFocus();

                //final ImageView subImageView = (ImageView)subView.findViewById(R.id.image);
                //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                //subImageView.setImageDrawable(drawable);

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ОПИСАНИЕ:");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);

                        if (subEditText.getText().length()==0)
                            tvActionReview.setText("Не заполнено");
                        else
                            tvActionReview.setText(subEditText.getText().toString());
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);
                    }
                });

                mBuilder.show();

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

    }

    Button btnEvent;
    TextView tvEvent;

    void FillEvent() //Мероприятия
    {
        btnEvent = (Button)findViewById(R.id.btnEvent);
        tvEvent = (TextView)findViewById(R.id.tvEvent);

        btnEvent .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_event, null);
                final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEventText);

                //final Spinner spinner = (Spinner) subView.findViewById(R.id.employee_spinner_dialog);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Pnvr1Activity.this,
                        R.array.pnvr_people, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                //spinner.setAdapter(adapter);

                final SearchableSpinner spcountries;

                spcountries=(SearchableSpinner) subView.findViewById(R.id.employee_spinner);
                spcountries.setAdapter(adapter);
                spcountries.setTitle("Выбор ответственного");
                spcountries.setPositiveButton("ОК");

                final DatePicker dp = (DatePicker) subView.findViewById(R.id.dateSrok);


                if (!tvEvent.getText().equals("Не заполнено"))
                    subEditText.setText(tvEvent.getText());

                final CheckBox cb = (CheckBox) subView.findViewById(R.id.checkBoxReg);
                //subEditText.requestFocus();

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ДОБАВЛЕНИЕ МЕРОПРИЯТИЯ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String cbResult = "Нет";
                        if (cb.isChecked()) {
                            cbResult = "Да";
                        }
                        else {
                            cbResult = "Нет";
                        }

                        final String cbText = cbResult;

                        if (subEditText.getText().length()==0)
                            tvEvent.setText("Не заполнено");
                        else
                            tvEvent.setText("Описание: " + subEditText.getText().toString() + "; Назначено: " + spcountries.getSelectedItem() + "; Срок исполнения: " + dp.getDayOfMonth() + "." + dp.getMonth() + "." + dp.getYear() + "; Необходимо регистрировать в ИС УО: " + cbText);

                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(subEditText.getWindowToken(), 0);*/
                    }
                });

                mBuilder.show();

                /*InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
            }
        });

    }

    void FillInstruction() //ИНСТРУКЦИИ
    {
        Button btn;
        final TextView tv;

        btn = (Button) findViewById(R.id.btnInstruction);
        tv  = (TextView) findViewById(R.id.tvInstruction);

        LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
        final View subView = inflater.inflate(R.layout.activity_dialog_instruction, null);
        final ListView listView_3 = (ListView) subView.findViewById(R.id.listview_instruction_dialog);
        final TextView tvMsg = (TextView) findViewById(R.id.textview_instruction_msg);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView tvMsg = (TextView) subView.findViewById(R.id.textview_instruction_msg);

                sqlLiteDatabase.open(getApplicationContext());

                //Вид инструкции
                ArrayList<String> listItems_1 = new ArrayList<String>();
                ArrayAdapter<String> adapter_1 = new ArrayAdapter<String>(Pnvr1Activity.this, android.R.layout.simple_spinner_item, listItems_1);
                adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                String query = "SELECT DISTINCT Type FROM Instruction ORDER BY Type";
                Cursor cursor = sqlLiteDatabase.database.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        String User = cursor.getString(0);
                        listItems_1.add(User);
                        Log.d("Alexey", "Заполняем " + User);
                    } while (cursor.moveToNext());
                }

                final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_instruction_type);
                spinner.setAdapter(adapter_1);

                //Участки
                ArrayList<String> listItems_2 = new ArrayList<String>();
                ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(Pnvr1Activity.this, android.R.layout.simple_spinner_item, listItems_2);
                adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                query = "SELECT DISTINCT Area FROM Instruction ORDER BY Area";
                cursor = sqlLiteDatabase.database.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        String User = cursor.getString(0);
                        listItems_2.add(User);
                        Log.d("Alexey", "Заполняем " + User);
                    } while (cursor.moveToNext());
                }

                final Spinner spinnerArea = (Spinner) subView.findViewById(R.id.spinner_instruction_area);
                spinnerArea.setAdapter(adapter_2);

                sqlLiteDatabase.close();

                // Список инструкций


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //Получение списка инструкций из БД
                        sqlLiteDatabase.open(getApplicationContext());

                        ArrayList<String> listItems_3 = new ArrayList<String>();
                        ArrayAdapter<String> adapter_3 = new ArrayAdapter<String>(Pnvr1Activity.this, android.R.layout.simple_spinner_item, listItems_3);
                        adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        String query = "SELECT Name FROM Instruction WHERE Type='" + spinner.getSelectedItem().toString() + "' AND  Area='" + spinnerArea.getSelectedItem().toString()  + "' ORDER BY Name";
                        Cursor cursor = sqlLiteDatabase.database.rawQuery(query, null);
                        Log.d("Alexey", "Список инструкций " + query);

                        if (cursor.moveToFirst()) {
                            do {
                                String Name = cursor.getString(0);
                                listItems_3.add(Name);
                            } while (cursor.moveToNext());
                        }

                        sqlLiteDatabase.close();

                        listItems = GetStringArray(listItems_3);
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView_3.setAdapter(customAdapter);

                        if (listView_3.getAdapter().getCount()==0) {
                            tvMsg.setText("На участке " + spinnerArea.getSelectedItem().toString() + " нет " + spinner.getSelectedItem().toString());
                            tvMsg.setVisibility(View.VISIBLE);
                        }
                        else
                            tvMsg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //
                //
                spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //Получение списка инструкций из БД
                        sqlLiteDatabase.open(getApplicationContext());

                        ArrayList<String> listItems_3 = new ArrayList<String>();
                        ArrayAdapter<String> adapter_3 = new ArrayAdapter<String>(Pnvr1Activity.this, android.R.layout.simple_spinner_item, listItems_3);
                        adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        String query = "SELECT Name FROM Instruction WHERE Type='" + spinner.getSelectedItem().toString() + "' AND  Area='" + spinnerArea.getSelectedItem().toString()  + "' ORDER BY Name";
                        Cursor cursor = sqlLiteDatabase.database.rawQuery(query, null);
                        Log.d("Alexey", "Список инструкций " + query);

                        if (cursor.moveToFirst()) {
                            do {
                                String Name = cursor.getString(0);
                                listItems_3.add(Name);
                                Log.d("Alexey", "Заполняем " + Name);
                            } while (cursor.moveToNext());
                        }

                        sqlLiteDatabase.close();

                        listItems = GetStringArray(listItems_3);
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView_3.setAdapter(customAdapter);

                        if (listView_3.getAdapter().getCount()==0) {
                            tvMsg.setText("На участке " + spinnerArea.getSelectedItem().toString() + " нет " + spinner.getSelectedItem().toString());
                            tvMsg.setVisibility(View.VISIBLE);
                        }
                        else
                            tvMsg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР ИНСТРУКЦИЙ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        String checkItemText = sPref.getString("CheckItemText","");
                        if (checkItemText.length()==0)
                            tv.setText("Не заполнено");
                        else
                            tv.setText(checkItemText);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.show();


            }
        });



    }

    void FillAuditPersonal() //Проверяемый персонал
    {
        Button btn;
        final TextView tv;

        btn = (Button) findViewById(R.id.btnPeople);
        tv = (TextView) findViewById(R.id.tvPeople);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Pnvr1Activity.this, DialogCheckboxActivity.class);
                //startActivity(intent);

                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_checkbox, null);
                //
                //SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
                final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_alert_dialog);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Pnvr1Activity.this,
                        R.array.pnvr_area, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // initiate a ListView
                final ListView listView = (ListView) subView.findViewById(R.id.listview_alert_dialog);

                listItems = getResources().getStringArray(R.array.pnvr_people_or);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*if (spinner.getSelectedItem().equals("Инструкции ОТиТБ"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);
                if (spinner.getSelectedItem().equals("Рабочие инструкции"))
                    listItems = getResources().getStringArray(R.array.pnvr_insraction_ri);
                if (spinner.getSelectedItem().equals("ПВР и НШС"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_pvr);*/

                        switch (position) {
                            case 0:
                                listItems = getResources().getStringArray(R.array.pnvr_people_or);
                                break;
                            case 1:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gpr);
                                break;
                            case 2:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bvr);
                                break;
                            case 3:
                                listItems = getResources().getStringArray(R.array.pnvr_people_vsht);
                                break;
                            case 4:
                                listItems = getResources().getStringArray(R.array.pnvr_people_shpshv);
                                break;
                            case 5:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzr);
                                break;
                            case 6:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzk);
                                break;
                            case 7:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gkr);
                                break;
                            case 8:
                                listItems = getResources().getStringArray(R.array.pnvr_people_smr);
                                break;
                            case 9:
                                listItems = getResources().getStringArray(R.array.pnvr_people_asutp);
                                break;
                            case 10:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gsho);
                                break;
                            case 11:
                                listItems = getResources().getStringArray(R.array.pnvr_people_pvs);
                                break;
                            case 12:
                                listItems = getResources().getStringArray(R.array.pnvr_people_sho);
                                break;
                            case 13:
                                listItems = getResources().getStringArray(R.array.pnvr_people_energo);
                                break;
                            case 14:
                                listItems = getResources().getStringArray(R.array.pnvr_people_hoz);
                                break;
                        }

                        // set the adapter to fill the data in ListView
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР СОТРУДНИКОВ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        String checkItemText = sPref.getString("CheckItemText","");
                        if (checkItemText.length()==0)
                            tv.setText("Не заполнено");
                        else
                            tv.setText(checkItemText);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.show();
            }
        });

    }

    void FillTeam() //Выбор участников команды
    {
        Button btn;
        final TextView tv;

        btn = (Button) findViewById(R.id.btnTeam);
        tv = (TextView) findViewById(R.id.tvTeam);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Pnvr1Activity.this, DialogCheckboxActivity.class);
                //startActivity(intent);

                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_checkbox, null);
                //
                //SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
                final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_alert_dialog);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Pnvr1Activity.this,
                        R.array.pnvr_area, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // initiate a ListView
                final ListView listView = (ListView) subView.findViewById(R.id.listview_alert_dialog);

                listItems = getResources().getStringArray(R.array.pnvr_people_or);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*if (spinner.getSelectedItem().equals("Инструкции ОТиТБ"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);
                if (spinner.getSelectedItem().equals("Рабочие инструкции"))
                    listItems = getResources().getStringArray(R.array.pnvr_insraction_ri);
                if (spinner.getSelectedItem().equals("ПВР и НШС"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_pvr);*/

                        switch (position) {
                            case 0:
                                listItems = getResources().getStringArray(R.array.pnvr_people_or);
                                break;
                            case 1:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gpr);
                                break;
                            case 2:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bvr);
                                break;
                            case 3:
                                listItems = getResources().getStringArray(R.array.pnvr_people_vsht);
                                break;
                            case 4:
                                listItems = getResources().getStringArray(R.array.pnvr_people_shpshv);
                                break;
                            case 5:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzr);
                                break;
                            case 6:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzk);
                                break;
                            case 7:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gkr);
                                break;
                            case 8:
                                listItems = getResources().getStringArray(R.array.pnvr_people_smr);
                                break;
                            case 9:
                                listItems = getResources().getStringArray(R.array.pnvr_people_asutp);
                                break;
                            case 10:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gsho);
                                break;
                            case 11:
                                listItems = getResources().getStringArray(R.array.pnvr_people_pvs);
                                break;
                            case 12:
                                listItems = getResources().getStringArray(R.array.pnvr_people_sho);
                                break;
                            case 13:
                                listItems = getResources().getStringArray(R.array.pnvr_people_energo);
                                break;
                            case 14:
                                listItems = getResources().getStringArray(R.array.pnvr_people_hoz);
                                break;
                        }

                        // set the adapter to fill the data in ListView
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР СОТРУДНИКОВ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        String checkItemText = sPref.getString("CheckItemText","");
                        if (checkItemText.length()==0)
                            tv.setText("Не заполнено");
                        else
                            tv.setText(checkItemText);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.show();
            }
        });

    }

    void FillResponsible() //Выбор ответственного
    {
        Button btn;
        final TextView tv;

        btn = (Button) findViewById(R.id.btnResponsible);
        tv = (TextView) findViewById(R.id.tvResponsible);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Pnvr1Activity.this, DialogCheckboxActivity.class);
                //startActivity(intent);

                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_checkbox, null);
                //
                //SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
                final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_alert_dialog);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Pnvr1Activity.this,
                        R.array.pnvr_area, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // initiate a ListView
                final ListView listView = (ListView) subView.findViewById(R.id.listview_alert_dialog);

                listItems = getResources().getStringArray(R.array.pnvr_people_or);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*if (spinner.getSelectedItem().equals("Инструкции ОТиТБ"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);
                if (spinner.getSelectedItem().equals("Рабочие инструкции"))
                    listItems = getResources().getStringArray(R.array.pnvr_insraction_ri);
                if (spinner.getSelectedItem().equals("ПВР и НШС"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_pvr);*/

                        switch (position) {
                            case 0:
                                listItems = getResources().getStringArray(R.array.pnvr_people_or);
                                break;
                            case 1:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gpr);
                                break;
                            case 2:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bvr);
                                break;
                            case 3:
                                listItems = getResources().getStringArray(R.array.pnvr_people_vsht);
                                break;
                            case 4:
                                listItems = getResources().getStringArray(R.array.pnvr_people_shpshv);
                                break;
                            case 5:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzr);
                                break;
                            case 6:
                                listItems = getResources().getStringArray(R.array.pnvr_people_bzk);
                                break;
                            case 7:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gkr);
                                break;
                            case 8:
                                listItems = getResources().getStringArray(R.array.pnvr_people_smr);
                                break;
                            case 9:
                                listItems = getResources().getStringArray(R.array.pnvr_people_asutp);
                                break;
                            case 10:
                                listItems = getResources().getStringArray(R.array.pnvr_people_gsho);
                                break;
                            case 11:
                                listItems = getResources().getStringArray(R.array.pnvr_people_pvs);
                                break;
                            case 12:
                                listItems = getResources().getStringArray(R.array.pnvr_people_sho);
                                break;
                            case 13:
                                listItems = getResources().getStringArray(R.array.pnvr_people_energo);
                                break;
                            case 14:
                                listItems = getResources().getStringArray(R.array.pnvr_people_hoz);
                                break;
                        }

                        // set the adapter to fill the data in ListView
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР СОТРУДНИКОВ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        String checkItemText = sPref.getString("CheckItemText","");
                        if (checkItemText.length()==0)
                            tv.setText("Не заполнено");
                        else
                            tv.setText(checkItemText);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.show();
            }
        });

    }

    void FillWorkplace() //Выбор места работ
    {
        Button btn;
        final TextView tv;

        btn = (Button) findViewById(R.id.btnWorkPlace);
        tv = (TextView) findViewById(R.id.tvWorkPlace);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Pnvr1Activity.this, DialogCheckboxActivity.class);
                //startActivity(intent);

                LayoutInflater inflater = LayoutInflater.from(Pnvr1Activity.this);
                View subView = inflater.inflate(R.layout.activity_dialog_checkbox, null);
                //
                //SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
                final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_alert_dialog);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Pnvr1Activity.this,
                        R.array.pnvr_workplace_type, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                // initiate a ListView
                final ListView listView = (ListView) subView.findViewById(R.id.listview_alert_dialog);

                listItems = getResources().getStringArray(R.array.pnvr_workplace_nz);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                listItems = getResources().getStringArray(R.array.pnvr_workplace_nz);
                                break;
                            case 1:
                                listItems = getResources().getStringArray(R.array.pnvr_workplace_all);
                                break;
                        }

                        // set the adapter to fill the data in ListView
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                        listView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        // sometimes you need nothing here
                    }
                });
                //

                ContextThemeWrapper cw = new ContextThemeWrapper( Pnvr1Activity.this, R.style.MyAlertDialogTheme14 );
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
                TextView title = new TextView(Pnvr1Activity.this);
                title.setText("ВЫБОР СОТРУДНИКОВ");
                /*title.setBackgroundResource(R.drawable.gradient);
                title.setGravity(Gravity.CENTER);*/
                title.setPadding(40, 20, 0, 0);
                title.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                title.setTextSize(14);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                mBuilder.setCustomTitle(title);
                //mBuilder.setTitle("КРАТКИЙ ОБЗОР ПО НАБЛЮДЕНИЮ ИЛИ ВЗАИМОДЕЙСТВИЮ:");
                //builder.setMessage("Краткий обзор по налюдению или взаимодействию");
                mBuilder.setView(subView);
                AlertDialog alertDialog = mBuilder.create();

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        String checkItemText = sPref.getString("CheckItemText","");
                        if (checkItemText.length()==0)
                            tv.setText("Не заполнено");
                        else
                            tv.setText(checkItemText);

                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("CheckItemText", String.valueOf(""));
                        ed.commit();
                    }
                });

                mBuilder.show();
            }
        });

    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    // Function to convert ArrayList<String> to String[]
    public static String[] GetStringArray(ArrayList<String> arr)
    {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }
}

