package com.social.network.soundrecordereasy.ui.main;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import com.social.network.soundrecordereasy.MainActivity;
import com.social.network.soundrecordereasy.R;
import com.social.network.soundrecordereasy.RecordFile;
import com.social.network.soundrecordereasy.RecordingRecyclerViewAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordedAudioFragment extends Fragment implements RecordingRecyclerViewAdapter.ItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private RecordingRecyclerViewAdapter adapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        ArrayList<RecordFile> filesNameList= loadFiles();

        // set up the RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.rvRecordings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordingRecyclerViewAdapter(getContext(), filesNameList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<RecordFile> loadFiles() {
        ArrayList<RecordFile> filesNameList= new ArrayList();

        String path = getContext().getFilesDir().getAbsolutePath();
        Log.d("Files", "Path: " + path);

        File directory = new File(path);
        File[] files = directory.listFiles();

        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            // get duration
            File file = new File(path + "/" + files[i].getName());
            Uri uri = Uri.fromFile(file);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getContext(),uri);
            String durationMillisec = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String durationText = DateUtils.formatElapsedTime(Integer.parseInt(durationMillisec) / 1000);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
            String name = files[i].getName();
            String duration = durationText;
            String dateAndTime = sdf.format(file.lastModified());


            filesNameList.add( new RecordFile(name, duration, dateAndTime));
        }
        return  filesNameList;
    }
}