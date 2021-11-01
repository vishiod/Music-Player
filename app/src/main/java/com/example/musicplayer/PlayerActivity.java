package com.example.musicplayer;

import static com.example.musicplayer.R.color.colorPrimary;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {

    static MediaPlayer mediaPlayer;
    Button playButton, nextButton, previousButton, fastForwardButton, fastRewindButton;
    TextView songName, startTime, endTime;
    SeekBar seekMusic;
    String songNameToBeDisplayedInTextView;
    int position;
    ArrayList<File> mySongs;

    Thread updateSeekBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mediaPlayer = new MediaPlayer();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        playButton = findViewById(R.id.playButton);
        fastForwardButton = findViewById(R.id.fastForwardButton);
        fastRewindButton = findViewById(R.id.fastRewindButton);

        songName = findViewById(R.id.songName);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);

        seekMusic = findViewById(R.id.seekBar);

        //If a song is running

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        position = bundle.getInt("pos", 0);

        songName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songNameToBeDisplayedInTextView = mySongs.get(position).getName();

        songName.setText(songNameToBeDisplayedInTextView);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        updateSeekBar = new Thread(() -> {
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = mediaPlayer.getCurrentPosition();

            while (currentPosition < totalDuration) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException | IllegalAccessError e) {
                    e.printStackTrace();
                }
                currentPosition = mediaPlayer.getCurrentPosition();
                seekMusic.setProgress(currentPosition);
            }
        });
        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();

        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(colorPrimary), PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        endTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, delay);
            }
        }, delay);

        playButton.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                playButton.setBackgroundResource(R.drawable.ic_play);
                mediaPlayer.pause();
            } else {
                playButton.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.start();
            }
        });

        nextButton.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = (position + 1) % (mySongs.size());

            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            songNameToBeDisplayedInTextView = mySongs.get(position).getName();

            songName.setText(songNameToBeDisplayedInTextView);
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.ic_pause);

        });

        previousButton.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0) ? (mySongs.size() - 1) : position - 1;

            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            songNameToBeDisplayedInTextView = mySongs.get(position).getName();

            songName.setText(songNameToBeDisplayedInTextView);
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.ic_pause);

        });

        fastForwardButton.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10 * 1000);
        });

        fastRewindButton.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10 * 1000);
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> nextButton.performClick());


    }

    public String milliSecondsToTimer(int milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}