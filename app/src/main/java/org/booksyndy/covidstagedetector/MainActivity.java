package org.booksyndy.covidstagedetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView micView;
    private LinearLayout swLL;
    private TextView timeView;
    private long startTime;
    private boolean recording=false;
    private Handler handler;
    private MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaRecorder recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
        recorder.setOutputFile(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/covidcheck_" + System.currentTimeMillis() + ".mp3");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        micView = findViewById(R.id.micView);
        swLL = findViewById(R.id.swLL);
        timeView = findViewById(R.id.recTimeView);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        micView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                startTime = System.currentTimeMillis();


                if (!recording) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 123);
                    }
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 234);
                    }

                    else {
                        recording = !recording;
                        startRecording();
                        Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                        swLL.setVisibility(View.VISIBLE);
                        timeView.setText("0:00");
                        handler = new Handler();
                        micView.setImageResource(R.drawable.ic_mic_24px);
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                int timeDiff = Long.valueOf(System.currentTimeMillis() - startTime).intValue();
                                setTimeElapsed(timeDiff);

                                handler.postDelayed(this, 500);
                            }
                        }, 500);

                    }



                }
                else{
                    stopRecording();

                    recording = !recording;
//                    Toast.makeText(MainActivity.this, "Stopped recording", Toast.LENGTH_SHORT).show();
                    swLL.setVisibility(View.INVISIBLE);
                    handler.removeMessages(0);
                    micView.setImageResource(R.drawable.ic_mic_none_24px);
                    timeView.setText("0:00");
                }
            }
        });





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    micView.performClick();
                } else {

                    Toast.makeText(this, "Can't record; please grant permission.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void setTimeElapsed(long millis) {

        int tsec = (int) millis/1000;
        int sec = tsec%60;
        int min = (tsec-sec)/60;

        if (tsec>300) {
            micView.performClick();
        }

        String timeString = "0:00";
        if (sec<=9) {
            timeString = min + ":0" + sec;
        }
        else {
            timeString = min + ":" + sec;
        }



        timeView.setText(timeString);

    }

    public void startRecording() {
        try {
            recorder.prepare();
            recorder.start();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to record. You may not have granted storage permissions. Please contact the developer.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void stopRecording() {
        try {
            recorder.stop();
        }
        catch (Exception e) {
            Toast.makeText(this, "Ran into an error.", Toast.LENGTH_SHORT).show();
        }
    }
}
