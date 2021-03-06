package com.kazzinc.checklist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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


public class QuestionsActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private MsSqlDatabase msSqlDatabase;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    private ArrayList<Answer> answers = new ArrayList<>();
    private int qIndex = 0;
    private Question question = null;
    SharedPreferences sPref;
    private int UserIdInfo;
    private String UserDate;
    private int UserShift;
    private String Photos;

    private TextView tvWorkPlace;

    private LinearLayout mPhotos;

    //private String path = "/sdcard/Android/data/com.kazzinc.checklist/files/Pictures/";

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    private String QuesType = "";
    private String QuesEquipType = "";

    private int TotalQues;
    private int photosCounter;

    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        verifyStoragePeremissions(QuestionsActivity.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        tvWorkPlace = (TextView) findViewById(R.id.workPlace);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        loadUserInfo();
        loadWorkPlaceInfo();
        //initDatabases();
        controlsEventInit();
        nextQuestion();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow_3);


    }

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
        QuesType = sPref.getString("QuesType","");

        QuesEquipType = sPref.getString("EquipmentTypeId","");

        tvWorkPlace.setText(loadWorkPlaceInfo());
        mPhotos = (LinearLayout) findViewById(R.id.photos);
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

    private void initDatabases()
    {
        msSqlDatabase = new MsSqlDatabase();

        sqlLiteDatabase = new SqlLiteDatabase(this);
        try {
            sqlLiteDatabase.create_db(this);
        } catch (IOException e) {

        }
    }

    //?????????????????????????? ?????????????? ?????????????? ???? ????????????
    private void controlsEventInit()
    {
        //???????????????? ????????????
        ((Button)findViewById(R.id.btn_1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passQuestion("????????????");
                showCheckDialog(this);
            }
        });
        //???????????????? ??????????
        ((Button)findViewById(R.id.btn_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passQuestion("??????????");
                if (checkComment()) {
                /*    nextQuestion();
                    clearComment();*/
                    showCheckDialog(this);
                }
                else
                    showCommentDialog(QuestionsActivity.this);
            }
        });
        //???????????????? ??????????????????????
        ((Button)findViewById(R.id.btn_3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passQuestion("??????????????????????");
                showCheckDialog(this);
            }
        });
        //???????????????? ???????????? ????????????
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
            Log.d("Alexey","???????????? ????????????  " + e.getMessage ());
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
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalMediaDirs()[0];// getExternalFilesDir("/sdcard/DCIM/Camera/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageName = image.getName();
        imageFilePath = image.getAbsolutePath();
        Log.d("Alexey","???????? ???????????????????? ????????: " + imageFilePath);
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
        btnPhoto.setText("???????? " + photosCounter);
        btnPhoto.setTransitionName(imageName);
        btnPhoto.setId(photosCounter);*/


        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(QuestionsActivityPSO.this, "?????????????? ????????????", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setType("image/jpg");
                intent.setAction (Intent.ACTION_VIEW);
                Uri hacked_uri = Uri.parse(getExternalMediaDirs()[0] + "/" + iv.getTransitionName());
                //Uri hacked_uri = Uri.parse(path + iv.getTransitionName());
                Log.d("Alexey","???????? ???????????????????? ???????? 1 : " + hacked_uri);
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
                showAddItemDialog(QuestionsActivity.this, iv.getTransitionName(), iv.getId());
                return true;    // <- set to true
            }
        });

        //mPhotos.addView(btnPhoto);
        mPhotos.addView(iv);

        return image;
    }

    private void showAddItemDialog(Context c, final String photoName, final int photoId) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("??????????????????????")
                .setMessage("?????????????? ?????????")
                //.setView(taskEditText)
                .setPositiveButton("????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Photos = Photos.replaceAll(photoName + ";","");
                        ImageView iv = (ImageView) findViewById(photoId);
                        mPhotos.removeView(iv);
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void showCommentDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setMessage("?????????????? ?????????????? ?? ???????? \"????????????????????\" ?? ???????????????? ????????")
                //.setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void showCheckDialog(View.OnClickListener c) {

        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.activity_check_dialog, null));
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingsDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // close your dialog
                settingsDialog.dismiss();
            }

        }, 600); // 10,000 ms delay

        settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                nextQuestion();
                clearComment();
            }
        });
    }

    private void UploadFile(String photoPath)
    {
        File file;

        try {
            file = new File(photoPath);
            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/from-data"), file);
            //Log.d("Alexey", "???????????????? " + file.getName() + " ????????????????");
            Log.d("Alexey", "????????????????_ " + photoPath + " ????????????????");
            final MultipartBody.Part photo = MultipartBody.Part.createFormData("photo", file.getName(), photoContent);

            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "????????");

            UploadService uploadService = APIClient.getClient().create(UploadService.class);

            uploadService.Upload (photo, description).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Log.d("Alexey", "????????????????_ ??????????????????");
                        Toast.makeText(getApplicationContext(), "???????????????? ?????????? ??????????????????", Toast.LENGTH_LONG);
                    }
                    else
                    {
                        Log.d("Alexey", "????????????????_ ?? ??????????????");
                        Toast.makeText(getApplicationContext(), "???????????????? ???????????????? ??????????", Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Alexey", "????????????????_ ?? ??????????????" + t.getMessage());
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
    //?????????????????? ????????????
    private void nextQuestion(){
        try {
            Photos = "";
            photosCounter=0;
            mPhotos.removeAllViews();

            sqlLiteDatabase.open(this);
            Log.d("Alexey", "?????? ???????????????????????? " + QuesEquipType + " QuesType " + QuesType);
            if (!QuesType.equals("2"))
                QuesEquipType="";

            Log.d("Alexey", "Quesr 2" + QuesEquipType);
            ArrayList<Question> questions = sqlLiteDatabase.getQuestions(QuesType,QuesEquipType);
            sqlLiteDatabase.close();

            TotalQues = questions.size();

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.right_arrow_3);

            if(qIndex < questions.size()){
                int qNumber = qIndex + 1;
                setTitle("???????????? " + qNumber + " ???? " + TotalQues);

                question = questions.get(qIndex++);
                ((TextView)findViewById(R.id.question_tv)).setText(question.getQuestext());
            }
            else{
                //(new UpdateAnswersAsyncTask()).execute();
                (new QuestionsActivity.SyncTask()).execute();

                /*if (QuesType.equals("1"))
                {
                    Intent intent = new Intent(QuestionsActivity.this, DangerActivity.class);
                    finish();
                    startActivity(intent);
                }
                else
                {*/
                    //???????????????????????? ?? ?????????????? ????????????????????
                    //Log.d("Alexey","?????????????? ???? ????????????????");
                    Intent intent = new Intent(QuestionsActivity.this, MenuActivity.class);
                    finish();

                    startActivity(intent);
                //}

            }
        }
        catch (Exception e)
        {
            Log.d("Alexey",e.getMessage());
        }
    }

    //???????????? ????????????
    private void passQuestion(String ansText){
        String comment = ((EditText)findViewById(R.id.editText)).getText().toString();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = df.format(c);

        Answer answer = new Answer(-1,UserIdInfo, question.getQuesId(), ansText, UserDate, UserShift, comment, loadWorkPlaceInfo(), formattedDate, Photos);

        answers.add(answer);
    }


    //???????????????????? ???????????? ???? ??????????
    class SyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Object... objects) {
            //???????? ???????? ?????????? ?? ?????????????????? ???? ???? ?????????????????? ?????????? ?????????? ????????????

            Log.d("Alexey","?????????????????? ?? ?????????????????? ????");

            UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);

            //?????????????????? ?? ?????????????????? ????
            sqlLiteDatabase.open(QuestionsActivity.this);

            sqlLiteDatabase.updateAnswers(updateAnswersModel);

            Log.d("Alexey","???????????????? ???? ????????????????????");

            sqlLiteDatabase.close();

            //sendPhotoToServer();
            /*String deleteQuery = "DELETE FROM Answer";
            sqlLiteDatabase.database.execSQL(deleteQuery, null);*/
            //showBottomToast("?????????????????????? ???????????????? ????????");



            return null;
        }

        @Override
        protected void onPostExecute(Void param){

            Log.d("Alexey","???? ????????????");
            saveNeedLoadParam(1);

        }
    }



    //???????????????????????????? ???????????????????? ?????????? ??????????????????????
    class UpdateAnswersAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {

            Log.d("Alexey","?????????????????? ?? ?????????????????? ????");

            UpdateAnswersModel updateAnswersModel = new UpdateAnswersModel(answers);

            //?????????????????? ?? ?????????????????? ????
            sqlLiteDatabase.open(QuestionsActivity.this);

            sqlLiteDatabase.updateAnswers(updateAnswersModel);

            Log.d("Alexey","???????????????? ???? ????????????????????");

            sqlLiteDatabase.close();

            //sendPhotoToServer();
            /*String deleteQuery = "DELETE FROM Answer";
            sqlLiteDatabase.database.execSQL(deleteQuery, null);*/
            //showBottomToast("?????????????????????? ???????????????? ????????");

            //???????????????????????? ?? ?????????????? ????????????????????
            Log.d("Alexey","?????????????? ???? ????????????????");
            Intent intent = new Intent(QuestionsActivity.this, MenuActivity.class);
            finish();
            startActivity(intent);

            return null;
        }
        @Override
        protected void onPostExecute(Void param){
            Log.d("Alexey","???? ????????????");
            saveNeedLoadParam(1);
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
        if (workPlaceName.length()==0)
            workPlaceName = sPref.getString("EquipmentName","");

        return workPlaceName;
    }
    public void showBottomToast(final String msg)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast1 = Toast.makeText(QuestionsActivity.this,msg, Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.BOTTOM, 0, 20);
                toast1.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog(this);
    }

    private void openQuitDialog(Context c) {

        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
                .setTitle("???????????????? ???????????????????? ??????-???????????")

                .setPositiveButton("????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (QuestionsActivity.this, MenuActivity.class);
                        intent.putExtra("inputPage", "checkList");
                        finish ();
                        startActivity (intent);
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private boolean checkComment()
    {
        boolean result = true;

        if (((EditText)findViewById(R.id.editText)).getText().length()==0)
            result = false;
        if (Photos.length()==0)
            result = false;

        return result;
    }
}
