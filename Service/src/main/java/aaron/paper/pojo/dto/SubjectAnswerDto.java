package aaron.paper.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectAnswerDto {
    private Long subjectId;

    private String answer;

    private Byte rightAnswer;

    private Object field1;

    private Object field2;

    private Object field3;

}
