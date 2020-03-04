package aaron.paper.pojo.dto;

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
public class PaperQueryDto {
    /**
     * 试卷名称
     */
    private String name;
    /**
     * 创建者
     */
    private String createdBy;
    /**
     * 难度
     */
    private Long difficulty;
    /**
     * 开始时间
     */
    private Date start;
    /**
     * 截至时间
     */
    private Date end;
}
