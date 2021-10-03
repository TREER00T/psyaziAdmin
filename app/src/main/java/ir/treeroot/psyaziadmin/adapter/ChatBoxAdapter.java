package ir.treeroot.psyaziadmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.model.Message;

public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {

    private List<Message> MessageList;
    private Message m;


    public ChatBoxAdapter(List<Message> MessMessagesList) {

        this.MessageList = MessMessagesList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        m = MessageList.get(position);
        holder.rec_yourSelf.setVisibility(View.VISIBLE);
        if (m.getFormat().equals("green")) {

            holder.rec_yourSelf.setVisibility(View.GONE);
            holder.tv_mySelf.setText(m.getMessage());
            holder.mySelf_time_zone.setText(m.getTimeZone());
            holder.tv_mySelf.setBackgroundResource(R.drawable.bac_chat_green);

            if (position == 0) {

                holder.group_by_time.setVisibility(View.VISIBLE);
                holder.group_by_time.setText(m.getGroupByTime());

            } else {
                if (!MessageList.get(position - 1).getGroupByTime().equals(m.getGroupByTime())) {
                    holder.group_by_time.setVisibility(View.VISIBLE);
                    holder.group_by_time.setText(m.getGroupByTime());
                } else {
                    holder.group_by_time.setVisibility(View.GONE);
                }
            }
        }

        holder.rec_mySelf.setVisibility(View.VISIBLE);
        if (m.getFormat().equals("white")) {

            holder.rec_mySelf.setVisibility(View.GONE);
            holder.tv_yourSelf.setText(m.getMessage());
            holder.yourSelf_time_zone.setText(m.getTimeZone());
            holder.tv_yourSelf.setBackgroundResource(R.drawable.bac_chat_gray);

            if (position == 0) {

                holder.group_by_time.setVisibility(View.VISIBLE);
                holder.group_by_time.setText(m.getGroupByTime());

            } else {
                if (!MessageList.get(position - 1).getGroupByTime().equals(m.getGroupByTime())) {
                    holder.group_by_time.setVisibility(View.VISIBLE);
                    holder.group_by_time.setText(m.getGroupByTime());
                } else {
                    holder.group_by_time.setVisibility(View.GONE);
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_yourSelf, tv_mySelf, yourSelf_time_zone, mySelf_time_zone, group_by_time;
        LinearLayout rec_mySelf, rec_yourSelf;

        public MyViewHolder(View view) {
            super(view);

            tv_yourSelf = itemView.findViewById(R.id.tv_yourSelf);
            tv_mySelf = itemView.findViewById(R.id.tv_mySelf);
            rec_mySelf = itemView.findViewById(R.id.rec_mySelf);
            rec_yourSelf = itemView.findViewById(R.id.rec_yourSelf);
            yourSelf_time_zone = itemView.findViewById(R.id.yourSelf_time_zone);
            mySelf_time_zone = itemView.findViewById(R.id.mySelf_time_zone);
            group_by_time = itemView.findViewById(R.id.group_by_time);
        }

    }
}