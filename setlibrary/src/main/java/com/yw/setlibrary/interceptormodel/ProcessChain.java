package com.yw.setlibrary.interceptormodel;

import java.util.List;

/**
 * @ProjectName: AndroidFFMpeg
 * @Package: com.yw.setlibrary.interceptormodel
 * @ClassName: ProcessChain
 * @Description: 链条实现类
 * @Author: wei.yang
 * @CreateDate: 2021/4/30 17:22
 * @UpdateUser: 更新者：wei.yang
 * @UpdateDate: 2021/4/30 17:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ProcessChain implements Chain {
    public List<Handler> mProcess;
    public RequestContext mChainContext;
    public int mIndex = 0;

    public ProcessChain(List<Handler> mProcess, int index, RequestContext mChainContext) {
        this.mProcess = mProcess;
        this.mChainContext = mChainContext;
        this.mIndex = index;
    }

    @Override
    public RequestContext getContext() {
        return mChainContext;
    }


    @Override
    public void proceed(RequestContext context) {
        if (mProcess.size() > mIndex) {//如果当前请求的索引小于事件的总个数
            //获取当前处理者
            Handler handler = mProcess.get(mIndex);
            ProcessChain chain = new ProcessChain(mProcess, mIndex + 1, mChainContext);
            handler.handle(chain);
        }
    }

    @Override
    public void abort() {
        System.out.println("中断请求");
    }
}
