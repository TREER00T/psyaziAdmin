package ir.treeroot.psyaziadmin.Ui.VideoPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.Utils.Link;

public class VideoPlayerActivity extends AppCompatActivity {

    Bundle bundle;
    BetterVideoPlayer video_layout;
    LinearLayout lin_videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        init();
        hideNavigationBar();
        videoPlayer();
    }

    private void videoPlayer() {
        video_layout.setSource(Uri.parse(bundle.getString(Link.Key_i_video_player)));
        lin_videoPlayer.setOnClickListener(v -> {
            lin_videoPlayer.setVisibility(View.GONE);
            video_layout.start();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        video_layout.pause();
    }

    public void init() {
        video_layout = findViewById(R.id.video_layout);
        lin_videoPlayer = findViewById(R.id.linearLayout_video_play);
        bundle = getIntent().getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video_layout.pause();
    }

    private void hideNavigationBar() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}