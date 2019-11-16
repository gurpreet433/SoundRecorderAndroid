package com.social.network.soundrecordereasy;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingUtility {

    private MediaRecorder recorder = null;
    private String fileName = null;
    private Context context;

    public RecordingUtility(Context context)
    {
        this.context = context;
    }

    public void startRecording()
    {
        fileName = context.getFilesDir().getAbsolutePath();
        String randomName = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        fileName += "/" + randomName + ".3gp";

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
}
