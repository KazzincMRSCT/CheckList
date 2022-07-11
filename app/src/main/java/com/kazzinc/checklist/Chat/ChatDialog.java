package com.kazzinc.checklist.Chat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.MenuActivity;
import com.kazzinc.checklist.PhotoRealPath;
import com.kazzinc.checklist.R;
import com.kazzinc.checklist.SqlLiteDatabase;
import com.kazzinc.checklist.SyncService;
import com.kazzinc.checklist.improvement_step3;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatDialog extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Handler timerHandler = new Handler();

    private String userName = "";
    private int chatUserTabNum;

    SharedPreferences sPref;
    String UserId;
    String UserRole;
    String UserName;
    //private int chatStatus;

    private int ErrorCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_dialog);

            EditText etSendMsg = findViewById(R.id.etSendMsg);
            Button btnSendMsg = findViewById(R.id.btnSendMsg);
            Button btnPlus = findViewById(R.id.btnPlus);


            //Открытие галереии
            btnPlus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //Camera
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(Intent.createChooser(takePicture, "Select Picture"), 0);//zero can be replaced with any action code
                    ///Photo
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.setType("image/*");
                    pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//Выбор несколько фото (разрешение ставить более 1 чекпоинта)
                    //pickPhoto.setAction(Intent.ACTION_GET_CONTENT);//установка стандарта выбора фото из ФМ
                    startActivityForResult(Intent.createChooser(pickPhoto, "Выбор фото"), 1);//one can be replaced with any action code
                }
            });


            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (etSendMsg.getText().toString().trim().length() > 0) {
                        setMsgChat(String.valueOf(etSendMsg.getText()));
                        etSendMsg.getText().clear();

                        ScrollView svCont = findViewById(R.id.svCont);
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
            });

            boolean result= isMyServiceRunning(SyncService.class);

            if (!result)
            {
                Intent serviceIntent = new Intent(this, SyncService.class);
                serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

                ContextCompat.startForegroundService(this, serviceIntent);
            }

            loadUserInfo();

            ErrorCount = 1;

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);// set drawable icon
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Bundle arguments = getIntent().getExtras();

            ScrollView svCont = findViewById(R.id.svCont);

            svCont.post(new Runnable() {
                @Override
                public void run() {
                    svCont.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            try {
                userName = arguments.get("userName").toString();
                chatUserTabNum = Integer.parseInt(String.valueOf(arguments.get("chatUserTabNum")));
                //chatStatus = Integer.parseInt(String.valueOf(arguments.get("chatStatus")));


            } catch (Exception e) {
                Log.d("Alexey", e.getMessage());
            }

            getSupportActionBar().setTitle(userName);


            doTask();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Alexey", "Chat Dialog (Global Error 1): " + e.getMessage());
        }


    }


    //получение списка с uri путями
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> listURI = new ArrayList<>();

        switch (requestCode) {
            case 1:
            case 0:
                if (resultCode == RESULT_OK) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            setMsgChat("img$" + imageUri);
                            String fileName = new File(imageUri.getPath()).getName();
                            //fileName = uri.pathSegments.last());
//                            Log.d("Alexey", "Chat Dialog Name: " + fileName);
                        }
                    }
                }
        }
    }



    void doTask()
    {
        try {
            Log.d("Alexey", "Chat Dialog (Search Error 1)");
            timerHandler.postDelayed(timerRunnable, 0);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            try {
                Log.d("Alexey", "Chat Dialog (Search Error 2)");
                LinearLayout linearLayout = findViewById(R.id.llCont);
                linearLayout.removeAllViews();

                inboxMg(chatUserTabNum);

                ScrollView svCont = findViewById(R.id.svCont);

                svCont.post(new Runnable() {
                    @Override
                    public void run() {
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                Log.d("Alexey", "Chat Dialog (Search Error End)");

                timerHandler.postDelayed(this, 2000);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Alexey", "Chat Dialog (Global Error): " + e.getMessage());
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                intent.putExtra("inputPage", "chat");
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void inboxMg(int chatUserTabNum){

        try {
            int id = 1;
            sqlLiteDatabase.open(this);
            String selectQuery = "";

            ErrorCount++;
            Log.d("Alexey", "Chat Dialog (Search Error 3) " + ErrorCount);

            selectQuery = "SELECT * FROM Chat WhERE UserTNFrom="+chatUserTabNum + " OR UserTNTo="+chatUserTabNum;
            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            cursor.getCount();
            int i=0;
            if (cursor.moveToFirst()) {
                do {
                    i++;
                    Log.d("Alexey", "Chat Dialog (Search Error 31)");
                    final CardView cw = new CardView(this);
                    cw.setId(id);
                    cw.setClipToOutline(true);

                    LinearLayout llCont = findViewById(R.id.llCont);

                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    //layoutParams.gravity=Gravity.RIGHT;
                    //llp.gravity=Gravity.RIGHT;

                    layoutParams.setMargins(15, 15, 15, 0);

                    Log.d("Alexey", "Chat Dialog (Search Error 32)");

                    LinearLayout ll = new LinearLayout(this);
                    ll.setLayoutParams(llp);

                    String msg = cursor.getString(6);

                    if (msg.contains("img$"))
                    {
                        ImageView imageView = new ImageView(this);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        if (UserName.contains("Алипов"))
                            imageView.setImageURI(Uri.parse("/storage/emulated/0/DCIM/Screenshots/Screenshot_20220603-073526_-.jpg"));
                        else
                            imageView.setImageURI(Uri.parse(msg.replace("img$","")));
                        LinearLayout.LayoutParams lpiv = new LinearLayout.LayoutParams (400, 400);
                        ll.addView(imageView, lpiv);

                        imageView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction (Intent.ACTION_VIEW);
//                                intent.setDataAndType(Uri.parse(msg.replace("img$","")), "image/jpg");
                                intent.setDataAndType(Uri.parse(msg.replace("img$","")), "image/jpg");
                                startActivity (intent);
                            }

                        });

                    }
                    else {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(llp);
                        tv.setTextSize(20);
                        tv.setTextColor(Color.parseColor("#E1E2E5"));
                        tv.setText(cursor.getString(6));
                        ll.addView(tv);
                    }

                    cw.addView(ll);

                    Log.d("Alexey", "Chat Dialog (Search Error 33)");

                    LinearLayout ll1 = new LinearLayout(this);

                    LinearLayout.LayoutParams llp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,1f);

                    //для входящих сообщений
                    /*if(cursor.getInt(7)==1){
                        layoutParams.gravity=Gravity.LEFT;
                        cw.setBackgroundResource(R.drawable.layout_bg_gray);
                        //для исходящих сообщений
                    }else if(cursor.getInt(7)==2){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;
                    }
*/
                    Log.d("Alexey", "Chat Dialog (Search Error 34)");
                    Log.d("Sergey", "Sergey " + cursor.getString(4));

                    cw.setBackgroundResource(R.drawable.layout_bg_message);

                    if(cursor.getString(2).equals(UserName)){
                        cw.setBackgroundResource(R.drawable.layout_bg_blue);
                        layoutParams.gravity=Gravity.RIGHT;

                        /////////////////
                        //ImageView Setup
                        ImageView imageView = new ImageView(this);

                        //setting image resource
                        if (cursor.getCount()==i)
                            imageView.setImageResource(R.drawable.tick);
                        else
                            imageView.setImageResource(R.drawable.read_tick);
                        //imageView.getLayoutParams().height = 24;

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                ((int) LinearLayout.LayoutParams.MATCH_PARENT, (int) RelativeLayout.LayoutParams.MATCH_PARENT);

                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                        imageView.setLayoutParams(params);

                        imageView.setPadding(0,0,10,0);

                        //setting image position
                    /*imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));*/

                        ll1.addView(imageView, llp1);
                        /////////////////
                    }

                    ll1.addView(cw, llp1);

                    llCont.addView(ll1,layoutParams);
                    Log.d("Alexey", "Chat Dialog (Search Error 35)");
                    id++;




                } while (cursor.moveToNext());
            }

            //тестовый вывод изображения


            Log.d("Alexey", "Chat Dialog (Search Error 31)");
            sqlLiteDatabase.close();
            Log.d("Alexey", "Chat Dialog (Search Error 4)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendMsg(){
        EditText etSendMsg = findViewById(R.id.etSendMsg);
        Button btnSendMsg = findViewById(R.id.btnSendMsg);
        ScrollView svCont = findViewById(R.id.svCont);

        etSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView svCont = findViewById(R.id.svCont);

                svCont.post(new Runnable() {
                    @Override
                    public void run() {
                        svCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etSendMsg.getText().toString().trim().length() > 0) {
                    boolean result= isMyServiceRunning(SyncService.class);

                    if (!result)
                    {
                        Intent serviceIntent = new Intent(ChatDialog.this, SyncService.class);
                        serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

                        ContextCompat.startForegroundService(ChatDialog.this, serviceIntent);
                    }

                    setMsgChat(String.valueOf(etSendMsg.getText()));

                    etSendMsg.getText().clear();
                }
            }
        });
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

    private String getTubNum(){
        try {
            sqlLiteDatabase.open(getApplicationContext());

            String selectQuery1 = "SELECT * FROM VpnConnection WhERE Id=2";

            //String selectQuery = "SELECT * FROM Chat WhERE UserTabNum="+chatUserTabNum;
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery1, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("Alexey", "LoginActivity8765434 "+cursor.getString(1));
                    return cursor.getString(1);
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setMsgChat(String msg){
        try {
            LinearLayout linearLayout = findViewById(R.id.llCont);
            sqlLiteDatabase.open(this);
            String updateQuery = "INSERT INTO Chat (UserTNFrom, UserNameFrom, UserTNTo, UserNameTo, DateTime, Message, Status, Deleted) VALUES (" + getTubNum() + ",'" + UserName + "'," + chatUserTabNum + ",'"+ userName +"','"+simpleDateFormat.format(new Date())+"', '"+msg+"', 2, 0)";
            sqlLiteDatabase.database.execSQL(updateQuery);
            linearLayout.removeAllViews();
            inboxMg(chatUserTabNum);
        } catch (SQLException e) {
            Log.d("Alexey", "Chat Dialog (Global Error 3): " + e.getMessage());
        }finally {
            sqlLiteDatabase.close();
        }
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        UserName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }

    @Override
    protected void onDestroy () {
        timerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}