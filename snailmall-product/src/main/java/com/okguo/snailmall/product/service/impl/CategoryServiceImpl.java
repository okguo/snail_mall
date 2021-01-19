package com.okguo.snailmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.okguo.common.exception.RRException;
import com.okguo.snailmall.product.entity.CategoryBrandRelationEntity;
import com.okguo.snailmall.product.service.CategoryBrandRelationService;
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