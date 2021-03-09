package com.okguo.snailmall.member.service.impl;

import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.member.entity.MemberLevelEntity;
import com.okguo.snailmall.member.service.MemberLevelService;
import com.okguo.snailmall.member.vo.UserLoginVO;
import com.okguo.snailmall.member.vo.UserRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.member.dao.MemberDao;
import com.okguo.snailmall.member.entity.MemberEntity;
import com.okguo.snailmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVO param) throws RRException {
        MemberEntity memberEntity = new MemberEntity();

        MemberLevelEntity memberLevelEntity = memberLevelService.queryDefaultMemberLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        this.checkUsernameExist(param.getUsername());
        this.checkMobileExist(param.getMobile());
        memberEntity.setUsername(param.getUsername());
        memberEntity.setMobile(param.getMobile());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(param.getPassword());
        memberEntity.setPassword(encode);

        baseMapper.insert(memberEntity);
    }

    @Override
    public MemberEntity login(UserLoginVO param) {
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", param.getUsername()).or().eq("mobile", param.getUsername()));
        if (memberEntity != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(param.getPassword(), memberEntity.getPassword());
            if (matches) {
                return memberEntity;
            }
        }
        return null;
    }

    private void checkMobileExist(String mobile) throws RRException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", mobile));
        if (count > 0) {
            throw new RRException(BizCodeEnum.MOBILE_EXIST_EXCEPTION.getMsg(), BizCodeEnum.MOBILE_EXIST_EXCEPTION.getCode());
        }

    }

    private void checkUsernameExist(String username) throws RRException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new RRException(BizCodeEnum.USERNAME_EXIST_EXCEPTION.getMsg(), BizCodeEnum.USERNAME_EXIST_EXCEPTION.getCode());
        }
    }

}