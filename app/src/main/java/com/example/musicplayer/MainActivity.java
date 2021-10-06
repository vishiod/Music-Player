package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);
        runTimePermission();
    }

    public void runTimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySongs();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden())
                arrayList.addAll(findSong(singleFile));
            else
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                    arrayList.add(singleFile);
        }
        return arrayList;
    }

    public void displaySongs(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        songs = new ArrayList<>();

        for(File song: mySongs){
            songs.add(song.getName().toString().replace(".mp3","").replace(".wav",""));
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(myAdapter);
    }
}