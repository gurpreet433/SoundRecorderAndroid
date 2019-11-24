package com.social.network.soundrecordereasy;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RecordingUtility {

    private MediaRecorder recorder = null;
    private String fileName = null;
    private Context context;
    Boolean isPlaying;
    Boolean isFileLoaded;
    MediaPlayer mp;
    SeekBar seekBar;
    int lengthPlayed;
    TextView timeLabel;
    ImageButton button;
    boolean isProgressChanged;
    private int storedProgress;

    public RecordingUtility(Context context)
    {
        this.context = context;
        isPlaying = false;
        isFileLoaded = false;
        mp = new MediaPlayer();
        lengthPlayed = 0;
        isProgressChanged = false;
    }

    public void startRecording()
    {

        fileName = context.getFilesDir().getAbsolutePath();
        UUID uuid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        fileName += "/" + "Recording_" + uuid.randomUUID().toString().substring(0, 8) + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        }catch (IOException ex)
        {
            Log.i("exception", "ioexception");
        }
        recorder.start();
    }

    // returns the location of the stored file
    public String StopRecording()
    {
        recorder.stop();
        recorder.release();
        recorder = null;
        return fileName;
    }



    public void play(final RecordFile file, final ImageButton btn, final SeekBar seekBar, final TextView timeLabel)
    {
        this.seekBar = seekBar;
        this.timeLabel = timeLabel;
        this.button = btn;
        if (!isPlaying) {
            btn.setImageResource(R.drawable.pause);
            final String path = context.getFilesDir().getAbsolutePath();
            isPlaying = true;
            try {
                if(!isFileLoaded) {
                    mp.setDataSource(path + File.separator + file.recordName);
                    mp.prepare();
                    isFileLoaded = true;
                    mp.start();


                    Log.i("seekbar", "max duration "+ file.getDurationInMilisec());
                    seekBar.setMax(file.getDurationInMilisec());
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                        @Override
                        public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {

                            if (mp != null && fromUser)
                            {

                                Log.i("seekbar", "progress onprogresschanged "+ progress);
                                mp.seekTo(progress);
                                isProgressChanged = true;
                                storedProgress = progress;
                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });


                }
                else
                {
                    mp.reset();
                    mp.setDataSource(path + File.separator + file.recordName);
                    mp.prepare();
                    mp.start();
                    if(isProgressChanged == true) {
                        isProgressChanged = false;
                        mp.seekTo(storedProgress);
                    }
                    else
                        mp.seekTo(lengthPlayed);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mp != null && mp.getCurrentPosition() <= seekBar.getMax()){

                        try {
                            Message m = new Message();
                            m.what = mp.getCurrentPosition();
                            handler.sendMessage(m);
                            Thread.sleep(1000);
                        }catch (InterruptedException e)
                        {

                        }

                        if(mp != null && (mp.getCurrentPosition() == seekBar.getMax()))
                        {
                            isPlaying = false;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    btn.setImageResource(R.drawable.play);
                                }
                            });
                        }
                    }
                }
            }).start();


        } else {
            btn.setImageResource(R.drawable.play);
            mp.pause();
            lengthPlayed=mp.getCurrentPosition();
            isPlaying = false;
        }


    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            int currPosition = msg.what;
            seekBar.setProgress(currPosition);

           Log.i("seekbar", "setting posing inside handler "+ currPosition);


            String playedTime = createTimeLabel(currPosition);
            timeLabel.setText(playedTime);

        }
    };


    public String createTimeLabel(int time)
    {
        String timeLable = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLable = min + ":";
        if (sec < 10) timeLable += "0";
        timeLable += sec;

        return timeLable;
    }

    public void stop()
    {

        isPlaying = false;
        if (button != null)
            button.setImageResource(R.drawable.play);
        if (mp != null){
            mp.pause();
            lengthPlayed = mp.getCurrentPosition();
        }
    }

}
