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
package vip.xiaonuo.biz.modular.media.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.media.entity.BmsMedia;
import vip.xiaonuo.biz.modular.media.param.BmsMediaAddParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaEditParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaIdParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaPageParam;

import java.util.List;

/**
 * 媒体文件Service接口
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
public interface BmsMediaService extends IService<BmsMedia> {

    /**
     * 获取媒体文件分页
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    Page<BmsMedia> page(BmsMediaPageParam bmsMediaPageParam);

    /**
     * 获取媒体文件列表
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    List<BmsMedia> list(BmsMediaPageParam bmsMediaPageParam);

    /**
     * 添加媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void add(BmsMediaAddParam bmsMediaAddParam);

    /**
     * 编辑媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void edit(BmsMediaEditParam bmsMediaEditParam);

    /**
     * 删除媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void delete(List<BmsMediaIdParam> bmsMediaIdParamList);

    /**
     * 获取媒体文件详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    BmsMedia detail(BmsMediaIdParam bmsMediaIdParam);

    /**
     * 获取媒体文件详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     **/
    BmsMedia queryEntity(String id);
}