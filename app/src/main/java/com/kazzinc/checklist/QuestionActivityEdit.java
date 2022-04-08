package com.kazzinc.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuestionActivityEdit extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    private String Photos = "";
    private int photosCounter;

    private LinearLayout mPhotos;
    String imageName;

    String ansId = "";
    String questId = "";
    String userId = "";
    String date ="";
    String shift ="";
    String workplace = "";

    SharedPreferences sPref;
    String UserId;
    private String UserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        mPhotos = (LinearLayout) findViewById(R.id.photos_edit);

        TextView twWorkPlace = (TextView) findViewById(R.id.workPlaceEdit);
        TextView twQuestion = (TextView) findViewById(R.id.question_tv_edit);
        TextView twComment = (TextView) findViewById(R.id.editText_edit);

        Bundle arguments = getIntent().getExtras();

        try {

            ansId = arguments.get("AnsId").toString();
            questId = arguments.get("QuestId").toString();
            userId = arguments.get("UserId").toString();
            date = arguments.get("Date").toString();
            shift = arguments.get("Shift").toString();
            workplace = arguments.get("WorkPlace").toString();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT Answer.AnswerId, Answer.AnswerText, Answer.AnswerComment, Answer.AnswerWorkPlaceName, Answer.AnswerDateTime, Answer.AnswerPhotos, Question.QuesText FROM Answer INNER JOIN Question ON Answer.AnswerQuesId = Question.QuesId WHERE (Answer.AnswerId ='" + ansId + "')";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    //Photos = cursor.getString(5);
                    twQuestion.setText(cursor.getString(6));
                    //twComment.setText(cursor.getString(2));
                    twWorkPlace.setText(cursor.getString(3));
                } while (cursor.moveToNext());
            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        /*if (Photos != null && !Photos.isEmpty()) {
            String[] items = Photos.split(";");
            for (String item : items) {
                loadIcon(item);
            }
        }*/

        controlsEventInit();
        loadUserInfo();
    }

    private void loadIcon(String imageName)
    {
        //ImageView Setup
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.image_attach_3);
        iv.setTransitionName(imageName);
        iv.setId(photosCounter);
        iv.setPadding(0,0,5,0);

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
                showAddItemDialog(QuestionActivityEdit.this, iv.getTransitionName(), iv.getId());
                return true;    // <- set to true
            }
        });

        mPhotos.addView(iv);
    }

    //инициализация событий нажатия на кнопки
    private void controlsEventInit()
    {
        //отвечаем Хорошо
        ((Button)findViewById(R.id.btn_1_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkComment()) {
                    passQuestion("Хорошо");
                }
                else
                    showCommentDialog(QuestionActivityEdit.this);
            }
        });
        //отвечаем Плохо
        ((Button)findViewById(R.id.btn_2_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkComment()) {
                    passQuestion("Плохо");
                }
                else
                    showCommentDialog(QuestionActivityEdit.this);
            }
        });
        //отвечаем Неприменимо
        ((Button)findViewById(R.id.btn_3_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passQuestion("Неприменимо");
                if (checkComment()) {
                    passQuestion("Неприменимо");
                }
                else
                    showCommentDialog(QuestionActivityEdit.this);
            }
        });
        //Нажимаем кнопку Камера
        ((ImageView)findViewById(R.id.imageView_edit)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });
    }

    //пройти вопрос
    private void passQuestion(String ansText){
        Log.d("Alexey","pass");

        String comment = ((EditText)findViewById(R.id.editText_edit)).getText().toString();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = df.format(c);

        try {
            sqlLiteDatabase.open(this);

            //String selectQuery = "UPDATE Answer SET AnswerText='" + ansText + "', AnswerComment='" + comment + "', AnswerPhotos='"+ Photos +"' WHERE AnswerId='" + ansId+"'";
            String selectQuery = "INSERT INTO Answer (AnswerUserId, AnswerQuesId, AnswerText, AnswerDate, AnswerShift, AnswerComment, AnswerWorkPlaceName, AnswerDateTime, AnswerPhotos) VALUES ('"+ userId + "','" + questId + "','" + ansText + "','" + date +"','" + shift  + "','" + comment + "','" + workplace + "','" + formattedDate + "','" + Photos +"')";
            Log.d("Alexey",selectQuery);
            sqlLiteDatabase.database.execSQL(selectQuery);
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey", e.getMessage());
        }

        Intent intent = new Intent(QuestionActivityEdit.this, AnswerResultActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("Date", date);
        intent.putExtra("WorkPlace", workplace);
        startActivity(intent);
        finish();
        saveNeedLoadParam(1);
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
                showAddItemDialog(QuestionActivityEdit.this, iv.getTransitionName(), iv.getId());
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

    private void saveNeedLoadParam(int needLoad)
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        getSupportActionBar().setTitle(userName);
    }

    private boolean checkComment()
    {
        boolean result = true;

        if (((EditText)findViewById(R.id.editText_edit)).getText().length()==0)
            result = false;
        if (Photos.length()==0)
            result = false;

        Log.d("Alexey", "Check " + result);
        return result;
    }

    private void showCommentDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setMessage("Укажите причину в поле \"Примечание\" и сделайте фото")
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
