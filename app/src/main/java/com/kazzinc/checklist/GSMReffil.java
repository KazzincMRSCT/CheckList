package com.kazzinc.checklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GSMReffil extends AppCompatActivity {

    SharedPreferences sPref;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    double valueDT,valueSAE15W40,valueSAE50,valueSAE10W40,value46,value86;
    SimpleDateFormat df;
    SimpleDateFormat dfDate;
    Date c;
    String equipOut="";
    String equipIn="";
    String area="";
    Spinner spinnerDT;
    Spinner spinnerOil;

    EditText etDT;
    EditText etSAE15W40;
    EditText etSAE50;
    EditText etSAE10W40;
    EditText et46;
    TextView etDescription;
    TextView et86;
    Button btnAvrReasonEdit;

    TextView tvDT;
    TextView tvSAE15W40;
    TextView tvSAE50;
    TextView tvSAE10W40;
    TextView tv46;
    TextView tv86;

    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_reffil);


        equipIn = getIntent().getStringExtra("inputEquipment");
        area = getIntent().getStringExtra("inputArea");
        setTitle("Заправка " + equipIn);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayAdapter<String> adapterDT = new ArrayAdapter<String>(this, R.layout.dropdown_list, R.id.list_content1, getReasonDT());
        final ArrayAdapter<String> adapterOil = new ArrayAdapter<String>(this, R.layout.dropdown_list, R.id.list_content1, getReasonOil());
        spinnerDT = (Spinner) findViewById(R.id.spReasonDT);
        spinnerDT.setAdapter(adapterDT);

        spinnerOil = (Spinner) findViewById(R.id.spReasonOil);
        spinnerOil.setAdapter(adapterOil);

        etDescription = (TextView) findViewById(R.id.etDescription);
        btnAvrReasonEdit = (Button) findViewById(R.id.btnAvrReasonEdit);
        btnAvrReasonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillAvrReason();
            }
        });

        spinnerOil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value =  adapterOil.getItem(i);

                if (value.equals("Аварийная замена")||value.equals("Аварийная доливка")) {
                    //Intent intent = new Intent(GSMReffil.this, AvrReasonActivity.class);
                    //startActivity(intent);
                    etDescription.setVisibility(View.VISIBLE);
                    btnAvrReasonEdit.setVisibility(View.VISIBLE);

                    /////////////////
                    FillAvrReason();
                    ///////////

                }
                else {
                    etDescription.setVisibility(View.GONE);
                    btnAvrReasonEdit.setVisibility(View.GONE);
                }


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });




        /*final String[] spisok = getResources().getStringArray(R.array.prem);
        spinner = (Spinner) findViewById(R.id.spReason);
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.dropdown_list, R.id.list_content1, spisok));*/

        TabLayout tabLayout = (TabLayout) findViewById(R.id.pages_tabs);

        etDT = (EditText) findViewById(R.id.etDT);
        etSAE15W40 = (EditText) findViewById(R.id.etSAE15W40);
        etSAE50 = (EditText) findViewById(R.id.etSAE50);
        etSAE10W40 = (EditText) findViewById(R.id.etSAE10W40);
        et46 = (EditText) findViewById(R.id.et46);
        et86 = (EditText) findViewById(R.id.et86);


        tvDT = (TextView) findViewById(R.id.tvDT);
        tvSAE15W40 = (TextView) findViewById(R.id.tvSAE15W40);
        tvSAE50 = (TextView) findViewById(R.id.tvSAE50);
        tvSAE10W40 = (TextView) findViewById(R.id.tvSAE10W40);
        tv46 = (TextView) findViewById(R.id.tv46);
        tv86 = (TextView) findViewById(R.id.tv86);

        spinnerDT.setVisibility(View.VISIBLE);
        spinnerOil.setVisibility(View.GONE);
        etDT.setVisibility(View.VISIBLE);
        tvDT.setVisibility(View.VISIBLE);
        etSAE15W40.setVisibility(View.GONE);
        tvSAE15W40.setVisibility(View.GONE);
        etSAE50.setVisibility(View.GONE);
        tvSAE50.setVisibility(View.GONE);
        etSAE10W40.setVisibility(View.GONE);
        tvSAE10W40.setVisibility(View.GONE);
        et46.setVisibility(View.GONE);
        tv46.setVisibility(View.GONE);
        tv86.setVisibility(View.GONE);
        et86.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
        btnAvrReasonEdit.setVisibility(View.GONE);

        valueDT = valueSAE15W40 = valueSAE50 = valueSAE10W40 = value46 = value86 = 0;

        c = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        //df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        dfDate = new SimpleDateFormat("YYYY-MM-dd 00:00:00");


        sqlLiteDatabase.open(GSMReffil.this);
        String selectQuery = "SELECT TaskEquipName FROM Task WHERE length(TaskEquipName)>0 AND TaskEquipName IS NOT NULL";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                equipOut = cursor.getString(0);
            }
            while (cursor.moveToNext());
        }
        sqlLiteDatabase.close();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        spinnerOil.setVisibility(View.GONE);
                        etDT.setVisibility(View.VISIBLE);
                        etSAE15W40.setVisibility(View.GONE);
                        etSAE50.setVisibility(View.GONE);
                        etSAE10W40.setVisibility(View.GONE);
                        et46.setVisibility(View.GONE);
                        et86.setVisibility(View.GONE);
                        etDescription.setVisibility(View.GONE);
                        btnAvrReasonEdit.setVisibility(View.GONE);

                        spinnerDT.setVisibility(View.VISIBLE);
                        tvDT.setVisibility(View.VISIBLE);
                        tvSAE15W40.setVisibility(View.GONE);
                        tvSAE50.setVisibility(View.GONE);
                        tvSAE10W40.setVisibility(View.GONE);
                        tv46.setVisibility(View.GONE);
                        tv86.setVisibility(View.GONE);

                        break;
                    case 1:
                        spinnerOil.setVisibility(View.VISIBLE);
                        etDT.setVisibility(View.GONE);
                        etSAE15W40.setVisibility(View.VISIBLE);
                        etSAE50.setVisibility(View.VISIBLE);
                        etSAE10W40.setVisibility(View.VISIBLE);
                        et46.setVisibility(View.VISIBLE);
                        et86.setVisibility(View.VISIBLE);

                        if (spinnerOil.getSelectedItem().toString().equals("Аварийная замена")||spinnerOil.getSelectedItem().toString().equals("Аварийная доливка")) {
                            if (spinnerOil.getSelectedItem().toString().equals("Аварийная замена"))
                                etDescription.setHint("Причина аварийной замены");
                            else
                                etDescription.setHint("Причина аварийной доливки");
                            etDescription.setVisibility(View.VISIBLE);
                            btnAvrReasonEdit.setVisibility(View.VISIBLE);
                        }
                        else {
                            etDescription.setVisibility(View.GONE);
                            btnAvrReasonEdit.setVisibility(View.GONE);
                        }

                        spinnerDT.setVisibility(View.GONE);
                        tvDT.setVisibility(View.GONE);
                        tvSAE15W40.setVisibility(View.VISIBLE);
                        tvSAE50.setVisibility(View.VISIBLE);
                        tvSAE10W40.setVisibility(View.VISIBLE);
                        tv46.setVisibility(View.VISIBLE);
                        tv86.setVisibility(View.VISIBLE);

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
    }

    private void FillAvrReason(){
        LayoutInflater inflater = LayoutInflater.from(GSMReffil.this);
        View subView = inflater.inflate(R.layout.activity_dialog_checkbox, null);

        //
        //SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
        final Spinner spinner = (Spinner) subView.findViewById(R.id.spinner_alert_dialog);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(GSMReffil.this,
                R.array.avr_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // initiate a ListView
        final ListView listView = (ListView) subView.findViewById(R.id.listview_alert_dialog);

        listItems = getResources().getStringArray(R.array.avr_rvd);


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
                        listItems = getResources().getStringArray(R.array.avr_rvd);
                        break;
                    case 1:
                        listItems = getResources().getStringArray(R.array.avr_uzly);
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

        ContextThemeWrapper cw = new ContextThemeWrapper( GSMReffil.this, R.style.MyAlertDialogTheme14 );
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(cw);
        TextView title = new TextView(GSMReffil.this);
        title.setText("ВЫБОР ПРИЧИНЫ УТЕЧКИ");
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
                etDescription.setTextColor(getResources().getColor(R.color.colorGreen));
                if (checkItemText.length()==0) {
                    etDescription.setText("");
                } else {
                    etDescription.setText(checkItemText);

                    //etDescription.setPadding(25,0,0,0);
                }
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
                //etDescription.setText("Не заполнено");
                ed.commit();
            }
        });

        mBuilder.show();
    }

    private ArrayList<String> getReasonDT() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT Description FROM GSMReason WHERE ReasonType='ДТ'";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<String> getReasonOil() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT Description FROM GSMReason WHERE ReasonType='Масло'";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GSMSave:

                try {
                    String reasonDT = "";
                    String reasonOil = "";

                    if (etDT.getText().toString().trim().length() > 0) {
                        valueDT = Double.parseDouble(etDT.getText().toString());
                        reasonDT = spinnerDT.getSelectedItem().toString();
                    }

                    if (etSAE15W40.getText().toString().trim().length() > 0) {
                        valueSAE15W40 = Double.parseDouble(etSAE15W40.getText().toString());
                    }
                    else {
                        valueSAE15W40 = 0;
                    }

                    if (etSAE50.getText().toString().trim().length() > 0) {
                        valueSAE50 = Double.parseDouble(etSAE50.getText().toString());
                    }
                    else {
                        valueSAE50 = 0;
                    }

                    if (etSAE10W40.getText().toString().trim().length() > 0) {
                        valueSAE10W40 = Double.parseDouble(etSAE10W40.getText().toString());
                    }
                    else {
                        valueSAE10W40 = 0;
                    }

                    if (et46.getText().toString().trim().length() > 0) {
                        value46 = Double.parseDouble(et46.getText().toString());
                    }
                    else {
                        value46 = 0;
                    }

                    if (et86.getText().toString().trim().length() > 0) {
                        value86 = Double.parseDouble(et86.getText().toString());
                    }
                    else {
                        value86 = 0;
                    }

                    if ((valueSAE15W40>0)||(valueSAE50>0)||(valueSAE10W40>0)||(value46>0)) {
                        reasonOil = spinnerOil.getSelectedItem().toString();
                    }

                    boolean getSave = false;

                    if (reasonOil.equals("Аварийная замена")||reasonOil.equals("Аварийная доливка"))
                    {
                        if (etDescription.getText().length()==0)
                            showTopToastShort("Не указана причина");
                        else {
                            getSave = true;
                            reasonOil += " (" + etDescription.getText() + ")";
                        }
                    }
                    else {
                        getSave = true;
                    }

                    if (getSave) {
                        sqlLiteDatabase.open(GSMReffil.this);

                        String[] array = GetUserDate().split("\\.");
                        String dateShiftActual = array[2]+"-"+array[1]+"-"+array[0]+" 00:00:00";

                        String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer, Confirmed, ReasonOil, T86,DT2) " +
                                "VALUES ('" + df.format(c) + "','" + dateShiftActual + "','" + GetUserShift() + "','" + equipOut + "','" + equipIn + "','" + GetUserName() + "','" + reasonDT + "','" + valueDT + "','" + valueSAE15W40 + "','" + valueSAE50 + "','" + valueSAE10W40 + "','" + value46 + "','0','0', '0','" + reasonOil + "','" + value86 +  "','0.0')";
                        //String insertQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, SendToServer) VALUES ('" + df.format(c) +"','"  + GetUserDate() +  "','" + GetUserShift() + "','" + equipOut + "','" + equipIn +"','" + GetUserName()  + "','','" + valueDT + "','" + valueSAE15W40 + "','" + valueSAE50 + "','" + valueSAE10W40+ "','" + value46 +"','0','0')";

                        sqlLiteDatabase.database.execSQL(insertQuery);

                        sqlLiteDatabase.close();

                        //saveNeedLoadParam(8);

                        Intent intent = new Intent(getApplication(), MenuActivity.class);
                        intent.putExtra("inputPage", "gsm");
                        startActivity(intent);
                        finish();
                        showBottomToastShort("Сохранено");
                    }

                }
                catch (Exception e){
                    Log.d("QweReq", e.getMessage());
                }

                return true;
            case android.R.id.home:
                Intent intent = new Intent (GSMReffil.this, GSMEquipment.class);
                intent.putExtra("inputArea", area);
                startActivity (intent);
                finish();
                return true;
            /*case R.id.help:
                showHelp();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showBottomToastShort(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(GSMReffil.this, msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
            }
        });
    }

    public void showTopToastShort(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(GSMReffil.this, msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.TOP, 0, 0);
                toast1.show();
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

    private String GetUserName()
    {
        SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        return sPref.getString("UserName","");
    }

    private int GetUserShift()
    {
        SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        return sPref.getInt("UserShift",0);
    }
    private String GetUserDateNewFormat()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userDate = sPref.getString("UserDateNewFormat","");
        return userDate;
    }
    private String GetUserDate()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userDate = sPref.getString("UserDate","");
        return userDate;
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent (GSMReffil.this, GSMEquipment.class);
        intent.putExtra("inputArea", area);
        startActivity (intent);
        finish();
    }
}
