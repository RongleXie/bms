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
package vip.xiaonuo.biz.modular.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.media.entity.BmsMedia;
import vip.xiaonuo.biz.modular.media.enums.BmsMediaStatusEnum;
import vip.xiaonuo.biz.modular.media.mapper.BmsMediaMapper;
import vip.xiaonuo.biz.modular.media.param.BmsMediaAddParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaEditParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaIdParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaPageParam;
import vip.xiaonuo.biz.modular.media.service.BmsMediaService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import java.util.List;

/**
 * 媒体文件Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
@Service
public class BmsMediaServiceImpl extends ServiceImpl<BmsMediaMapper, BmsMedia> implements BmsMediaService {

    @Override
    public Page<BmsMedia> page(BmsMediaPageParam bmsMediaPageParam) {
        QueryWrapper<BmsMedia> queryWrapper = new QueryWrapper<BmsMedia>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q
                .like(BmsMedia::getFileName, bmsMediaPageParam.getSearchKey())
                .or()
                .like(BmsMedia::getOriginalName, bmsMediaPageParam.getSearchKey()));
        }
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getFileName())) {
            queryWrapper.lambda().like(BmsMedia::getFileName, bmsMediaPageParam.getFileName());
        }
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getFileType())) {
            queryWrapper.lambda().eq(BmsMedia::getFileType, bmsMediaPageParam.getFileType());
        }
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsMedia::getStatus, bmsMediaPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getStartCreateTime()) && ObjectUtil.isNotEmpty(bmsMediaPageParam.getEndCreateTime())) {
            queryWrapper.lambda().between(BmsMedia::getCreateTime, bmsMediaPageParam.getStartCreateTime(), bmsMediaPageParam.getEndCreateTime());
        }
        if(ObjectUtil.isAllNotEmpty(bmsMediaPageParam.getSortField(), bmsMediaPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bmsMediaPageParam.getSortOrder());
            queryWrapper.orderBy(true, bmsMediaPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsMediaPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(BmsMedia::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsMedia> list(BmsMediaPageParam bmsMediaPageParam) {
        QueryWrapper<BmsMedia> queryWrapper = new QueryWrapper<BmsMedia>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bmsMediaPageParam.getFileType())) {
            queryWrapper.lambda().eq(BmsMedia::getFileType, bmsMediaPageParam.getFileType());
        }
        // 只查询启用状态的数据
        queryWrapper.lambda().eq(BmsMedia::getStatus, BmsMediaStatusEnum.ENABLE.getValue());
        queryWrapper.lambda().orderByDesc(BmsMedia::getCreateTime);
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsMediaAddParam bmsMediaAddParam) {
        BmsMedia bmsMedia = BeanUtil.toBean(bmsMediaAddParam, BmsMedia.class);
        // 默认状态为启用
        if(ObjectUtil.isEmpty(bmsMedia.getStatus())) {
            bmsMedia.setStatus(BmsMediaStatusEnum.ENABLE.getValue());
        }
        // 默认下载次数为0
        if(ObjectUtil.isEmpty(bmsMedia.getDownloadCount())) {
            bmsMedia.setDownloadCount(0);
        }
        this.save(bmsMedia);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BmsMediaEditParam bmsMediaEditParam) {
        BmsMedia bmsMedia = this.queryEntity(bmsMediaEditParam.getId());
        BeanUtil.copyProperties(bmsMediaEditParam, bmsMedia);
        this.updateById(bmsMedia);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsMediaIdParam> bmsMediaIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bmsMediaIdParamList, BmsMediaIdParam::getId));
    }

    @Override
    public BmsMedia detail(BmsMediaIdParam bmsMediaIdParam) {
        return this.queryEntity(bmsMediaIdParam.getId());
    }

    @Override
    public BmsMedia queryEntity(String id) {
        BmsMedia bmsMedia = this.getById(id);
        if(ObjectUtil.isEmpty(bmsMedia)) {
            throw new CommonException("媒体文件不存在，id值为：{}", id);
        }
        return bmsMedia;
    }
}