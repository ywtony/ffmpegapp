package com.yw.cameralib.camera;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.cameralib
 * @ClassName: Camera1Manager
 * @Description: Camera摄像头管理类，支持所有的Android机型
 * @Author: wei.yang
 * @CreateDate: 2021/4/13 15:33
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/13 15:33
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Camera1Manager {
    private Camera1Manager() {
    }

    private static Camera1Manager instance;

    public synchronized static Camera1Manager getInstance() {
        if (instance == null) {
            instance = new Camera1Manager();
        }
        return instance;
    }


}
