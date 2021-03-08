package com.okguo.snailmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.exception.RRException;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.member.entity.MemberEntity;
import com.okguo.snailmall.member.vo.UserRegisterVO;

import java.util.Map;

/**
 * 会员
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:56:12
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVO param) throws RRException;
}

