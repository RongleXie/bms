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
package vip.xiaonuo.biz.modular.tag.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.tag.entity.BmsTag;
import vip.xiaonuo.biz.modular.tag.param.BmsTagAddParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagEditParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagIdParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagPageParam;

import java.util.List;

/**
 * 标签Service接口
 *
 * @author xiaonuo
 * @date 2024/01/01
 **/
public interface BmsTagService extends IService<BmsTag> {

    /**
     * 获取标签分页
     */
    Page<BmsTag> page(BmsTagPageParam bmsTagPageParam);

    /**
     * 获取标签列表
     */
    List<BmsTag> list(BmsTagPageParam bmsTagPageParam);

    /**
     * 添加标签
     */
    void add(BmsTagAddParam bmsTagAddParam);

    /**
     * 编辑标签
     */
    void edit(BmsTagEditParam bmsTagEditParam);

    /**
     * 删除标签
     */
    void delete(List<BmsTagIdParam> bmsTagIdParamList);

    /**
     * 获取标签详情
     */
    BmsTag detail(BmsTagIdParam bmsTagIdParam);

    /**
     * 获取标签详情（内部使用，抛出异常）
     */
    BmsTag queryEntity(String id);
}