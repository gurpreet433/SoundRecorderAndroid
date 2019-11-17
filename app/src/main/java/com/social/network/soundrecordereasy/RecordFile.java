package com.social.network.soundrecordereasy;

public class RecordFile {
    String recordName;
    String duration;
    String dateAndTime;

    public RecordFile(String recordName, String duration, String dateAndTime) {
        this.recordName = recordName;
        this.duration = duration;
        this.dateAndTime = dateAndTime;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}

