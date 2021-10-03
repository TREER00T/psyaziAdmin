package ir.treeroot.psyaziadmin.model;

import android.content.Context;

public class PvChat {


    public PvChat(Context context, String id, String username, String pnumber, String aliasname, String image, int status, int imgState) {
        this.context = context;
        this.id = id;
        this.username = username;
        this.pnumber = pnumber;
        this.aliasname = aliasname;
        this.image = image;
        this.status = status;
        this.imgState = imgState;
    }

    Context context;
    String id, username, pnumber, aliasname, image;
    int imgState,status;

    public Context getContext() {
        return context;
    }

    public int getImgState() {
        return imgState;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPnumber() {
        return pnumber;
    }

    public String getAliasname() {
        return aliasname;
    }

    public String getImage() {
        return image;
    }

    public int getStatus() {
        return status;
    }


}
