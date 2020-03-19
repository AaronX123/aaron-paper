package aaron.paper.pojo.vo;

import aaron.common.data.common.BaseQueryVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * 试卷
 * @author xiaoyouming
 * @version 1.0
 * @since 2019-08-19
 */
public class PaperVo extends BaseQueryVo {
    /**
     * 试卷id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 试卷名称
     */
    private String name;
    /**
     * 试卷类型id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paperType;
    /**
     * 试卷类型，通过id从数据字典中查询
     */
    private String paperTypeValue;
    /**
     * 难度id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long difficulty;
    /**
     * 难度，通过id从数据字典中查询
     */
    private String difficultyValue;
    /**
     * 组卷日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date combExamTime;
    /**
     * 试卷分数
     */
    private Double score;
    /**
     * 试卷描述
     */
    private String description;
    /**
     * 试卷是否为模板
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
     * 试卷状态
     */
    private Byte status;
    /**
     * 创建者
     */
    private String paperCreator;
    /**
     * 创建时间
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createdTime;
    /**
     * 公司id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    private String companyValue;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updatedBy;

    private String updatedByValue;
    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date updatedTime;
    public PaperVo() {
    }

    @Override
    public String toString() {
        return "PaperVo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", paperType=" + paperType +
                ", paperTypeValue='" + paperTypeValue + '\'' +
                ", difficulty=" + difficulty +
                ", difficultyValue='" + difficultyValue + '\'' +
                ", combExamTime=" + combExamTime +
                ", score=" + score +
                ", description='" + description + '\'' +
                ", template=" + template +
                ", downloadTimes=" + downloadTimes +
                ", publishTimes=" + publishTimes +
                ", status=" + status +
                ", paperCreator='" + paperCreator + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByValue() {
        return updatedByValue;
    }

    public void setUpdatedByValue(String updatedByValue) {
        this.updatedByValue = updatedByValue;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyValue() {
        return companyValue;
    }

    public void setCompanyValue(String companyValue) {
        this.companyValue = companyValue;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getPaperType() {
        return paperType;
    }

    public void setPaperType(Long paperType) {
        this.paperType = paperType;
    }

    public String getPaperTypeValue() {
        return paperTypeValue;
    }

    public void setPaperTypeValue(String paperTypeValue) {
        this.paperTypeValue = paperTypeValue;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficultyValue() {
        return difficultyValue;
    }

    public void setDifficultyValue(String difficultyValue) {
        this.difficultyValue = difficultyValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getCombExamTime() {
        return combExamTime;
    }

    public void setCombExamTime(Date combExamTime) {
        this.combExamTime = combExamTime;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getTemplate() {
        return template;
    }

    public void setTemplate(Byte template) {
        this.template = template;
    }

    public Integer getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(Integer downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public Integer getPublishTimes() {
        return publishTimes;
    }

    public void setPublishTimes(Integer publishTimes) {
        this.publishTimes = publishTimes;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getPaperCreator() {
        return paperCreator;
    }

    public void setPaperCreator(String paperCreator) {
        this.paperCreator = paperCreator;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

}
