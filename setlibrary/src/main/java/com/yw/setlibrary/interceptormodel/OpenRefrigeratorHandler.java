package com.yw.setlibrary.interceptormodel;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: HandlerOpenRefrigerator
 * @Description: 打开冰箱
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 17:26
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 17:26
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OpenRefrigeratorHandler implements Handler {
    @Override
    public void handle(Chain chain) {
        RequestContext requestContext = chain.getContext();
        //处理具体的操作
        if (!requestContext.mRefrigerator.isOpen()) {//如果冰箱未打开
            requestContext.mRefrigerator.open();
        }
        //处理完毕继续执行下一个操作
        chain.proceed(requestContext);
    }
}
