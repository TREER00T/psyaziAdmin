package ir.treeroot.psyaziadmin.model;

import com.google.gson.annotations.SerializedName;

public class Admin {

    //Request Upload Image From Server
    @SerializedName("status")
    String status;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    Source source;



    public String getStatus() {
        return status;
    }

    private String id, username, password, response;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
