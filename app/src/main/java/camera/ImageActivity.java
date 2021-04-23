package camera;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.yw.ffmpeg.BaseActivity;
import com.yw.ffmpeg.R;

import java.io.File;

/**
 * Created By Chengjunsen on 2018/9/5
 */
public class ImageActivity extends BaseActivity {
    private String path = null;
    private ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);
        mImageView = findViewById(R.id.image_view);
        path = getIntent().getStringExtra("path");
        resolvImage();
    }

    @Override
    public void videoPathCallback(String vidoPath) {

    }

    private void resolvImage() {
        if (path.isEmpty()) {
            return;
        }
        mImageView.setImageURI(Uri.fromFile(new File(path)));
    }
}
