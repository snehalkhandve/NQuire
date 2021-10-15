package com.adityagunjal.sdl_project.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ModelDraft implements Serializable {
    public String userID, questionID,draftID;
    public HashMap<String, String> draft;

    public ModelDraft(){}



    public ModelDraft(String userID, String questionID, String draftID, HashMap<String, String> draft) {
       this.userID = userID;
       this.questionID = questionID;
       this.draftID = draftID;
       this.draft = draft;
    }

    public String getDraftID() {
        return draftID;
    }

    public void setDraftID(String draftID) {
        this.draftID = draftID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public HashMap<String, String> getDraft() {
        return draft;
    }

    public void setDraft(HashMap<String, String> draft) {
        this.draft = draft;
    }

}
