package com.heima.utils;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {

    private static ThreadLocal<WmUser> threadLocal = new ThreadLocal<>();

    public static void setUser(WmUser wmUser) {
        threadLocal.set(wmUser);
    }

    public static WmUser getUser(){
        return threadLocal.get();
    }

    public static void clear(){
        threadLocal.remove();
    }
}
