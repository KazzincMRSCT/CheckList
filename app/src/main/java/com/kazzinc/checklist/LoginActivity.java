package com.kazzinc.checklist;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.ActivityForNotification.OnVPNTOReboot;
import com.kazzinc.checklist.Model.ApplicationData;
import com.kazzinc.checklist.Model.EmlpECPKey;
import com.kazzinc.checklist.Model.Equipment;
import com.kazzinc.checklist.Model.HelpInUseApps;
import com.kazzinc.checklist.Model.Question;
import com.kazzinc.checklist.Model.Reason;
import com.kazzinc.checklist.Model.RiskSafety;
import com.kazzinc.checklist.Model.Task;
import com.kazzinc.checklist.Model.TaskDetail;
import com.kazzinc.checklist.Model.TaskDetailModify;
import com.kazzinc.checklist.Model.TaskEmployee;
import com.kazzinc.checklist.Model.TaskUser;
import com.kazzinc.checklist.Model.User;
import com.kazzinc.checklist.Model.WorkPlace;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TODO = "";
    private MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    private ProgressDialog progressDialog;
    public static final int DIALOG_DOWLOAD_PROGRESS = 1;
    String userRole;
    String userName;
    String Version = "";
    String MacAdress = "";
    String Wifi = "";
    String Phone = "";
    int userId;
    int userType;
    public String txt = "";
    public String FilePath = "";
    private String Shift;
    private String formattedDate;
    private String newFormattedDate;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mVersion;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] PERMISSIONS_WIFI = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        verifyStoragePeremissions(LoginActivity.this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION},2);

        createLogFile();
        CheckKey();

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

        SimpleDateFormat nDF = new SimpleDateFormat("MM.dd.yyyy");
        newFormattedDate = nDF.format(c);

        Log.d("Alexey", "Дата: " + formattedDate + " Смена: " + Shift);

        initDatabases();

        sqlLiteDatabase.open(getApplicationContext());

        //Проверка на текущую смену
        String query = "SELECT DISTINCT TaskDate, TaskShift, TaskId FROM Task";
        Cursor cursor = sqlLiteDatabase.database.rawQuery(query, null);

        String TASK_DATE = "";
        String TASK_SHIFT = "";
        String TASK_ID = "";
        if (cursor.moveToFirst()) {
            do {
                TASK_DATE = cursor.getString(0);
                TASK_SHIFT = cursor.getString(1);
                TASK_ID = cursor.getString(2);
            } while (cursor.moveToNext());
        }

        //saveSignState(1);

        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("Notify", 0);
        ed.commit();

        if ((TASK_DATE.equals("")) || !((formattedDate.equals(TASK_DATE)) && (Shift.equals(TASK_SHIFT)))) {
            saveSignState(1);
            saveNeedSign(0);
        }

        //

        ReadFileTask tsk = new ReadFileTask();
        tsk.execute("http://192.168.164.5:818/Content/Update/Version.txt");
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.login);
        mEmailView.setText(loadUserLogin());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (msSqlDatabase.checkConnection()) {
            try {
                if (vpnActive(this)) {
                    new SyncService().setVpnConnection(this, 1);
                    Intent intent = new Intent(this, OnVPNTOReboot.class);
                    startActivity(intent);
                } else {
                    if (Integer.parseInt(String.valueOf(new SyncService().getVpnConnection(this, 0))) < SystemClock.elapsedRealtime() && Integer.parseInt(new SyncService().getVpnConnection(this, 1)) == 1) {
                        Intent intent = new Intent(getApplicationContext(), OnVPNTOReboot.class);
                        //Ключ для открытия активити по таймеру
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        new SyncService().setVpnConnection(this, 0);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    //Проверяем включен ли впн на телефоне
    public static boolean vpnActive(Context context){
        Log.d("Sergey", "vpn11 method work");
        //this method doesn't work below API 21
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        boolean vpnInUse = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }

        Network[] networks = connectivityManager.getAllNetworks();
        for(int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(networks[i]);
            if(caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true;
                break;
            }
        }

        return vpnInUse;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CheckKey() {
        String path = "/storage/emulated/0/Key";
        Log.d("Files", "Path: " + path);

        if (Files.exists(Paths.get(path))) {
            File directory = new File(path);
            File[] files = directory.listFiles();

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
                }
            }
        }
    }

    private void saveNeedSign(int needSign) {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("NeedSign", Integer.valueOf(needSign));
        ed.commit();
    }

    private void saveSignState(int Value) {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SignState", String.valueOf(Value));
        ed.putString("ModifySignState", String.valueOf(Value));
        ed.commit();
        Log.d("Alexey", "ECP статус подписи при входе - " + Value);
    }

    //Обновление данных с сервера
    class SyncDatabasesTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        private View rootView;

        @Override
        protected Boolean doInBackground(Void... params) {
            //если есть связь с удаленной бд то обновляем иначе вывод ошибки

            if (msSqlDatabase.checkConnection()) {
                try {

                    sqlLiteDatabase.open(LoginActivity.this);

                    /*//обновляем пользователей в локальной бд
                    List<RiskSafety> rsList = msSqlDatabase.getRiskSafety();
                    sqlLiteDatabase.open(LoginActivity.this);
                    String deleteQuery = "DELETE FROM RiskSafety";
                    sqlLiteDatabase.database.execSQL(deleteQuery);
                    sqlLiteDatabase.updateRiskSafety(rsList);*/

                    //обновляем вопросы в локальной бд
                    List<Question> questionList = msSqlDatabase.getQuestions();
                    sqlLiteDatabase.updateQuestions(questionList);

                    //обновляем места работ в локальной бд
                    List<WorkPlace> workPlaceList = msSqlDatabase.getWorkPlaceList();
                    sqlLiteDatabase.updateWorkPlaceList(workPlaceList);

                    //обновляем список рабочих из СУЭНЗ в локальной бд
                    List<TaskEmployee> employeeList = msSqlDatabase.getTaskEmployeeList();
                    sqlLiteDatabase.updateEmployeeList(employeeList);

                    //обновляем список мастеров и начальников участков в локальной бд
                    List<TaskUser> taskUserList = msSqlDatabase.getTaskUserList();
                    sqlLiteDatabase.updateUserList(taskUserList);

                    //обновляем список мастеров и начальников участков в локальной бд
                    List<Equipment> equipmentList = msSqlDatabase.getEquipment();
                    sqlLiteDatabase.updateEquipment(equipmentList);

                    //обновляем список оснований для заправки в локальной бд
                    List<Reason> reasonList = msSqlDatabase.getReason();
                    sqlLiteDatabase.updateReason(reasonList);

                    //удаляем старые данные по ГСМ
                    //String deleteGSM = "DELETE FROM GSM WHERE Date < '" + newFormattedDate + "'";
                    /*String deleteGSM = "DELETE FROM GSM WHERE SendToServer=1";
                    sqlLiteDatabase.database.execSQL(deleteGSM);*/

                    //удаляем старые данные по РВД
                    String deleteRVD = "DELETE FROM RVD WHERE Date < '" + newFormattedDate + "'";
                    sqlLiteDatabase.database.execSQL(deleteRVD);

                    //обновляем вопросы в локальной бд
                    /*List<Question> questions = msSqlDatabase.getQuestions();
                    sqlLiteDatabase.updateQuestions(questions);*/

                    //обнвляем ответы в удаленной бд
                    /*ArrayList<Answer> answers = sqlLiteDatabase.getAnswers();
                    UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);
                    msSqlDatabase.updateAnswers(updateAnswersModel);;*/
                    sqlLiteDatabase.close();

                    showBottomToastShort("Справочные данные обновлены");
                } catch (Exception e) {
                    Log.d("Alexey", "LoginActivity (Error 1): " + e.getMessage());
                }
            }

            return true;
        }


        protected void onPostExecute(boolean preverse) {

        }
    }

    private static void verifyStoragePeremissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //Обновление данных из СУЭНЗ
    class SyncTask extends AsyncTask<Object, Void, Void> {
        private Context mContext;
        private View rootView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Object... objects) {
            //если есть связь с удаленной бд то обновляем иначе вывод ошибки

            if (msSqlDatabase.checkConnection()) {
                try {

                        /*userType = 1;
                        if (userRole.equals("Мастер смены"))
                            userType=2;*/

                    switch (userRole) {
                        case ("Мастер смены"):
                            userType = 2;
                            break;
                        case ("Начальник участка"):
                            userType = 3;
                            break;
                        case ("Начальник рудника"):
                            userType = 4;
                            break;
                        default:
                            userType = 1;
                            break;
                    }

                    //Удаляем старую информацию о наряде
                    sqlLiteDatabase.open(getApplicationContext());
                    sqlLiteDatabase.database.execSQL("delete from Task");
                    sqlLiteDatabase.database.execSQL("delete from TaskDetail");
                    sqlLiteDatabase.database.execSQL("delete from TaskDetailModify");
                    sqlLiteDatabase.database.execSQL("delete from HelpInUseApps");
                    sqlLiteDatabase.database.execSQL("delete from EmlpECPKey");



                    sqlLiteDatabase.close();

                    if(msSqlDatabase.checkConnection()) {//Загрузк данных по видео обучению с сервера
                        sqlLiteDatabase.open(getApplicationContext());

                        List<HelpInUseApps> helpInUseApps = msSqlDatabase.GetHelpInUseApps();
                        sqlLiteDatabase.updateHelpInUseApps(helpInUseApps);

                        sqlLiteDatabase.close();
                    }


                    int checkUpload =0;

                    if(checkUpload==0){
                        try {
                            sqlLiteDatabase.open(getApplicationContext());
                            String selectQuery = "";
                            selectQuery = "SELECT * FROM TaskEmployee WhERE TaskEmplPassword="+getTubNum();

                            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
                            if (cursor.moveToFirst()) {
                                do {
                                    //Toast.makeText(getApplicationContext(), cursor.getString(1)+"   "+cursor.getString(3)+"    "+cursor.getString(4), Toast.LENGTH_LONG).show();

                                    String name =cursor.getString(1);
                                    int tubNum = cursor.getInt(3);
                                    int AreaId = cursor.getInt(4);
                                    String VersionApp = getVersionApp();

                                    new MsSqlDatabase().InsertCheckApp(name, tubNum,AreaId,VersionApp);

                                } while (cursor.moveToNext());
                            }
                            sqlLiteDatabase.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        checkUpload =1;
                    }


                    //Загружаем информацию по ЭЦП
                    sqlLiteDatabase.open(getApplicationContext());
                    String UserId = sPref.getString("UserId","");
                    List<EmlpECPKey> emplEcp = msSqlDatabase.GetECPEmplId(UserId);
                    sqlLiteDatabase.updateemplEcp(emplEcp);

                    //загружаем информацию из СУЭНЗ в локальную бд
                    List<Task> task = msSqlDatabase.getTaskList(userId, userType, newFormattedDate);

                    sqlLiteDatabase.open(LoginActivity.this);
                    sqlLiteDatabase.updateTask(task);
                    Log.d("Alexey", "LoginActivity1 (task) " + userId + " " + userType + " " + newFormattedDate);

                    //загружаем информацию из СУЭНЗ в локальную бд
                    List<TaskDetail> taskDetail = msSqlDatabase.getTaskDetailList(userId, userType, newFormattedDate);
                    sqlLiteDatabase.open(LoginActivity.this);
                    sqlLiteDatabase.updateTaskDetail(taskDetail);


                    //загружаем информацию по изменению СУЭНЗ в локальную бд
                    List<TaskDetailModify> taskDetailModify = msSqlDatabase.getTaskDetailModify(userId, newFormattedDate);
                    sqlLiteDatabase.open(LoginActivity.this);
                    sqlLiteDatabase.updateTaskModify(taskDetailModify);

                    //Информация для учета ГСМ
                    sqlLiteDatabase.open(getApplicationContext());

                    String select = "SELECT DISTINCT TaskEquipName FROM Task";
                    Cursor cursor = sqlLiteDatabase.database.rawQuery(select, null);

                    String Equipment = "";
                    Integer GSM = 0;

                    //Обновляем мнформацию по наряд-заданиям (подпись)
                    if (cursor.moveToFirst()) {
                        do {
                            Equipment = cursor.getString(0);
                            if (Equipment.contains("UTIMEC-"))
                                GSM = 1;

                        } while (cursor.moveToNext());
                    }

                    //загружаем информацию по GSM в локальную бд
                    String eq = Equipment;
                    if (GSM == 1)
                        eq = "Заправщик";

                    Log.d("Alexey", "GSMвбазу: загрузка 1: " + Equipment + ",  " + eq);

                    if (eq.length()>0) {
                        Log.d("Alexey", "GSMвбазу: загрузка: " + Equipment);
                        List<com.kazzinc.checklist.Model.GSM> gsm = msSqlDatabase.getGSM(Equipment);
                        if (gsm.size()>0) {
                            sqlLiteDatabase.open(LoginActivity.this);
                            sqlLiteDatabase.database.execSQL("delete from GSM where SendToServer=1");
                            sqlLiteDatabase.updateGSM(gsm, eq);
                        }
                    }

                    sqlLiteDatabase.close();

                    saveGSMData(GSM, Equipment, eq);
                    //
                    //Передаем на сервер версию ПО и mac-адрес клиента
                    msSqlDatabase.updateVersion(userName, MacAdress, Version, newFormattedDate, Wifi, Phone);

                } catch (Exception e) {
                    Log.d("Alexey", "LoginActivity (Error 2): " + e.getMessage());
                }
            }
//            return true;
            return null;
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

        @Override
        protected void onPostExecute(Void param) {

            Intent intent;

            if (userRole.equals("Мастер смены") || userRole.equals("Начальник участка") || userRole.equals("Начальник рудника"))
                intent = new Intent(LoginActivity.this, MenuMasterActivity.class);
            else
                intent = new Intent(LoginActivity.this, MenuActivity.class);

            showProgress(false);

            startActivity(intent);

            finish();

        }
    }

    private void initDatabases() {
        msSqlDatabase = new MsSqlDatabase();

        sqlLiteDatabase = new SqlLiteDatabase(this);
        try {
            sqlLiteDatabase.create_db(this);
        } catch (IOException e) {
            Log.d("Alexey", "LoginActivity (Error 3): " + e.getMessage());
        }
    }

    private void attemptLogin() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(30);

            showProgress(true);

            Intent serviceIntent = new Intent(LoginActivity.this, SyncService.class);
            serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

            ContextCompat.startForegroundService(LoginActivity.this, serviceIntent);


            if (mAuthTask != null) {
                return;
            }

            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            /*if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }*/

            // Check for a valid password.
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            } else if (!isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
        } catch (Exception e) {
            Log.d("Alexey", "LoginActivity (Error 4): " + e.getMessage());
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return (email != null && !email.isEmpty());
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mEmail;
        private String mPassword;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;

            try {
                sqlLiteDatabase.open(getApplicationContext());
                String updateQuery = "UPDATE VpnConnection SET TimeWorkPhone = "+password+" WHERE Id = 2";
                sqlLiteDatabase.database.execSQL(updateQuery);
            } catch (SQLException e) {

            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(500);
                ;
            } catch (InterruptedException e) {
                return false;
            }*/

            //Блок для проверки логина и пароля
            boolean authentication = false;

            String login = ((EditText) findViewById(R.id.login)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            User user = null;

            //попытка подключения к удаленной бд иначе к локальной
            if (msSqlDatabase.checkConnection()) {
                user = msSqlDatabase.auth(login, password);
            } else {
                try {
                    sqlLiteDatabase.open(LoginActivity.this);
                    user = sqlLiteDatabase.auth(login, password);
                    sqlLiteDatabase.close();
                } catch (Exception e) {
                    Log.d("Alexey", "LoginActivity (Error 5): " + e.getMessage());
                }
                showBottomToastShort("Нет подключения к серверу");
            }

            //Log.d("Alexey", "SyncService1 saveUserInfo null " + user.getUserId() + " " + user.getUserName() + " " + userRole);
            //если пользователь найден то переключаемся на основую активность иначе вывод ошибки
            if (user != null) {
                ApplicationData.setAuthUser(user);
                authentication = true;

                String workplace = "";
                userName = "";
                userRole = "";
                userName = user.getUserName();
                userRole = user.getUserRole();
                userId = user.getUserId();

                Log.d("Alexey", "SyncService1 saveUserInfo exec" + user.getUserId() + " " + user.getUserName() + " " + userRole);
                saveUserInfo(user.getUserId(), user.getUserName(), userRole, user.getUserLogin(), user.getUserAreaId());

                saveWorkPlaceInfo(workplace);

                //Получаем риски и меры безопасности
                if (msSqlDatabase.checkConnection()) {
                    try {
                        sqlLiteDatabase.open(LoginActivity.this);

                        //обновляем пользователей в локальной бд
                        Log.d("Alexey", "getRiskSafety lsdkf;dsk");
                        List<RiskSafety> rsList = msSqlDatabase.getRiskSafety(userId);
                        sqlLiteDatabase.open(LoginActivity.this);
                        String deleteQuery = "DELETE FROM RiskSafety";
                        sqlLiteDatabase.database.execSQL(deleteQuery);
                        sqlLiteDatabase.updateRiskSafety(rsList);
                        sqlLiteDatabase.close();
                    } catch (Exception e) {
                        Log.d("Alexey", "LoginActivity (Error получение рисков и мер безопасности): " + e.getMessage());
                    }
                }
                //
            }
            return authentication;
        }

        private void saveWorkPlaceInfo(String workPlaceName) {
            sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("WorkPlaceName", workPlaceName);
            ed.putString("NeedLoad", String.valueOf(0));
            ed.commit();
        }

        private void saveUserInfo(int UserId, String UserName, String UserRole, String UserLogin, int UserAreaId) {
            sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("UserId", String.valueOf(UserId));
            ed.putString("UserName", String.valueOf(UserName));
            ed.putString("UserRole", String.valueOf(UserRole));
            ed.putString("UserAreaId", String.valueOf(UserAreaId));
            ed.putString("UserLogin", String.valueOf(UserLogin));
            ed.putString("UserDate", formattedDate);
            ed.putString("UserDateNewFormat", newFormattedDate);
            ed.putInt("UserShift", Integer.valueOf(Shift));
            ed.commit();
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                if (userRole.equals("Рабочий") || userRole.equals("Мастер смены") || userRole.equals("Начальник участка") || userRole.equals("Начальник рудника")) {
                    (new SyncTask()).execute();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.putExtra("inputPage", "task");
                    showProgress(false);

                    startActivity(intent);

                    finish();
                }

                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("Alexey", "LoginActivity (Error 6): " + e.getMessage());
                }*/

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    //показ сообщений
    public void showToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBottomToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 20);
                toast1.show();
            }
        });
    }

    public void showBottomToastShort(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.BOTTOM, 0, 150);
                toast1.show();
            }
        });
    }

    public void showTopToastShort(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.TOP, 0, 10);
                toast1.show();
            }
        });
    }

    private String loadUserLogin() {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userLogin = sPref.getString("UserLogin", "");
        return userLogin;
    }

    private void saveGSMData(int GSM, String Equipment, String GSMEq) {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("GSMEquipment", Equipment);
        ed.putString("GSMEq", GSMEq);
        ed.putInt("GSM", GSM);
        ed.commit();
        Log.d("Alexey", "GSMвбазу: saveGSMData: " + Equipment);
    }

    public void CheckCurrentVersion() {
        sqlLiteDatabase.open(LoginActivity.this);
        String selectQuery = "SELECT Version FROM Updates ";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Version = cursor.getString(0);
            }
            while (cursor.moveToNext());

        }
        sqlLiteDatabase.close();

        mVersion = (TextView) findViewById(R.id.version);
        mVersion.setText("Версия " + Version);
    }

    public String getVersionApp() {
        sqlLiteDatabase.open(LoginActivity.this);
        String selectQuery = "SELECT Version FROM Updates ";

        Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(0);
            }
            while (cursor.moveToNext());

        }
        sqlLiteDatabase.close();

        return "";
    }

    public static boolean NeedUpdate(String vDB, String vServer) {
        boolean result = false;

        //vDB = "2.1.9";
        if ((vServer.equals("2.3.0")) && (!vDB.equals("2.3.0")))
            result = true;
        else {
            vDB = vDB.replace(".", "");
            vServer = vServer.replace(".", "");

            Log.d("Alexey", "Версия БД: " + vDB);
            Log.d("Alexey", "Версия тхт: " + vServer);

            if (Integer.parseInt(vDB) < Integer.parseInt(vServer))
                result = true;
        }

        Log.d("Alexey", "Версия Надо ли обновлять: " + result);
        return result;
    }

    public void CheckUpdate() {
        if (txt.length() > 0) {
            boolean needUpdate = NeedUpdate(Version, txt);

            if (needUpdate) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showAddItemDialog(LoginActivity.this);
                    }
                });
            } else
                (new SyncDatabasesTask()).execute();
        }
    }

    //Проверка версии
    private class ReadFileTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            URL url;

            CheckCurrentVersion();

            if (msSqlDatabase.checkConnection()) {
                try {
                    //Получаем mac-адрес
                    MacAdress = getMacAddr();
                    Wifi = getWifi();
                    Phone = getPhone();

                    //create url object to point to the file location on internet
                    url = new URL(params[0]);
                    //make a request to server
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //get InputStream instance
                    InputStream is = con.getInputStream();
                    //create BufferedReader object
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    //read content of the file line by line
                    while ((line = br.readLine()) != null) {
                        txt += line;
                    }

                    br.close();

                    CheckUpdate();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Alexey", "LoginActivity (Error 7): " + e.getMessage());
                }
            }

            return null;
        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static String getPhone() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String device = Build.DEVICE;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model) + " " + device;
        } else {
            return capitalize(manufacturer) + " " + model + " " + device;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String getWifi() {
        String ssid  = "-";
        try {
            WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            ssid  = info.getSSID();
            return ssid.replace("\"","");
        }
        catch (Exception ex) {
        }
        return ssid;
    }

    private void showAddItemDialog(Context c) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                    .setTitle("Доступна новая версия")
                    .setCancelable(false)
                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String url = "http://192.168.164.5:818/Content/Update/app-debug.apk";
                            new DownloadFileAsync().execute(url);

                            //Удаляем файл текущей БД при наличии обновления
                            File dbFile= new File(getDatabasePath("Mobile.db").getPath());
                            dbFile.delete();
                        }
                    })
                    .create();
            dialog.show();
        }
        catch (Exception e) {
            Log.d("Alexey", "LoginActivity (Error 8) " + e.getMessage());
        }
    }


    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWLOAD_PROGRESS:
                progressDialog = new ProgressDialog (this);
                progressDialog.setMessage ("Загрузка файла....");
                progressDialog.setProgressStyle (ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable (false);
                progressDialog.show ( );
                return progressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            int count;
            try {

                PackageManager m = getPackageManager();
                FilePath = getPackageName();
                PackageInfo p = m.getPackageInfo(FilePath, 0);
                FilePath = p.applicationInfo.dataDir;
                Log.d ("Alexey", "dataDir1 " + FilePath);

                File folder = new File(FilePath + "/files");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    // Do something on success
                } else {
                    // Do something else on failure
                }

                URL url = new URL (params[0]);
                URLConnection conexion = url.openConnection ( );
                conexion.connect ( );

                int lenghofFile = conexion.getContentLength ( );
                //Log.d ("Alexey", "длина файла: " + lenghofFile);

                InputStream input = new BufferedInputStream (url.openStream ( ));
                //OutputStream output = new FileOutputStream (FilePath + "/files/app-debug.apk");
                OutputStream output = new FileOutputStream (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app-debug.apk");
                byte data[] = new byte[1024];
                long total = 0;

                try {
                    while ((count = input.read(data)) != 1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghofFile));
                        output.write(data, 0, count);
                    }
                }
                catch (Exception e)
                {
                    Log.d("Alexey", "LoginActivity (Error 9): " + e.getMessage());
                }
                try {
                    output.flush();
                    output.close();
                    input.close();
                }
                catch (Exception e)
                {
                    Log.d("Alexey", "LoginActivity (Error 10): " + e.getMessage());
                }

            } catch (Exception e) {
                Log.d("Alexey", "LoginActivity (Error 11): " + e.getMessage());
            }
            Install();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dismissDialog (DIALOG_DOWLOAD_PROGRESS);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute ( );
            showDialog (DIALOG_DOWLOAD_PROGRESS);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress (Integer.parseInt (values[0]));
        }
    }

    public void Install() {
        try {

            //Удаление старой БД перед обновлением*/
            File file = new File("/data/user/0/com.kazzinc.checklist/databases");
            String[] files;
            files = file.list();
            for (int i=0; i<files.length; i++) {
                File myFile = new File(file, files[i]);
                myFile.delete();
            }

            //Uri fileUri = FileProvider.getUriForFile (this, BuildConfig.APPLICATION_ID + ".provider", new File ( "/data/user/0/com.kazzinc.checklist/files/app-debug.apk"));
            Uri fileUri = FileProvider.getUriForFile (this, BuildConfig.APPLICATION_ID + ".provider", new File ( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app-debug.apk"));

            //Log.d ("Alexey", "Путь " + fileUri);

            Intent intent = new Intent (Intent.ACTION_VIEW, fileUri);

            intent.putExtra (Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    .setDataAndType (fileUri, "application/vnd.android.package-archive")
                    .setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity (intent);

        } catch (Exception e) {
            Log.d("Alexey", "LoginActivity (Error 12): " + e.getMessage());
        }
    }

    public static void createLogFile() {
        try {
            String path = "/storage/emulated/0/Key/Checklist.log";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //////////////////////////////////////
    // Получение системных путей
    private String getRootOfExternalStorage() {
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this,null);
        for(File file : externalStorageFiles) {
            // Получение полного пути приложения  /storage/emulated/0/Android/data/com.example.sportapp/files
//            return file.getAbsolutePath();
            // получение системного пути /storage/emulated/0
            return file.getAbsolutePath().replaceAll("/Android/data/" + getPackageName() + "/files", "");
        }
        return null;
    }
    ////////////////////////////////
}

