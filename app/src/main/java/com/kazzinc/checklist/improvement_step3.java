package com.kazzinc.checklist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class improvement_step3 extends AppCompatActivity {

    LinearLayout cont;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    String Id = "";

    String Photos;


    EditText etImproveResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improvement_step3);

        cont = (LinearLayout)  findViewById(R.id.photosContainer);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null)
        {
            Id= arguments.getString("improveId");
        }

        Button pickImage = (Button) findViewById(R.id.addPhotoGallery);
        pickImage.setOnClickListener(new View.OnClickListener() {

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
    }

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

                            CompressedPhotoPath(PhotoRealPath.getRealPathFromUri(improvement_step3.this, imageUri));
                            //listURI.add(imageUri);
                            listURI.add(PhotoRealPath.getName(PhotoRealPath.getRealPathFromUri(improvement_step3.this, imageUri)));
                        }
                        GetPhotos(listURI);
                    }

                }
        }
    }

    protected void CompressedPhotoPath (String Path)
    {
        try {

            String fileName = PhotoRealPath.getName(Path);

            Bitmap b = BitmapFactory.decodeFile(Path);

            // original measurements
            int origWidth = b.getWidth();
            int origHeight = b.getHeight();

            final int destWidth = 1280;//or the width you need

            Bitmap b2 = Bitmap.createScaledBitmap(b, 1280, 920, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
            // we save the file, at least until we have made use of it
            File f = new File(getExternalMediaDirs()[0] + "/" + fileName);
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            // remember close de FileOutput
            fo.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void GetPhotos(final List<String> strigData)
    {
        Photos = "";

        for (int i=0; i<strigData.size(); i++) {

            Photos = Photos + strigData.get(i) + ";";

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
            layoutParams.setMargins(5, 10, 5, 10);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(64, 64);
            lp.setMargins(5, 10, 0, 0);

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            ImageView img = new ImageView(this);
            img.setLayoutParams(layoutParams);
            img.setImageBitmap(BitmapFactory.decodeFile(getExternalMediaDirs()[0] + "/"+ strigData.get(i)));
            //img.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            img.setBackgroundResource(R.drawable.photoborder);
            ll.addView(img);

            final ImageButton ib = new ImageButton(this);
            ib.setBackgroundResource(R.drawable.clear64);
            ib.setTransitionName(String.valueOf(i));
            ll.addView(ib, lp);

            ib.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    strigData.remove(Integer.parseInt(ib.getTransitionName()));
                    cont.removeAllViews();
                    GetPhotos(strigData);
                }
            });

            cont.addView(ll);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);

        /*for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#4682B4")), 0,     spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                return true;

            case R.id.ImproveSave:
                SaveStep3();

                Intent intent = new Intent(improvement_step3.this, improvement.class);
                startActivity(intent);

                showBottomToast("Ваше предложение добавлено!");

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showBottomToast(final String msg)
    {
        Toast toast1 = Toast.makeText(improvement_step3.this,msg, Toast.LENGTH_LONG);
        toast1.setGravity(Gravity.BOTTOM, 0, 20);
        toast1.show();
    }

    private void SaveStep3()
    {
        sqlLiteDatabase.open(improvement_step3.this);

        String updateQuery = "UPDATE Improvement SET Photos = '" + Photos + "' WHERE Id='" + Id + "'" ;

        sqlLiteDatabase.database.execSQL(updateQuery);

        sqlLiteDatabase.close();
    }
}