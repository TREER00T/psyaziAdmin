package ir.treeroot.psyaziadmin.adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static ir.treeroot.psyaziadmin.Utils.Link.url_pv_null;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Ui.Chats.ChatActivity;
import ir.treeroot.psyaziadmin.model.PvChat;

public class PvChatAdapter extends RecyclerView.Adapter<PvChatAdapter.ViewHolder> {

    private List<PvChat> list;
    Context c;

    public PvChatAdapter(List<PvChat> list,Context context) {
        this.list = list;
        this.c=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pv_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PvChat data = list.get(position);

        if (data.getImgState() == 0) {
            Picasso.get()
                    .load(url_pv_null).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.pv_img);
        }
        if (data.getImgState() == 1) {

            Picasso.get()
                    .load(data.getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.pv_img);
        }

        if (data.getStatus() == 1) {

            holder.online.setVisibility(View.VISIBLE);

        }

        holder.name_pv.setText(data.getAliasname());
        // holder.text_pv.setText(data.ge());

        holder.relativeLayout_pv.setOnClickListener(view -> {
            Intent i = new Intent(c, ChatActivity.class);
            i.putExtra("username", data.getUsername());
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_pv, text_pv;
        CircleImageView pv_img;
        View online;
        RelativeLayout relativeLayout_pv;

        public ViewHolder(View view) {
            super(view);
            name_pv = view.findViewById(R.id.name_pv);
            text_pv = view.findViewById(R.id.text_pv);
            pv_img = view.findViewById(R.id.pv_img);
            online = view.findViewById(R.id.online);
            relativeLayout_pv = view.findViewById(R.id.relativeLayout_pv);
        }

    }
}
