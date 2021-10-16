package com.github.ASDFGQWERY.sciencesleep1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    TextView playerPosition, playerDuration, tv;
    SeekBar seekBar;
    ImageView btRew, btPlay, btPause, btFF;
    Button repeatBtn;
    Boolean repeatFlag = false;

    private static boolean userPressedBackAgain;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler(Looper.myLooper());
    Runnable runnable;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //終了ボタン処理
        Button closeapp = findViewById(R.id.closeapp);
        closeapp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                //moveTaskToBack(true);
                //finishAffinity();
                //System.exit(0);
                mediaPlayer.release();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
                System.exit(0);
            }
        });

        //広告
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //Player実装
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btFF = findViewById(R.id.bt_ff);
        tv = (TextView) this.findViewById(R.id.tv);
        tv.setSelected(true);  // Set focus to the textview

        btPause.setVisibility(View.GONE);

        mediaPlayer = MediaPlayer.create(this, R.raw.einslowv42l3);

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this,0);
            }
        };



        int duration = mediaPlayer.getDuration();
        String sDuration = convertFormat(duration);
        playerDuration.setText(sDuration);

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable,0);
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

        btFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (mediaPlayer.isPlaying() && duration != currentPosition) {
                    currentPosition = currentPosition + 5000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                    currentPosition = currentPosition - 5000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (repeatFlag){
                    mediaPlayer.start();

                } else {

                    btPause.setVisibility(View.GONE);
                    btPlay.setVisibility(View.VISIBLE);
                    mediaPlayer.seekTo(0);
                }
            }
        });

        repeatBtn = findViewById(R.id.repeatBtn);


    }

    /*
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

     */

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration)-
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }


    public void repeatSong(View view) {

        if (repeatFlag){
            repeatBtn.setBackgroundResource(R.drawable.ic_repeat_off);
        } else {
            repeatBtn.setBackgroundResource(R.drawable.ic_repeat_on);
        }
        repeatFlag = !repeatFlag;

    }

    //2度戻るボタンで終了
    @Override
    public void onBackPressed(){
        if (!userPressedBackAgain){
            //Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            userPressedBackAgain = true;
        } else {
            mediaPlayer.release();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
            System.exit(0);
        }
        new CountDownTimer(1500,100){
            @Override
            public void onTick(long millsUntilFinished) {

            }

            @Override
            public void onFinish(){
                userPressedBackAgain = false;
            }
        }.start();
    }




}