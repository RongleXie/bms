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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据脱敏工具类
 * 用于日志记录时对敏感字段进行脱敏处理，防止敏感信息泄露
 *
 * @author bms-security
 * @date 2026/03/31
 */
public class CommonDesensitizeUtil {

    /**
     * 需要脱敏的字段名列表（不区分大小写）
     * 包含：密码、令牌、密钥、卡号、手机号、身份证等敏感字段
     */
    private static final Set<String> SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            // 密码相关
            "password", "pwd", "pass", "newpassword", "oldpassword", "confirmPassword",
            // 令牌相关
            "token", "accessToken", "refreshToken", "authToken", "authorization",
            // 密钥相关
            "secret", "secretKey", "apiKey", "privateKey", "private_key",
            // 卡号相关
            "card", "cardNo", "cardNumber", "creditCard",
            // 个人信息
            "phone", "mobile", "tel", "telephone", "cellphone",
            "idcard", "idCard", "idCardNo", "identityCard",
            "email", "mail",
            // 其他敏感字段
            "ssn", "socialSecurityNumber",
            "bankAccount", "bankCard",
            "cvv", "cvv2",
            "salt", "captcha"
    ));

    /**
     * 脱敏后显示的值
     */
    private static final String MASK_VALUE = "******";

    /**
     * 对JSON字符串进行脱敏处理
     *
     * @param jsonString 原始JSON字符串
     * @return 脱敏后的JSON字符串
     */
    public static String desensitizeJson(String jsonString) {
        if (StrUtil.isBlank(jsonString)) {
            return jsonString;
        }
        
        try {
            // 尝试解析为JSON对象
            if (JSONUtil.isTypeJSON(jsonString)) {
                Object parsed = JSONUtil.parse(jsonString);
                if (parsed instanceof JSONObject) {
                    return desensitizeJsonObject((JSONObject) parsed).toString();
                } else if (parsed instanceof JSONArray) {
                    return desensitizeJsonArray((JSONArray) parsed).toString();
                }
            }
            return jsonString;
        } catch (Exception e) {
            // JSON解析失败，返回原始字符串（安全降级）
            return jsonString;
        }
    }

    /**
     * 对JSONObject进行脱敏处理
     *
     * @param jsonObject 原始JSONObject
     * @return 脱敏后的JSONObject
     */
    private static JSONObject desensitizeJsonObject(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (isSensitiveField(key)) {
                result.put(key, MASK_VALUE);
            } else if (value instanceof JSONObject) {
                result.put(key, desensitizeJsonObject((JSONObject) value));
            } else if (value instanceof JSONArray) {
                result.put(key, desensitizeJsonArray((JSONArray) value));
            } else if (value instanceof String && JSONUtil.isTypeJSON((String) value)) {
                result.put(key, desensitizeJson((String) value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 对JSONArray进行脱敏处理
     *
     * @param jsonArray 原始JSONArray
     * @return 脱敏后的JSONArray
     */
    private static JSONArray desensitizeJsonArray(JSONArray jsonArray) {
        JSONArray result = new JSONArray();
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                result.add(desensitizeJsonObject((JSONObject) item));
            } else if (item instanceof JSONArray) {
                result.add(desensitizeJsonArray((JSONArray) item));
            } else if (item instanceof String && JSONUtil.isTypeJSON((String) item)) {
                result.add(desensitizeJson((String) item));
            } else {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 判断字段名是否为敏感字段（不区分大小写）
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    public static boolean isSensitiveField(String fieldName) {
        if (StrUtil.isBlank(fieldName)) {
            return false;
        }
        // 不区分大小写匹配
        String lowerFieldName = fieldName.toLowerCase();
        // 完全匹配
        if (SENSITIVE_FIELDS.contains(lowerFieldName)) {
            return true;
        }
        // 部分匹配（字段名包含敏感关键词）
        for (String sensitiveField : SENSITIVE_FIELDS) {
            if (lowerFieldName.contains(sensitiveField.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对单个字段值进行脱敏
     * 用于需要自定义脱敏规则的场景
     *
     * @param fieldName 字段名
     * @param value     字段值
     * @return 脱敏后的值
     */
    public static Object desensitizeField(String fieldName, Object value) {
        if (isSensitiveField(fieldName)) {
            return MASK_VALUE;
        }
        if (value instanceof String && JSONUtil.isTypeJSON((String) value)) {
            return desensitizeJson((String) value);
        }
        return value;
    }

    /**
     * 对手机号进行部分脱敏（保留前3后4位）
     *
     * @param phone 手机号
     * @return 脱敏后的手机号，如：138****1234
     */
    public static String desensitizePhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return MASK_VALUE;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 对身份证号进行部分脱敏（保留前6后4位）
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号，如：310***********1234
     */
    public static String desensitizeIdCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 10) {
            return MASK_VALUE;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 对邮箱进行部分脱敏（保留前3字符和@后的域名）
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱，如：abc***@example.com
     */
    public static String desensitizeEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return MASK_VALUE;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, Math.min(3, atIndex));
        String suffix = email.substring(atIndex);
        return prefix + "***" + suffix;
    }
}