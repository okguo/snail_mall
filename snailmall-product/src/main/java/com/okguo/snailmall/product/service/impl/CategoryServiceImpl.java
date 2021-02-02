package com.okguo.snailmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.okguo.common.constant.RedisConstants;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.product.entity.CategoryBrandRelationEntity;
import com.okguo.snailmall.product.service.CategoryBrandRelationService;
import com.okguo.snailmall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;

import com.okguo.snailmall.product.dao.CategoryDao;
import com.okguo.snailmall.product.entity.CategoryEntity;
import com.okguo.snailmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @Description: 查询商品类别树形结构
     * @Author: Guoyongfu
     * @Date: 2021/1/11 20:37
     */
    @Override
    public List<CategoryEntity> queryWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        return categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .peek(e -> e.setChildren(wrapCategory(e, categoryEntities)))
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
    }

    /**
     * @param asList
     * @Description: 批量删除
     * @Author: Guoyongfu
     * @Date: 2021/1/13 12:22
     */
    @CacheEvict(value = "category", allEntries = true)
    @Override
    public void removeByCateIds(List<Long> asList) {
        //TODO 判断是否已被使用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * @param category
     * @Description: 新增类别
     * @Author: Guoyongfu
     * @Date: 2021/1/15 19:37
     */
    @CacheEvict(value = "category", allEntries = true)
    @Override
    public boolean save(CategoryEntity category) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", category.getName());
        List<CategoryEntity> categoryEntities = baseMapper.selectList(wrapper);
        if (categoryEntities.size() > 0) {
            throw new RRException("已存在名称相同的类别");
        }
        return baseMapper.insert(category) == 1;
    }

    @Override
    public Long[] queryCategoryPathById(Long categoryId) {
        List<Long> list = new ArrayList<>();
        List<Long> parentPath = findParentPath(categoryId, list);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[3]);
    }

    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(category.getCatId());
        categoryBrandRelationEntity.setCatelogName(category.getName());
        categoryBrandRelationService.update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", category.getCatId()));
    }

    @Cacheable(value = "category", key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> queryLevelOneCategory() {
        log.info("查询一级分类走了数据库。。。");
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * @Description: 使用spring Cache 缓存
     * @Author: Guoyongfu
     * @Date: 2021/1/31 10:43
     */
    @Override
    public Map<String, List<Catelog2Vo>> queryCatalogJsonV2() {
        log.info("查询了数据库。。。。");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> l1Category = this.getParent_cid(selectList, 0L);
        return getResultMap(selectList, l1Category);
    }

    @Cacheable(value = "category", key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> queryCatalogJsonV3() {

        log.info("查询了数据库。。。。");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> l1Category = this.getParent_cid(selectList, 0L);

        return getResultMap(selectList, l1Category);
    }

    private Map<String, List<Catelog2Vo>> getResultMap(List<CategoryEntity> selectList, List<CategoryEntity> l1Category) {
        return l1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), l1 -> {
            List<CategoryEntity> l2Category = getParent_cid(selectList, l1.getCatId());
            List<Catelog2Vo> catelog2VoList = new ArrayList<>();
            if (l2Category != null) {
                catelog2VoList = l2Category.stream().map(l2 -> {
                    List<CategoryEntity> l3Category = getParent_cid(selectList, l2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3VoList = new ArrayList<>();
                    if (l3Category != null) {
                        catelog3VoList = l3Category.stream().map(l3 -> new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
                    }
                    return new Catelog2Vo(l1.getCatId().toString(), catelog3VoList, l2.getCatId().toString(), l2.getName());
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
    }


    /**
     * 流程 查询缓存中有没有，如果有，直接返回，如果没有从数据库中查出，添加到缓存，并返回
     */
    @Override
    public Map<String, List<Catelog2Vo>> queryCatalogJsonV1() {
        return this.queryCatalogJsonByRedissonLock();
    }

    //使用本地锁  保障数据库查询次数不多
    public Map<String, List<Catelog2Vo>> queryCatalogJsonByLocalLock() {

        synchronized (this) {
            return this.queryCatalogJsonFromDb();
        }
    }

    //使用redisson  保障数据库查询次数不多
    public Map<String, List<Catelog2Vo>> queryCatalogJsonByRedissonLock() {

        RLock lock = redisson.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> map;
        try {
            map = this.queryCatalogJsonFromDb();
        } finally {
            lock.unlock();
        }
        return map;
    }

    public Map<String, List<Catelog2Vo>> queryCatalogJsonFromDb() {

        String categoryJson = redisTemplate.opsForValue().get(RedisConstants.PRODUCT_CATEGORY_KEY);
        if (StringUtils.isNotEmpty(categoryJson)) {
            log.info("查询了redis......");
            return JSONObject.parseObject(categoryJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }

        log.info("查询了数据库。。。。");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> l1Category = this.getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> collect = getResultMap(selectList, l1Category);
        //
        redisTemplate.opsForValue().set(RedisConstants.PRODUCT_CATEGORY_KEY, JSON.toJSONString(collect));
        return collect;
    }


    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long catId) {
        return selectList.stream().filter(item -> item.getParentCid().equals(catId)).collect(Collectors.toList());
    }

    private List<Long> findParentPath(Long categoryId, List<Long> parentPath) {
        parentPath.add(categoryId);
        CategoryEntity categoryEntity = this.getById(categoryId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), parentPath);
        }
        return parentPath;
    }


    private List<CategoryEntity> wrapCategory(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .peek(categoryEntity -> categoryEntity.setChildren(wrapCategory(categoryEntity, all)))
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
    }


}