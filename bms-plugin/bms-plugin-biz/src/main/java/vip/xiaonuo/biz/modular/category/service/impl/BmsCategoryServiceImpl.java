/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.category.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.category.entity.BmsCategory;
import vip.xiaonuo.biz.modular.category.enums.BmsCategoryStatusEnum;
import vip.xiaonuo.biz.modular.category.mapper.BmsCategoryMapper;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryAddParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryEditParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryIdParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryPageParam;
import vip.xiaonuo.biz.modular.category.service.BmsCategoryService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.consts.CacheConstant;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.CommonSqlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BmsCategoryServiceImpl extends ServiceImpl<BmsCategoryMapper, BmsCategory> implements BmsCategoryService {

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public Page<BmsCategory> page(BmsCategoryPageParam bmsCategoryPageParam) {
        QueryWrapper<BmsCategory> queryWrapper = new QueryWrapper<BmsCategory>().checkSqlInjection();
        queryWrapper.select("ID", "PARENT_ID", "NAME", "CODE", "DESCRIPTION", "ICON", "STATUS", "SORT_CODE", "CREATE_TIME", "UPDATE_TIME");
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q
                .like(BmsCategory::getName, bmsCategoryPageParam.getSearchKey())
                .or()
                .like(BmsCategory::getCode, bmsCategoryPageParam.getSearchKey()));
        }
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getName())) {
            queryWrapper.lambda().like(BmsCategory::getName, bmsCategoryPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getCode())) {
            queryWrapper.lambda().like(BmsCategory::getCode, bmsCategoryPageParam.getCode());
        }
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsCategory::getStatus, bmsCategoryPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getParentId())) {
            queryWrapper.lambda().eq(BmsCategory::getParentId, bmsCategoryPageParam.getParentId());
        }
        if(ObjectUtil.isAllNotEmpty(bmsCategoryPageParam.getSortField(), bmsCategoryPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bmsCategoryPageParam.getSortOrder());
            CommonSqlUtil.validateSortField(bmsCategoryPageParam.getSortField());
            queryWrapper.orderBy(true, bmsCategoryPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsCategoryPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BmsCategory::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsCategory> list(BmsCategoryPageParam bmsCategoryPageParam) {
        String cacheKey = CacheConstant.BMS_CATEGORY_LIST_CACHE_KEY;
        Object cached = commonCacheOperator.get(cacheKey);
        if (ObjectUtil.isNotEmpty(cached)) {
            return JSON.parseArray(cached.toString(), BmsCategory.class);
        }
        QueryWrapper<BmsCategory> queryWrapper = new QueryWrapper<BmsCategory>().checkSqlInjection();
        queryWrapper.select("ID", "PARENT_ID", "NAME", "CODE", "STATUS", "SORT_CODE");
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsCategory::getStatus, bmsCategoryPageParam.getStatus());
        } else {
            queryWrapper.lambda().eq(BmsCategory::getStatus, BmsCategoryStatusEnum.ENABLE.getValue());
        }
        queryWrapper.lambda().orderByAsc(BmsCategory::getSortCode);
        queryWrapper.last("LIMIT 1000");
        List<BmsCategory> result = this.list(queryWrapper);
        commonCacheOperator.put(cacheKey, JSON.toJSONString(result), CacheConstant.CACHE_EXPIRE_30_MINUTES);
        return result;
    }

    @Override
    public List<BmsCategory> tree(BmsCategoryPageParam bmsCategoryPageParam) {
        String cacheKey = CacheConstant.BMS_CATEGORY_TREE_CACHE_KEY;
        Object cached = commonCacheOperator.get(cacheKey);
        if (ObjectUtil.isNotEmpty(cached)) {
            return JSON.parseArray(cached.toString(), BmsCategory.class);
        }
        List<BmsCategory> allList = this.listFromDb(bmsCategoryPageParam);
        List<BmsCategory> treeList = buildTree(allList, "0");
        commonCacheOperator.put(cacheKey, JSON.toJSONString(treeList), CacheConstant.CACHE_EXPIRE_30_MINUTES);
        return treeList;
    }

    private List<BmsCategory> listFromDb(BmsCategoryPageParam bmsCategoryPageParam) {
        QueryWrapper<BmsCategory> queryWrapper = new QueryWrapper<BmsCategory>().checkSqlInjection();
        queryWrapper.select("ID", "PARENT_ID", "NAME", "CODE", "STATUS", "SORT_CODE");
        if(ObjectUtil.isNotEmpty(bmsCategoryPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsCategory::getStatus, bmsCategoryPageParam.getStatus());
        } else {
            queryWrapper.lambda().eq(BmsCategory::getStatus, BmsCategoryStatusEnum.ENABLE.getValue());
        }
        queryWrapper.lambda().orderByAsc(BmsCategory::getSortCode);
        queryWrapper.last("LIMIT 1000");
        return this.list(queryWrapper);
    }

    private List<BmsCategory> buildTree(List<BmsCategory> allList, String parentId) {
        List<BmsCategory> resultList = new ArrayList<>();
        for (BmsCategory category : allList) {
            if (parentId.equals(category.getParentId())) {
                List<BmsCategory> children = buildTree(allList, category.getId());
                category.setChildren(children.isEmpty() ? null : children);
                resultList.add(category);
            }
        }
        return resultList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsCategoryAddParam bmsCategoryAddParam) {
        long count = this.count(new QueryWrapper<BmsCategory>().lambda()
            .eq(BmsCategory::getName, bmsCategoryAddParam.getName())
            .eq(BmsCategory::getParentId, ObjectUtil.isEmpty(bmsCategoryAddParam.getParentId()) ? "0" : bmsCategoryAddParam.getParentId()));
        if(count > 0) {
            throw new CommonException("分类名称已存在：{}", bmsCategoryAddParam.getName());
        }

        BmsCategory bmsCategory = BeanUtil.toBean(bmsCategoryAddParam, BmsCategory.class);
        if(ObjectUtil.isEmpty(bmsCategory.getParentId())) {
            bmsCategory.setParentId("0");
        }
        bmsCategory.setStatus(BmsCategoryStatusEnum.ENABLE.getValue());
        this.save(bmsCategory);
        clearCategoryCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BmsCategoryEditParam bmsCategoryEditParam) {
        BmsCategory bmsCategory = this.queryEntity(bmsCategoryEditParam.getId());

        String parentId = ObjectUtil.isEmpty(bmsCategoryEditParam.getParentId()) ? "0" : bmsCategoryEditParam.getParentId();
        long count = this.count(new QueryWrapper<BmsCategory>().lambda()
            .eq(BmsCategory::getName, bmsCategoryEditParam.getName())
            .eq(BmsCategory::getParentId, parentId)
            .ne(BmsCategory::getId, bmsCategoryEditParam.getId()));
        if(count > 0) {
            throw new CommonException("分类名称已存在：{}", bmsCategoryEditParam.getName());
        }

        BeanUtil.copyProperties(bmsCategoryEditParam, bmsCategory);
        this.updateById(bmsCategory);
        clearCategoryCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsCategoryIdParam> bmsCategoryIdParamList) {
        List<String> idList = CollStreamUtil.toList(bmsCategoryIdParamList, BmsCategoryIdParam::getId);
        if(ObjectUtil.isNotEmpty(idList)) {
            long childCount = this.count(new QueryWrapper<BmsCategory>().lambda()
                .in(BmsCategory::getParentId, idList));
            if(childCount > 0) {
                throw new CommonException("存在子分类的分类无法删除，请先删除子分类");
            }
            this.removeByIds(idList);
            clearCategoryCache();
        }
    }

    @Override
    public BmsCategory detail(BmsCategoryIdParam bmsCategoryIdParam) {
        return this.queryEntity(bmsCategoryIdParam.getId());
    }

    @Override
    public BmsCategory queryEntity(String id) {
        BmsCategory bmsCategory = this.getById(id);
        if(ObjectUtil.isEmpty(bmsCategory)) {
            throw new CommonException("分类不存在，id值为：{}", id);
        }
        return bmsCategory;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void disableStatus(BmsCategoryIdParam bmsCategoryIdParam) {
        this.update(new LambdaUpdateWrapper<BmsCategory>().eq(BmsCategory::getId,
                bmsCategoryIdParam.getId()).set(BmsCategory::getStatus, BmsCategoryStatusEnum.DISABLED.getValue()));
        clearCategoryCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void enableStatus(BmsCategoryIdParam bmsCategoryIdParam) {
        this.update(new LambdaUpdateWrapper<BmsCategory>().eq(BmsCategory::getId,
                bmsCategoryIdParam.getId()).set(BmsCategory::getStatus, BmsCategoryStatusEnum.ENABLE.getValue()));
        clearCategoryCache();
    }

    private void clearCategoryCache() {
        commonCacheOperator.remove(CacheConstant.BMS_CATEGORY_LIST_CACHE_KEY, CacheConstant.BMS_CATEGORY_TREE_CACHE_KEY);
    }
}