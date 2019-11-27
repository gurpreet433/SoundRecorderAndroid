package com.social.network.soundrecordereasy.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.social.network.soundrecordereasy.R;
import com.social.network.soundrecordereasy.RecordingUtility;

public class RecordingFragment extends Fragment   {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Boolean isRecording;
    Chronometer timerWidget;
    RecordingUtility recorder;

    TextView statusText;
    private String mParam1;
    private String mParam2;

    // permission
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 220;

    FloatingActionButton recordButton;

    private OnFragmentInteractionListener mListener;

    public RecordingFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordingFragment.
     */
    public static RecordingFragment newInstance(String param1, String param2) {
        RecordingFragment fragment = new RecordingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions( permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        }
        else
        {
            permissionToRecordAccepted = true;
            recorder = new RecordingUtility(getContext());
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        isRecording = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                recorder = new RecordingUtility(getContext());
                break;
        }
        if (!permissionToRecordAccepted)
        {
            Toast.makeText(getContext(), "Cannot record audio without permission!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        Log.i("here", "here");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_recording, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordButton =  view.findViewById(R.id.record_button);
        timerWidget  =  view.findViewById(R.id.timer_widget);
        statusText   =  view.findViewById(R.id.status_text);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording == null)
                    isRecording = true;

                if(permissionToRecordAccepted)
                    togglePng();
                else
                    Toast.makeText(getContext(),"Please provide permission to app from setting menu",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePng() {
        if (isRecording) {
            recordButton.setImageResource(R.drawable.stop);
            timerWidget.setBase(SystemClock.elapsedRealtime());
            timerWidget.start();
            recorder.startRecording();
            statusText.setText("Recording..");
            isRecording = false;
            Log.i("here", "here");
        } else {
            recordButton.setImageResource(R.drawable.mic_img);
            timerWidget.stop();
            timerWidget.setBase(SystemClock.elapsedRealtime());
            String recordingfile = "";

            Log.i("here", "here1");
            Log.i("here", recorder.toString());
            statusText.setText("Tap the button to start recording");
            recordingfile = recorder.StopRecording();
            Toast.makeText(getContext(), "File Saved: " + recordingfile + "\n", Toast.LENGTH_SHORT).show();
            saveInSharePref();
            isRecording = true;
        }
    }

    private void saveInSharePref() {
        SharedPreferences pref = getActivity().getApplicationContext()
                .getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("recorded", true);
        editor.commit();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
