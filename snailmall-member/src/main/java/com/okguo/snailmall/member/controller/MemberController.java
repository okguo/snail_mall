package com.okguo.snailmall.member.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.member.feign.CouponFeignService;
import com.okguo.snailmall.member.vo.UserLoginVO;
import com.okguo.snailmall.member.vo.UserRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.okguo.snailmall.member.entity.MemberEntity;
import com.okguo.snailmall.member.service.MemberService;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.R;


/**
 * 会员
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:56:12
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    /**
     * 列表
     */
    @RequestMapping("/listMemberCoupons")
//    @RequiresPermissions("member:member:list")
    public R listMemberCoupons() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        memberEntity.setUsername("张三哈哈哈");

        R couponList = couponFeignService.getCouponList();

        return Objects.requireNonNull(R.ok().put("member", memberEntity)).put("coupons", couponList.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVO param) {
        try {
            memberService.register(param);
        } catch (RRException e) {
            if (e.getCode() == BizCodeEnum.USERNAME_EXIST_EXCEPTION.getCode()) {
                return R.error(BizCodeEnum.USERNAME_EXIST_EXCEPTION.getCode(), BizCodeEnum.USERNAME_EXIST_EXCEPTION.getMsg());
            } else if (e.getCode() == BizCodeEnum.MOBILE_EXIST_EXCEPTION.getCode()) {
                return R.error(BizCodeEnum.MOBILE_EXIST_EXCEPTION.getCode(), BizCodeEnum.MOBILE_EXIST_EXCEPTION.getMsg());
            }
        }
        return R.ok();
    }


    @PostMapping("/login")
    public R login(@RequestBody UserLoginVO param) {
        MemberEntity memberEntity = memberService.login(param);
        if (memberEntity == null) {
            return R.error(BizCodeEnum.USER_ACCOUNT_ERROR_EXCEPTION.getCode(), BizCodeEnum.USER_ACCOUNT_ERROR_EXCEPTION.getMsg());
        }
        return R.ok(memberEntity);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
