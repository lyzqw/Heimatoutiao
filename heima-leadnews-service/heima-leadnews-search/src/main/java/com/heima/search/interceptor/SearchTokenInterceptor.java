package com.heima.search.interceptor;

import com.heima.model.user.pojos.ApUser;
import com.heima.utils.ApUserThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchTokenInterceptor implements HandlerInterceptor {

    //得到header中的信息，存入到当前线程中
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (userId != null && !userId.isEmpty()) {
            ApUser wmUser = new ApUser();
            wmUser.setId(Integer.valueOf(userId));
            ApUserThreadLocalUtil.setUser(wmUser);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ApUserThreadLocalUtil.clear();
    }
}
