package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.getSongs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public class PlaySong extends AppCompatActivity {
    private TextView textView;
    private ImageButton previous;
    private ImageButton play_pause;
    private ImageButton next;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Intent intent = getIntent();
        String song_name = intent.getStringExtra("com.example.musicplayer.CustomAdapter.SongName");

        textView = findViewById(R.id.mp_song_text);
        previous = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        textView.setText(song_name);
        File song = getSong(song_name, Environment.getExternalStorageDirectory());
        MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.fromFile(song));
        mediaPlayer.start();
    }

    private File getSong(String name, File file) {
        File listFiles[] = file.listFiles();
        if (listFiles != null) {
            for (File curr : listFiles) {
                if (!curr.isHidden() && curr.isDirectory()) {
                    return getSong(name, curr);
                } else {
                    if (curr.getName().endsWith(".mp3") && !curr.getName().startsWith(".") && curr.getName().replace(".mp3", "") == name) {
                        return curr;
                    }
                }
            }
        }
        return file;
    }
}