package com.kazzinc.checklist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kazzinc.checklist.Model.Answer;
import com.kazzinc.checklist.Model.Question;
import com.kazzinc.checklist.Model.UpdateAnswersModel;
import com.kazzinc.services.APIClient;
import com.kazzinc.services.UploadService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuestionsActivityPSO extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private MsSqlDatabase msSqlDatabase;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private LinearLayout mPhotos;

    private ArrayList<Answer> answers = new ArrayList<>();
    private int qIndex = 0;
    private Question question = null;
    SharedPreferences sPref;
    private int UserIdInfo;
    private String UserDate;
    private int UserShift;
    private String Photos;
    private int photosCounter;

    private TextView tvWorkPlace;

    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    private String QuesType = "";

    private int Critical;
    private int NonCritical;
    private int TotalQues;

    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_pso);

        Critical=0;
        NonCritical=0;
        TotalQues=0;

        photosCounter=0;

        try {
            verifyStoragePeremissions(QuestionsActivityPSO.this);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }

            mPhotos = (LinearLayout) findViewById(R.id.photos);
            tvWorkPlace = (TextView) findViewById(R.id.workPlace);

            loadUserInfo();
            loadWorkPlaceInfo();
            //initDatabases();
            controlsEventInit();
            nextQuestion();
        }
        catch (Exception e) {
            Log.d("Alexey", e.getMessage());
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow_3);

        /*ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);*/
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                passQuestion("---");
                nextQuestion();
                clearComment();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String userId = sPref.getString("UserId","");
        UserIdInfo = Integer.valueOf(userId);
        String userName = sPref.getString("UserName","");
        UserDate = sPref.getString("UserDate","");
        UserShift = sPref.getInt("UserShift",2);
        QuesType = sPref.getString("QuesType","");;

        tvWorkPlace.setText("Подстанция " + loadWorkPlaceInfo());
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }*/

    private void initDatabases()
    {
        msSqlDatabase = new MsSqlDatabase();

        sqlLiteDatabase = new SqlLiteDatabase(this);
        try {
            sqlLiteDatabase.create_db(this);
        } catch (IOException e) {

        }
    }

    //инициализация событий нажатия на кнопки
    private void controlsEventInit()
    {
        //отвечаем Хорошо
        ((Button)findViewById(R.id.btn_1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passQuestion("Да");
                nextQuestion();
                clearComment();
            }
        });
        //отвечаем Плохо
        ((Button)findViewById(R.id.btn_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passQuestion("Нет");
                nextQuestion();
                clearComment();
            }
        });
        //Нажимаем кнопку Камера
        ((ImageView)findViewById(R.id.imageView)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });
    }

    private void openCameraIntent() {
        try {
            Intent pictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);

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
            }
        }
        catch(Exception e)
        {
            Log.d("Maxim","Запуск камеры  " + e.getMessage ());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // imageView.setImageURI(Uri.parse(imageFilePath));
            }
            else if (resultCode == RESULT_CANCELED) {
                // Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
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
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(path + iv.getTransitionName()), "image/*");
                //final Button btnPhoto = new Button(QuestionsActivityPSO.this);
                //btnPhoto.setTransitionName(imageName);
                startActivity(intent);
            }

        });

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                /*Toast.makeText(getApplicationContext(), "Long Clicked " ,
                        Toast.LENGTH_SHORT).show();*/
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                showAddItemDialog(QuestionsActivityPSO.this, iv.getTransitionName(), iv.getId());
                return true;    // <- set to true
            }
        });

        //mPhotos.addView(btnPhoto);
        mPhotos.addView(iv);

        return image;
    }

    private void showAddItemDialog(Context c, final String photoName, final int photoId) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
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

    private void UploadFile(String photoPath)
    {
        File file;

        try {
            file = new File(photoPath);
            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/from-data"), file);
            //Log.d("Alexey", "Загрузка " + file.getName() + " началась");

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

    private void clearComment()
    {
        ((EditText)findViewById(R.id.editText)).setText("");
    }
    //следующий вопрос
    private void nextQuestion(){
        try {
            Photos = "";
            photosCounter=0;
            mPhotos.removeAllViews();

            sqlLiteDatabase.open(this);
            ArrayList<Question> questions = sqlLiteDatabase.getQuestions(QuesType,"");
            sqlLiteDatabase.close();

            TotalQues = questions.size();



            //getSupportActionBar().setTitle("Вопрос " + qNumber + " из " + TotalQues);


            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow_3);

            if(qIndex < questions.size()){
                int qNumber = qIndex + 1;
                setTitle("Вопрос " + qNumber + " из " + TotalQues);

                question = questions.get(qIndex++);
                ((TextView)findViewById(R.id.question_tv)).setText(question.getQuestext());
            }
            else{
                saveResult();
                (new UpdateAnswersAsyncTask()).execute();
            }
        }
        catch (Exception e)
        {
            Log.d("Alexey",e.getMessage());
        }
    }

    public void setTitle(String title){
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.RIGHT);
        textView.setTextColor(getResources().getColor(R.color.colorDarkLight4));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }

    //пройти вопрос
    private void passQuestion(String ansText){
        String comment = ((EditText)findViewById(R.id.editText)).getText().toString();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = df.format(c);

        Answer answer = new Answer(-1,UserIdInfo, question.getQuesId(), ansText, UserDate, UserShift, comment, loadWorkPlaceInfo(), formattedDate, Photos);

        answers.add(answer);

        if (ansText.equals("Нет"))
        {
            if (question.getQuesIsCritical()==1)
                Critical++;
            else
                NonCritical++;
        }
    }

    //автоматическое обновление после прохождения
    class UpdateAnswersAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);

            //обновляем в локальной бд
            sqlLiteDatabase.open(QuestionsActivityPSO.this);

            sqlLiteDatabase.updateAnswers(updateAnswersModel);

            sqlLiteDatabase.close();
            saveNeedLoadParam(1);
            //возвращаемся в главную активность
            Intent intent = new Intent(QuestionsActivityPSO.this, QuestionResult.class);
            finish();
            startActivity(intent);

            return true;
        }
        protected void onPostExecute(boolean preverse){
        }
    }

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

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }
    private String loadWorkPlaceInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        String workPlaceName = sPref.getString("WorkPlaceName","");

        return workPlaceName;
    }
    private void saveResult()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("TotalQues", TotalQues);
        ed.putInt("Critical", Critical);
        ed.putInt("NonCritical", NonCritical);
        ed.commit();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog(this);
    }

    private void openQuitDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("Прервать заполнение чек-листа?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (QuestionsActivityPSO.this, MenuActivity.class);
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
