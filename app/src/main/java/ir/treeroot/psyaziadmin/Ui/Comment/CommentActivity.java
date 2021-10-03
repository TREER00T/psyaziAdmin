package ir.treeroot.psyaziadmin.Ui.Comment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.treeroot.psyaziadmin.model.Comment;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.adapter.CommentAdapter;

import static ir.treeroot.psyaziadmin.Utils.Link.MyPref;
import static ir.treeroot.psyaziadmin.Utils.Link.URL_SEND_Comment;

public class CommentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CommentAdapter adapter;
    List<Comment> commentList = new ArrayList<>();
    Bundle bundle;
    TextView txt_comment,title_comment;
    SharedPreferences shPref;
    ImageView send_comment;
    static String username, comment;
    int id;
    EditText comment_editText;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();

        requestQueue = Volley.newRequestQueue(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);

        bundle = getIntent().getExtras();
        id = Integer.parseInt(bundle.getString(Link.Id_post));
        txt_comment.setText(bundle.getString("text"));
        title_comment.setText(bundle.getString("title"));
        username = shPref.getString("username", "");

        findViewById(R.id.back_comment).setOnClickListener(v -> finish());
        send_comment.setOnClickListener(v -> SendData());

        getComment();
        comment_editText.addTextChangedListener(commentWatcher);
    }

    public void init() {
        recyclerView = findViewById(R.id.recyclerView_comment);
        comment_editText = findViewById(R.id.comment_editText);
        send_comment = findViewById(R.id.send_comment);
        txt_comment = findViewById(R.id.txt_comment);
        title_comment = findViewById(R.id.title_comment);
    }

    private void SendData() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        comment = comment_editText.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, URL_SEND_Comment, response -> {

            if (response != null) {
                pd.dismiss();
                comment_editText.getText().clear();
            }

        }, error -> {
        }) {

            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                params.put("id_item", String.valueOf(id));
                params.put("comment", comment);
                params.put("username", username);
                return params;

            }
        };

        requestQueue.add(request);

    }


    public void getComment() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Link.URL_GET_Comment + id,
                null, response -> {

            if (response != null) {
                pd.dismiss();

                try {

                    JSONArray jsonArray = response.getJSONArray("comments");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String username = jsonObject.getString("username");
                        String Comment = jsonObject.getString("comment");

                        Comment c = new Comment();

                        c.setUsername(username);
                        c.setComment(Comment);

                        commentList.add(c);
                        adapter = new CommentAdapter(this, commentList);

                        recyclerView.setAdapter(adapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, error -> {
        });

        requestQueue.add(jsonObjectRequest);

    }

    private final TextWatcher commentWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String comment_TextWatcher = comment_editText.getText().toString();

            if (!comment_TextWatcher.matches("")) {

                send_comment.setVisibility(View.VISIBLE);

            } else {

                send_comment.setVisibility(View.GONE);

            }

        }


        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}