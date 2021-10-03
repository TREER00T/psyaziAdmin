package ir.treeroot.psyaziadmin.Ui.Chats;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;

import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.model.Admin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckUserActivity extends AppCompatActivity {
    TextView user_username;
    Api request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);

        request = APIClient.getApiClient(Link.url).create(Api.class);
        user_username=findViewById(R.id.user_username);
        findViewById(R.id.btn_chat).setOnClickListener(v -> RequestFromServer());

    }



    private void RequestFromServer() {
        String SearchUser = user_username.getText().toString();

        if (SearchUser.isEmpty()) {
            user_username.setError("لطفا فیلد خالی را پر کنید");
        } else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.show();
            pd.setContentView(R.layout.progressbar);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Call<Admin> call = request.SearchUser(SearchUser);
            call.enqueue(new Callback<Admin>() {
                @Override
                public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {

                    assert response.body() != null;

                    if (response.body().getResponse().equals("200")) {
                        Admin admin= response.body();
                        String id=admin.getSource().getId();
                        Intent i = new Intent(CheckUserActivity.this,ChatActivity.class);
                        i.putExtra("userToId",id);
                        i.putExtra("usernameTo",SearchUser);
                        startActivity(i);

                    }else if (response.body().getResponse().equals("404")){

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("همچنین کاربری وجود ندارد")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FFFA3C3C"))
                                .font(R.font.isans)
                                .show();
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
                    Toast.makeText(CheckUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}