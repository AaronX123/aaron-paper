package aaron.paper.biz.service.impl;

import aaron.paper.biz.service.CreatePaperService;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class CreatePaperServiceImpl implements CreatePaperService {
    /**
     * 快速组卷
     *
     * @param paperDTO
     * @return 成功返回 <code>true</code> 否则 <code>false</code>
     */
    @Override
    public boolean generateFastMode(PaperDto paperDTO) {
        return false;
    }

    /**
     * 标准组卷
     *
     * @param paperDTO
     * @param combExamConfigDTO
     * @return
     */
    @Override
    public boolean generateNormalMode(PaperDto paperDTO, CustomizedCombExamConfigVo combExamConfigDTO) {
        return false;
    }

    /**
     * 模版组卷
     *
     * @param paperDTO
     * @return
     */
    @Override
    public boolean generateTemplateMode(PaperDto paperDTO) {
        return false;
    }

    /**
     * 插入试卷流程
     *
     * @param paperDTO
     * @param subjectMap
     * @return
     */
    @Override
    public boolean insertNewPaper(PaperDto paperDTO, Map<SubjectDto, SubjectAnswerDto[]> subjectMap) {
        return false;
    }
}
