package com.ck.mybus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyBus
{
    //缓存表，注册时候将class与内部注解过的函数一一对应保存
    private Map<Class, List<SubscribleMethod>> METHOD_CACHE = new HashMap<>();

    //执行表
    private Map<String, List<Subscription>> SUBSCRIBLES = new HashMap<>();

    //注销表
    //获得对应所有标签执行表，根据标签获得集合，判断本次注销是否为同一个对象
    private Map<Class, List<String>> REGISTER = new HashMap<>();

    private static volatile MyBus instance;

    private MyBus() {}

    public static MyBus getInstance() {
        if (null == instance) {
            synchronized (MyBus.class) {
                if (null == instance) {
                    instance = new MyBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     * @param object
     */
    public void register(Object object) {

        //找到对应类中所有被Subscrible注解的函数
        //并将其lable、Method、执行函数的参数类型 缓存起来
        List<SubscribleMethod> subscribles = findSubscrible(object.getClass());

        //为了方便注销
        List<String> labels = REGISTER.get(object.getClass());
        if (null == labels) {
            labels = new ArrayList<>();
            REGISTER.put(object.getClass(), labels);
        }

        for (SubscribleMethod subscribleMethod : subscribles) {
            //拿到标签
            String lable = subscribleMethod.getLable();

            if (!labels.contains(lable)) {
                labels.add(lable);
            }

            //执行表数据
            List<Subscription> subscriptions = SUBSCRIBLES.get(lable);
            //判断执行表数据是否存在
            if (null == subscriptions) {
                subscriptions = new ArrayList<>();
                SUBSCRIBLES.put(lable, subscriptions);
            }
            subscriptions.add(new Subscription(subscribleMethod, object));
        }
    }

    private List<SubscribleMethod> findSubscrible(Class<?> aClass) {
        //先从缓存表里面拿
        List<SubscribleMethod> subscribleMethods = METHOD_CACHE.get(aClass);

        if (null == subscribleMethods) {
            //缓存里面没有,找到类里面的所有被注解过的函数
            subscribleMethods = new ArrayList<>();
            Method[] methods = aClass.getDeclaredMethods();


            for (Method method : methods) {
                Subscrible subscrible = method.getAnnotation(Subscrible.class);
                if (null != subscrible) {
                    String[] values = subscrible.value();
                    //被注解过的函数的参数类型
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (String value : values) {
                        //设置private的权限
                        method.setAccessible(true);
                        subscribleMethods.add(new SubscribleMethod(value, method, parameterTypes));
                    }
                }
            }

            METHOD_CACHE.put(aClass, subscribleMethods);
        }

        return subscribleMethods;
    }


    /**
     * 发送事件给订阅者
     * @param lable
     * @param params
     */
    public void post(String lable, Object... params) {

        List<Subscription> subscriptions = SUBSCRIBLES.get(lable);
        if (null == subscriptions) return;
        for (Subscription subscription : subscriptions) {
            SubscribleMethod subscribleMethod = subscription.getSubscribleMethod();
            //拿到参数类型
            Class[] paramterClass = subscribleMethod.getParamterClass();
            //真实的参数
            Object[] realParams = new Object[paramterClass.length];
            if (null != params) {
                for (int i = 0; i < paramterClass.length; i++) {
                    //传进来的参数类型是method需要的类型
                    if (i < params.length && paramterClass[i].isInstance(params[i])) {
                        realParams[i] = params[i];
                    } else {
                        realParams[i] = null;
                    }
                }
            }
            try {
                subscribleMethod.getMethod().invoke(subscription.getObject(), realParams);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * app完全退出时，清理所有缓存
     */
    public void clear() {
        METHOD_CACHE.clear();
        SUBSCRIBLES.clear();
        REGISTER.clear();
    }

    public void unregister(Object object) {
        //拿到对应对象类型的所有注册的标签
        List<String> labels = REGISTER.get(object.getClass());

        if (null != labels) {
            for (String label : labels) {
                //获得执行表中对应label所有函数
                List<Subscription> subscriptions = SUBSCRIBLES.get(label);
                if (null != subscriptions) {
                    //迭代器循环删除
                    Iterator<Subscription> iterator = subscriptions.iterator();
                    while (iterator.hasNext()) {
                        Subscription subscription = iterator.next();
                        //对象是同一个才删除
                        if (subscription.getObject() == object) {
                            iterator.remove();
                        }
                    }
                }
            }
        }


    }
}
