package com.yw.setlibrary;

import com.yw.setlibrary.interceptormodel.CloseRefrigeratorHandler;
import com.yw.setlibrary.interceptormodel.Handler;
import com.yw.setlibrary.interceptormodel.MoveElephantHandler;
import com.yw.setlibrary.interceptormodel.OpenRefrigeratorHandler;
import com.yw.setlibrary.interceptormodel.ProcessChain;
import com.yw.setlibrary.interceptormodel.RequestContext;

import java.util.ArrayList;
import java.util.List;


/**
 * 模拟把发现放入冰箱：
 * 1.打开冰箱
 * 2.把大象放进去
 * 3.关闭冰箱
 */
public class MainClass {
    public static void main(String[] args) {
        //构造请求
        RequestContext requestContext = new RequestContext(new RequestContext.Elephant(), new RequestContext.Refrigerator());
        //构造处理步骤
        List<Handler> datas = new ArrayList<>();
        datas.add(new OpenRefrigeratorHandler());
        datas.add(new MoveElephantHandler());
        datas.add(new CloseRefrigeratorHandler());
        //执行任务
        ProcessChain processChain = new ProcessChain(datas, 0, requestContext);
        processChain.proceed(requestContext);
    }
}