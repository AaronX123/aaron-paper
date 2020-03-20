package aaron.paper.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifySubjectAnswerDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String answer;
}
