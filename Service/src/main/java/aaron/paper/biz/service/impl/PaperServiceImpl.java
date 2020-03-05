package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.CombExamConfigItemDto;
import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.common.aop.annotation.FullCommonField;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.TokenUtils;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.dao.PaperDao;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.manager.baseinfo.BaseInfoApi;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class PaperServiceImpl extends ServiceImpl<PaperDao, Paper> implements PaperService {
    @Autowired
    BaseInfoApi baseInfoApi;

    @Autowired
    CommonState state;

    @Autowired
    BaseService baseService;

    @Autowired
    PaperSubjectService paperSubjectService;

    @Autowired
    PaperSubjectAnswerService paperSubjectAnswerService;


    @Override
    public void test(){
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        wrapper.eq(Paper.COMPANY_ID,"624197208534945792");
        Paper paper = baseMapper.selectOne(wrapper);
    }

    /**
     * 快速组卷
     *
     * @param paperDTO
     * @return 成功返回 <code>true</code> 否则 <code>false</code>
     */
    @FullCommonField
    @Override
    public boolean generateFastMode(PaperDto paperDTO) {
        paperDTO.setCombExamTime(paperDTO.getUpdatedTime());
        SubjectPackage subjectPackage = baseInfoApi.getSubjectAndAnswer(new CommonRequest<>(state.getVersion(), TokenUtils.getToken(),paperDTO.getConfigId())).getData();
        Map<SubjectDto,List<SubjectAnswerDto>> map = baseService.parseSubjectPackage(subjectPackage);
        return baseService.insertNewPaper(paperDTO,map);
    }

    /**
     * 标准组卷
     *
     * @param paperDTO
     * @param configVo
     * @return
     */
    @FullCommonField
    @Override
    public boolean generateNormalMode(PaperDto paperDTO, CustomizedCombExamConfigVo configVo) {
        paperDTO.setCombExamTime(paperDTO.getCreatedTime());
        paperDTO.setName(configVo.getName());
        paperDTO.setStatus(Byte.valueOf(configVo.getStatus()));
        paperDTO.setDescription(configVo.getRemark());
        paperDTO.setPaperType(configVo.getPaperType());
        paperDTO.setDifficulty(configVo.getDifficulty());
        List<CombExamConfigItemDto> dtoList = CommonUtils.convertList(configVo.getCombExamConfigItemVos(),CombExamConfigItemDto.class);
        CommonResponse<SubjectPackage> response = baseInfoApi.getSubjectAndAnswerCustomized(new CommonRequest<List<CombExamConfigItemDto>>(state.getVersion(),TokenUtils.getToken(),dtoList));
        Map<SubjectDto,List<SubjectAnswerDto>> map = baseService.parseSubjectPackage(response.getData());
        return baseService.insertNewPaper(paperDTO,map);
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

    /**
     * 插入试卷
     *
     * @param paper
     * @param subjectList
     * @param subjectAnswerList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertPaper(Paper paper, List<PaperSubject> subjectList, List<PaperSubjectAnswer> subjectAnswerList) {

        if (baseMapper.insert(paper) == 0){
            throw new PaperException(PaperError.PAPER_INSERT_FAILURE);
        }
        if (!paperSubjectService.saveBatch(subjectList)){
            throw new PaperException(PaperError.PAPER_SUBJECT_INSERT_FAILURE);
        }
        if (!paperSubjectAnswerService.saveBatch(subjectAnswerList)){
            throw new PaperException(PaperError.PAPER_SUBJECT_ANSWER_INSERT_FAILURE);
        }
        return true;
    }

    /**
     * 通过查询参数查询试卷
     *
     * @param paperQueryDTO 查询试卷参数
     * @return 返回满足此参数的试卷列表
     */
    @Override
    public Map<String, Object> queryPaper(PaperQueryDto paperQueryDTO) {
        return null;
    }

    /**
     * 准备要删除的试卷数据
     *
     * @param paperIds 试卷id数组
     * @return 删除成功的条数
     */
    @Override
    public int prepareDelete(Long[] paperIds) {
        return 0;
    }

    /**
     * 准备修改的资源
     *
     * @param paperDetail
     * @return
     */
    @Override
    public boolean prepareModify(PaperDetail paperDetail) {
        return false;
    }

    /**
     * 获取试卷详情
     *
     * @param id
     * @return
     */
    @Override
    public PaperDetail getPaperInfo(Long id) {
        return null;
    }

    /**
     * download
     *
     * @param paperDTO the ids of which are downloaded
     * @return the count of downloaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean downloadTemplate(PaperDto paperDTO) {
        return false;
    }

    /**
     * upload
     *
     * @param paperDTO the id of uploaded
     * @return the count of uploaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean uploadTemplate(PaperDto paperDTO) {
        return false;
    }

    /**
     * remove batch of templates
     *
     * @param paperTemplateIds the paper id of a template
     * @return the count of removed paper
     */
    @Override
    public int deleteTemplate(Long[] paperTemplateIds) {
        return 0;
    }

    /**
     * 对一张试卷进行深复制并且进行插入
     *
     * @param p
     * @param newPo
     * @return
     */
    @Override
    public boolean deepCopyPaper(Paper p, Paper newPo) {
        return false;
    }

    /**
     * 查询模板
     *
     * @param paperQueryDTO
     * @return
     */
    @Override
    public Map<String, Object> queryTemplate(PaperQueryDto paperQueryDTO) {
        return null;
    }



}
