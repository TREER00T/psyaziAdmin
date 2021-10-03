package ir.treeroot.psyaziadmin.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.model.AddPost;

public class asynctask extends AsyncTask<Void,Void,Void> {

    @SuppressLint("StaticFieldLeak")
    Context c;
    DatabaseSqlite db;
    List<AddPost> data;
    @SuppressWarnings("deprecation")
    public asynctask(Context c, List<AddPost> data, DatabaseSqlite db){
        this.db=db;
        this.c=c;
        this.data=data;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        SQLiteDatabase database = db.getWritableDatabase();

        for (int i = 0; i < data.size(); i++) {

            AddPost addPost = data.get(i);
            if((!db.ExitsPostId(addPost.getPostid()))){

                ContentValues values = new ContentValues();
                values.put(Link.Key_postid,addPost.getPostid());
                values.put(Link.Key_Title,addPost.getTitle());
                values.put(Link.Key_text,addPost.getText());
                values.put(Link.Key_img,addPost.getImg());
                values.put(Link.Key_image,addPost.getImage());
                values.put(Link.Key_aliasname,addPost.getAliasname());
                values.put(Link.Key_about,addPost.getAbout());
                values.put(Link.Key_format,addPost.getFormat());
                database.insert(Link.TABLE_NAME,null,values);

            }
        }

        return null;
    }
}
