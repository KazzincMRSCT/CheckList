package com.kazzinc.checklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.Model.Answer;
import com.kazzinc.checklist.Model.Equipment;
import com.kazzinc.checklist.Model.Question;
import com.kazzinc.checklist.Model.UpdateAnswersModel;
import com.kazzinc.checklist.Model.User;
import com.kazzinc.checklist.Model.WorkPlace;
import com.kazzinc.services.APIClient;
import com.kazzinc.services.UploadService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Intent intentService;
    private TableLayout mChklWorkPlace;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    SharedPreferences sPref;
    Handler timerHandler = new Handler();

    private MsSqlDatabase msSqlDatabase = new MsSqlDatabase();
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private ArrayList<Answer> answers = new ArrayList<>();

    private LinearLayout mLayoutShift;

    private String UserRole;
    private String QuesType;

    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    private String Shift;
    String formattedDate;
    String UserId;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Shift = "1";
        Date c = Calendar.getInstance().getTime();

        int h = c.getHours();
        if ((h >= 6) && (h < 18))
            Shift = "2";
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        formattedDate = df.format(c);

        loadUserInfo();

        //(new SyncDatabasesTask()).execute();

        stopService(
                new Intent(MainActivity.this, SyncService.class));

        LinearLayout llEquipment = (LinearLayout) findViewById(R.id.llEquipment);
        LinearLayout llWorkPlace = (LinearLayout) findViewById(R.id.llWorkPlace);

        CardView cardEquipment = (CardView) findViewById(R.id.cardEquipment);
        CardView cardWorkPlace = (CardView) findViewById(R.id.cardWorkPlace);
        CardView cardPSO1 = (CardView) findViewById(R.id.cardPSO1);
        CardView cardPSO6 = (CardView) findViewById(R.id.cardPSO6);

        TextView tw = (TextView) findViewById(R.id.textView);

        tw.setText("Выбор чек-листа");

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
                tw.setText("Выбор чек-листа");
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

                Vibrator vibro = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibro.vibrate(30);

                switch (v.getId()) {

                    case R.id.cardEquipment:
                        QuesType="2";
                        saveUserInfo();
                        try {
                            Intent intentEquipment = new Intent (MainActivity.this, EquipmentSelect.class);
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
                            Intent intentPSO = new Intent (MainActivity.this, WorkPlaceSelect.class);
                            //finish ();
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
                            Intent intentPSO = new Intent (MainActivity.this, WorkPlaceSelect.class);
                            //finish ();
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
                            Intent intentWorkPlace = new Intent (MainActivity.this, WorkPlaceSelect.class);
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

        Intent serviceIntent = new Intent(this, SyncService.class);
        serviceIntent.putExtra("inputExtra", "Служба синхронизации данных");

        ContextCompat.startForegroundService(this, serviceIntent);

    }

    /*Runnable timerRunnable = new Runnable()
    {
        @Override public void run()
        {
            if (loadNeedLoadParam()==1) {
                try {
                    (new UpdateAnswersAsyncTask()).execute();
                }
                catch (Exception e)
                {
                    Log.d("Alexey", "Ошибка загрузки " + e.getMessage());
                }
            }
            timerHandler.postDelayed(this, 3000);
        }
    };*/

    private void sendPhotoToServer()
    {
        if(msSqlDatabase.checkConnection()){
            try {
                sqlLiteDatabase.open(this);
                String selectQuery = "SELECT  * FROM Answer";
                Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        String AnswerPhotos = cursor.getString(9);
                        if (AnswerPhotos != null && !AnswerPhotos.isEmpty()) {
                            String[] items = AnswerPhotos.split(";");
                            for (String item : items) {
                                UploadFile(path + item);
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
        File file;

        try {
            file = new File(photoPath);
            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/from-data"), file);

            final MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", file.getName(), photoContent);

            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Файл");

            UploadService uploadService = APIClient.getClient().create(UploadService.class);

            uploadService.Upload (photo, description).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Загрузка файла выполнена", Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Проблема загрузки файла", Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Alexey", e.getMessage());
        }
    }

    private boolean IsHaveEquipment(){

        boolean result = false;

        String selectQuery = "SELECT DISTINCT TaskEquipName FROM Task WHERE TaskDate='" + formattedDate + "' AND TaskShift=" + Shift + " AND TaskEmplId=" + UserId + " AND TaskEquipId>0 ORDER BY TaskEquipName";

        sqlLiteDatabase.open(this);

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

        sqlLiteDatabase.open(this);

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

    //автоматическое обновление после прохождения
    class UpdateAnswersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            //проверяем соединение с удаленной бд и обновялем вопросы в удаленной бд
            if(msSqlDatabase.checkConnection()){
                UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);
                //обновляем в локальной бд
                sqlLiteDatabase.open(MainActivity.this);

                sqlLiteDatabase.updateAnswers(updateAnswersModel);

                String selectQuery = "SELECT  * FROM Answer";

                Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

                //проверяем соединение с удаленной бд и обновялем вопросы в удаленной бд
                if(msSqlDatabase.checkConnection()){
                    try {
                        if (cursor.moveToFirst()) {
                            do {
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
                            } while (cursor.moveToNext());
                        }
                        saveNeedLoadParam(0);
                        showToast("Обновление данных выполнено");

                    }
                    catch (Exception e){
                        Log.d("Alexey",e.getMessage());
                    }
                }
                else {
                    saveNeedLoadParam(1);
                }
                sqlLiteDatabase.close();

                sendPhotoToServer();

                showToast("Выполняется загрузка фото");
            }

            return true;
        }

        protected void onPostExecute(boolean preverse){
        }
    }

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    private int loadNeedLoadParam()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userId = sPref.getString("NeedLoad","0");
        return Integer.valueOf(userId);
    }

    private String loadWorkPlaceInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String workPlaceName = sPref.getString("WorkPlaceName","");

        return workPlaceName;
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");

        //getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#979797\">Чек-лист / " + userName + "</font>")));
        getSupportActionBar().setTitle(userName);
        //getSupportActionBar().setTitle("Чек-лист / " + userName + " / " + loadWorkPlaceInfo());
    }
    private void saveUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("UserDate", formattedDate);
        ed.putInt("UserShift", Integer.valueOf(Shift));
        ed.putString("QuesType", QuesType);
        ed.commit();
    }

    /*
     * adapter where the list values will be set
     */
    private ArrayAdapter<String> dogsAdapter(String dogsArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogsArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Generate bottom border only
                LayerDrawable bottomBorder = getBorders(
                        Color.parseColor("#73BD42"), // Background color
                        Color.LTGRAY, // Border color
                        0, // Left border in pixels
                        0, // Top border in pixels
                        0, // Right border in pixels
                        1 // Bottom border in pixels
                );

                // setting the ID and text for every items in the list
                String item = getItem(position);
                String[] itemArr = item.split("::");
                String text = itemArr[0];
                String id = itemArr[1];

                // visual settings for the list item
                TextView listItem = new TextView(MainActivity.this);

                listItem.setText(text);
                listItem.setTag(id);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setGravity(Gravity.CENTER);
                listItem.setTextColor(Color.WHITE);

                //listItem.setBackground(bottomBorder);

                return listItem;
            }
        };

        return adapter;
    }
    // Custom method to generate one or multi side border for a view
    protected LayerDrawable getBorders(int bgColor, int borderColor,
                                       int left, int top, int right, int bottom){
        // Initialize new color drawables
        ColorDrawable borderColorDrawable = new ColorDrawable(borderColor);
        ColorDrawable backgroundColorDrawable = new ColorDrawable(bgColor);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                backgroundColorDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                left, // Number of pixels to add to the left bound [left border]
                top, // Number of pixels to add to the top bound [top border]
                right, // Number of pixels to add to the right bound [right border]
                bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }

    public void showToast(final String msg)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showBottomToast(final String msg)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(MainActivity.this,msg, Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 20);
                toast1.show();
            }
        });
    }

    public void showBottomToastShort(final String msg)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.BOTTOM, 0, 20);
                toast1.show();
            }
        });
    }

    //Обновление данных с сервера
    class SyncDatabasesTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        private View rootView;

        @Override
        protected Boolean doInBackground(Void... params) {
            //если есть связь с удаленной бд то обновляем иначе вывод ошибки
            if(msSqlDatabase.checkConnection()) {
                try {
                    //обновляем пользователей в локальной бд
                    List<User> userList = msSqlDatabase.getUsers();
                    sqlLiteDatabase.open(MainActivity.this);
                    sqlLiteDatabase.updateUsers(userList);

                    //обновляем вопросы в локальной бд
                    List<Question> questionList = msSqlDatabase.getQuestions();
                    sqlLiteDatabase.open(MainActivity.this);
                    sqlLiteDatabase.updateQuestions(questionList);

                    //обновляем места работ в локальной бд
                    List<WorkPlace> workPlaceList = msSqlDatabase.getWorkPlaceList();
                    sqlLiteDatabase.open(MainActivity.this);
                    sqlLiteDatabase.updateWorkPlaceList(workPlaceList);

                    //обновляем оборудование в локальной бд
                    Log.d("Alexey", "Equipment_ 1");
                    List<Equipment> equipmentList = msSqlDatabase.getEquipment();
                    sqlLiteDatabase.open(MainActivity.this);
                    sqlLiteDatabase.updateEquipment(equipmentList);
                    Log.d("Alexey", "Equipment_ 2");

                    //обновляем вопросы в локальной бд
                    /*List<Question> questions = msSqlDatabase.getQuestions();
                    sqlLiteDatabase.updateQuestions(questions);*/

                    //обнвляем ответы в удаленной бд
                    /*ArrayList<Answer> answers = sqlLiteDatabase.getAnswers();
                    UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);
                    msSqlDatabase.updateAnswers(updateAnswersModel);;*/
                    sqlLiteDatabase.close();

                    showBottomToastShort("Справочные данные обновлены 1");
                }
                catch (Exception e){
                    String m = e.getMessage();
                    Log.d("Alexey", m);
                }
            }

            return true;
        }
        protected void onPostExecute(boolean preverse){

        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog(this);
    }

    private void openQuitDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Выйти из приложения?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                        finish ();
                        startActivity (intent);
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
}
