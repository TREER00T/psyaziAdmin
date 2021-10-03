package ir.treeroot.psyaziadmin.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.model.Admin;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.MainActivity;
import ir.treeroot.psyaziadmin.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyaziadmin.Utils.Link.MyPref;

public class admin_login extends AppCompatActivity {
    AppCompatButton login_btn;
    TextInputEditText username_login, password_login;
    Api request;
    SharedPreferences shPref;

    //
    //
    //
    //
    //
    //
    //هش پسورد ادمین
    //
    //
    //
    //
    //
    //
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        init();

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        if (shPref.contains(Link.username)) {
            startActivity(new Intent(admin_login.this, MainActivity.class));
            finish();
        }

        request = APIClient.getApiClient(Link.url).create(Api.class);


        login_btn.setOnClickListener(v -> requestLogin());
        username_login.addTextChangedListener(loginWatcher);
        password_login.addTextChangedListener(loginWatcher);


    }

    public void init() {
        username_login = findViewById(R.id.username_login);
        password_login = findViewById(R.id.password_login);
        login_btn = findViewById(R.id.login_btn);
    }

    private void requestLogin() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String username = Objects.requireNonNull(username_login.getText()).toString();
        String password = Objects.requireNonNull(password_login.getText()).toString();

        Call<Admin> call = request.loginAccount(username, password);
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {

                assert response.body() != null;

                if (response.body().getResponse().equals("Admin_LOGIN")) {

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("خوش آمدید")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FF018786"))
                            .font(R.font.isans)
                            .show();
                    SharedPreferences.Editor sEdit = shPref.edit();
                    sEdit.putString(Link.username, username).apply();
                    startActivity(new Intent(admin_login.this, MainActivity.class));
                    finish();




                } else if (response.body().getResponse().equals("NO_ACCOUNT")) {

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("نام کاربری یا گذرواژه اشتباه است")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FF018786"))
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


            }
        });


    }


    private final TextWatcher loginWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            if (username_login.length() < 6) {
                username_login.setError("حدائقل شش حرف الزامی است");
            }

            if (password_login.length() < 6) {
                password_login.setError("حدائقل شش حرف الزامی است");
            }

        }


        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}