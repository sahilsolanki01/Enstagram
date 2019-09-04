package com.solanki.enstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {
    boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        final LinearLayout linearLayout = findViewById(R.id.linear);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        setTitle(username+"'s"+"  "+"Photo's");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject object : objects){
                        ParseFile parseFile = (ParseFile) object.get("image");

                        parseFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e==null && data!=null){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                                    final ImageView imageView = new ImageView(getApplicationContext());

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setImageBitmap(bitmap);
                                    linearLayout.addView(imageView);

                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(isImageFitToScreen) {
                                                isImageFitToScreen=false;
                                                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                                imageView.setAdjustViewBounds(true);
                                            }else{
                                                isImageFitToScreen=true;
                                                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

    }
}
