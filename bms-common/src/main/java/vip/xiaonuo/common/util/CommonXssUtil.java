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
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS过滤工具类
 * 使用Jsoup进行HTML内容清洗，防止XSS攻击
 *
 * @author xiaonuo
 * @date 2026/03/31
 */
public class CommonXssUtil {

    /**
     * 纯文本过滤 - 清除所有HTML标签
     * 用于评论、昵称等纯文本输入场景
     *
     * @param content 输入内容
     * @return 清洗后的纯文本
     */
    public static String filterPlainText(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        // 使用Safelist.none()清除所有HTML标签
        return Jsoup.clean(content, Safelist.none());
    }

    /**
     * 富文本过滤 - 白名单保留安全标签
     * 用于文章内容等富文本输入场景
     * 保留Markdown渲染所需的常用HTML标签
     *
     * @param content 输入内容
     * @return 清洗后的富文本
     */
    public static String filterRichText(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        // 自定义白名单：保留Markdown常用标签
        Safelist safelist = Safelist.relaxed()
                // 移除危险标签
                .removeTags("script", "iframe", "frame", "frameset", "object", "embed", "applet", "form", "input", "button", "textarea", "select", "option", "link", "style", "meta", "base")
                // 移除危险属性
                .removeAttributes(":all", "onclick", "onerror", "onload", "onmouseover", "onmouseout", "onmousedown", "onmouseup", "onkeydown", "onkeyup", "onfocus", "onblur", "onchange", "onsubmit", "onreset", "action", "formaction", "data", "srcdoc", "xlink:href")
                // 保留安全的属性
                .addAttributes("a", "href", "title", "target", "rel")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("video", "src", "controls", "width", "height")
                .addAttributes("audio", "src", "controls")
                .addAttributes("table", "border", "cellpadding", "cellspacing", "width")
                .addAttributes("td", "colspan", "rowspan", "width", "height")
                .addAttributes("th", "colspan", "rowspan", "width", "height")
                .addAttributes("code", "class")
                .addAttributes("pre", "class")
                .addAttributes("span", "class")
                .addAttributes("div", "class")
                .addAttributes("p", "class");
        
        // 强制a标签添加rel="noopener noreferrer"防止钓鱼攻击
        Safelist finalSafelist = safelist.addEnforcedAttribute("a", "rel", "noopener noreferrer");
        
        return Jsoup.clean(content, finalSafelist);
    }

    /**
     * Markdown内容过滤 - 针对Markdown格式的特殊处理
     * Markdown语法本身是安全的，但需要过滤嵌入的HTML标签
     *
     * @param content Markdown内容
     * @return 清洗后的Markdown内容
     */
    public static String filterMarkdown(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        // Markdown允许的HTML白名单（GitHub风格）
        Safelist safelist = Safelist.basic()
                // 添加Markdown常用的额外标签
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "br", "hr", "pre", "code", "blockquote", "table", "thead", "tbody", "tr", "th", "td", "img", "video", "audio", "del", "ins", "mark", "sub", "sup")
                // 移除危险标签
                .removeTags("script", "iframe", "frame", "frameset", "object", "embed", "applet", "form", "input", "button", "textarea", "select", "option", "link", "style", "meta", "base")
                // 移除所有事件属性
                .removeAttributes(":all", "onclick", "onerror", "onload", "onmouseover", "onmouseout", "onmousedown", "onmouseup", "onkeydown", "onkeyup", "onfocus", "onblur", "onchange", "onsubmit", "onreset", "action", "formaction", "data", "srcdoc", "xlink:href")
                // 添加安全属性
                .addAttributes("a", "href", "title", "target")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("video", "src", "controls", "width", "height")
                .addAttributes("audio", "src", "controls")
                .addAttributes("code", "class")
                .addAttributes("pre", "class")
                .addAttributes("table", "border", "cellpadding", "cellspacing")
                .addAttributes("td", "colspan", "rowspan")
                .addAttributes("th", "colspan", "rowspan");
        
        // 强制a标签添加rel="noopener noreferrer"
        Safelist finalSafelist = safelist.addEnforcedAttribute("a", "rel", "noopener noreferrer");
        
        return Jsoup.clean(content, finalSafelist);
    }

    /**
     * URL过滤 - 校验URL是否安全
     * 防止javascript:、data:等危险协议
     *
     * @param url 输入URL
     * @return 清洗后的URL，不安全则返回空
     */
    public static String filterUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return url;
        }
        // 清除URL中的危险协议
        String cleanUrl = Jsoup.clean(url, Safelist.none());
        
        // 检查是否为安全协议
        String lowerUrl = cleanUrl.toLowerCase().trim();
        if (lowerUrl.startsWith("javascript:") || 
            lowerUrl.startsWith("data:") || 
            lowerUrl.startsWith("vbscript:") ||
            lowerUrl.startsWith("file:")) {
            return "";
        }
        
        return cleanUrl;
    }

    /**
     * 昵称/用户名过滤
     * 清除HTML标签，保留纯文本
     *
     * @param nickname 输入昵称
     * @return 清洗后的昵称
     */
    public static String filterNickname(String nickname) {
        return filterPlainText(nickname);
    }

    /**
     * 邮箱过滤
     * 清除HTML标签和危险字符
     *
     * @param email 输入邮箱
     * @return 清洗后的邮箱
     */
    public static String filterEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return email;
        }
        // 清除HTML标签
        String cleanEmail = Jsoup.clean(email, Safelist.none());
        // 移除可能的注入字符
        return cleanEmail.replaceAll("[<>\"'&]", "");
    }

    /**
     * 网站URL过滤
     * 校验并清洗网站URL
     *
     * @param website 输入网站URL
     * @return 清洗后的网站URL
     */
    public static String filterWebsite(String website) {
        if (StrUtil.isBlank(website)) {
            return website;
        }
        String cleanUrl = filterUrl(website);
        // 如果URL不以http开头，添加https://
        if (StrUtil.isNotBlank(cleanUrl) && !cleanUrl.toLowerCase().startsWith("http")) {
            cleanUrl = "https://" + cleanUrl;
        }
        return cleanUrl;
    }
}