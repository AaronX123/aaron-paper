package aaron.paper.biz.service;

import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */
public interface CreatePaperService {
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
}
