package com.kazzinc.checklist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences sPref;
    String UserId;
    String UserRole;
    String UserName;
    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        loadUserInfo();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvUser = (TextView) findViewById(R.id.userName);
        tvUser.setText(UserName);

        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btnKey = (Button) findViewById(R.id.btnKey);
        Button btnImprovement = (Button) findViewById(R.id.btnImprovement);
        Button btnVideoLesson = findViewById(R.id.btnVideoLesson);
        Button btnDocs = findViewById(R.id.btnDocs);

        final boolean haveKey = isHaveKey();

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setType("image/jpg");
                intent.setAction (Intent.ACTION_VIEW);
                Uri hacked_uri = Uri.parse(path + "udostoverenie-1.jpg");
                intent.setDataAndType(hacked_uri, "image/jpg");
                startActivity (intent);
            }

        });
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setType("image/jpg");
                intent.setAction (Intent.ACTION_VIEW);
                Uri hacked_uri = Uri.parse(path + "udostoverenie-2.jpeg");
                intent.setDataAndType(hacked_uri, "image/jpg");
                startActivity (intent);
            }

        });
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setType("image/jpg");
                intent.setAction (Intent.ACTION_VIEW);
                Uri hacked_uri = Uri.parse(path + "udostoverenie-3.jpg");
                intent.setDataAndType(hacked_uri, "image/jpg");
                startActivity (intent);
            }

        });
        btnKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (haveKey) {
                    Intent intent = new Intent(AccountActivity.this, KeyActivity.class);
                    startActivity(intent);
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(AccountActivity.this, R.style.MyAlertDialogTheme))
                            .setTitle("Ключ ЭЦП не найден")
                            //.setView(taskEditText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    dialog.show();
                }
            }

        });
        btnImprovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, improvement.class);
                startActivity(intent);
            }

        });

        btnVideoLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, VideoLesson.class);
                startActivity(intent);
            }

        });

        btnDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, Docs.class);
                startActivity(intent);
            }
        });



        verifyStoragePeremissions(AccountActivity.this);

        if (haveKey) {
            btnKey.setText("Ключ зарегистрирован");
            createFile();
        }
        else
            btnKey.setText(Html.fromHtml("<font color='#E74C3C'>Ключ отсутствует</font>"));

    }

    public static void createFile() {
        try {
            String path = "/storage/emulated/0/Key/data.cfg";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream writer = new FileOutputStream(path);
                writer.write(("1234Zz").getBytes());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        getSupportActionBar().setTitle("Личный кабинет");
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

                    sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("Key", name);
                    ed.commit();
                    return true;
                }
            }
            return false;
        }
        else
            return false;


    }
}
