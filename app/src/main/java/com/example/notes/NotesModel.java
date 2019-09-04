package com.example.notes;

import android.widget.TextView;

public class NotesModel {

    private String titleText;
    private String descNotes;
    private String noteDates;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotesModel() {

    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDescNotes() {
        return descNotes;
    }

    public void setDescNotes(String descNotes) {
        this.descNotes = descNotes;
    }

    public String getNoteDates() {
        return noteDates;
    }

    public void setNoteDates(String noteDates) {
        this.noteDates = noteDates;
    }
}
