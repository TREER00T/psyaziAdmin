package ir.treeroot.psyaziadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.treeroot.psyaziadmin.fragment.AccountFragment;
import ir.treeroot.psyaziadmin.fragment.AddPostFragment;
import ir.treeroot.psyaziadmin.fragment.PostFragment;
import ir.treeroot.psyaziadmin.fragment.PvChatFragment;

import static ir.treeroot.psyaziadmin.Utils.Link.MyPref;
import static ir.treeroot.psyaziadmin.Utils.Link.url_chat;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar btnNav;
    FragmentManager fragmentManager;
    String usernameFrom;
    SharedPreferences shPref;
    Socket socket;

    {
        try {

            socket = IO.socket(url_chat);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (savedInstanceState == null) {

            btnNav.setItemSelected(R.id.post, true);
            fragmentManager = getSupportFragmentManager();

            PostFragment postFragment = new PostFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, postFragment).commit();

        }

        btnNav.setOnItemSelectedListener(i -> {

            Fragment fragment = null;

            switch (i) {

                case R.id.account:
                    fragment = new AccountFragment();
                    break;

                case R.id.post:
                    fragment = new PostFragment();
                    break;

                case R.id.addPost:
                    fragment = new AddPostFragment();
                    break;

                case R.id.talk:
                    fragment = new PvChatFragment();
                    break;
            }

            if (fragment != null) {

                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();

            }

        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        userDisconnected(usernameFrom);


    }

    public void userDisconnected(String username) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("disconnect", postData);

    }


    public void init() {
        btnNav = findViewById(R.id.btn_nav);

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        usernameFrom = shPref.getString("username", "");
    }

}