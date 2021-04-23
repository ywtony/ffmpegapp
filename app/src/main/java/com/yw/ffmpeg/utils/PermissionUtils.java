package com.yw.ffmpeg.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import com.yw.ffmpeg.R;

import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.utils
 * @ClassName: PermissionUtils
 * @Description: 权限工具
 * @Author: wei.yang
 * @CreateDate: 2021/4/13 13:29
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/13 13:29
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PermissionUtils {
    private PermissionUtils() {
    }

    private static PermissionUtils instance;

    public synchronized static PermissionUtils getInstance() {
        if (instance == null) {
            instance = new PermissionUtils();
        }
        return instance;
    }

    public static final int REQUEST_CODE_SETTING = 103;

    /**
     * @param context     上下文
     * @param listener    成功回调
     * @param permissions 单个权限
     */
    @SuppressLint("WrongConstant")
    public void requestPermission(Context context, PermissionSuccessListener listener, String... permissions) {
        try {
            DefaultRationale.PermissionSetting mSetting = new DefaultRationale.PermissionSetting(context);
            AndPermission.with(context)
                    .runtime()
                    .permission(permissions)
                    .rationale(new DefaultRationale()).onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    listener.onSuccess();
                }
            }).onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    listener.onSuccess();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("WrongConstant")
    public void requestPermission(Context context, PermissionSuccessListener listener, PermissionFailedListener failedListener, String... permissions) {
        DefaultRationale.PermissionSetting mSetting = new DefaultRationale.PermissionSetting(context);
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new DefaultRationale()).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                listener.onSuccess();
            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                failedListener.onFailed();
                if (AndPermission.hasAlwaysDeniedPermission(context, data)) {
                    mSetting.showSetting(data);
                }
            }
        }).start();
    }

    public void toPermissionSetting(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            toSystemConfig(context);
        } else {
            try {
                toApplicationInfo(context);
            } catch (Exception e) {
                e.printStackTrace();
                toSystemConfig(context);
            }
        }
    }

    /**
     * 应用信息界面
     *
     * @param context
     */
    private void toApplicationInfo(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    /**
     * 系统设置界面
     */
    private void toSystemConfig(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 成功回掉
     */
    public interface PermissionSuccessListener {
        void onSuccess();
    }

    /**
     * 失败回掉
     */
    public interface PermissionFailedListener {
        void onFailed();
    }

    static class DefaultRationale implements Rationale<List<String>> {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, data);
            String message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames));
            new AlertDialog.Builder(context)
                    .setTitle("权限")
                    .setMessage(message)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    executor.cancel();
                }
            }).show();
        }

        /**
         * @description：权限设置dialog
         * @author： 张慧彪
         * @time 18-7-26 下午2:14
         */
        static class PermissionSetting {
            private Context context;

            public PermissionSetting(Context context) {
                this.context = context;
            }

            @SuppressLint("StringFormatInvalid")
            public void showSetting(List<String> permissions) {
                List<String> permissionNames = Permission.transformText(context, permissions);
                String message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("温馨提示")
                        .setMessage(message).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }

            /**
             * Set permissions.
             */
            private void setPermission(Context context) {
                AndPermission.with(context)
                        .runtime()
                        .setting()
                        .start(REQUEST_CODE_SETTING);
            }

        }

    }
}
