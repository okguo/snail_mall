package com.okguo.snailmall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/05 19:00
 */
@Data
public class UserRegisterVO {
    @NotEmpty(message = "用户名为必填项")
    @Length(min = 6, max = 18, message = "用户名长度必须在6-18位之间")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度必须在8-18位之间")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String mobile;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
