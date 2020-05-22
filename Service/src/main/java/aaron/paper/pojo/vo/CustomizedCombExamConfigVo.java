package aaron.paper.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CustomizedCombExamConfigVo {
    private String name;
    private String status;
    private Long difficulty;
    private String remark;
    private Long paperType;
    private List<CustomizedCombExamConfigItemVo> combExamConfigItemVOs;
}
