package ir.treeroot.psyaziadmin.Ui.About;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

public class About extends AppCompatActivity {

    ImageView checked;
    EditText about;
    Api request;
    DatabaseSqlite db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();

        checked.setOnClickListener(v -> RequestFromServer());
        findViewById(R.id.back_about).setOnClickListener(v -> finish());
        db = new DatabaseSqlite(this);

        request = APIClient.getApiClient(Link.url).create(Api.class);
        GetAbout();

    }

    public void init() {
        checked = findViewById(R.id.check);
        about = findViewById(R.id.abouts);
    }

    private void RequestFromServer() {
        String abouts = about.getText().toString();

        if (abouts.isEmpty()) {
            about.setError("لطفا فیلد خالی را پر کنید");
        } else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.show();
            pd.setContentView(R.layout.progressbar);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Call<Admin> call = request.about(abouts);
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


    private void GetAbout() {
        if(db.ExitsPostTable()){
            List<AddPost> addPosts = db.postList();
            String abouts = addPosts.get(0).getAbout();
            about.setText(abouts);
        }
        request.GetData().enqueue(new Callback<List<AddPost>>() {

            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {

                List<AddPost> addPosts = response.body();
                assert addPosts != null;
                String abouts = addPosts.get(0).getAbout();
                about.setText(abouts);
                db.InsertPost(addPosts);

            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {
            }

        });
    }
}