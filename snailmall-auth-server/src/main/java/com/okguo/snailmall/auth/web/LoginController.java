package com.okguo.snailmall.auth.web;

import com.alibaba.fastjson.TypeReference;
import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.utils.R;
import com.okguo.snailmall.auth.constant.AuthServerConstant;
import com.okguo.snailmall.auth.feign.MemberFeignService;
import com.okguo.snailmall.auth.feign.ThirdPartFeignService;
import com.okguo.snailmall.auth.vo.UserRegisterVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/04 21:21
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("mobile") String mobile) {

        //TODO 接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile);
        if (StringUtils.isNotEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String redisSmsCode = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + mobile, redisSmsCode, 10L, TimeUnit.MINUTES);
        thirdPartFeignService.sendSmsCode(mobile, code);

        return R.ok();
    }

    /**
     * RedirectAttributes attributes 重定向携带数据
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVO vo, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
//            model.addAttribute("errors", errors);
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.snailmall.com/reg.html";
        }

        //校验验证码
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getMobile());
        if (StringUtils.isNotEmpty(redisCode)) {
            String code = redisCode.split("_")[0];
            if (code.equals(vo.getCode())) {
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getMobile());
                R register = memberFeignService.register(vo);
                if (register.getCode() == 0) {
                    return "redirect:/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData(new TypeReference<String>() {
                    }));
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.snailmall.com/reg.html";
                }
            }else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.snailmall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码已失效");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.snailmall.com/reg.html";
        }
    }


}
