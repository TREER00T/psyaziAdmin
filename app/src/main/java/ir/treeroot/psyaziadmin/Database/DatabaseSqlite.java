package ir.treeroot.psyaziadmin.Database;

import static ir.treeroot.psyaziadmin.Utils.Link.Key_postid;
import static ir.treeroot.psyaziadmin.Utils.Link.TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.model.AddPost;

public class DatabaseSqlite extends SQLiteOpenHelper {
    Context c;

    public DatabaseSqlite(@Nullable Context c) {
        super(c, Link.DB_NAME, null, Link.DB_VERSION);
        this.c = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , postid TEXT, title TEXT , text TEXT ,img TEXT,image TEXT,aliasname TEXT , about TEXT ,format TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void InsertPost(List<AddPost> data) {
        asynctask asynctask = new asynctask(c, data, this);
        asynctask.execute();
    }

    public boolean ExitsPostTable() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME, null);
        return cursor.getCount() > 0;
    }

    public void deleteCourse(AddPost courseName) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, Key_postid + " = ?",  new String[] { String.valueOf(courseName.getRow()) });


    }



    public boolean ExitsPostId(String postid) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE postid = '" + postid + "'", null);
        return cursor.getCount() > 0;
    }

    public List<AddPost> postList() {

        List<AddPost> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY postid DESC" , null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {

            while (!cursor.isAfterLast()) {
                AddPost addPost = new AddPost();
                addPost.setPostid(cursor.getString(1));
                addPost.setTitle(cursor.getString(2));
                addPost.setText(cursor.getString(3));
                addPost.setImg(cursor.getString(4));
                addPost.setImage(cursor.getString(5));
                addPost.setAliasname(cursor.getString(6));
                addPost.setAbout(cursor.getString(7));
                addPost.setFormat(cursor.getString(8));
                cursor.moveToNext();
                list.add(addPost);
            }
        }
        return list;
    }
}
