package com.yw.setlibrary.interceptormodel;

/**
 * 使多个对象都有机会处理请求，从而避免请求的发送者与接受者的耦合关系，将对象连成一条链，沿着这条链依次传递请求，直到有对象处理他位置
 *
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: Chain
 * @Description: 拦截器模式链条-----拆分大象放冰箱的步骤：1.打开冰箱 2.把大象放到冰箱中 3.观赏冰箱
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 15:52
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 15:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface Chain {
    /**
     * 请求上下文
     * @return
     */
    RequestContext getContext();

    /**
     * 开始或继续执行请求
     * @param context
     */
    void proceed(RequestContext context);

    /**
     * 中断执行
     */
    void abort();
}
