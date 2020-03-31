package aaron.paper.pojo.model;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Data
@Accessors(chain = true)
public class PaperSubject extends Model<PaperSubject> {
    private static final long serialVersionUID = -5509709494009584723L;
    /**
     * 试题id
     */
    private Long id;

    /**
     * 试卷id
     */
    private Long paperId;

    /**
     * 题目
     */
    private String subject;

    private Long categoryId;
    /**
     * 题目类型从基础数据中取
     */
    private Long subjectTypeId;

    /**
     * 难度
     */
    private Long difficulty;

    /**
     * 分数
     */
    private Double score;

    /**
     * 预留
     */
    private String field1;

    /**
     * 预留
     */
    private String field2;

    /**
     * 预留
     */
    private String field3;

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public static final String ID = "id";
    public static final String PAPER_ID = "paper_id";
    public static final String SUBJECT  = "subject";
    public static final String CATEGORY_ID = "category_id";
    public static final String SUBJECT_TYPE_ID = "subject_type_id";
    public static final String DIFFICULTY = "difficulty";
    public static final String SCORE = "score";
    public static final String FIELD1 = "field1";
    public static final String FIELD2 = "field2";
    public static final String FIELD3 = "field3";

}
