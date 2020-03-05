package aaron.paper.biz.service;

import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.dao.PaperDao;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public interface PaperService extends IService<Paper> {
    void test();


    /**
     * 快速组卷
     * @param paperDTO
     * @param paperDTO
     * @return 成功返回 <code>true</code> 否则 <code>false</code>
     */
    boolean generateFastMode(PaperDto paperDTO);

    /**
     * 标准组卷
     * @param paperDTO
     * @param combExamConfigDTO
     * @return
     */
    boolean generateNormalMode(PaperDto paperDTO, CustomizedCombExamConfigVo combExamConfigDTO);

    /**
     * 模版组卷
     * @param paperDTO
     * @return
     */
    boolean generateTemplateMode(PaperDto paperDTO);
    /**
     * 插入试卷流程
     * @param paperDTO
     * @param subjectMap
     * @return
     */
    boolean insertNewPaper(PaperDto paperDTO, Map<SubjectDto, SubjectAnswerDto[]> subjectMap);


    /**
     * 通过查询参数查询试卷
     * @param paperQueryDTO 查询试卷参数
     * @return 返回满足此参数的试卷列表
     *
     */
    Map<String,Object> queryPaper(PaperQueryDto paperQueryDTO);

    /**
     * 准备要删除的试卷数据
     * @param paperIds 试卷id数组
     * @return 删除成功的条数
     */
    int prepareDelete(Long[] paperIds);

    /**
     * 准备修改的资源
     * @param paperDetail
     * @return
     */
    boolean prepareModify(PaperDetail paperDetail);

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    PaperDetail getPaperInfo(Long id);

    /**
     * download
     * @param paperDTO the ids of which are downloaded
     * @return the count of downloaded
     * @exception Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    boolean downloadTemplate(PaperDto paperDTO);

    /**
     * upload
     * @param paperDTO the id of uploaded
     * @return the count of uploaded
     * @exception Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    boolean uploadTemplate(PaperDto paperDTO);

    /**
     * remove batch of templates
     * @param paperTemplateIds the paper id of a template
     * @return the count of removed paper
     */
    int deleteTemplate(Long[] paperTemplateIds);


    /**
     * 对一张试卷进行深复制并且进行插入
     * @param p
     * @param newPo
     * @return
     */
    boolean deepCopyPaper(Paper p, Paper newPo);

    /**
     * 查询模板
     * @param paperQueryDTO
     * @return
     */
    Map<String,Object> queryTemplate(PaperQueryDto paperQueryDTO);
}
