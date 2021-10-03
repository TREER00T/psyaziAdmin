package ir.treeroot.psyaziadmin.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.model.Admin;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.model.Status;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment {

    TextInputEditText title, textOfTitle;
    ImageView AddPost_btn_img, image_loader;
    AppCompatButton btn_addPost;
    Bitmap bitmap;
    String ImageSelect;
    Api request;
    private Uri mCropImageUri;
    String mediaPath;
    String[] mediaColumns = {MediaStore.Video.Media._ID};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_post, container, false);
        init(v);

        AddPost_btn_img.setOnClickListener(v1 -> CheckData());



        title.addTextChangedListener(AddPostWatcher);
        textOfTitle.addTextChangedListener(AddPostWatcher);

        request = APIClient.getApiClient(Link.url).create(Api.class);
        return v;
    }

    public void CheckData() {
        String[] format = {"Photo", "Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.choose_format);
        builder.setItems(format, (dialog, which) -> {
            switch (format[which]) {
                case "Photo":
                    GetGallery();
                    btn_addPost.setOnClickListener(v12 -> Get_Upload());
                    break;
                case "Video":
                    permissionRuntimeGallery();
                    btn_addPost.setOnClickListener(v -> uploadVideo(Objects.requireNonNull(title.getText()).toString(),
                            Objects.requireNonNull(textOfTitle.getText()).toString()));
                    break;
            }
        });
        builder.show();
    }

    private void permissionRuntimeGallery() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();

        } else {


            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);


        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(requireActivity())
                    .setTitle("درخواست مجوز")
                    .setMessage("برای دسترسی به گالری باید مجوز را تایید کنید")
                    .setPositiveButton("موافقم", (dialogInterface, i) -> reqPermission())
                    .setNegativeButton("لغو", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();

        } else {

            reqPermission();

        }
    }

    private void reqPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Link.READ_EXTERNAL_STORAGE_REQUEST_COD);

    }


    public void init(View v) {
        title = v.findViewById(R.id.title);
        textOfTitle = v.findViewById(R.id.textOfTitle);
        AddPost_btn_img = v.findViewById(R.id.AddPost_btn_img);
        image_loader = v.findViewById(R.id.image_loader);
        btn_addPost = v.findViewById(R.id.btn_addPost);
    }

    void Get_Upload() {
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        Call<Admin> callback = request.UploadImage(ImageSelect, Objects.requireNonNull(title.getText()).toString(),
                Objects.requireNonNull(textOfTitle.getText()).toString());
        callback.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {
                if (response.isSuccessful()) {

                    pd.dismiss();
                    new StyleableToast
                            .Builder(requireActivity())
                            .text("با موفقیت ثبت شد")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FF018786"))
                            .font(R.font.isans)
                            .show();
                }


            }

            @Override
            public void onFailure(@NotNull Call<Admin> call, @NotNull Throwable t) {

            }
        });
    }

    public void uploadVideo(String title,String text) {
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        File file = new File(mediaPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody titles = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody textOfTitle = RequestBody.create(MediaType.parse("text/plain"), text);

        Call<Status> call = request.uploadVideo(fileToUpload, filename,titles,textOfTitle);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {
                Status serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        new StyleableToast
                                .Builder(requireActivity())
                                .text(serverResponse.getMessage())
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FF018786"))
                                .font(R.font.isans)
                                .show();
                    } else {
                        new StyleableToast
                                .Builder(requireActivity())
                                .text("خطا در اتصال سرور")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FFFA3C3C"))
                                .font(R.font.isans)
                                .show();
                    }
                }
                pd.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {

            }
        });
    }


    void Compress() {

        int size = (int) (bitmap.getHeight() * (1080.0 / bitmap.getWidth()));
        Bitmap b = Bitmap.createScaledBitmap(bitmap, 1080, size, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ImageSelect = Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    void GetGallery() {
        CropImage.startPickImageActivity(requireContext(), this);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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

                assert result != null;
                Uri uri = result.getUri();


                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    image_loader.setImageBitmap(bitmap);
                    Compress();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = requireActivity().getContentResolver()
                    .query(selectedVideo, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            mediaPath = cursor.getString(columnIndex);

            image_loader.setImageBitmap(getThumbnailPathForLocalFile(getActivity(), selectedVideo));
            cursor.close();

        }


    }


    public Bitmap getThumbnailPathForLocalFile(Activity context, Uri fileUri) {
        long fileId = getFileId(context, fileUri);
        return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
    }

    public long getFileId(Activity context, Uri fileUri) {

        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            return cursor.getInt(columnIndex);
        }
        return 0;
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
                .start(requireContext(), this);
    }


    private final TextWatcher AddPostWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String titleInput = Objects.requireNonNull(title.getText()).toString().trim();
            String textInput = Objects.requireNonNull(textOfTitle.getText()).toString().trim();

            btn_addPost.setEnabled(!titleInput.isEmpty() && !textInput.isEmpty());


        }


        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}