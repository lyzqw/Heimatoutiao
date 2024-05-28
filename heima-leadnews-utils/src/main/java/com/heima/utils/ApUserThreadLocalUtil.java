package com.heima.utils;

import com.heima.model.user.pojos.ApUser;

public class ApUserThreadLocalUtil {

    private static ThreadLocal<ApUser> threadLocal = new ThreadLocal<>();

    public static void setUser(ApUser wmUser) {
        threadLocal.set(wmUser);
    }

    public static ApUser getUser(){
        return threadLocal.get();
    }

    public static void clear(){
        threadLocal.remove();
    }
}
