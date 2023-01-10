package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.Songs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PlaySong extends AppCompatActivity {
    private TextView textView;
    private ImageButton previous;
    private ImageButton play_pause;
    private ImageButton next;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ImageView imageView;
    //private ArrayList<File> Songs;
    private static boolean isPlaying = false;
    private Thread UpdateSeek;
    int[] song_position = new int[0];
    private int currDuration = 0;

    private void play_song(){
        this.onDestroy();
        Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
        textView.setText(Songs.get(song_position[0]).getName());
        mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        isPlaying = true;
        seekBar.setProgress(0);
        play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Intent intent = getIntent();
        //Bundle bundle = intent.getExtras();
        //Songs = (ArrayList) bundle.getParcelableArrayList("com.example.musicplayer.CustomAdapter.SongList");
        String song_name = intent.getStringExtra("com.example.musicplayer.CustomAdapter.SongName");
        song_position = new int[]{intent.getIntExtra("com.example.musicplayer.CustomAdapter.SongPosition", 0)};
        textView = findViewById(R.id.mp_song_text);
        previous = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.album_cover_image);

       // File[] ext = ContextCompat.getExternalFilesDirs(PlaySong.this, )
        //Added for Marquee
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setText(song_name);

        //On Click Listeners for Image Buttons
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                if(song_position[0] !=0){
                    song_position[0]--;
                }
                else{
                    song_position[0]=Songs.size()-1;
                }
                Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
                textView.setText(Songs.get(song_position[0]).getName());
                mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                currDuration = mediaPlayer.getDuration();
                isPlaying = true;
                seekBar.setProgress(0);
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                  //this.play_song();
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    isPlaying = false;
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                else{
                    mediaPlayer.start();
                    isPlaying = true;
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(song_position[0] !=Songs.size()-1){
                    song_position[0]++;
                }
                else{
                    song_position[0]=0;
                }
                Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
                textView.setText(Songs.get(song_position[0]).getName().replace(".mp3", ""));
                mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(PlaySong.this, "Hello Restarting", Toast.LENGTH_SHORT).show();
                        currDuration = 0;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        if(song_position[0] !=Songs.size()-1){
                            song_position[0]++;
                        }
                        else{
                            song_position[0]=0;
                        }
                        Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
                        textView.setText(Songs.get(song_position[0]).getName().replace(".mp3", ""));
                        mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                currDuration = 0;

                            }
                        });
                        currDuration = mediaPlayer.getDuration();
                        isPlaying = true;
                        seekBar.setProgress(0);
                        play_pause.setImageResource(R.drawable.ic_baseline_pause_24);

                        if(UpdateSeek!=null){
                            UpdateSeek.interrupt();
                            UpdateSeek = null;
                        }

                        MediaPlayer finalMediaPlayer = mediaPlayer;
                        UpdateSeek = new Thread(){
                            @Override
                            public void run() {
                                int CurrentPosition = 0;
                                try{
                                    while (finalMediaPlayer !=null && CurrentPosition < finalMediaPlayer.getDuration()) {
                                        try {
                                            if (finalMediaPlayer != null && finalMediaPlayer.isPlaying())
                                                CurrentPosition = finalMediaPlayer.getCurrentPosition();
                                            if (seekBar != null)
                                                seekBar.setProgress(CurrentPosition);
                                            Thread.sleep(1000);
                                        }
                                        catch (NullPointerException e){
                                            e.printStackTrace();
                                        }
                                        catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                catch(IllegalStateException e){
                                    e.printStackTrace();
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        };

                        UpdateSeek.start();

                    }
                });
                currDuration = mediaPlayer.getDuration();
                isPlaying = true;
                seekBar.setProgress(0);
                play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
//                synchronized (UpdateSeek){
//                    UpdateSeek.notify();
//                }
            }
        });


        //SeekBar Listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b && isPlaying){
                    mediaPlayer.seekTo(i);
                    //seekBar.setProgress(i);
                }
                else if(b && !mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(i);
                    //seekBar.setProgress(i);
                    mediaPlayer.pause();
                    isPlaying = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(isPlaying) {
                    mediaPlayer.pause();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isPlaying){
                    mediaPlayer.start();
                    play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
                else{
                    mediaPlayer.pause();
                    play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }

            }
        });

        //Starts Playing as soon as a song is clicked
        File song = Songs.get(song_position[0]);
        Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(PlaySong.this, "Hello World", Toast.LENGTH_SHORT).show();
            }
        });
        currDuration = mediaPlayer.getDuration();
        isPlaying =true;
        seekBar.setMax(mediaPlayer.getDuration());

        //Getting Album Cover from mp3 File
            //String albumId = song.;
            //Uri image = ContentUris.withAppendedId(uri, Long.parseLong(albumId));
            //        Picasso.get().load(image)
            //                .fit()
            //                .centerCrop()
            //                .error(R.drawable.music)
            //                .into(imageView);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(song.getAbsolutePath());
        byte[] data = mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            imageView.setImageResource(R.drawable.music); //any default cover resourse folder
        }

        //imageView.setAdjustViewBounds(true);
        //imageView.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
        //String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);


        //SeekBar Updater
        UpdateSeek = new Thread(){
            @Override
            public void run() {
                int CurrentPosition = 0;
                try{
                while (mediaPlayer!=null && CurrentPosition <= currDuration) {

                    try {

                        if (mediaPlayer != null && mediaPlayer.isPlaying())
                            CurrentPosition = mediaPlayer.getCurrentPosition();
                        if (seekBar != null)
                            seekBar.setProgress(CurrentPosition);
                        Thread.sleep(1000);
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                    try {
//                        synchronized (this) {
//                            this.wait();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                }
                catch(IllegalStateException e){
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
//Inside try
//        if(CurrentPosition==currDuration){
//            if(song_position[0] !=Songs.size()-1){
//                song_position[0]++;
//            }
//            else{
//                song_position[0]=0;
//            }
//            isPlaying = false;
//            UpdateSeek.interrupt();
//            UpdateSeek = null;
//            mediaPlayer.stop();
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer = null;
//
//            textView = findViewById(R.id.mp_song_text);
//            previous = findViewById(R.id.previous);
//            play_pause = findViewById(R.id.play_pause);
//            next = findViewById(R.id.next);
//            seekBar = findViewById(R.id.seekBar);
//            imageView = findViewById(R.id.album_cover_image);
//
//
//            textView.setSelected(true);
//            textView.setFocusable(true);
//            textView.setFocusableInTouchMode(true);
//            textView.setText(Songs.get(song_position[0]).getName().replace(".mp3", ""));
//
//            File song = Songs.get(song_position[0]);
//            Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
//            mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();
//                }
//            });
//            isPlaying =true;
//            seekBar.setMax(mediaPlayer.getDuration());
//
//
//        }
        UpdateSeek.start();


    }
    @Override
    protected void onPause() {
        super.onPause();
//        Intent backToMain = new Intent(PlaySong.this, MainActivity.class);
//        backToMain.putExtra("com.example.musicplayer.PlaySong.SongPosition", song_position[0]);
//        startActivity(backToMain);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent backToMain = getIntent();
//        int song_position = backToMain.getIntExtra("com.example.musicplayer.PlaySong.SongPosition", 0);
        isPlaying = false;
        UpdateSeek.interrupt();
        UpdateSeek = null;
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

        textView = findViewById(R.id.mp_song_text);
        previous = findViewById(R.id.previous);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.album_cover_image);


        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setText(Songs.get(song_position[0]).getName().replace(".mp3", ""));

        File song = Songs.get(song_position[0]);
        Uri uri = Uri.parse(Songs.get(song_position[0]).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        isPlaying =true;
        seekBar.setMax(mediaPlayer.getDuration());

        if(UpdateSeek!=null){
            UpdateSeek.interrupt();
            UpdateSeek = null;
        }

        UpdateSeek = new Thread(){
            @Override
            public void run() {
                int CurrentPosition = 0;
                try{
                    while (mediaPlayer!=null && CurrentPosition < mediaPlayer.getDuration()) {
                        try {
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
                                    CurrentPosition = mediaPlayer.getCurrentPosition();
                            if (seekBar != null)
                                seekBar.setProgress(CurrentPosition);
                            Thread.sleep(1000);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch(IllegalStateException e){
                    e.printStackTrace();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        UpdateSeek.start();


    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        UpdateSeek.interrupt();
        UpdateSeek = null;
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}