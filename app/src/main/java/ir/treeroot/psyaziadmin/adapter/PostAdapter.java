package ir.treeroot.psyaziadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.treeroot.psyaziadmin.Interface.RecyclerViewClickInterface;
import ir.treeroot.psyaziadmin.Ui.VideoPlayer.VideoPlayerActivity;
import ir.treeroot.psyaziadmin.model.AddPost;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Ui.Comment.CommentActivity;
import ir.treeroot.psyaziadmin.Utils.Link;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<AddPost> data;
    private static RecyclerViewClickInterface recyclerViewClickInterface;

    public PostAdapter(Context context, List<AddPost> data, RecyclerViewClickInterface recyclerViewClickInterface) {

        this.context = context;
        this.data = data;
        PostAdapter.recyclerViewClickInterface = recyclerViewClickInterface;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new MyViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        AddPost addPost = data.get(position);

        holder.title.setText(addPost.getTitle());
        holder.aliasname_admin.setText(addPost.getAliasname());

        holder.comment_post.setOnClickListener(v -> {

            Intent i = new Intent(context, CommentActivity.class);
            i.putExtra(Link.Id_post, addPost.getPostid());
            i.putExtra("text", addPost.getText());
            i.putExtra("title", addPost.getTitle());
            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        });

        String format = addPost.getFormat();

        try {
            if (format != null) {
                holder.img.setVisibility(View.VISIBLE);
                holder.linearLayout_play.setVisibility(View.VISIBLE);
                if (format.equals("MP4")) {

                    holder.img.setVisibility(View.GONE);
                    holder.video_post.setVideoURI(Uri.parse(addPost.getImg()));

                    holder.linearLayout_play.setOnClickListener(v -> {

                        Intent i=new Intent(context, VideoPlayerActivity.class);
                        i.putExtra(Link.Key_i_video_player,addPost.getImg());
                        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    });

                }

                holder.video_post.setVisibility(View.VISIBLE);
                holder.linearLayout_play.setVisibility(View.VISIBLE);
                if (format.equals("JPG")) {
                    holder.video_post.setVisibility(View.GONE);
                    holder.linearLayout_play.setVisibility(View.GONE);

                    Glide.with(context)
                            .load(addPost.getImg()).into(holder.img);
                }
            }
        } catch (NullPointerException ignored) {
        }


        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3")).setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setBaseAlpha(1).setDropoff(50).build();

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        Picasso.get()
                .load(addPost.getImage()).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.profile_image);
        holder.more_item.setOnClickListener(v ->
                recyclerViewClickInterface.onItemClick(addPost));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, aliasname_admin;
        ImageView img, comment_post, more_item;
        CircleImageView profile_image;
        VideoView video_post;
        LinearLayout linearLayout_play;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img_post);
            profile_image = itemView.findViewById(R.id.profile_image);
            aliasname_admin = itemView.findViewById(R.id.aliasname_admin);
            comment_post = itemView.findViewById(R.id.comment_post);
            more_item = itemView.findViewById(R.id.more_item);
            video_post = itemView.findViewById(R.id.video_post);
            linearLayout_play = itemView.findViewById(R.id.linearLayout_play);

        }
    }
}
