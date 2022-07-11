package com.kazzinc.checklist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.kazzinc.checklist.ActivityForNotification.OnVPNTOReboot;
import com.kazzinc.checklist.Model.Answer;
import com.kazzinc.checklist.Model.ChatModel;
import com.kazzinc.checklist.Model.Task;
import com.kazzinc.checklist.Model.TaskDetail;
import com.kazzinc.checklist.Model.TaskDetailModify;
import com.kazzinc.services.APIClient;
import com.kazzinc.services.UploadService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {
    public static final String CHANNEL_ID = "SyncServiceChannel";
    public static final String CHANNEL1_ID = "SyncServiceChannel1";
    Handler timerHandler = new Handler();

    SharedPreferences sPref;
    private MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    //private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";
    private String emailSubject;
    private String emailText;
    private String userRole;
    private String UserId;
    private String UserKey;
    private String UserDate;
    private int UserShift;
    private int UserType;
    private int GSM;
    private String GSMEquipment;
    protected int activityCode;

    private ArrayList<Answer> answers = new ArrayList<>();

    @Override
    public void onCreate() {
        Log.d("Alexey", "Сервис создан");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            String input = intent.getStringExtra("inputExtra");
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MenuActivity.class);
            notificationIntent.putExtra("inputPage", "task");
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Чек-лист")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.check_logo_gold)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        } catch (Exception e) {

        }

        doTask();

        return START_NOT_STICKY;
    }

    void doTask()
    {
        try {
            timerHandler.postDelayed(timerRunnable, 0);
            timerHandler.postDelayed(timerSendToServer, 15000);
            timerHandler.postDelayed(timerCheckModify, 5000);
            Log.d("Alexey", "SyncService1 (timer GetChatMessage -2)");
            timerHandler.postDelayed(timerChatMessage, 0);
        } catch (Exception e) {
            Log.d("Alexey", "SyncService1 (Global Error): " + e.getMessage());

        }

    }

    Runnable timerSendToServer = new Runnable()
    {
        @Override public void run()
        {

            try {
                Log.d("Alexey", "SyncService1 (timer SendToServer)");
                (new LoadDataToServer(SyncService.this)).execute();
            }
            catch (Exception e)
            {
                Log.d("Alexey", "Ошибка загрузки " + e.getMessage());
            }
            timerHandler.postDelayed(this, 15000);
        }

    };

    Runnable timerChatMessage = new Runnable()
    {
        @Override public void run() {

            Log.d("Alexey", "SyncService1 (timer GetChatMessage 0)");

            try {
                Log.d("Alexey", "SyncService1 (timer GetChatMessage -1)");
                (new GetChatMessage(SyncService.this)).execute();
            } catch (Exception e) {
            }

            Log.d("Alexey", "SyncService1 (timer GetChatMessage 01)");


                try {
                    Log.d("Alexey", "SyncService1 (timer GetChatMessage 02)");

                    String selectQuery = "SELECT * FROM Chat WHERE IsAlarm ISNULL";
                    Log.d("Alexey", "getNitification 222" + selectQuery);
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                    Log.d("Alexey", "SyncService1 (timer GetChatMessage 03)");
                    if (cursor.moveToFirst()) {
                        do {
                            Log.d("Alexey", "SyncService1 (timer GetChatMessage 04)");
                            String From = cursor.getString(2);
                            Log.d("Alexey", "getNitification 444 " + From);


                            String updateQuery = "UPDATE Chat SET IsAlarm=1 WHERE Id='" + cursor.getString(0) + "'";
                            sqlLiteDatabase.database.execSQL(updateQuery);

                            try {
                                Log.d("Alexey", "getNitification Определяем уведомление");

                                Intent notificationIntent = new Intent(getApplicationContext(), NotifyActivity.class);
                                notificationIntent.putExtra("inputPage", "gsm");
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        0, notificationIntent, 0);

                /*Spannable sb = new SpannableString("Bold text");
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Notification.Builder notificationBuilder =
                                            new Notification.Builder(getApplicationContext(), CHANNEL1_ID)
                                                    .setDefaults(Notification.DEFAULT_ALL)
                                                    .setSmallIcon(R.drawable.notification)
                                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.alert25))
                                                    .setContentTitle(Html.fromHtml(String.format(Locale.getDefault(), "<strong><h1>%s</h1></strong>", "НОВОЕ СООБЩЕНИЕ")))
                                                    .setContentText(Html.fromHtml(String.format(Locale.getDefault(), "<font size=\"18\">%s</font>", From)))
                                                    .setAutoCancel(true)
                                                    .setColor(Color.RED)
                                                    .setPriority(Notification.PRIORITY_MAX)
                                                    /*.setStyle(new Notification.InboxStyle()
                                                    .Inbox("Строка 1")
                                                    .Inbox("Строка 1")
                                                        .addLine("Строка 1")
                                                        .addLine(sb)
                                                        .setSummaryText("+3 more"))*/
                                                    //.setPriority(1)// this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                                    //.setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);

                                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL1_ID, "Новое сообщение", NotificationManager.IMPORTANCE_HIGH);
                                    // Configure the notification channel.
                                    AudioAttributes att = new AudioAttributes.Builder()
                                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                            .build();
                                    notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, att);
                                    notificationChannel.setDescription("");
                                    notificationChannel.enableLights(true);
                                    notificationChannel.setLightColor(Color.RED);
                                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                                    notificationChannel.enableVibration(true);

                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.createNotificationChannel(notificationChannel);

                                    notificationManager.notify(0, notificationBuilder.build());

                                } else {
                                    NotificationCompat.Builder notificationBuilder =
                                            new NotificationCompat.Builder(getApplicationContext())
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("ВНИМАНИЕ")
                                                    .setContentText(From)
                                                    .setAutoCancel(true)
                                                    .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                                    //.setContentIntent(pendingIntent);
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(0, notificationBuilder.build());
                                }

                                //startForeground(111, notification);

                                Log.d("Alexey", "1Notif1 Показываем уведомление");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("Alexey", "1Notif1: ошибка " + e.getMessage());
                                Log.d("Alexey", "1Notif1: ошибка " + e.getStackTrace());
                            }


                        } while (cursor.moveToNext());
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

            timerHandler.postDelayed(this, 5000);
        }

    };

    class LoadDataToServer extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public LoadDataToServer(Context context){

            this.mContext = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("Alexey", "SendToServer Upload Chat: началось...");
            Uri uri = Uri.parse("content://media/external/images/media/696");
//            String sname = PhotoRealPath.getName(PhotoRealPath.getRealPathFromUri(getApplicationContext(), uri));
            String sname = PhotoRealPath.getRealPathFromUri(getApplicationContext(), uri);

            Log.d("Alexey", "SendToServer Upload Chat: " + sname);

//            UploadFile(PhotoRealPath.getRealPathFromUri(getApplicationContext(), uri));
            Log.d("Alexey", "Upload Chat: выполнено...");

            if(msSqlDatabase.checkConnection()) { //Получение уведомлений
                try {
                    ArrayList<com.kazzinc.checklist.Model.Notification> notificationList = msSqlDatabase.GetNotification();

                    sqlLiteDatabase.open(getApplicationContext());

                    sqlLiteDatabase.updateNotification(notificationList);

                    Log.d("Alexey", "getNitification 111");

                    String selectQuery = "SELECT * FROM Notification WHERE NotifyIsAlarm ISNULL";
                    Log.d("Alexey", "getNitification 2");
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                    Log.d("Alexey", "getNitification 3");
                    if (cursor.moveToFirst()) {
                        do {
                            Log.d("Alexey", "getNitification 4");
                            String Text = cursor.getString(1);
                            String Type = cursor.getString(4);
                            String Date = cursor.getString(2);

                            /*String updateQuery = "UPDATE Notification SET NotifyIsAlarm=1 WHERE NotifyId='" + cursor.getString(0) + "'";
                            sqlLiteDatabase.database.execSQL(updateQuery);*/

                            try {
                                Log.d("Alexey", "getNitification Определяем уведомление");

                                Intent notificationIntent = new Intent(getApplicationContext(), NotifyActivity.class);
                                notificationIntent.putExtra("inputPage", "gsm");
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        0, notificationIntent, 0);

                /*Spannable sb = new SpannableString("Bold text");
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Notification.Builder notificationBuilder =
                                            new Notification.Builder(getApplicationContext(), CHANNEL1_ID)
                                                    .setDefaults(Notification.DEFAULT_ALL)
                                                    .setSmallIcon(R.drawable.notification)
                                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.alert25))
                                                    .setContentTitle(Html.fromHtml(String.format(Locale.getDefault(), "<strong><h1>%s</h1></strong>", "ВНИМАНИЕ!")))
                                                    .setContentText(Html.fromHtml(String.format(Locale.getDefault(), "<font size=\"18\">%s</font>", Type)))
                                                    .setAutoCancel(true)
                                                    .setColor(Color.RED)
                                                    .setPriority(Notification.PRIORITY_MAX)
                                                    /*.setStyle(new Notification.InboxStyle()
                                                    .Inbox("Строка 1")
                                                    .Inbox("Строка 1")
                                                        .addLine("Строка 1")
                                                        .addLine(sb)
                                                        .setSummaryText("+3 more"))*/
                                                    //.setPriority(1)// this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                                    //.setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);

                                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL1_ID, "Аварийное оповещение", NotificationManager.IMPORTANCE_HIGH);
                                    // Configure the notification channel.
                                    AudioAttributes att = new AudioAttributes.Builder()
                                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                            .build();
                                    notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, att);
                                    notificationChannel.setDescription("");
                                    notificationChannel.enableLights(true);
                                    notificationChannel.setLightColor(Color.RED);
                                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                                    notificationChannel.enableVibration(true);

                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.createNotificationChannel(notificationChannel);

                                    notificationManager.notify(0, notificationBuilder.build());

                                } else {
                                    NotificationCompat.Builder notificationBuilder =
                                            new NotificationCompat.Builder(getApplicationContext())
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setContentTitle("ВНИМАНИЕ")
                                                    .setContentText(Text)
                                                    .setAutoCancel(true)
                                                    .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                                    //.setContentIntent(pendingIntent);
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(0, notificationBuilder.build());
                                }

                                //startForeground(111, notification);

                                Log.d("Alexey", "1Notif1 Показываем уведомление");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("Alexey", "1Notif1: ошибка " + e.getMessage());
                                Log.d("Alexey", "1Notif1: ошибка " + e.getStackTrace());
                            }


                        } while (cursor.moveToNext());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//            }

            Log.d("Alexey", "getNitification step 4");

            if(msSqlDatabase.checkConnection()) { //Передача данных по улучшениям на сервер
                //Переда данных по улучшениям на сервер
                sqlLiteDatabase.open(getApplicationContext());
                String selectQuery = "SELECT Id, EmplId, EmplName, EmplArea, EmplProff, DateTime2, Title, Offer, Result, ifnull(Photos,'') as Photos, IsDeleted, IsSendToServer FROM Improvement WHERE IsSendToServer=1";
                Cursor cursorSelect = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                int i = 0;
                //Обновляем мнформацию по наряд-заданиям (подпись)
                if (cursorSelect.moveToFirst()) {
                    do {
                        try {
                            int Id = Integer.parseInt(cursorSelect.getString(0));
                            int EmplId = Integer.parseInt(cursorSelect.getString(1));
                            String EmplName = cursorSelect.getString(2);
                            String EmplArea = cursorSelect.getString(3);
                            String EmplProff = cursorSelect.getString(4);
                            String DateTime = cursorSelect.getString(5);
                            String Title = cursorSelect.getString(6);
                            String Offer = cursorSelect.getString(7);
                            String Result = cursorSelect.getString(8);
                            String Photos = cursorSelect.getString(9);
                            int IsDeleted = Integer.parseInt(cursorSelect.getString(10));

                            msSqlDatabase.updateImprovement(Id, EmplId, EmplName, EmplArea, EmplProff, DateTime, Title, Offer, Result, "", IsDeleted);
                            //sqlLiteDatabase.open(TaskDetailActivity.this);
                            String updateQuery = "UPDATE Improvement SET IsSendToServer='1' WHERE Id='" + Id + "'";
                            sqlLiteDatabase.database.execSQL(updateQuery);

                            /////
                            for (int t = 0; t < Photos.split(";").length; t++)
                            {
                                String fileName = Photos.split(";")[t];
                                msSqlDatabase.ImprovementDocs(Id, PhotoRealPath.getName(fileName));
                                UploadFile(getExternalMediaDirs()[0] + "/" + fileName);
                            }
                            /////
                        }
                        catch (Exception e)
                        {
                            Log.d("Alexey", "Ошибка timer SendToServer step 4 " + e.getMessage());
                        }

                    } while (cursorSelect.moveToNext());
                }
            }

            Log.d("Alexey", "getNitification step 5");

            if(msSqlDatabase.checkConnection()) {//Передача данных по GSM на сервер
                try{
                    sqlLiteDatabase.open(getApplicationContext());

                    Log.d("Alexey", "GSM начало передачи");

                    String selectQuery = "SELECT DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, IFNULL(ReasonOil,'') as ReasonOil, Confirmed, T86 FROM GSM WHERE SendToServer=0";
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
                    Log.d("Alexey", "GSM Send -1");

                    if (cursor.moveToFirst()) {
                        do {
                            Log.d("Alexey", "GSM Send 0");
                            String DateEvent = cursor.getString(0);
                            String Date = cursor.getString(1);
                            Integer Shift = Integer.parseInt(cursor.getString(2));
                            String EquipOut = cursor.getString(3);
                            String EquipIn = cursor.getString(4);
                            String EmplOut = cursor.getString(5);
                            String Reason = cursor.getString(6);
                            double DT = Double.parseDouble(cursor.getString(7));
                            double SAE15W40 = Double.parseDouble(cursor.getString(8));
                            double SAE50 = Double.parseDouble(cursor.getString(9));
                            double SAE10W40 = Double.parseDouble(cursor.getString(10));
                            double T46 = Double.parseDouble(cursor.getString(11));
                            double T86 = Double.parseDouble(cursor.getString(15));
                            int Deleted = Integer.parseInt(cursor.getString(12));
                            String ReasonOil = cursor.getString(13);
                            int Confirmed = Integer.parseInt(cursor.getString(14));

                            boolean result=false;

                            Log.d("Alexey", "GSM result send " + DateEvent + ", " + Date + ", " + Shift+ ", " + EquipOut+ ", " + EquipIn+ ", " + EmplOut+ ", " + Reason+ ", " + DT+ ", " + SAE15W40+ ", " + SAE50+ ", " + SAE10W40+ ", " + T46+ ", " + Deleted + T46+ ", " + ReasonOil);

                            result = msSqlDatabase.UpdateGSM(DateEvent,Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40, SAE50, SAE10W40, T46, Deleted, ReasonOil, Confirmed, T86);

                            Log.d("Alexey", "GSM Send 9 " + result);

                            if (result) {
                                Log.d("Alexey", "GSM Send 10");
                                sqlLiteDatabase.open(getApplicationContext());
                                String updateQuery = "UPDATE GSM SET SendToServer=1 WHERE DateEvent='" + DateEvent + "'";
                                sqlLiteDatabase.database.execSQL(updateQuery);
                                sqlLiteDatabase.close();
                            }

                        } while (cursor.moveToNext());
                    }
                } catch (SQLException | NumberFormatException e) {

                }
            }

            Log.d("Alexey", "getNitification step 6");

            if(msSqlDatabase.checkConnection()) {//Передача данных по чек-листам на сервер
                try{
                    sqlLiteDatabase.open(getApplicationContext());

                    String selectQuery = "SELECT * FROM Answer WHERE IsSendToServer ISNULL OR IsSendToServer<>1";
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            int AnswerId = Integer.parseInt(cursor.getString(0));
                            Log.d("Alexey", "Чек-лист " + AnswerId);
                            int AnswerUserId = Integer.parseInt(cursor.getString(1));
                            int AnswerQuesId = Integer.parseInt(cursor.getString(2));
                            String AnswerText = cursor.getString(3);
                            String AnswerDate = cursor.getString(4);
                            int AnswerShift = Integer.parseInt(cursor.getString(5));
                            String AnswerComment = cursor.getString(6);
                            String AnswerWorkPlaceName = cursor.getString(7);
                            String AnswerDateTime = cursor.getString(8);
                            String AnswerPhotos = cursor.getString(9);

                            msSqlDatabase.updateAnswer(AnswerUserId,AnswerQuesId,AnswerText,AnswerDate,AnswerShift,AnswerComment, AnswerWorkPlaceName, AnswerDateTime, AnswerPhotos);

                            try {
                                sqlLiteDatabase.open(getApplicationContext());
                                String updateQuery = "UPDATE Answer SET IsSendToServer=1 WHERE AnswerId='" + AnswerId + "'";
                                sqlLiteDatabase.database.execSQL(updateQuery);
                                sqlLiteDatabase.close();
                            }
                            catch (Exception e)
                            {
                                Log.d("Alexey", "Ошибка: " + e.getMessage());
                            }

                        } while (cursor.moveToNext());
                    }
                    sendPhotoToServer();
                } catch (SQLException | NumberFormatException e) {

                }
            }

            if(msSqlDatabase.checkConnection()) {//Передача данных по РВД на сервер
                try {
                    sqlLiteDatabase.open(getApplicationContext());

                    String selectQuery = "SELECT DateEvent, Date, Shift, Equipment, OldNumber, NewNumber, MotoHours, SpecialHours, Place, Reason, Deleted FROM RVD WHERE SendToServer=0";
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                    //Обновляем мнформацию по РВД
                    if (cursor.moveToFirst()) {
                        do {
                            String DateEvent = cursor.getString(0);
                            String Date = cursor.getString(1);
                            Integer Shift = Integer.parseInt(cursor.getString(2));
                            String Equipment = cursor.getString(3);
                            int OldNumber = Integer.parseInt(cursor.getString(4));
                            int NewNumber = Integer.parseInt(cursor.getString(5));
                            int MotoHours = Integer.parseInt(cursor.getString(6));
                            int SpecialHours = Integer.parseInt(cursor.getString(7));
                            String Place = cursor.getString(8);
                            String Reason = cursor.getString(9);
                            int Deleted = Integer.parseInt(cursor.getString(10));

                            boolean result=false;

                            result = msSqlDatabase.UpdateRVD(DateEvent, Date, Shift, Equipment, OldNumber, NewNumber, MotoHours, SpecialHours, Place, Reason, Deleted);
                            if (result) {
                                sqlLiteDatabase.open(getApplicationContext());
                                String updateQuery = "UPDATE RVD SET SendToServer=1 WHERE DateEvent='" + DateEvent + "'";
                                sqlLiteDatabase.database.execSQL(updateQuery);
                                sqlLiteDatabase.close();
                            }

                        } while (cursor.moveToNext());
                    }
                } catch (SQLException | NumberFormatException e) {
                }
            }

            String eq = loadGsmEq();

            Log.d("Alexey", "getNitification step 8");

            if(msSqlDatabase.checkConnection()) {//Загрузка данных по ГСМ с сервера

                try{
                    if (eq.length()>0) {
                        List<com.kazzinc.checklist.Model.GSM> gsm = msSqlDatabase.getGSM(eq);
                        sqlLiteDatabase.open(getApplicationContext());
                        sqlLiteDatabase.updateGSM(gsm,eq);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    sqlLiteDatabase.close();
                }

            }

            //Отправка уведомлений ГСМ
            sqlLiteDatabase.open(getApplicationContext());

            String selectQuery = "SELECT COUNT(EquipOut) FROM GSM WHERE Confirmed=0 AND Deleted=0";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            int countNotify=0;

            //Обновляем мнформацию по наряд-заданиям (подпись)
            if (cursor.moveToFirst()) {
                do {
                    countNotify = Integer.parseInt(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();

            SharedPreferences sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            sPref.getInt("Notify",1);

            if ((countNotify>0)&&(!eq.equals("Заправщик")&&(eq.length()>0))) {
                try {
                    Log.d("Alexey", "1Notif1 Заправщик " + eq);

                    Intent notificationIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    notificationIntent.putExtra("inputPage", "gsm");
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, notificationIntent, 0);

                /*Spannable sb = new SpannableString("Bold text");
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Notification.Builder notificationBuilder =
                                new Notification.Builder(getApplicationContext(), CHANNEL1_ID)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setSmallIcon(R.drawable.notification)
                                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.gas_station10))
                                        .setContentTitle(Html.fromHtml(String.format(Locale.getDefault(), "<strong><h1>%s</h1></strong>", "Учёт ГСМ")))
                                        .setContentText(Html.fromHtml(String.format(Locale.getDefault(), "<font size=\"18\">%s</font>", "Подтвердите заправку ГСМ")))
                                        .setAutoCancel(true)
                                        .setColor(Color.RED)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        /*.setStyle(new Notification.InboxStyle()
                                                .addLine("Строка 1")
                                                .addLine(sb)
                                                .setSummaryText("+3 more"))*/
                                        //.setPriority(1)// this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                        //.setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL1_ID, "Уведомления по ГСМ", NotificationManager.IMPORTANCE_HIGH);
                        // Configure the notification channel.
                        AudioAttributes att = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build();
                        notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, att);
                        notificationChannel.setDescription("");
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                        notificationChannel.enableVibration(true);

                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(notificationChannel);

                        notificationManager.notify(0, notificationBuilder.build());

                    } else {
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("Учет ГСМ")
                                        .setContentText("Требуется подтверждение информации по ГСМ")
                                        .setAutoCancel(true)
                                        .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                        //.setContentIntent(pendingIntent);
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(0, notificationBuilder.build());
                    }

                    //startForeground(111, notification);

                    Log.d("Alexey", "1Notif1 Показываем уведомление");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Alexey", "1Notif1: ошибка " + e.getMessage());
                    Log.d("Alexey", "1Notif1: ошибка " + e.getStackTrace());
                }
            }

            return true;
        }

        protected void onPostExecute(boolean preverse){
        }
    }

    class GetChatMessage extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public GetChatMessage(Context context){

            this.mContext = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("Alexey", "SyncService1 (timer GetChatMessage 1)");

            if(msSqlDatabase.checkConnection()) {
                try {

                    sqlLiteDatabase.open(getApplicationContext());

                    Long longn = SystemClock.elapsedRealtime();
                    String selectQuery = "SELECT * FROM Chat WHERE IsSendToServer IS NULL";
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                    boolean result=false;
                    if (cursor.moveToFirst()) {
                        do {
                            int UserTNFrom = cursor.getInt(1);
                            String UserNameFrom = cursor.getString(2);
                            int UserTNTo = cursor.getInt(3);
                            String UserNameTo = cursor.getString(4);
                            String DateTime = cursor.getString(5);
                            String Message = cursor.getString(6);

                            result = msSqlDatabase.insertChatMsg(UserTNFrom, UserNameFrom, UserTNTo, UserNameTo, DateTime, Message);

                            Log.d("Alexey", "SyncService1 (insertChatMsg): " + UserTNFrom + "," + UserNameFrom + "," + UserTNTo + "," + UserNameTo + "," + DateTime + "," + Message);

                        } while (cursor.moveToNext());
                    }

                    Log.d("Alexey", "SyncService1 (timer GetChatMessage 2)");

                    ArrayList<ChatModel> msgList = (ArrayList<ChatModel>) msSqlDatabase.GetChatMessage(Integer.parseInt(getTubNum()));

                    Log.d("Alexey", "SyncService1 (timer GetChatMessage 3)");

                    sqlLiteDatabase.updateChatMsg(msgList);

                    Log.d("Alexey", "SyncService1 (timer GetChatMessage 4)");

                } catch (Exception e) {

                    e.printStackTrace();
                }
                finally {
                    sqlLiteDatabase.close();
                }
            }

            return true;
        }

        protected void onPostExecute(boolean preverse){
        }
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

    private String loadGsmEq() {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        return sPref.getString("GSMEq", "");
    }

    Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {

            Log.d("Alexey", "SyncService timerRunnable");

            if (loadNeedLoadParam()>0) { //1 - чек-лист; 2 - наряд; 3 - моточасы; 4 - все; 5 - ПНВР; 6 - чек-лист опасности и средства контроля
                try {
                    loadUserData();
                    (new UpdateAnswersAsyncTask()).execute();
                }
                catch (Exception e)
                {
                    saveNeedLoadParam(4);
                    Log.d("Alexey", "Ошибка загрузки " + e.getMessage());
                }
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    protected String getVpnConnection(Context context, int i){
        try {
            sqlLiteDatabase.open(context);

            String selectQuery = "SELECT * FROM VpnConnection WHERE Id=1";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if(i==0) {

                if (cursor.moveToFirst()) {
                    do {
                        return cursor.getString(1);
                    } while (cursor.moveToNext());
                }
            }else if(i==1){

                if (cursor.moveToFirst()) {
                    do {
                        return cursor.getString(2);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLException e) {

        }
        return "";
    }

    private void setVpnConnection(){
        try {
            sqlLiteDatabase.open(getApplicationContext());
            Long longn = SystemClock.elapsedRealtime();
            String updateQuery = "UPDATE VpnConnection SET TimeWorkPhone = "+longn+" WHERE Id = 1";
            sqlLiteDatabase.database.execSQL(updateQuery);
        } catch (SQLException e) {

        }
    }


    protected void setVpnConnection(Context context, int i){
        try{
            sqlLiteDatabase.open(context);
            Long longn = SystemClock.elapsedRealtime();
            String updateQuery = "UPDATE VpnConnection SET OnVpn="+i+", TimeWorkPhone = "+longn+" WHERE Id = 1";
            sqlLiteDatabase.database.execSQL(updateQuery);
        } catch (SQLException e) {

        }
    }



    Runnable timerCheckModify = new Runnable()
    {
        @Override public void run()
        {

            try {
                if(new LoginActivity().vpnActive(getApplicationContext()) && activityCode!=1){
//                new OnVPNBeforeReboot().setTextOnVpn("У вас включен VPN для того что бы пользоваться прилочением Чек-лист и Sipnetic пожалуйста перезагрузите телефон");
                    activityCode=1;
                    setVpnConnection(getApplicationContext(),1);
                    Intent intent = new Intent(getApplicationContext(), OnVPNTOReboot.class);
                    //Ключ для открытия активити по таймеру
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else {
                    if(Integer.parseInt(String.valueOf(getVpnConnection(getApplicationContext(),0)))<SystemClock.elapsedRealtime() && Integer.parseInt(getVpnConnection(getApplicationContext(),1))==1){
                        if(activityCode!=1) {
                            setVpnConnection(getApplicationContext(),1);
                            Intent intent = new Intent(getApplicationContext(), OnVPNTOReboot.class);
                            //Ключ для открытия активити по таймеру
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }else {
                        setVpnConnection(getApplicationContext(),0);
                        activityCode=0;
                    }
                }

                setVpnConnection();
            } catch (NumberFormatException e) {

            }

            try
            {


            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Log.d("Alexey", "SyncService timerCheckModify");
                loadUserData();

                switch (userRole) {
                    case  ("Мастер смены"):
                        UserType=2;
                        break;
                    case ("Начальник участка"):
                        UserType=3;
                        break;
                    case ("Начальник рудника"):
                        UserType=4;
                        break;
                    case ("Рабочий"):
                        UserType=1;
                        break;
                    default:
                        UserType=0;
                        break;
                }

                if (UserType>0) {
                    Log.d("Alexey", "SyncService1 (timer 2)");
                    (new LoadModify(SyncService.this)).execute();
                }
            }
            catch (Exception e)
            {
                Log.d("Alexey", "Ошибка загрузки " + e.getMessage());
            }

            timerHandler.postDelayed(this, 15000);
        }
    };

    private int loadNeedLoadParam()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userId = sPref.getString("NeedLoad","0");
        userRole = sPref.getString("UserRole","");
        String QuesType = sPref.getString("QuesType","");

        switch (QuesType)
        {
            case "1":
                emailSubject = "Проверка рабочего места";
                break;
            case "2":
                emailSubject = "Проверка оборудования";
                break;
            case "3":
                emailSubject = "Проверка ПСО 1";
                break;
            case "4":
                emailSubject = "Проверка ПСО 6";
                break;
        }
        emailText = "Результат заполнения чек-листа <b>" + emailSubject + "</b> <br><br> Подстанция <b>" + sPref.getString("WorkPlaceName","") + "</b><br>" + sPref.getString("PSOResult","") + "<br><br><br><i>Сообщение было отправлено автоматически. Пожалуйста, не отвечайте на него.<i>";
        emailSubject = "Чек-лист / " + emailSubject;

        return Integer.valueOf(userId);

    }

    private int loadSignState()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String ss = sPref.getString("SignState","1");

        return Integer.valueOf(ss);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service stopped...", Toast.LENGTH_SHORT).show();
    }

    //автоматическое обновление после прохождения
    class UpdateAnswersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... params) {

            //проверяем соединение с удаленной бд и обновялем вопросы в удаленной бд
            if(msSqlDatabase.checkConnection())
            {
                Log.d("Alexey", "ECP: проверка передачи подписи на сервер ");
                //Передача подписи основного наряда без изменений на сервер (2)
                if ((loadNeedLoadParam()==2) || (loadNeedLoadParam()==4)) { //1 - чек-лист; 2 - наряд; 3 - моточасы;  5 - ПНВР; 4 - все; 6 - чек-лист опасности и средства контроля

                    boolean haveKey = isHaveKey();

                    boolean result=false;
                    try {
                        sqlLiteDatabase.open(getApplicationContext());
                        String updateQuery = "SELECT DISTINCT TaskId, TaskSignId, TaskEmplId FROM Task";
                        Cursor cursorUpdate = sqlLiteDatabase.database.rawQuery(updateQuery, null);

                        //Обновляем мнформацию по наряд-заданиям (подпись)
                        if (cursorUpdate.moveToFirst()) {
                            do {
                                int TASK_ID = Integer.parseInt(cursorUpdate.getString(0));
                                int TASK_SIGN_ID = loadSignState();//Integer.parseInt(cursorUpdate.getString(1));
                                int TASK_EMPL_ID = Integer.parseInt(cursorUpdate.getString(2));

                                if (haveKey)
                                    result = msSqlDatabase.updateTask(TASK_ID, TASK_SIGN_ID, TASK_EMPL_ID, 2, UserKey);
                                else
                                    result = msSqlDatabase.updateTaskNoKey(TASK_ID, TASK_SIGN_ID);
                            } while (cursorUpdate.moveToNext());
                        }
                        //sqlLiteDatabase.close();
                    }
                    catch (Exception e) {
                        Log.d("Alexey", "ECP: 11 ошибка выполнения процедуры на сервер сервер старт 2" + e.getMessage());
                    }

                    if (result)
                        saveNeedSign(0);
                    else
                        saveNeedSign(1);
                }

                if (loadNeedLoadParam()==7) { //1 - чек-лист; 2 - наряд; 3 - моточасы;  5 - ПНВР; 4 - все; 6 - чек-лист опасности и средства контроля; 7 - подпись изменения наряд-задания
                    sqlLiteDatabase.open(getApplicationContext());
                    String updateQuery = "SELECT DISTINCT TaskId, TaskSignId, TaskEmplId FROM Task";
                    Cursor cursorUpdate = sqlLiteDatabase.database.rawQuery(updateQuery, null);

                    //Обновляем мнформацию по наряд-заданиям (подпись)
                    if (cursorUpdate.moveToFirst()) {
                        do {
                            int TASK_ID = Integer.parseInt(cursorUpdate.getString(0));
                            int TASK_SIGN_ID = Integer.parseInt(cursorUpdate.getString(1));
                            int TASK_EMPL_ID = Integer.parseInt(cursorUpdate.getString(2));
                            try {
                                msSqlDatabase.updateTask(TASK_ID, TASK_SIGN_ID, TASK_EMPL_ID,  4, UserKey);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } while (cursorUpdate.moveToNext());
                    }
                    //sqlLiteDatabase.close();
                }

                if ((loadNeedLoadParam()==3) || (loadNeedLoadParam()==4)) { //1 - чек-лист; 2 - наряд; 3 - моточасы; 4 - все;  5 - ПНВР; 6 - чек-лист опасности и средства контроля
                    try {
                        Log.d("Alexey","Мото проверка ");
                        sqlLiteDatabase.open(getApplicationContext());
                        String motoQuery = "SELECT MotoEquipName, MotoDate, MotoShift, CASE WHEN MotoDVS='' THEN 0 ELSE MotoDVS END as MotoDVS, CASE WHEN MotoCompress='' THEN 0 ELSE MotoCompress END as MotoCompress, CASE WHEN MotoPerfor='' THEN 0 ELSE MotoPerfor END as MotoPerfor, CASE WHEN MotoMaslo='' THEN 0 ELSE MotoMaslo END as MotoMaslo, CASE WHEN MotoLeft='' THEN 0 ELSE MotoLeft END as MotoLeft, CASE WHEN MotoRight='' THEN 0 ELSE MotoRight END as MotoRight FROM Moto";
                        Cursor cursorMoto = sqlLiteDatabase.database.rawQuery(motoQuery, null);

                        if (cursorMoto.moveToFirst()) {
                            do {
                                String MotoEquipName = cursorMoto.getString(0);
                                String MotoDate = cursorMoto.getString(1);
                                int MotoShift = Integer.parseInt(cursorMoto.getString(2));
                                float MotoDVS = Float.parseFloat(cursorMoto.getString(3));
                                float MotoCompress = Float.parseFloat(cursorMoto.getString(4));
                                float MotoPerfor = Float.parseFloat(cursorMoto.getString(5));
                                float MotoMaslo = Float.parseFloat(cursorMoto.getString(6));
                                float MotoLeft = Float.parseFloat(cursorMoto.getString(7));
                                float MotoRight = Float.parseFloat(cursorMoto.getString(8));

                                Log.d("Alexey", "ECP Загрузка данных на сервер мото");
                                msSqlDatabase.updateMoto(MotoEquipName, MotoDate, MotoShift, MotoDVS, MotoCompress, MotoPerfor, MotoMaslo, MotoLeft, MotoRight);

                            } while (cursorMoto.moveToNext());
                        }
                    } catch (NumberFormatException e) {

                    }
                    //Обновляем мнформацию по моточасам
                    //sqlLiteDatabase.close();
                }

                if ((loadNeedLoadParam()==5) || (loadNeedLoadParam()==4)) { //1 - чек-лист; 2 - наряд; 3 - моточасы; 4 - все;  5 - ПНВР; 6 - чек-лист опасности и средства контроля
                    try {
                        sqlLiteDatabase.open(getApplicationContext());
                        String pnvrQuery = "SELECT User, Date, Shift, CheckedPersonal, Task, Workplace, Responsible, Team, PodrjadOrg, RiskTool, Dangers, Instruction, Сonformity, Assessment, ShortReview, ClassBehavior, BehaviorReview, PSO, ActionReview, Event FROM PNVR";

                        Log.d("Alexey","ПНВР " + pnvrQuery);

                        Cursor cursorPNVR = sqlLiteDatabase.database.rawQuery(pnvrQuery, null);

                        //Обновляем мнформацию по моточасам
                        if (cursorPNVR.moveToFirst()) {
                            do {
                                String User = cursorPNVR.getString(0);
                                String Date = cursorPNVR.getString(1);
                                Log.d("Alexey","ПНВР Дата " + Date);
                                int Shift = Integer.parseInt(cursorPNVR.getString(2));
                                String CheckedPersonal = cursorPNVR.getString(3);
                                String Task = cursorPNVR.getString(4);
                                String Workplace = cursorPNVR.getString(5);
                                String Responsible = cursorPNVR.getString(6);
                                String Team = cursorPNVR.getString(7);
                                String PodrjadOrg = cursorPNVR.getString(8);
                                String RiskTool= cursorPNVR.getString(9);
                                String Dangers = cursorPNVR.getString(10);
                                String Instruction = cursorPNVR.getString(11);
                                String Сonformity = cursorPNVR.getString(12);
                                String Assessment = cursorPNVR.getString(13);
                                String ShortReview = cursorPNVR.getString(14);
                                String ClassBehavior = cursorPNVR.getString(15);
                                String BehaviorReview = cursorPNVR.getString(16);
                                String PSO = cursorPNVR.getString(17);
                                String ActionReview= cursorPNVR.getString(18);
                                String Event= cursorPNVR.getString(19);

                                msSqlDatabase.updatePNVR(User, Date, Shift, CheckedPersonal, Task, Workplace, Responsible, Team, PodrjadOrg, RiskTool, Dangers, Instruction, Сonformity, Assessment, ShortReview, ClassBehavior, BehaviorReview, PSO, ActionReview, Event);
                            } while (cursorPNVR.moveToNext());
                            //sqlLiteDatabase.close();
                        }
                        sqlLiteDatabase.open(getApplicationContext());
                        String deleteQuery = "DELETE FROM PNVR";
                        sqlLiteDatabase.database.execSQL(deleteQuery);
                        //sqlLiteDatabase.close();
                    } catch (SQLException | NumberFormatException e) {
                    }

                }

                if ((loadNeedLoadParam()==6) || (loadNeedLoadParam()==4)) { //1 - чек-лист; 2 - наряд; 3 - моточасы; 4 - все;  5 - ПНВР; 6 - чек-лист опасности и средства контроля
                   try {
                       String checklistQuery = "Select UserId, Date, Shift, Object, UserDateTime, Danger, ControlTools, Improvement, UserIsSafety FROM CheckList";

                       Cursor cursor = sqlLiteDatabase.database.rawQuery(checklistQuery, null);

                       //Обновляем мнформацию по моточасам
                       if (cursor.moveToFirst()) {
                           do {
                               int UserId = Integer.parseInt(cursor.getString(0));
                               String Date = cursor.getString(1);
                               int Shift = Integer.parseInt(cursor.getString(2));
                               String Object = cursor.getString(3);
                               String UserDateTime = cursor.getString(4);
                               String Danger = cursor.getString(5);
                               String ControlTools = cursor.getString(6);
                               String Improvement = cursor.getString(7);
                               String UserIsSafety = cursor.getString(8);

                               String sQuery = "Select TDWorkType FROM TaskDetail WHERE TDWP1 = " + Object;
                               Cursor sCursor = sqlLiteDatabase.database.rawQuery(checklistQuery, null);

                            /*String WorkType ="";
                            if (sCursor.moveToFirst()) {
                                do {
                                    WorkType = sCursor.getString(3);
                                }
                                while (sCursor.moveToNext());
                            }*/


                               msSqlDatabase.updateCheckListByEmpl(UserId, Date, Shift, Object, UserDateTime, Danger, ControlTools, Improvement, UserIsSafety);
                           } while (cursor.moveToNext());
                       }
                   } catch (NumberFormatException e) {

                   }
                }

                saveNeedLoadParam(0);
                sqlLiteDatabase.close();
            }
            else {
                saveNeedLoadParam(4);
            }
            return true;
        }

        protected void onPostExecute(boolean preverse){
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean isHaveKey() {
            try {
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
            } catch (Exception e) {
            }
            return false;
        }
    }

    class LoadModify extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;

        public LoadModify(Context context){

            this.mContext = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("Alexey", "ECP: начинается повтор ");
            if((msSqlDatabase.checkConnection())&&(UserId.length()>0)) {
                try {
                    Log.d("Alexey", "ECP: 01 ");
                    sqlLiteDatabase.open(getApplicationContext());

                    //загружаем информацию из СУЭНЗ в локальную бд
                    List<Task> task = msSqlDatabase.getTaskList(Integer.parseInt(UserId), UserType, UserDate);
                    int rowCount = task.size();
                    if (rowCount>0) {
                        sqlLiteDatabase.open(getApplicationContext());
                        sqlLiteDatabase.updateTask(task);
                    }

                    Log.d("Alexey", "ECP: 02 ");

                    //загружаем информацию из СУЭНЗ в локальную бд
                    List<TaskDetail> taskDetail = msSqlDatabase.getTaskDetailList(Integer.parseInt(UserId), UserType, UserDate);
                    int rowCount1 = taskDetail.size();
                    if (rowCount1>0) {
                        sqlLiteDatabase.open(getApplicationContext());
                        sqlLiteDatabase.updateTaskDetail(taskDetail);
                    }
                    Log.d("Alexey", "ECP: 03 ");

                    //загружаем информацию по изменению СУЭНЗ в локальную бд
                    List<TaskDetailModify> taskDetailModify = msSqlDatabase.getTaskDetailModify(Integer.parseInt(UserId), UserDate);
                    int rowCount2 = taskDetailModify.size();
                    //if (rowCount2>0) {
                    sqlLiteDatabase.open(getApplicationContext());
                    sqlLiteDatabase.updateTaskModify(taskDetailModify);
                    //}

                    Log.d("Alexey", "ECP: проверка условия на повторное подписание " + loadNeedSign());

                    //Подписываем наряд очередная попытка
                    if (loadNeedSign()==1) {
                        Log.d("Alexey", "ECP: повторная попытка подписания старт ");

                        boolean haveKey = isHaveKey();

                        boolean result = false;
                        try {
                            sqlLiteDatabase.open(getApplicationContext());
                            String updateQuery = "SELECT DISTINCT TaskId, TaskSignId, TaskEmplId FROM Task";
                            Cursor cursorUpdate = sqlLiteDatabase.database.rawQuery(updateQuery, null);

                            //Обновляем мнформацию по наряд-заданиям (подпись)
                            if (cursorUpdate.moveToFirst()) {
                                do {
                                    int TASK_ID = Integer.parseInt(cursorUpdate.getString(0));
                                    int TASK_SIGN_ID = loadSignState();//Integer.parseInt(cursorUpdate.getString(1));
                                    int TASK_EMPL_ID = Integer.parseInt(cursorUpdate.getString(2));

                                    if (haveKey)
                                        result = msSqlDatabase.updateTask(TASK_ID, TASK_SIGN_ID, TASK_EMPL_ID, 2, UserKey);
                                    else
                                        result = msSqlDatabase.updateTaskNoKey(TASK_ID, TASK_SIGN_ID);
                                } while (cursorUpdate.moveToNext());
                            }
                            //sqlLiteDatabase.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("Alexey", "ECP: повторная попытка подписания ошибка " + e.getMessage());
                        }

                        if (result)
                            saveNeedSign(0);
                        else
                            saveNeedSign(1);

                        Log.d("Alexey", "ECP: повторная попытка подписания итог " + result);
                    }
                    ///////////////

                    //Уведомления по ГСМ
                    Log.d("Alexey", "GSMвбазу: 0 " + GSM + " " + GSMEquipment);
                    /*if ((GSM==0)&&(GSMEquipment.length()>0))
                    {
                        Log.d("Alexey", "GSMвбазу: 1 ");

                        try {

                            String resultJson = msSqlDatabase.GetGSM(UserDate, UserShift, GSMEquipment);

                            Log.d("Alexey", "GSMвбазу: " + resultJson);

                            if (resultJson.length()>0)
                            {
                                JSONArray jsonarray = new JSONArray(resultJson);
                                for(int i=0; i < jsonarray.length(); i++) {
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                                    String dateEvent = jsonobject.getString("DateEvent").replace("T"," ");
                                    String date    = jsonobject.getString("Date").replace("T"," ");;
                                    String shift  = jsonobject.getString("Shift");
                                    String equipOut  = jsonobject.getString("EquipOut");
                                    String equipIn  = jsonobject.getString("EquipIn");
                                    String emplOut  = jsonobject.getString("EmplOut");
                                    String reason  = jsonobject.getString("Reason");
                                    String dt  = jsonobject.getString("DT");
                                    String sAE15W40  = jsonobject.getString("SAE15W40");
                                    String sAE10W40  = jsonobject.getString("SAE10W40");
                                    String t46  = jsonobject.getString("T46");
                                    String deleted  = jsonobject.getString("Deleted");
                                    String confirmed  = jsonobject.getString("Сonfirmed");

                                    Log.d("Alexey", "GSMвбазу: sAE10W40 - " + sAE10W40);

                                    sqlLiteDatabase.open(getApplicationContext());

                                    String updateQuery = "INSERT INTO GSM (DateEvent, Date, Shift, EquipOut, EquipIn, EmplOut, Reason, DT, SAE15W40,SAE10W40,T46,Deleted,Confirmed) " +
                                            "VALUES ('" + dateEvent + "','" + date + "','" + shift + "','" + equipOut+ "','" + equipIn + "','" + emplOut + "','" + reason + "','" + dt + "','" + sAE15W40  + "','" + sAE10W40 + "','" + t46 + "','" + deleted + "','" + confirmed + "' )";

                                    Log.d("Alexey", "GSMвбазу: updateQuery - " + updateQuery);

                                    sqlLiteDatabase.database.execSQL(updateQuery);
                                    sqlLiteDatabase.close();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("Alexey", "GSMвбазу: errrrr - " + e.getMessage());
                        }
                    }*/

                    ///

                    sqlLiteDatabase.close();
                    Log.d("Alexey", "Update2: 02 ");



                } catch (Exception e) {
                    Log.d("Alexey", "Ошибка загрузки изменения наряда " + e.getMessage());
                }
            }

            return true;
        }



        protected void onPostExecute(boolean preverse){
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean isHaveKey() {
            try {
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
            } catch (Exception e) {

            }
            return false;
        }
    }

    private void sendEmail(String email) {
        try {
            //Getting content for email
            String subject = emailSubject;
            String message = emailText;
            //Creating SendMail object
            SendMail sm = new SendMail(this, email, subject, message);
            //Executing sendmail to send email
            sm.execute();
        } catch (Exception e) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveNeedSign(int needSign)
    {
        try {
            sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putInt("NeedSign", Integer.valueOf(needSign));
            ed.commit();
        } catch (Exception e) {

        }
    }

    private int loadNeedSign()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        int ss = sPref.getInt("NeedSign",0);

        return Integer.valueOf(ss);
    }

    private void saveNeedLoadParam(int needLoad)
    {
        try{
            sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("NeedLoad", String.valueOf(needLoad));
            ed.commit();
        } catch (Exception e) {

        }
    }

    public void showBottomToast(final String msg)
    {
        try {
            Toast toast1 = Toast.makeText(SyncService.this,msg, Toast.LENGTH_LONG);
            toast1.setGravity(Gravity.BOTTOM, 0, 20);
            toast1.show();
        } catch (Exception e) {

        }
    }

    private void sendPhotoToServer()
    {
        if(msSqlDatabase.checkConnection()){
            try {
                sqlLiteDatabase.open(this);
                String selectQuery = "SELECT  * FROM Answer WHERE IsSendPhotoToServer ISNULL OR IsSendPhotoToServer<>1";
                Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        int AnswerId = cursor.getInt(0);
                        String AnswerPhotos = cursor.getString(9);
                        if (AnswerPhotos != null && !AnswerPhotos.isEmpty()) {
                            String[] items = AnswerPhotos.split(";");
                            for (String item : items) {
                                //UploadFile(path + item);
                                UploadFile(getExternalMediaDirs()[0] + "/" + item);
                                Log.d("Alexey", "Файлы тест " + getExternalMediaDirs()[0] + "/" + item);
                                try {
                                    sqlLiteDatabase.open(getApplicationContext());
                                    String updateQuery = "UPDATE Answer SET IsSendPhotoToServer=1 WHERE AnswerId='" + AnswerId + "'";
                                    sqlLiteDatabase.database.execSQL(updateQuery);
                                    sqlLiteDatabase.close();
                                }
                                catch (Exception e)
                                {
                                    Log.d("Alexey", "Ошибка: " + e.getMessage());
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }
                sqlLiteDatabase.close();
            }
            catch (Exception e){
                Log.d("Alexey",e.getMessage());
            }
        }
    }

    private void UploadFile(String photoPath)
    {
        Log.d("Alexey", "SyncService1 error: 0");
        File file;

        try {

            /*Сжатие*/

            /**/

            file = new File(photoPath);
            Log.d("Alexey", "SyncService1 error: 1"+file);

            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/from-data"), file);

            final MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", file.getName(), photoContent);

            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Файл");


            Log.d("Alexey", "SyncService1 (timer SendToServer step Upload) path " + photoPath);

            UploadService uploadService = APIClient.getClient().create(UploadService.class);

            uploadService.Upload (photo, description).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Загрузка файла выполнена", Toast.LENGTH_LONG);
                        Log.d("Alexey", "SyncService1 (timer SendToServer step Upload) удачно");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Проблема загрузки файла", Toast.LENGTH_LONG);
                        Log.d("Alexey", "SyncService1 (timer SendToServer step Upload) проблема");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                    Log.d("Alexey", "SyncService1 (timer SendToServer step Upload) проблема1 " + t.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Alexey", "SyncService1 error: " + e.getMessage());
        }
    }

    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "Служба синхронизации данных",
                        NotificationManager.IMPORTANCE_LOW
                        //NotificationManager.IMPORTANCE_HIGH
                );
                serviceChannel.setSound(null, null);

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);
            }
        } catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }
    }

    private void loadUserData()
    {
        try {
            sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            UserId = sPref.getString("UserId", "0");
            UserDate = sPref.getString("UserDateNewFormat", "0");
            UserShift = sPref.getInt("UserShift", 0);
            UserKey = sPref.getString("Key", "0");
            GSM = sPref.getInt("GSM",0);
            GSMEquipment = sPref.getString("GSMEquipment", "");
            Log.d("Alexey", "SyncService1 (loadUserData) " + UserId);
            Log.d("Alexey", "GSMвбазу: loadUserData: " + GSMEquipment);
        }
        catch (Exception e)
        {
            Log.d("Alexey", "loadUserData: Ошибка " + e.getMessage());
        }
    }
}

