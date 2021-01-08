package com.okguo.snailmall.member.dao;

import com.okguo.snailmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author okGuo
 * @email guoyongfu.1@hotmail.com
 * @date 2021-01-08 13:56:12
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
