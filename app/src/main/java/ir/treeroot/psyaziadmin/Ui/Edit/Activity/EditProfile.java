package ir.treeroot.psyaziadmin.Ui.Edit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import ir.treeroot.psyaziadmin.Database.DatabaseSqlite;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.model.AddPost;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.model.Admin;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.Link;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    ImageView checked;
    EditText aliasname;
    Api request;
    DatabaseSqlite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

        checked.setOnClickListener(v -> RequestFromServer());
        findViewById(R.id.back_name).setOnClickListener(v -> finish());
        db = new DatabaseSqlite(this);

        request = APIClient.getApiClient(Link.url).create(Api.class);
        GetName();
    }

    public void init() {
        checked = findViewById(R.id.check_name);
        aliasname = findViewById(R.id.fNameAndLName);
    }

    private void GetName() {
        if(db.ExitsPostTable()){
            List<AddPost> addPosts = db.postList();
            String aliasName = Objects.requireNonNull(addPosts).get(0).getAliasname();
            aliasname.setText(aliasName);
        }
        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();

                String aliasName = Objects.requireNonNull(addPosts).get(0).getAliasname();
                aliasname.setText(aliasName);
                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {
            }

        });
    }

    private void RequestFromServer() {

        String aliasName = aliasname.getText().toString();

        if (aliasName.isEmpty()) {
            aliasname.setError("لطفا فیلد خالی را پر کنید");
        } else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.show();
            pd.setContentView(R.layout.progressbar);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Call<Admin> call = request.aliasName(aliasName);
            call.enqueue(new Callback<Admin>() {

                @Override
                public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {

                    assert response.body() != null;
                    if (response.body().getResponse().equals("SUCCESS")) {
                        finish();
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<Admin> call, @NotNull Throwable t) {
                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("خطا در اتصال سرور")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FFFA3C3C"))
                            .font(R.font.isans)
                            .show();
                }

            });

        }

    }

}