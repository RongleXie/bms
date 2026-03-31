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
package vip.xiaonuo.common.consts;

/**
 * 缓存静态常量
 *
 * @author dongxiayu
 * @date 2023/1/30 0:44
 **/
public class CacheConstant {

    /**
     * 权限资源
     */
    public static final String PERMISSION_RESOURCE_CACHE_KEY = "permission-resource";

    /**
     * 权限资源Method
     */
    public static final String PERMISSION_RESOURCE_METHOD_CACHE_KEY = "permission-resource-method";

    /**
     * B端权限列表
     */
    public static final String AUTH_B_PERMISSION_LIST_CACHE_KEY = "auth-b-permission-list:";

    /**
     * C端权限列表
     */
    public static final String AUTH_C_PERMISSION_LIST_CACHE_KEY = "auth-c-permission-list:";

    // ========== BMS业务缓存 ==========

    /**
     * 分类列表缓存Key
     */
    public static final String BMS_CATEGORY_LIST_CACHE_KEY = "bms-category-list";

    /**
     * 分类树缓存Key
     */
    public static final String BMS_CATEGORY_TREE_CACHE_KEY = "bms-category-tree";

    /**
     * 标签列表缓存Key
     */
    public static final String BMS_TAG_LIST_CACHE_KEY = "bms-tag-list";

    /**
     * 文章浏览量计数器缓存Key前缀
     */
    public static final String BMS_ARTICLE_VIEW_COUNT_PREFIX = "bms-article-view:";

    /**
     * 文章点赞计数器缓存Key前缀
     */
    public static final String BMS_ARTICLE_LIKE_COUNT_PREFIX = "bms-article-like:";

    /**
     * 文章评论计数器缓存Key前缀
     */
    public static final String BMS_ARTICLE_COMMENT_COUNT_PREFIX = "bms-article-comment:";

    /**
     * 缓存过期时间：30分钟（秒）
     */
    public static final long CACHE_EXPIRE_30_MINUTES = 30 * 60L;

    /**
     * 缓存过期时间：1小时（秒）
     */
    public static final long CACHE_EXPIRE_1_HOUR = 60 * 60L;

    /**
     * 计数器同步间隔：5分钟（秒）
     */
    public static final long COUNTER_SYNC_INTERVAL = 5 * 60L;

}
