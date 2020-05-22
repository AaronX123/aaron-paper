package aaron.paper.pojo.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Data
@Accessors(chain = true)
public class Paper extends Model<Paper> {
    private static final long serialVersionUID = 2202509504164060119L;
    /**
     * 试卷id
     */
    private Long id;

    /**
     * 试卷名
     */
    private String name;

    /**
     * 试卷类型
     */
    private Long paperType;

    /**
     * 难度
     */
    private Long difficulty;

    /**
     * 组卷日期
     */
    private Date combExamTime;

    /**
     * 组卷人
     */
    private String paperCreator;

    /**
     * 卷面分数
     */
    private Double score;

    /**
     * 卷子描述
     */
    private String description;

    /**
     * 是否为模板
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
     * 状态
     */
    private Byte status;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 创建者
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新者
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 版本
     */
    private Long version;

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PAPER_TYPE = "paper_type";
    public static final String DIFFICULTY = "difficulty";
    public static final String COMB_EXAM_TIME = "comb_exam_time";
    public static final String PAPER_CREATOR = "paper_creator";
    public static final String SCORE = "score";
    public static final String DESCRIPTION = "description";
    public static final String TEMPLATE = "template";
    public static final String DOWNLOAD_TIMES = "download_times";
    public static final String PUBLISH_TIMES = "publish_times";
    public static final String STATUS = "status";
    public static final String ORG_ID ="org_id";
    public static final String COMPANY_ID = "company_id";
    public static final String CREATED_BY = "created_by";
    public static final String CREATED_TIME = "created_time";
    public static final String UPDATED_BY = "updated_by";
    public static final String UPDATE_TIME = "updated_time";
    public static final String VERSION = "version";


}
