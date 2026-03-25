package vip.xiaonuo.biz.modular.article.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

@Getter
@Setter
@TableName("BMS_ARTICLE_VERSION")
public class BmsArticleVersion extends CommonEntity {

    @TableId
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "文章ID")
    private String articleId;

    @Schema(description = "版本号")
    private Integer versionNumber;

    @Schema(description = "文章标题快照")
    private String title;

    @Schema(description = "文章内容快照")
    private String content;

    @Schema(description = "文章摘要快照")
    private String summary;

    @Schema(description = "变更摘要")
    private String changeSummary;

    @Schema(description = "状态")
    private String status;
}