package com.ck.mybus;

public class Subscription {

    //所有注解过的方法
    private SubscribleMethod subscribleMethod;

    //class实例对象
    private Object object;

    public Subscription(SubscribleMethod subscribleMethod, Object object) {
        this.subscribleMethod = subscribleMethod;
        this.object = object;
    }

    public SubscribleMethod getSubscribleMethod() {
        return subscribleMethod;
    }

    public Object getObject() {
        return object;
    }
}
