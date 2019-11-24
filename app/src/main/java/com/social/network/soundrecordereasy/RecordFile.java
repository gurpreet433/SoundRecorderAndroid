package com.social.network.soundrecordereasy;

public class RecordFile {
    String recordName;
    String duration;
    String dateAndTime;
    int durationInMilisec;

    public RecordFile(String recordName, String duration, String dateAndTime, int durationInMilisec) {
        this.recordName = recordName;
        this.duration = duration;
        this.dateAndTime = dateAndTime;
        this.durationInMilisec = durationInMilisec;
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

    int getDurationInMilisec()
    {
        return durationInMilisec;
    }
}

