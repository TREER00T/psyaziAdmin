package ir.treeroot.psyaziadmin.Ui.Edit.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.model.Admin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostFragment extends Fragment {
    TextInputEditText edit_title, edit_textOfTitle;
    AppCompatButton edit_btn_addPost;
    Api request;
    Bundle bundle;
    String title_bundle, text_bundle, postid_bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_edit_post, container, false);
        init(v);

        bundle=getArguments();

        if (bundle != null){

            title_bundle  = bundle.getString("title");
            text_bundle   = bundle.getString("text");
            postid_bundle = bundle.getString("postid");
            edit_title.setText(title_bundle);
            edit_textOfTitle.setText(text_bundle);

        }

        edit_btn_addPost.setOnClickListener(v12 -> Get_Upload());

        edit_title.addTextChangedListener(AddPostWatcher);
        edit_textOfTitle.addTextChangedListener(AddPostWatcher);

        return v;
    }
    public void init(View v) {
        edit_title = v.findViewById(R.id.edit_title);
        edit_textOfTitle = v.findViewById(R.id.edit_textOfTitle);
        edit_btn_addPost = v.findViewById(R.id.edit_btn_addPost);
    }
    void Get_Upload() {
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        request = APIClient.getApiClient(Link.url).create(Api.class);

        Call<Admin> callback = request.EditPost(postid_bundle,
                Objects.requireNonNull(edit_title.getText()).toString(),
                Objects.requireNonNull(edit_textOfTitle.getText()).toString());

        callback.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {
                if (response.isSuccessful()) {

                    pd.dismiss();
                }


            }

            @Override
            public void onFailure(@NotNull Call<Admin> call, @NotNull Throwable t) {

            }
        });
    }


    private final TextWatcher AddPostWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String titleInput = Objects.requireNonNull(edit_title.getText()).toString().trim();
            String textInput = Objects.requireNonNull(edit_textOfTitle.getText()).toString().trim();

            edit_btn_addPost.setEnabled(!titleInput.isEmpty() && !textInput.isEmpty());


        }


        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}