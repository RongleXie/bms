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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import java.util.List;

/**
 * 标签Service接口实现类
 *
 * @author xiaonuo
 * @date 2024/01/01
 **/
@Service
public class BmsTagServiceImpl extends ServiceImpl<BmsTagMapper, BmsTag> implements BmsTagService {

    @Override
    public Page<BmsTag> page(BmsTagPageParam bmsTagPageParam) {
        QueryWrapper<BmsTag> queryWrapper = new QueryWrapper<BmsTag>().checkSqlInjection();
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
            queryWrapper.orderBy(true, bmsTagPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsTagPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BmsTag::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsTag> list(BmsTagPageParam bmsTagPageParam) {
        QueryWrapper<BmsTag> queryWrapper = new QueryWrapper<BmsTag>().checkSqlInjection();
        queryWrapper.lambda().eq(BmsTag::getStatus, BmsTagStatusEnum.ENABLE.getValue());
        queryWrapper.lambda().orderByAsc(BmsTag::getSortCode);
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsTagAddParam bmsTagAddParam) {
        BmsTag bmsTag = BeanUtil.toBean(bmsTagAddParam, BmsTag.class);
        bmsTag.setStatus(BmsTagStatusEnum.ENABLE.getValue());
        this.save(bmsTag);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BmsTagEditParam bmsTagEditParam) {
        BmsTag bmsTag = this.queryEntity(bmsTagEditParam.getId());
        BeanUtil.copyProperties(bmsTagEditParam, bmsTag);
        this.updateById(bmsTag);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsTagIdParam> bmsTagIdParamList) {
        this.removeByIds(CollStreamUtil.toList(bmsTagIdParamList, BmsTagIdParam::getId));
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
}