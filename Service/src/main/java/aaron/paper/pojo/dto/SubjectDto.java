package aaron.paper.pojo.dto;

import aaron.common.data.common.BaseDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public class SubjectDto extends BaseDto {
    /**
     * 题目类别
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long difficulty;

    /**
     * 状态位
     */
    private Byte status;

    /**
     * 题目类别id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    /**
     * 题目类别名字
     */
    private String categoryName;

    /**
     * 题目分数
     */
    private Double score;

    /**
     * 题目难度名
     */
    private String difficultyName;
    /**
     * 题型id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subjectTypeId;
    /**
     * 题目类型名字
     */
    private String subjectTypeName;


    /**
     * 题目数量
     */
    private Integer num;
}
