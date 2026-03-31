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
package vip.xiaonuo.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL工具类，处理数据库兼容性问题及SQL注入防护
 *
 * @author yubaoshan
 * @date 2026/2/12 18:43
 **/
public class CommonSqlUtil {

    /** Oracle IN子句最大元素数量限制 */
    private static final int IN_CLAUSE_LIMIT = 999;

    /** 用户ID白名单正则：只允许字母、数字、下划线、横线（雪花ID格式） */
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /** API URL白名单正则：只允许字母、数字、下划线、横线、斜杠 */
    private static final Pattern API_URL_PATTERN = Pattern.compile("^[a-zA-Z0-9_/-]+$");

    /** 排序字段白名单正则：只允许字母、数字、下划线（防止注入） */
    private static final Pattern SORT_FIELD_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * 安全的IN查询，自动处理Oracle IN子句1000限制
     * 当集合大小超过999时，自动拆分为多个IN子句用OR连接：
     * (column IN (...999个) OR column IN (...999个) OR ...)
     *
     * @param wrapper MyBatis-Plus LambdaQueryWrapper
     * @param column  查询列
     * @param values  IN子句的值集合
     * @param <T>     实体类型
     */
    public static <T> void safeIn(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isEmpty(values)) {
            return;
        }
        if (values.size() <= IN_CLAUSE_LIMIT) {
            wrapper.in(column, values);
            return;
        }
        List<?> valueList = values instanceof List ? (List<?>) values : new ArrayList<>(values);
        List<? extends List<?>> partitions = CollectionUtil.split(valueList, IN_CLAUSE_LIMIT);
        wrapper.and(w -> {
            for (int i = 0; i < partitions.size(); i++) {
                if (i == 0) {
                    w.in(column, partitions.get(i));
                } else {
                    w.or().in(column, partitions.get(i));
                }
            }
        });
    }

    /**
     * 使用预计算表的子查询替代IN查询，适用于大数据量场景
     * 通过MAP表查找SCOPE_KEY，再从SCOPE表获取orgId列表：
     * column IN (SELECT ORG_ID FROM SYS_USER_DATA_SCOPE WHERE USER_ID = '{userId}'
     *   AND SCOPE_KEY = (SELECT SCOPE_KEY FROM SYS_USER_DATA_SCOPE_MAP WHERE USER_ID = '{userId}' AND API_URL = '{apiUrl}'))
     * <p>
     * 按API维度精确过滤，相同orgId集合的API共享SCOPE_KEY，大幅减少预计算表数据量。
     * SQL固定长度，不受数据量影响，数据库可缓存执行计划，走索引高效查询。
     * </p>
     *
     * @param wrapper MyBatis-Plus LambdaQueryWrapper
     * @param column  查询列
     * @param userId  当前登录用户ID（必须符合白名单格式）
     * @param apiUrl  当前请求的API地址（必须符合白名单格式）
     * @param <T>     实体类型
     * @throws CommonException 如果参数不符合白名单格式，抛出异常
     */
    public static <T> void scopeIn(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> column, String userId, String apiUrl) {
        // 使用正则白名单验证，防止SQL注入
        validateUserId(userId);
        validateApiUrl(apiUrl);
        wrapper.inSql(column, "SELECT ORG_ID FROM SYS_USER_DATA_SCOPE WHERE USER_ID = '" + userId
                + "' AND SCOPE_KEY = (SELECT SCOPE_KEY FROM SYS_USER_DATA_SCOPE_MAP WHERE USER_ID = '"
                + userId + "' AND API_URL = '" + apiUrl + "')");
    }

    /**
     * 验证用户ID格式（正则白名单）
     * 只允许字母、数字、下划线、横线，防止SQL注入
     *
     * @param userId 用户ID
     * @throws CommonException 如果格式不合法
     */
    public static void validateUserId(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new CommonException("用户ID不能为空");
        }
        if (!ReUtil.isMatch(USER_ID_PATTERN, userId)) {
            throw new CommonException("用户ID格式不合法，存在SQL注入风险：{}", userId);
        }
    }

    /**
     * 验证API URL格式（正则白名单）
     * 只允许字母、数字、下划线、横线、斜杠，防止SQL注入
     *
     * @param apiUrl API URL
     * @throws CommonException 如果格式不合法
     */
    public static void validateApiUrl(String apiUrl) {
        if (StrUtil.isBlank(apiUrl)) {
            throw new CommonException("API URL不能为空");
        }
        if (!ReUtil.isMatch(API_URL_PATTERN, apiUrl)) {
            throw new CommonException("API URL格式不合法，存在SQL注入风险：{}", apiUrl);
        }
    }

    /**
     * 验证排序字段格式（正则白名单）
     * 只允许字母、数字、下划线，防止SQL注入
     * 
     * @param sortField 排序字段（驼峰或下划线格式）
     * @throws CommonException 如果格式不合法
     */
    public static void validateSortField(String sortField) {
        if (StrUtil.isBlank(sortField)) {
            return; // 空值不验证，由业务层决定默认排序
        }
        // 先转为下划线格式再验证
        String underlineField = StrUtil.toUnderlineCase(sortField);
        if (!ReUtil.isMatch(SORT_FIELD_PATTERN, underlineField)) {
            throw new CommonException("排序字段格式不合法，存在SQL注入风险：{}", sortField);
        }
    }

    /**
     * 验证排序字段是否在白名单集合中
     * 提供更严格的白名单校验，只允许预定义的字段排序
     *
     * @param sortField 排序字段
     * @param allowedFields 允许的字段白名单（下划线格式）
     * @throws CommonException 如果字段不在白名单中
     */
    public static void validateSortFieldInWhitelist(String sortField, Set<String> allowedFields) {
        if (StrUtil.isBlank(sortField)) {
            return; // 空值不验证
        }
        // 先验证格式
        validateSortField(sortField);
        // 转为下划线格式检查白名单
        String underlineField = StrUtil.toUnderlineCase(sortField);
        if (!allowedFields.contains(underlineField)) {
            throw new CommonException("排序字段不在允许的白名单中：{}", sortField);
        }
    }
}
