package com.yw.setlibrary.interceptormodel;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: Handler
 * @Description: 处理者接口
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 17:21
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 17:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface Handler {
    /**
     * 处理链条上的请求
     * @param chain
     */
    void handle(Chain chain);
}
