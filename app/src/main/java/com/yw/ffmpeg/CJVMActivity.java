package com.yw.ffmpeg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg
 * @ClassName: CJVMActivity
 * @Description: c调用java测试
 * @Author: wei.yang
 * @CreateDate: 2021/3/15 11:18
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/15 11:18
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CJVMActivity extends FragmentActivity {
    private Button btnClick;
    private TextView tvContent;
    static {
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjvm);
        initViews();
    }
    private void initViews(){
        btnClick = findViewById(R.id.btnCjvm);
        tvContent = findViewById(R.id.tvCjvmContent);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvContent.setText(getName());
            }
        });
    }

    /**
     * 获取用户名称
     * @return
     */
    public static native String getName();
}
