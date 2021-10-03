package ir.treeroot.psyaziadmin.model;

import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;
    public String getMessage() {
        return message;
    }
    public boolean getSuccess() {
        return success;
    }
}
