package com.okguo.snailmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.okguo.common.utils.PageUtils;
import com.okguo.snailmall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:56:12
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity queryDefaultMemberLevel();

}

