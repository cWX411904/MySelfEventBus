package com.ck.mybus;

import java.lang.reflect.Method;

public class SubscribleMethod {

    //标签
    private String lable;

    private Method method;

    //参数类型,参数可能有多个，那么对应的类型也会有多个
    private Class[] paramterClass;

    public SubscribleMethod(String lable, Method method, Class[] paramterClass) {
        this.lable = lable;
        this.method = method;
        this.paramterClass = paramterClass;
    }

    public String getLable() {
        return lable;
    }

    public Method getMethod() {
        return method;
    }

    public Class[] getParamterClass() {
        return paramterClass;
    }
}
