package aaron.paper.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPaperSubjectDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subjectTypeId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long difficulty;
    private String subject;
    private String categoryValue;
    private String difficultyValue;
    private String subjectTypeName;
    /**
     * 标记是否为组卷服务传过去的原始题目，如果从基础数据服务中添加题目，则在接收对象时此数据不为9999
     */
    private Integer mark;
    /**
     * 答案list
     */
    private List<ModifySubjectAnswerDto> modifySubjectAnswerDtos;
}
