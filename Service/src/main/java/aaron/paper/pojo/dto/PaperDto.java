package aaron.paper.pojo.dto;

import aaron.common.data.common.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperDto extends BaseDto {
    /**
     * 试卷名称
     */
    private String name;
    /*
     * 配置id，通过id生成试卷信息
     */
    private Long configId;
    /**
     * 试卷类型，通过id从数据字典中查询
     */
    private Long paperType;
    /**
     * 难度，通过id从数据字典中查询
     */
    private Long difficulty;
    /**
     * 组卷日期
     */
    private Date combExamTime;
    /**
     * 试卷分数
     */
    private Double score;
    /**
     * 试卷创建者
     */
    private String paperCreator;
    /**
     * 试卷描述
     */
    private String description;
    /**
     * 试卷是否为模板
     */
    private Byte template;
    /**
     * 下载次数
     */
    private Integer downloadTimes;
    /**
     * 发布次数
     */
    private Integer publishTimes;
    /**
     * 试卷状态
     */
    private Byte status;
    /**
     * 版本
     */
    private Long version;
}
