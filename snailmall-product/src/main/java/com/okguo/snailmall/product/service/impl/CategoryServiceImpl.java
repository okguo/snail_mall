package com.okguo.snailmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.product.entity.CategoryBrandRelationEntity;
import com.okguo.snailmall.product.service.CategoryBrandRelationService;
import com.okguo.snailmall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
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
import org.springframework.util.Assert;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(category.getCatId());
        categoryBrandRelationEntity.setCatelogName(category.getName());
        categoryBrandRelationService.update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", category.getCatId()));
    }

    @Override
    public List<CategoryEntity> queryLevelOneCategory() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<Catelog2Vo>> queryCatalogJson() {
        List<CategoryEntity> l1Category = this.queryLevelOneCategory();

        return l1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), l1 -> {
            List<CategoryEntity> l2Category = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l1.getCatId()));
            List<Catelog2Vo> catelog2VoList = new ArrayList<>();
            if (l2Category != null) {
                catelog2VoList = l2Category.stream().map(l2 -> {
                    List<CategoryEntity> l3Category = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
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