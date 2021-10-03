package ir.treeroot.psyaziadmin.fragment;

import static ir.treeroot.psyaziadmin.Utils.Link.MyPref;
import static ir.treeroot.psyaziadmin.Utils.Link.url_chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Ui.Chats.CheckUserActivity;
import ir.treeroot.psyaziadmin.adapter.PvChatAdapter;
import ir.treeroot.psyaziadmin.model.PvChat;


public class PvChatFragment extends Fragment {

    Socket socket;
    String usernameFrom;
    SharedPreferences shPref;
    Handler handler;
    RecyclerView rec_list_chat;
    PvChatAdapter adapter;
    List<PvChat> pvChats = new ArrayList<>();

    {
        try {
            socket = IO.socket(url_chat);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        init(v);

        shPref = requireActivity().getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        usernameFrom = shPref.getString("username", "");
        handler = new Handler();

        v.findViewById(R.id.floatingAction_chat)
                .setOnClickListener(v1 ->
                        startActivity(new Intent(getActivity(), CheckUserActivity.class)));

        socket.connect();
        sendUserName(usernameFrom);
        socket.on("ChatList", handlerInComingMessage);



        return v;


    }

    private void init(View v) {

        rec_list_chat = v.findViewById(R.id.recyclerview_pv);

    }


    public Emitter.Listener handlerInComingMessage = new Emitter.Listener() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void call(Object... args) {

            handler.post(() -> {

                JSONObject jsonObject = (JSONObject) args[0];
                String id, username, pnumber, aliasname, image;
                int imgState,status;

                try {

                    id = jsonObject.getString("id");
                    username = jsonObject.getString("username");
                    pnumber = jsonObject.getString("pnumber");
                    aliasname = jsonObject.getString("aliasname");
                    image = jsonObject.getString("image");
                    status = jsonObject.getInt("status");
                    imgState = jsonObject.getInt("imgState");

                    PvChat m = new PvChat(requireActivity(), id, username, pnumber, aliasname, image, status, imgState);

                    pvChats.add(m);

                    adapter = new PvChatAdapter(pvChats,requireActivity());
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                    rec_list_chat.setAdapter(adapter);
                    rec_list_chat.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }

    };

    private void sendUserName(String username) {

        JSONObject postData = new JSONObject();

        try {

            postData.put("username", username);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("GetListChat", postData);
    }




}