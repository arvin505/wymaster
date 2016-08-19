package com.miqtech.master.client.watcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoyi on 2016/6/29.
 * <p>
 * 抽取观察者通用代码
 * 需要订阅的类型用enum表示
 * 所有对象存在hashmap中，替换之前每一个观察者类型需要单独新建一个java文件
 * 使用的时候注意要在enum @see{ObserverableType}中新增观测类型
 * 切勿在新增观查对象的时使用之前定义的类型，以免造成混淆
 * <p>
 * 使用方法:
 * 1 首先你需要在@see{ObserverableType}添加你需要观察的类型
 * 2 在需要观察的地方通过@see{getInstance()}拿到实例
 * 3 观察者实现抽象观察者接口@see{ISubscribe}
 * 4 调用subscribe方法订阅
 * 5 被观察者调用@see{notifyChange()}
 * 6 在onDestroy()调用unSubscribe()释放资源，这一步必须
 */
public class Observerable {

    private static final String TAG = "Observerable";

    /**
     * 定义观察者对象类型
     */
    public enum ObserverableType {
        APPLYSTATE //活动报名状态
        , CARDUSE //卡券使用
        , COLLECTSTATE //收藏状态
        , CROWDLIST //众筹列表
        , EXCHANGE //金币兑换
        , QRCODEDIALOG //二维码
        , ZIFAMATCH //自发赛
        , REWARD_COMMENT//悬赏令评论模块,0表示评论没滑到顶部，1表示评论滑到顶部，2表示返回卡片页面,3表示悬赏令规则,4表示悬赏令分享5表示删除或者评论后同步卡片评论数字
    }


    private static Map<ObserverableType, List<ISubscribe>> mSubscribes;
    private volatile static Observerable instance;

    private Observerable() {
    }

    public static Observerable getInstance() {
        if (instance == null) {
            synchronized (Observerable.class) {
                if (instance == null) {
                    instance = new Observerable();
                    mSubscribes = new HashMap<>();
                }
            }
        }
        return instance;
    }

    /**
     * 订阅观察者
     *
     * @param key       观察者类型
     * @param subscribe 观察者实现
     */
    public void subscribe(ObserverableType key, ISubscribe subscribe) {
        if (mSubscribes.containsKey(key)) {
            List<ISubscribe> keySubscribes = mSubscribes.get(key);
            if (!keySubscribes.contains(subscribe)) {
                keySubscribes.add(subscribe);
            }
        } else {
            List<ISubscribe> keySubScribes = new ArrayList<>();
            keySubScribes.add(subscribe);
            mSubscribes.put(key, keySubScribes);
        }
    }

    /**
     * 取消订阅 在Activity fragment等 销毁时调用避免内存泄露
     *
     * @param key       观察者类型
     * @param subscribe 观察者实现
     */
    public void unSubscribe(ObserverableType key, ISubscribe subscribe) {
        if (mSubscribes.containsKey(key)) {
            List<ISubscribe> keySubscribes = mSubscribes.get(key);
            if (keySubscribes.contains(subscribe)) {
                keySubscribes.remove(subscribe);
            }
        }

    }

    /**
     * 通知观察者改变
     *
     * @param key  观察者key
     * @param data 有的可能需要传入数据
     */
    public <T> void notifyChange(ObserverableType key, T... data) {
        List<ISubscribe> keySubscribes = mSubscribes.get(key);
        if (keySubscribes == null) return;
        for (ISubscribe subscribe : keySubscribes) {
            subscribe.update(data);
        }
    }

    /**
     * 抽象观察者
     */
    public interface ISubscribe {
        <T> void update(T... data);
    }
}
