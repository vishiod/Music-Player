package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button playButton, nextButton, previousButton, fastForwardButton, fastRewindButton;
    TextView songName, startTime, endTime;
    SeekBar seekMusic;
    BarVisualizer visualizer;

    String sName;
    public static final String EXTRA_NAME = "Song Name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        previousButton = (Button) findViewById(R.id.previousButton);
        nextButton     = (Button) findViewById(R.id.nextButton);
        playButton     = (Button) findViewById(R.id.playButton);
        fastForwardButton = (Button) findViewById(R.id.fastForwardButton);
        fastRewindButton  = (Button) findViewById(R.id.fastRewindButton);

        songName  = (TextView) findViewById(R.id.songName);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime   = (TextView) findViewById(R.id.endTime);

        seekMusic = (SeekBar) findViewById(R.id.seekBar);

        visualizer = (BarVisualizer) findViewById(R.id.blast);

        //If a song is running

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String currentSongName = i.getStringExtra("songName");
        position = bundle.getInt("pos", 0);

        songName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName();

        songName.setText(sName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    playButton.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    playButton.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });
    }
}