package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    //public static HashMap<String,String> mp;
    public static ArrayList<File> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        //Permission from User for External Storage Read Access using Dexter Library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    //If Permission granted
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        //Getting a List of Songs in the form of a String
                        songs = getSongs(Environment.getExternalStorageDirectory());


                        //Setting Recycler View

                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        CustomAdapter adapter = new CustomAdapter(songs);
                        recyclerView.setAdapter(adapter);
                        //recyclerView.se



                    }

                    //If Permission is not granted
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "This Permission is required to proceed further", Toast.LENGTH_SHORT).show();
                    }

                    //Permission not granted and App reopened
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    //Method that returns an ArrayList of Files whose name ends with .mp3 and does not start with a .
    public static ArrayList<File> getSongs(File file){
        ArrayList<File> ans = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if(listFiles!=null){
            for(File curr:listFiles){
                if(!curr.isHidden() && curr.isDirectory()){
                    ans.addAll(getSongs(curr));
                }
                else{
                    if(curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".")){
                        ans.add(curr);
                    }
                }
            }
        }
        return ans;
    }

}



