package com.solanki.enstagram;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ListActivity extends AppCompatActivity {
    ArrayList<String> list;
    ListView listView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> tempList = new ArrayList();

                for (String temp : list) {
                    if (temp.toLowerCase().contains(newText.toLowerCase())) {
                        tempList.add(temp);
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, tempList);
                listView.setAdapter(adapter);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Intent intent = new Intent(ListActivity.this, Share_Images.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            finish();
        } else if (item.getItemId() == R.id.myfeed) {
            Intent intent = new Intent(ListActivity.this, MyFeedActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle("User Feed");

        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, UserFeedActivity.class);
                intent.putExtra("username", list.get(position));
                startActivity(intent);
            }
        });

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        parseQuery.addAscendingOrder("username");

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            list.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
