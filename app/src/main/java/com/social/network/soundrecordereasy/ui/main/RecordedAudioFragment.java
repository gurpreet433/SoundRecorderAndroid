package com.social.network.soundrecordereasy.ui.main;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import com.social.network.soundrecordereasy.R;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordedAudioFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static RecordedAudioFragment newInstance(int index) {
        RecordedAudioFragment fragment = new RecordedAudioFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);
        final TextView filesList = root.findViewById(R.id.filesList);

        loadFiles(filesList);

        return root;
    }

    private void loadFiles(TextView filesList) {

        String fileNames = "";
        String path = getContext().getFilesDir().getAbsolutePath();
        Log.d("Files", "Path: " + path);

        File directory = new File(path);
        File[] files = directory.listFiles();

        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            fileNames += (files[i].getName() + " \n");
            Log.d("Files", "FileName11:" + fileNames);
        }

        filesList.setText(fileNames);
        Log.d("Files", "FileName22:" + fileNames);
    }
}