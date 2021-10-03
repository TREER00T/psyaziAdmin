package ir.treeroot.psyaziadmin.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import ir.treeroot.psyaziadmin.Database.DatabaseSqlite;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.model.AddPost;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Ui.About.About;
import ir.treeroot.psyaziadmin.Ui.Edit.Activity.EditProfile;
import ir.treeroot.psyaziadmin.Utils.Link;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {

    CardView cardView, about;
    Api request;
    ImageView profile_loader;
    Bitmap bitmap;
    boolean check = true;
    String ImagePath = "image";
    FloatingActionButton SelectImageGallery, uploadProfile;
    Toolbar toolbar_name;
    DatabaseSqlite db;
    private Uri mCropImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        init(v);

        cardView.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), EditProfile.class)));
        about.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), About.class)));
        uploadProfile.setOnClickListener(v1 -> ImageUploadToServerFunction());

        db = new DatabaseSqlite(getActivity());

        SelectImageGallery.setOnClickListener(v1 -> CropImage.startPickImageActivity(requireActivity(),this));

        request = APIClient.getApiClient(Link.url).create(Api.class);

        GetData();
        return v;

    }



    public void init(View v) {
        cardView = v.findViewById(R.id.aliasname_cardview);
        about = v.findViewById(R.id.about);
        profile_loader = v.findViewById(R.id.profile_loader);
        SelectImageGallery = v.findViewById(R.id.floatingAction);
        uploadProfile = v.findViewById(R.id.uploadProfile);
        toolbar_name = v.findViewById(R.id.toolbar_name);
    }

    private void GetData() {

        if(db.ExitsPostTable()){

            List<AddPost> addPosts = db.postList();
            String aliasName = addPosts.get(0).getAliasname();
            String image = addPosts.get(0).getImage();
            Picasso.get()
                    .load(image).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(profile_loader);
            toolbar_name.setTitle(aliasName);

        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();
                String aliasName = addPosts.get(0).getAliasname();
                String image = addPosts.get(0).getImage();
                Picasso.get()
                        .load(image).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(profile_loader);
                toolbar_name.setTitle(aliasName);

                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {

            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            Uri imageUri = CropImage.getPickImageResultUri(requireActivity(), data);

            if (CropImage.isReadExternalStoragePermissionsRequired(requireActivity(), imageUri)) {

                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            } else {

                startCropImageActivity(imageUri);

            }

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri uri = result.getUri();

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

                    profile_loader.setImageBitmap(bitmap);
                    uploadProfile.setVisibility(View.VISIBLE);

                } catch (IOException e) {

                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startCropImageActivity(mCropImageUri);

        } else {

            Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();

        }

    }

    private void startCropImageActivity(Uri imageUri) {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(requireActivity(),this);

    }

    public void ImageUploadToServerFunction() {

        ProgressDialog pd = new ProgressDialog(getActivity());

        ByteArrayOutputStream byteArrayOutputStreamObject;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);


        @SuppressWarnings("deprecation")
        @SuppressLint("StaticFieldLeak")
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                pd.show();
                pd.setContentView(R.layout.progressbar);
                pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                pd.dismiss();

                profile_loader.setImageResource(android.R.color.transparent);
                uploadProfile.setVisibility(View.GONE);
                GetData();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<>();

                HashMapParams.put(ImagePath, ConvertImage);

                return imageProcessClass.ImageHttpRequest(Link.profile_img, HashMapParams);
            }

        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();

    }


    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                /// HttpsURLConnection
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, StandardCharsets.UTF_8));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();

        }


        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;

                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));

            }

            return stringBuilderObject.toString();

        }

    }

}
