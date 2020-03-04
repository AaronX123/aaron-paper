package aaron.paper.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperDetail {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdBy;
    private String paperCreator;
    /**
     * 试卷类型
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long category;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long difficulty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date combExamTime;
    private Double score;
    private String description;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long version;
    private List<PaperSubject> currentPaperSubjectDtoList;
    /**
     * 在前端删除的试题的Id
     */
    private List<Long> deletedId;
}
