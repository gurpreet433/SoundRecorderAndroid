package com.social.network.soundrecordereasy.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.social.network.soundrecordereasy.BuildConfig;
import com.social.network.soundrecordereasy.MainActivity;
import com.social.network.soundrecordereasy.R;
import com.social.network.soundrecordereasy.RecordFile;
import com.social.network.soundrecordereasy.RecordingRecyclerViewAdapter;
import com.social.network.soundrecordereasy.RecordingUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordedAudioFragment extends Fragment implements RecordingRecyclerViewAdapter.ItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Context mContext;
    private RecyclerView recyclerView;

    private PageViewModel pageViewModel;
    private RecordingRecyclerViewAdapter adapter;
    ArrayList<RecordFile> filesNameListDataSet;
    RecordingUtility utility;


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
        utility =new RecordingUtility(getContext());
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        filesNameListDataSet = new ArrayList<RecordFile>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        filesNameListDataSet = loadFiles();

        // set up the RecyclerView
        recyclerView = root.findViewById(R.id.rvRecordings);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecordingRecyclerViewAdapter(getContext(), filesNameListDataSet);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        // Toast.makeText(getContext(), "You clicked " +adapter.getItem(position) + " on row number "
        //  position, Toast.LENGTH_SHORT).show();
        createDialog(adapter.getItem(position));
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.i("longClick", "here");
        createLongDialog(adapter.getItem(position));
    }

    private boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    private void createLongDialog(final RecordFile file)
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.onpressed_layout);


        Button cancleButton = dialog.findViewById(R.id.cancle_button_dialog);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        TextView shareFile = dialog.findViewById(R.id.share_file);
        shareFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            try {
                File tmpFile = new File(getContext().getCacheDir() + "/" + file.getRecordName() + "file.aac");

                File file1 = new File(getContext().getFilesDir().getAbsolutePath() +
                        "/" + file.getRecordName());
                FileInputStream in = new FileInputStream(file1);

                FileOutputStream out = new FileOutputStream(tmpFile, false);
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                } finally {
                    in.close();
                    out.close();

                }

                Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, tmpFile.getAbsoluteFile());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Recorded audio file"));

            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            }
        });

        TextView renameFile = dialog.findViewById(R.id.rename_file);
        renameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("here", "rename");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.theme_color_dialog_box);
                final EditText text = new EditText(getContext());

                text.setText(file.getRecordName().replaceAll(".aac$",""));

                builder.setTitle("Rename file").setView(text);
                builder.setPositiveButton("rename", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        final String newName = text.getText().toString();

                        File currentFile = new File(getContext().getFilesDir().getAbsolutePath() +
                                "/" + file.getRecordName());
                        File newFile = new File(getContext().getFilesDir().getAbsolutePath() +
                                "/" + newName + ".aac");

                        if(rename(currentFile, newFile)){
                            //Success
                            Log.i("success", "success");
                            loadFiles();
                            adapter.notifyDataSetChanged();
                        } else {
                            //Fail
                            Log.i(TAG, "Fail");
                        }

                       dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });



        TextView deleteFile = dialog.findViewById(R.id.delete_file);
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext(), R.style.theme_color_dialog_box)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")


                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface deleteDialog, int which) {
                            Log.i("delete", "delete");

                            File fdelete = new File(getContext().getFilesDir().getAbsolutePath() +
                                    "/" + file.getRecordName());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                   Log.i("delete", "deleted");
                                } else {
                                    Log.i("delete", "not deleted");
                                }
                            }
                            deleteDialog.dismiss();
                            dialog.dismiss();
                            loadFiles();
                            adapter.notifyDataSetChanged();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface deleteDialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            }
        });





        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                utility.stop();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       dialog.setCancelable(true);
        dialog.show();
    }

    private void createDialog(final RecordFile file) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.play_recording);
        utility = new RecordingUtility(getContext());

        TextView fileName = dialog.findViewById(R.id.recording_name_player);
        fileName.setText(file.getRecordName());

        final TextView timePlayed = dialog.findViewById(R.id.time_played);
        TextView totalTime = dialog.findViewById(R.id.total_time);
        totalTime.setText(file.getDuration());

        final ImageButton playButton =  dialog.findViewById(R.id.play_button);
        final SeekBar seekbar = dialog.findViewById(R.id.seek_bar);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
                utility.play(file, playButton, seekbar, timePlayed);
                utility.setSeekBar(file);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                utility.stop();
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private ArrayList<RecordFile> loadFiles() {
        filesNameListDataSet.clear();

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

            try {
                mmr.setDataSource(getContext(),uri);

                String durationMillisec = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String durationText = DateUtils.formatElapsedTime(Integer.parseInt(durationMillisec) / 1000);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
                String name = files[i].getName();
                String duration = durationText;
                String dateAndTime = sdf.format(file.lastModified());
                mmr.release();
                filesNameListDataSet.add( new RecordFile(name, duration, dateAndTime, Integer.parseInt(durationMillisec)));

            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }

        }
        return  filesNameListDataSet;
    }


    @Override
    public void onPause() {
        super.onPause();
        utility.stop();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        SharedPreferences pref = null;
        if(mContext != null) {
           pref = mContext.getApplicationContext()
                    .getSharedPreferences("MyPref", 0);
        }

        if (pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            Log.i("he rere", "herere");

            Boolean isDataChanged = pref.getBoolean("recorded", false);

            if (isVisibleToUser && isDataChanged == true) {
                loadFiles();
                adapter.notifyDataSetChanged();

                Log.i("herere", "herere1");

                editor.putBoolean("recorded", false);
                editor.commit();

                if(recyclerView != null)
                {
                    int index;
                    if(filesNameListDataSet != null) {
                        index = filesNameListDataSet.size() - 1;
                        recyclerView.scrollToPosition(index);
                    }
                }
            }
        }
    }
}