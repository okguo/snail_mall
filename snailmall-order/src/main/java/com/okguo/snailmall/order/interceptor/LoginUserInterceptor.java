package com.okguo.snailmall.order.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.okguo.common.constant.AuthServerConstant;
import com.okguo.common.vo.MemberVO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/18 11:04
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public ThreadLocal<MemberVO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //远程访问订单服务，默认会先走到拦截器，进行登录操作。
        boolean match = new AntPathMatcher().match("/order/order/status/**", request.getRequestURI());
        if (match) {
            return true;
        }

        MemberVO memberVO = JSONObject.parseObject(JSON.toJSONString(request.getSession().getAttribute(AuthServerConstant.LOGIN_USER_SESSION)), MemberVO.class);
        if (memberVO != null) {
            threadLocal.set(memberVO);
            return true;
        }else {
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.snailmall.com/login.html");
            return false;
        }
    }
}
