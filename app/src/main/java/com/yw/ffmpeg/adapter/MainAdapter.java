package com.yw.ffmpeg.adapter;

import android.content.Context;

import com.yw.ffmpeg.R;
import com.yw.ffmpeg.bean.ClassBean;

import org.byteam.superadapter.SuperViewHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.adapter
 * @ClassName: MainAdapter
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:32
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:32
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MainAdapter extends BaseListAdapter<ClassBean> {
    public MainAdapter(Context context, List<ClassBean> mData, int layoutResId, OnListItemClickListener<ClassBean> listener) {
        super(context, mData, layoutResId, listener);
    }

    @Override
    public void onBindData(@NotNull SuperViewHolder holder, int viewType, int layoutPosition, @NotNull ClassBean data) {
        holder.setText(R.id.tvTitle,data.getTitle());
    }

}
