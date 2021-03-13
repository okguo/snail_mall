package com.okguo.snailmall.cart.intercept;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.okguo.common.constant.AuthServerConstant;
import com.okguo.common.constant.CartConstant;
import com.okguo.common.vo.MemberVO;
import com.okguo.snailmall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/11 16:01
 */
@Slf4j
public class CartIntercept implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfo = new UserInfoTo();

        HttpSession session = request.getSession();
        log.info("CartIntercept->preHandle" + JSON.toJSONString(session.getAttribute(AuthServerConstant.LOGIN_USER_SESSION)));
        MemberVO memberVO = JSONObject.parseObject(JSON.toJSONString(session.getAttribute(AuthServerConstant.LOGIN_USER_SESSION)), MemberVO.class);
//        MemberVO memberVO = (MemberVO) session.getAttribute();
        if (memberVO != null) {
            userInfo.setUserId(memberVO.getId());
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (name.equals(AuthServerConstant.LOGIN_USER_SESSION)) {
                        userInfo.setUserKey(cookie.getValue());
                        userInfo.setTempUser(true);
                    }
                }
            }
        }

        if (StringUtils.isEmpty(userInfo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfo.setUserKey(uuid);
        }

        threadLocal.set(userInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("snailmall.com");
            cookie.setMaxAge(CartConstant.TEP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
