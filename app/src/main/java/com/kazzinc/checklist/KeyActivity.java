package com.kazzinc.checklist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class KeyActivity extends AppCompatActivity {

    private MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
    StringBuilder pswd = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Дополнительно");

        final TextView tvPswd = (TextView) findViewById(R.id.etKeyPassword);
        ImageView ivShowPswd = (ImageView) findViewById(R.id.ivShowPswd);

        GetPassword();

        tvPswd.setText(pswd);

        Button btnSave = (Button) findViewById(R.id.btnSaveKeyPassword);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Files", "File Write: ОК1");

                try {

                    String txt = tvPswd.getText().toString();

                    String path = "/storage/emulated/0/Key/data.cfg";

                    FileOutputStream writer = new FileOutputStream(path);
                    writer.write((txt).getBytes());
                    writer.close();

                    Toast toast1 = Toast.makeText(KeyActivity.this, "Пароль сохранен", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.BOTTOM, 0, 150);
                    toast1.show();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });

        Button btnChekKey = (Button) findViewById(R.id.btnChekKey);
        btnChekKey.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                GetPassword();
                (new KeyActivity.CheckKey()).execute();
            }
        });

        ivShowPswd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==MotionEvent.ACTION_DOWN)
                    tvPswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                if(arg1.getAction() == MotionEvent.ACTION_UP){
                    tvPswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); //finger was lifted
                }

                return true;
            }
        });


    }

    protected void GetPassword()
    {
        File file = new File("/storage/emulated/0/Key", "data.cfg");
        //Read text from file
            pswd.setLength(0);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                pswd.append(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }
    //Обновление данных с сервера
    class CheckKey extends AsyncTask<Void, Void, String> {
        private Context mContext;
        private View rootView;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        TextView tvKeyInfo = (TextView) findViewById(R.id.tvKeyInfo);

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(Void... params) {
            //если есть связь с удаленной бд то обновляем иначе вывод ошибки

//            TextView tvKeyInfo = (TextView) findViewById(R.id.tvKeyInfo);
//
//            tvKeyInfo.setText("Пожалуйста, подождите...");

            String result = "";

            if (msSqlDatabase.checkConnection()) {
                try {

                    try {

                        boolean haveKey = isHaveKey();
                        if (haveKey) {
                            //Читаем пароль от сертификата ЭЦП
                            File file = new File("/storage/emulated/0/Key", "data.cfg");
                            StringBuilder pswd = new StringBuilder();
                            String line;

                            try {
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                while ((line = br.readLine()) != null) {
                                    pswd.append(line);
                                }
                                br.close();
                            } catch (IOException e) {
                                //You'll need to add proper error handling here
                            }
                        }

                        SharedPreferences sPref;
                        String UserKey = "";
                        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                        UserKey = sPref.getString("Key", "0");

                        String key = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("/storage/emulated/0/Key/" + UserKey)));



                        MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
                        result = msSqlDatabase.keyInfo(key,pswd.toString());
                        String result0 = result.replace("\"","");

                        String[] resultArray = result0.split("\\|");

                        switch (resultArray[0])
                        {
                            case "0":
                                result = "<strong><font color='#8BC34A'>Проверка выполнена успешно.</font></strong><br><font color='#979797'>Ключ действителен до:  " + resultArray[2] + "</font>";
                                break;
                            default:
                                result = "<strong><font color='#F44336'>Ошибка</font></strong><font color='#979797'><br>" +resultArray[1] + "</font>";
                                break;
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    Log.d("Alexey", "LoginActivity (Error 1): " + e.getMessage());
                }
            }
            else
            {
                result = "Нет подключения к WiFi";
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvKeyInfo.setTextColor(Color.parseColor("#979797"));
            tvKeyInfo.setText("Пожалуйста, подождите...");
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
                tvKeyInfo.setText(Html.fromHtml(s));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean isHaveKey() {
            String path = "/storage/emulated/0/Key";
            Log.d("Files", "Path: " + path);

            if (Files.exists(Paths.get(path))) {
                File directory = new File(path);
                File[] files = directory.listFiles();
                Log.d("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    String name = files[i].getName();
                    Log.d("Files", "FileName:" + files[i].getName());
                    String filePath = path + "/" + files[i].getName();
                    String extension = filePath.substring(filePath.lastIndexOf(".")).replace(".", "");
                    Log.d("Files", "File Ex: " + extension);

                    boolean isBBegin = name.startsWith("RSA");

                    if ((isBBegin) && (extension.equals("p12"))) {
                        return true;
                    }
                }
                return false;
            }
            else
                return false;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
