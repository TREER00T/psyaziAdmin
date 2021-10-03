package ir.treeroot.psyaziadmin.Interface;


import com.google.gson.annotations.SerializedName;


import java.util.List;

import ir.treeroot.psyaziadmin.model.AddPost;
import ir.treeroot.psyaziadmin.model.Admin;
import ir.treeroot.psyaziadmin.model.Status;
import ir.treeroot.psyaziadmin.model.users;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.FormUrlEncoded;

public interface Api {


    //Request Login From Server
    @GET("login.php")
    Call<Admin> loginAccount(
            @Query("username") String username,
            @Query("password") String password);

    //Request Upload Image From Server
    @FormUrlEncoded
    @POST("up.php")
    Call<Admin> UploadImage(
            @Field("img") String img,
            @Field("title") String title,
            @Field("text") String text);

    //Request Get Data From Server
    @GET("getDataPost.php")
    Call<List<AddPost>> GetData();

    //Request Set about
    @POST("About.php")
    Call<Admin> about(
            @Query("about") String about);

    //Request Set Change AliasName
    @POST("AliASName.php")
    Call<Admin> aliasName(
            @Query("aliasname") String aliasname);


    //Request Delete Post
    @SerializedName("postid")
    @POST("delete.php")
    Call<Admin> DeletePost(
            @Query("postid") String postid);


    //Request Edit Post
    @POST("update.php")
    Call<Admin> EditPost(
            @Query("postid") String postid,
            @Query("title") String title,
            @Query("text") String text);



    //Request Search user
    @POST("send_user_username.php")
    Call<Admin> SearchUser(
            @Query("username") String username);


    //Request Chat Profile users
    @GET("getDataUsersChat.php")
    Call<List<users>> ProfileUsers(
            @Query("username") String username);

    //Request Update Row In Local
    @GET("updateData.php")
    Call<List<AddPost>> UpdateData();


    //Request Upload Video Post Details
    @Multipart
    @POST("video_upload_to_server.php")
    Call<Status> uploadVideo(
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name,
            @Part("title") RequestBody title,
            @Part("text") RequestBody text);


}












