package com.yw.setlibrary.interceptormodel;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: MoveElephantHandler
 * @Description: java类作用描述
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 17:31
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 17:31
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MoveElephantHandler  implements Handler{
    @Override
    public void handle(Chain chain) {
        RequestContext context = chain.getContext();
        //处理逻辑
        if(context.mRefrigerator.isOpen()){
            context.mRefrigerator.move();
            //向下移动
            chain.proceed(context);
        }else{
            //发生异常 中断链条
            chain.abort();
        }

    }
}
