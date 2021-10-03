package ir.treeroot.psyaziadmin.Ui.Chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.adapter.ChatBoxAdapter;
import ir.treeroot.psyaziadmin.model.Message;
import ir.treeroot.psyaziadmin.model.users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyaziadmin.Utils.Link.MyPref;
import static ir.treeroot.psyaziadmin.Utils.Link.url_chat;


public class ChatActivity extends AppCompatActivity {

    String usernameFrom, usernameTo, usernameToClick;
    ImageView btn, attach_file, bc_chat;
    EditText editText;
    Handler handler;
    Api request;
    Thread thread;
    TextView aliasnameUsersChat, is_typing;
    CircleImageView users_img_chat;
    RecyclerView rec_chat;
    List<Message> MessageList = new ArrayList<>();
    ChatBoxAdapter adapter;
    SharedPreferences shPref;
    Bundle bundle;
    Socket socket;

    {
        try {
            socket = IO.socket(url_chat);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        bundle = getIntent().getExtras();
        usernameToClick = bundle.getString("username");
        usernameTo = bundle.getString("usernameTo");
        findViewById(R.id.back_chat).setOnClickListener(v -> finish());
        request = APIClient.getApiClient(Link.url).create(Api.class);
        GetProfileAdmin(usernameTo);
        GetProfileAdmin(usernameToClick);
        editText.addTextChangedListener(ChatEditTextWatcher);
        editText.addTextChangedListener(IsTyping);

        Glide.with(this)
                .load("https://treeroot.ir/data/wallpaper/1.png").into(bc_chat);

        //noinspection deprecation
        handler = new Handler();
        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        usernameFrom = shPref.getString("username", "");
        socket.emit("nickname", usernameFrom);


        btn.setOnClickListener(v1 -> {

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeZone = new SimpleDateFormat("HH:mm");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat groupBy = new SimpleDateFormat("dd MMMM yyyy");
            String dataTime = timeZone.format(calendar.getTime());
            String groupByTime = groupBy.format(calendar.getTime());
            String message = editText.getText().toString();
            sendMessage(message, dataTime, groupByTime);
            editText.setText("");

        });

        socket.connect();
        socket.on("message", handlerInComingMessage);
        socket.on("SelectData", handlerInComingMessageSelect);
        socket.on("isTyping", HandlerIsTyping);
        socket.on("StopTyping", HandlerStopTyping);

        sendUser(usernameTo);
        sendUser(usernameToClick);

    }


    public void init() {

        btn = findViewById(R.id.send_msg);
        editText = findViewById(R.id.edittext_msg);
        rec_chat = findViewById(R.id.rec_chat);
        bc_chat = findViewById(R.id.bc_chat);
        attach_file = findViewById(R.id.attach_file);
        aliasnameUsersChat = findViewById(R.id.aliasname_users_chat);
        users_img_chat = findViewById(R.id.img_users_chat);
        is_typing = findViewById(R.id.is_typing);

    }

    public Emitter.Listener handlerInComingMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message, usernameId, time_zone, groupByTime;

                try {

                    message = jsonObject.getString("message");
                    time_zone = jsonObject.getString("timezone");
                    usernameId = jsonObject.getString("from");
                    groupByTime = jsonObject.getString("groupByTime");

                    String format;
                    if (usernameId.equals(usernameFrom)) {

                        format = "green";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    } else {

                        if (usernameId.equals(usernameTo)) {

                            format = "white";

                            Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                            MessageList.add(m);

                        }

                        if (usernameId.equals(usernameToClick)) {

                            format = "white";

                            Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                            MessageList.add(m);

                        }

                    }

                    adapter = new ChatBoxAdapter(MessageList);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    if (MessageList.size() == 0) {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size()), 0);

                    } else {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size() - 1), 0);

                    }

                    rec_chat.setAdapter(adapter);
                    rec_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };


    public Emitter.Listener HandlerIsTyping = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message;

                try {

                    message = jsonObject.getString("message");
                    is_typing.setText(message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };


    public Emitter.Listener HandlerStopTyping = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message;

                try {

                    message = jsonObject.getString("message");
                    is_typing.setText(message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };

    public Emitter.Listener handlerInComingMessageSelect = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String message, usernameId, time_zone, groupByTime;

                try {

                    message = jsonObject.getString("message");
                    time_zone = jsonObject.getString("timezone");
                    usernameId = jsonObject.getString("from");
                    groupByTime = jsonObject.getString("groupByTime");
                    String format;

                    if (usernameId.equals(usernameFrom)) {

                        format = "green";

                        Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                        MessageList.add(m);

                    } else {

                        if (usernameId.equals(usernameTo)) {

                            format = "white";

                            Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                            MessageList.add(m);

                        }

                        if (usernameId.equals(usernameToClick)) {

                            format = "white";

                            Message m = new Message(ChatActivity.this, message, format, time_zone, groupByTime);

                            MessageList.add(m);

                        }
                    }

                    adapter = new ChatBoxAdapter(MessageList);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    if (MessageList.size() == 0) {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size()), 0);

                    } else {

                        rec_chat.postDelayed(() -> rec_chat.scrollToPosition(MessageList.size() - 1), 0);

                    }

                    rec_chat.setAdapter(adapter);
                    rec_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };

    private void sendMessage(String message, String TimeZone, String groupByTime) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("from", usernameFrom);
            postData.put("to", usernameTo);
            postData.put("to", usernameToClick);
            postData.put("message", message);
            postData.put("timezone", TimeZone);
            postData.put("groupByTime", groupByTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("message", postData);

    }

    private void sendUser(String usernameToId) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("from", usernameFrom);
            postData.put("to", usernameToId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("GetData", postData);

    }


    private final TextWatcher ChatEditTextWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!editText.getText().toString().isEmpty() && !(editText.length() < 1)) {

                btn.setVisibility(View.VISIBLE);
                attach_file.setVisibility(View.GONE);

            } else {

                btn.setVisibility(View.GONE);
                attach_file.setVisibility(View.VISIBLE);

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };


    private final TextWatcher IsTyping = new TextWatcher() {

        JSONObject after;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            JSONObject postData = new JSONObject();

            try {

                postData.put("to", usernameTo);
                postData.put("to", usernameToClick);
                postData.put("message", "در حال نوشتن ...");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("isTyping", postData);

        }


        @Override
        public void afterTextChanged(Editable s) {

            after = new JSONObject();

            try {

                after.put("to", usernameTo);
                after.put("to", usernameToClick);
                after.put("message", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            thread = new Thread(() -> {

                try {

                    Thread.sleep(1500);
                    handler.post(() -> socket.emit("StopTyping", after));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            thread.start();

        }

    };


    private void GetProfileAdmin(String To) {

        request.ProfileUsers(To).enqueue(new Callback<List<users>>() {

            @Override
            public void onResponse(@NotNull Call<List<users>> call, @NotNull Response<List<users>> response) {

                List<users> user = response.body();
                String aliasname_chat = user.get(0).getAliasname();

                Picasso.get()
                        .load(user.get(0).getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(users_img_chat);

                aliasnameUsersChat.setText(aliasname_chat);

            }

            @Override
            public void onFailure(@NotNull Call<List<users>> call, @NotNull Throwable t) {

            }

        });

    }

}