package com.yw.setlibrary.interceptormodel;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: CloseRefrigeratorHandler
 * @Description: 关闭冰箱
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 17:35
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 17:35
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CloseRefrigeratorHandler implements Handler {
    @Override
    public void handle(Chain chain) {
        RequestContext context = chain.getContext();
        if(context.mRefrigerator.isOpen()){
            context.mRefrigerator.close();
        }

    }
}
