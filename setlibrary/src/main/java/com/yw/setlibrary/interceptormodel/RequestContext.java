package com.yw.setlibrary.interceptormodel;

/**
 * 参考地址：https://blog.csdn.net/weixin_43901866/article/details/89972507
 *
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: RequestContext
 * @Description: 请求上下文
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 15:57
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 15:57
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RequestContext {
    public Elephant mElephant;
    public Refrigerator mRefrigerator;

    public RequestContext(Elephant elephant, Refrigerator refrigerator) {
        this.mElephant = elephant;
        this.mRefrigerator = refrigerator;
    }

    /**
     * 大象类
     */
    public static class Elephant {


    }

    /**
     * 冰箱类
     */
    public static class Refrigerator {
        private boolean open;

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public void open() {
            open = true;
            System.out.println("打开冰箱");
        }

        public void move() {
            System.out.println("移动大象");
        }

        public void close(){
            System.out.println("关闭冰箱");
        }
    }
}


