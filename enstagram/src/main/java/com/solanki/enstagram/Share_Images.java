package com.solanki.enstagram;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import bolts.Task;

public class Share_Images extends AppCompatActivity {
    ImageView imageView;
    Intent intent;
    ProgressBar progressBar;
    Button button;
    private int mProgressStatus;
    private Handler mHandler = new Handler();
    private static final int REQUEST_CAPTURE_IMAGE = 100;


    public void upload(View view) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("SELECT ONE");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Camera",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        upload_camera();
                    }
                });

        builder1.setNegativeButton(
                "Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        upload_gallery();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }

    private void upload_camera() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }

    }

    private void upload_gallery() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }


            }
        }else if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share__images);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        button = findViewById(R.id.buttonNext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }




    public void startProgress() {

        mProgressStatus = 0;
        progressBar.setProgress(mProgressStatus);
        new  AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                while (mProgressStatus < 100) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgressStatus += 2;
                    mHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(mProgressStatus);
                        }
                    });
                }
                return null;
            }

        }.execute();
    }

    public void next(View view) {
        progressBar.setVisibility(View.VISIBLE);
        startProgress();
        button.setEnabled(false);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        ParseFile parseFile = new ParseFile("image.jpg", data);
        ParseObject parseObject = new ParseObject("Image");
        parseObject.put("image", parseFile);
        parseObject.put("username", ParseUser.getCurrentUser().getUsername());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                    intent = new Intent(Share_Images.this, MyFeedActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Problem Encountered While Uploading Image", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            }
        });

    }

    public void click(View view) {
        if (view.getId() == R.id.backgroundLayout || view.getId() == R.id.imageView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
