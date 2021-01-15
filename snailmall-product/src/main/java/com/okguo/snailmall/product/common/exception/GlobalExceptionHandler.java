package com.okguo.snailmall.product.common.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.okguo.common.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/01/15 16:06
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Object MethodArgumentNotValidHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {

        if (exception instanceof RRException || exception instanceof MethodArgumentNotValidException) {
            log.warn("[globalException] requestUri:{}, errorMsg:{}", request.getRequestURI(), exception.getMessage(),exception);
        } else {
            log.error("[globalException] requestUri:{}, errorMsg:{}", request.getRequestURI(), exception.getMessage(), exception);
        }

        ModelAndView mv = new ModelAndView();
        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exs = (MethodArgumentNotValidException) exception;
            BindingResult bindingResult = exs.getBindingResult();
            StringBuilder buffer = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                buffer.append(Objects.requireNonNull(fieldError.getDefaultMessage()).concat(";"));
            }
            mv.addObject("code", 1);
            mv.addObject("data", buffer.toString());
            mv.addObject("success", false);
            mv.setView(new FastJsonJsonView());
        } else if (exception instanceof RRException) {
            RRException bizException = (RRException) exception;
            mv.addObject("code", bizException.getCode());
            mv.addObject("data", bizException.getMessage());
            mv.addObject("success", false);
            mv.setView(new FastJsonJsonView());
        } else if (exception instanceof HttpMessageNotReadableException) {
            mv.addObject("code", 1);
            mv.addObject("data", "传入参数解析失败");
            mv.addObject("success", false);
            mv.setView(new FastJsonJsonView());
        } else {
            mv.addObject("code", 1);
            mv.addObject("data", "系统异常");
            mv.addObject("success", false);
            mv.setView(new FastJsonJsonView());
        }
        return mv;
    }

}
