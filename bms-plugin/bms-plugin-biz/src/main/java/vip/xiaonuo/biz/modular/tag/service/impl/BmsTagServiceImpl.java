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
package vip.xiaonuo.biz.modular.tag.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.tag.entity.BmsTag;
import vip.xiaonuo.biz.modular.tag.enums.BmsTagStatusEnum;
import vip.xiaonuo.biz.modular.tag.mapper.BmsTagMapper;
import vip.xiaonuo.biz.modular.tag.param.BmsTagAddParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagEditParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagIdParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagPageParam;
import vip.xiaonuo.biz.modular.tag.service.BmsTagService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.consts.CacheConstant;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.CommonSqlUtil;

import java.util.List;

@Service
public class BmsTagServiceImpl extends ServiceImpl<BmsTagMapper, BmsTag> implements BmsTagService {

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public Page<BmsTag> page(BmsTagPageParam bmsTagPageParam) {
        QueryWrapper<BmsTag> queryWrapper = new QueryWrapper<BmsTag>().checkSqlInjection();
        queryWrapper.select("ID", "NAME", "CODE", "COLOR", "DESCRIPTION", "STATUS", "SORT_CODE", "CREATE_TIME", "UPDATE_TIME");
        if(ObjectUtil.isNotEmpty(bmsTagPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q
                .like(BmsTag::getName, bmsTagPageParam.getSearchKey())
                .or()
                .like(BmsTag::getCode, bmsTagPageParam.getSearchKey()));
        }
        if(ObjectUtil.isNotEmpty(bmsTagPageParam.getName())) {
            queryWrapper.lambda().like(BmsTag::getName, bmsTagPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(bmsTagPageParam.getCode())) {
            queryWrapper.lambda().eq(BmsTag::getCode, bmsTagPageParam.getCode());
        }
        if(ObjectUtil.isNotEmpty(bmsTagPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsTag::getStatus, bmsTagPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bmsTagPageParam.getStartCreateTime()) && ObjectUtil.isNotEmpty(bmsTagPageParam.getEndCreateTime())) {
            queryWrapper.lambda().between(BmsTag::getCreateTime, bmsTagPageParam.getStartCreateTime(), bmsTagPageParam.getEndCreateTime());
        }
        if(ObjectUtil.isAllNotEmpty(bmsTagPageParam.getSortField(), bmsTagPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bmsTagPageParam.getSortOrder());
            CommonSqlUtil.validateSortField(bmsTagPageParam.getSortField());
            queryWrapper.orderBy(true, bmsTagPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsTagPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BmsTag::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsTag> list(BmsTagPageParam bmsTagPageParam) {
        String cacheKey = CacheConstant.BMS_TAG_LIST_CACHE_KEY;
        Object cached = commonCacheOperator.get(cacheKey);
        if (ObjectUtil.isNotEmpty(cached)) {
            return JSON.parseArray(cached.toString(), BmsTag.class);
        }
        QueryWrapper<BmsTag> queryWrapper = new QueryWrapper<BmsTag>().checkSqlInjection();
        queryWrapper.select("ID", "NAME", "CODE", "COLOR", "STATUS", "SORT_CODE");
        queryWrapper.lambda().eq(BmsTag::getStatus, BmsTagStatusEnum.ENABLE.getValue());
        queryWrapper.lambda().orderByAsc(BmsTag::getSortCode);
        queryWrapper.last("LIMIT 1000");
        List<BmsTag> result = this.list(queryWrapper);
        commonCacheOperator.put(cacheKey, JSON.toJSONString(result), CacheConstant.CACHE_EXPIRE_30_MINUTES);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsTagAddParam bmsTagAddParam) {
        long count = this.count(new QueryWrapper<BmsTag>().lambda()
            .eq(BmsTag::getName, bmsTagAddParam.getName()));
        if(count > 0) {
            throw new CommonException("标签名称已存在：{}", bmsTagAddParam.getName());
        }
        BmsTag bmsTag = BeanUtil.toBean(bmsTagAddParam, BmsTag.class);
        bmsTag.setStatus(BmsTagStatusEnum.ENABLE.getValue());
        this.save(bmsTag);
        clearTagCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BmsTagEditParam bmsTagEditParam) {
        BmsTag bmsTag = this.queryEntity(bmsTagEditParam.getId());
        long count = this.count(new QueryWrapper<BmsTag>().lambda()
            .eq(BmsTag::getName, bmsTagEditParam.getName())
            .ne(BmsTag::getId, bmsTagEditParam.getId()));
        if(count > 0) {
            throw new CommonException("标签名称已存在：{}", bmsTagEditParam.getName());
        }
        BeanUtil.copyProperties(bmsTagEditParam, bmsTag);
        this.updateById(bmsTag);
        clearTagCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsTagIdParam> bmsTagIdParamList) {
        this.removeByIds(CollStreamUtil.toList(bmsTagIdParamList, BmsTagIdParam::getId));
        clearTagCache();
    }

    @Override
    public BmsTag detail(BmsTagIdParam bmsTagIdParam) {
        return this.queryEntity(bmsTagIdParam.getId());
    }

    @Override
    public BmsTag queryEntity(String id) {
        BmsTag bmsTag = this.getById(id);
        if(ObjectUtil.isEmpty(bmsTag)) {
            throw new CommonException("标签不存在，id值为：{}", id);
        }
        return bmsTag;
    }

    private void clearTagCache() {
        commonCacheOperator.remove(CacheConstant.BMS_TAG_LIST_CACHE_KEY);
    }
}