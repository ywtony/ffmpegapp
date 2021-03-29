package com.yw.ffmpeg.bean;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.ffmpeg.bean
 * @ClassName: ClassBean
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/3/23 9:25
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/3/23 9:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ClassBean {
    private String title;
    private String className;

    public ClassBean(String title, String className) {
        this.title = title;
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
