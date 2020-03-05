package aaron.paper.pojo.dto;

import aaron.common.data.common.BaseDto;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectDto that = (SubjectDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(difficulty, that.difficulty) &&
                Objects.equals(status, that.status) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(categoryName, that.categoryName) &&
                Objects.equals(score, that.score) &&
                Objects.equals(difficultyName, that.difficultyName) &&
                Objects.equals(subjectTypeId, that.subjectTypeId) &&
                Objects.equals(subjectTypeName, that.subjectTypeName) &&
                Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, remark, difficulty, status, categoryId, categoryName, score, difficultyName, subjectTypeId, subjectTypeName, num);
    }
}
