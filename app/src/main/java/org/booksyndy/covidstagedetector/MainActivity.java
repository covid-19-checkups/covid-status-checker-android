package org.booksyndy.covidstagedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        micView = findViewById(R.id.micView);
        swLL = findViewById(R.id.swLL);
        timeView = findViewById(R.id.recTimeView);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        micView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                startTime = System.currentTimeMillis();

                recording = !recording;

                if (recording) {
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
                else{
                    Toast.makeText(MainActivity.this, "Stopped recording", Toast.LENGTH_SHORT).show();
                    swLL.setVisibility(View.INVISIBLE);
                    handler.removeMessages(0);
                    micView.setImageResource(R.drawable.ic_mic_none_24px);
                    timeView.setText("0:00");
                }
            }
        });




    }

    public void setTimeElapsed(long millis) {

        int tsec = (int) millis/1000;
        int sec = tsec%60;
        int min = (tsec-sec)/60;

        if (tsec>61) {
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
}
