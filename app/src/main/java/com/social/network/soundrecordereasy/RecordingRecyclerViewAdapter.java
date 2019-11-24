package com.social.network.soundrecordereasy;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecordingRecyclerViewAdapter extends RecyclerView.Adapter<RecordingRecyclerViewAdapter.ViewHolder> {

    private List<RecordFile> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecordingRecyclerViewAdapter(Context context, List<RecordFile> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_recording_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecordFile recordFile = mData.get(position);
        holder.recordingName.setText(recordFile.getRecordName());
        holder.recordingDuration.setText(recordFile.getDuration());
        holder.recordingDateTime.setText(recordFile.getDateAndTime());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView recordingName;
        TextView recordingDuration;
        TextView recordingDateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            recordingName = itemView.findViewById(R.id.recording_name);
            recordingDuration = itemView.findViewById(R.id.recording_duration);
            recordingDateTime = itemView.findViewById(R.id.recording_date_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null){

                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {

            // Todo : long click
            return false;
        }
    }



    // convenience method for getting data at click position
    public RecordFile getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

