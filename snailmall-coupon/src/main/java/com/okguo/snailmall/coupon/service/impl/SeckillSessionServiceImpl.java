package com.okguo.snailmall.coupon.service.impl;

import com.okguo.snailmall.coupon.entity.SeckillSkuRelationEntity;
import com.okguo.snailmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.coupon.dao.SeckillSessionDao;
import com.okguo.snailmall.coupon.entity.SeckillSessionEntity;
import com.okguo.snailmall.coupon.service.SeckillSessionService;
import org.springframework.util.Assert;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService relationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> latest3DaySession() {
        List<SeckillSessionEntity> sessionEntities = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
        if (sessionEntities != null && sessionEntities.size() > 0) {
            return sessionEntities.stream().peek(session -> session.setSkuRelationEntities(relationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId())))).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private String startTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String endTime() {
        return LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}