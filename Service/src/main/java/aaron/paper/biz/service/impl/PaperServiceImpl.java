package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.CombExamConfigItemDto;
import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.common.aop.annotation.FullCommonField;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.data.exception.StarterError;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.RPCUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.api.dto.FuzzySearch;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.api.dto.PaperIdWithName;
import aaron.paper.biz.dao.PaperDao;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.manager.baseinfo.BaseInfoApi;
import aaron.paper.manager.user.UserInfoApi;
import aaron.paper.pojo.dto.*;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import aaron.paper.pojo.vo.PaperVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    UserInfoApi userApi;

    @Autowired
    SnowFlake snowFlake;

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
        // 从基础数据获取题目
        CommonResponse response = baseInfoApi.getSubjectAndAnswer(new CommonRequest<>(state.getVersion(), TokenUtils.getToken(),paperDTO.getConfigId()));
        SubjectPackage subjectPackage = RPCUtils.parseResponse(response,SubjectPackage.class,RPCUtils.BASEINFO);
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
        List<CombExamConfigItemDto> dtoList = CommonUtils.convertList(configVo.getCombExamConfigItemVOs(),CombExamConfigItemDto.class);
        // 从基础数据获取题目
        CommonResponse response = baseInfoApi.getSubjectAndAnswerCustomized(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),dtoList));
        SubjectPackage subjectPackage = RPCUtils.parseResponse(response,SubjectPackage.class,RPCUtils.BASEINFO);
        Map<SubjectDto,List<SubjectAnswerDto>> map = baseService.parseSubjectPackage(subjectPackage);
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
        return baseService.downLoad(paperDTO,true);
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
     * @param isTemplate 是否是查询模板
     * @return 返回满足此参数的试卷列表
     */
    @Override
    public Map<String, Object> queryPaper(PaperQueryDto paperQueryDTO, boolean isTemplate) {
        UserPermission userPermission = TokenUtils.getUser();
        Page<Paper> page = new Page<>(paperQueryDTO.getCurrentPage(),paperQueryDTO.getPageSize());
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        if (isTemplate){
            wrapper.eq(Paper.TEMPLATE,1);
        }else {
            wrapper.eq(Paper.TEMPLATE,0);
            if (userPermission.getCompanyId() != null){
                wrapper.eq(Paper.COMPANY_ID,userPermission.getCompanyId());
            }else {
                wrapper.eq(Paper.ORG_ID,TokenUtils.getUser().getOrgId());
            }
        }
        if (paperQueryDTO.getDifficulty() != null){
            wrapper.eq(Paper.DIFFICULTY,paperQueryDTO.getDifficulty());
        }
        if (StringUtils.isNotBlank(paperQueryDTO.getCreatedBy())){
            wrapper.likeRight(Paper.PAPER_CREATOR,paperQueryDTO.getCreatedBy());
        }
        if (paperQueryDTO.getStart() != null && paperQueryDTO.getEnd() != null){
            wrapper.between(Paper.COMB_EXAM_TIME,paperQueryDTO.getStart(),paperQueryDTO.getEnd());
        }
        if (StringUtils.isNotBlank(paperQueryDTO.getName())){
            wrapper.likeRight(Paper.NAME,paperQueryDTO.getName());
        }
        wrapper.orderByDesc(Paper.UPDATE_TIME);
        page = baseMapper.selectPage(page,wrapper);
        List<Paper> paperList = page.getRecords();
        return convertId(paperList,page.getTotal());
    }

    private Map<String,Object> convertId(List<Paper> paperList,long total){
        Map<String,Object> map = new HashMap<>(2);
        List<PaperVo> paperVoList = new ArrayList<>(paperList.size());
        for (Paper paper : paperList) {
            PaperVo vo = CommonUtils.copyProperties(paper,PaperVo.class);
            vo.setCompanyValue(baseService.getUserInfoCache(vo.getCompanyId(),BaseService.COMPANY));
            vo.setUpdatedByValue(baseService.getUserInfoCache(vo.getUpdatedBy(),BaseService.USER));
            vo.setPaperTypeValue(baseService.getBaseInfoCache(vo.getPaperType(),BaseService.DICTIONARY));
            vo.setDifficultyValue(baseService.getBaseInfoCache(vo.getDifficulty(),BaseService.DICTIONARY));
            paperVoList.add(vo);
        }
        map.put("paperVO",paperVoList);
        map.put("total",total);
        return map;
    }

    /**
     * 准备要删除的试卷数据
     *
     * @param paperIds 试卷id数组
     * @return 删除成功的条数
     */
    @Override
    public boolean paperDelete(Long[] paperIds) {
        List<Long> delList = Arrays.asList(paperIds);
        List<Paper> deletedPaperList = listByIds(delList);
        // 已经发布的试卷不能删除
        deletedPaperList.forEach(paper -> {if (paper.getPublishTimes() != 0){
            throw new PaperException(PaperError.PAPER_PUBLISHED_CANT_DELETE);
            }});
        List<PaperSubject> paperSubjectList = paperSubjectService.listSubjectByPaperIdList(delList);
        List<Long> delSubjectIdList = paperSubjectList.stream().map(PaperSubject::getId).collect(Collectors.toList());
        PaperServiceImpl service = (PaperServiceImpl) AopContext.currentProxy();
        return service.deletePaper(delList,delSubjectIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deletePaper(List<Long> paperIdList, List<Long> subjectIdList){
        try {
            return paperSubjectAnswerService.deleteBySubjectId(subjectIdList) &&
                    paperSubjectService.removeByIds(subjectIdList) &&
                    removeByIds(paperIdList);
        }catch (Exception e){
            throw new PaperException(PaperError.PAPER_DELETE_FAILURE);
        }
    }
    /**
     * 准备修改的资源
     *
     * @param paperDto
     * @return
     */
    @Override
    public boolean paperModify(ModifyPaperDto paperDto) {
        PaperServiceImpl paperService = (PaperServiceImpl) AopContext.currentProxy();
        Paper paper = handlePaper(paperDto);
        // 处理已经删除的试题
        List<Long> deletedIdList = paperDto.getDeletedId();
        double deletedScore = 0;
        boolean hasDelete = false;
        // 获取删除的题目分值
        if (deletedIdList != null && deletedIdList.size() != 0){
            hasDelete = true;
            List<PaperSubject> deletedPaperSubject = paperSubjectService.listByIds(deletedIdList);
            if (!CommonUtils.isEmpty(deletedPaperSubject)){
                for (PaperSubject paperSubject : deletedPaperSubject) {
                    deletedScore += paperSubject.getScore();
                }
            }
        }

        // 处理新添加的试题
        List<ModifyPaperSubjectDto> modifyPaperDtoList = paperDto.getCurrentPaperSubjectDtoList();
        if (!CommonUtils.isEmpty(modifyPaperDtoList)){
            // 获取mark不为9999的试题Id,9999是存在的试题
            List<Long> addedSubjectIdList = modifyPaperDtoList.stream().filter(s ->
                s.getMark() == null || 9999 != (s.getMark())
            ).map(ModifyPaperSubjectDto::getId).collect(Collectors.toList());
            double addedScore = 0;
            if (!CommonUtils.isEmpty(addedSubjectIdList)){
                // 从基础数据服务中获取新添加的试题并进行拷贝
                CommonResponse response = baseInfoApi.getSubjectById(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),addedSubjectIdList));
                SubjectPackage subjectPackage = RPCUtils.parseResponse(response,SubjectPackage.class,RPCUtils.BASEINFO);
                Map<SubjectDto, List<SubjectAnswerDto>> newSubjectMap = baseService.parseSubjectPackage(subjectPackage);
                // 拷贝试题
                List<PaperSubject> addedSubject = new ArrayList<>(8);
                List<PaperSubjectAnswer> addedSubjectAnswer = new ArrayList<>(32);
                for (Map.Entry<SubjectDto, List<SubjectAnswerDto>> entry : newSubjectMap.entrySet()) {
                    if (CommonUtils.notNull(entry,entry.getKey(),entry.getValue())){
                        SubjectDto subjectDto = entry.getKey();
                        PaperSubject subject = new PaperSubject();
                        subject.setId(snowFlake.nextId());
                        subject.setPaperId(paper.getId());
                        subject.setCategoryId(subjectDto.getCategoryId());
                        subject.setDifficulty(subjectDto.getDifficulty());
                        subject.setSubjectTypeId(subjectDto.getSubjectTypeId());
                        subject.setSubject(subjectDto.getName());
                        // 题库里不保存试题分值 todo 增加设置题目分数的位置
                        subject.setScore((double) 5);
                        for (SubjectAnswerDto answerDto : entry.getValue()) {
                            PaperSubjectAnswer answer = new PaperSubjectAnswer();
                            answer.setId(snowFlake.nextId());
                            answer.setPaperSubjectId(subject.getId());
                            answer.setAnswer(answerDto.getAnswer());
                            answer.setRightAnswer(answerDto.getRightAnswer());
                            addedSubjectAnswer.add(answer);
                        }
                        addedSubject.add(subject);
                        addedScore += subject.getScore();
                    }
                }
                paper.setScore(paper.getScore() - deletedScore + addedScore);
                return paperService.modifyPaper(paper,deletedIdList,addedSubject,addedSubjectAnswer,hasDelete,true);
            }
            return paperService.modifyPaper(paper,deletedIdList,null,null,hasDelete,false);
        }
        return paperService.modifyPaper(paper,deletedIdList,null,null,hasDelete,false);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean modifyPaper(Paper paper, List<Long> deletedIdList, List<PaperSubject> addedSubject,
                               List<PaperSubjectAnswer> addedAnswer, boolean hasDel, boolean hasNew){
        if (hasDel && hasNew){
            boolean f1 = paperSubjectAnswerService.deleteBySubjectId(deletedIdList);
            boolean f2 = paperSubjectService.removeByIds(deletedIdList);
            boolean f3 = updateById(paper);
            boolean f4 = paperSubjectService.saveBatch(addedSubject);
            boolean f5 = paperSubjectAnswerService.saveBatch(addedAnswer);
            return f1 && f2 && f3 && f4 && f5;
        }else if (hasDel){
            boolean f1 = paperSubjectAnswerService.deleteBySubjectId(deletedIdList);
            boolean f2 = paperSubjectService.removeByIds(deletedIdList);
            boolean f3 = updateById(paper);
            return f1 && f2 && f3;
        }else if (hasNew){
            boolean f1 = updateById(paper);
            boolean f2 = paperSubjectService.saveBatch(addedSubject);
            boolean f3 = paperSubjectAnswerService.saveBatch(addedAnswer);
            return f1 && f2 && f3;
        }else {
            return updateById(paper);
        }
    }

    private Paper handlePaper(ModifyPaperDto paperDto){
        UserPermission user = TokenUtils.getUser();
        Paper realPaper = getById(paperDto.getId());
        Paper newPaper = CommonUtils.copyProperties(realPaper,Paper.class);
        newPaper.setUpdatedTime(new Date());
        newPaper.setUpdatedBy(user.getId());
        newPaper.setDescription(paperDto.getDescription());
        newPaper.setName(paperDto.getName());
        newPaper.setDifficulty(paperDto.getDifficulty());
        newPaper.setPaperType(paperDto.getCategory());
        return newPaper;
    }
    /**
     * 获取试卷详情
     *
     * @param id
     * @return
     */
    @Override
    public PaperDetail getPaperInfo(long id) {
        // 获取试卷
        Paper paper = getById(id);
        if (paper == null){
            throw new PaperException(PaperError.PAPER_NOT_EXIST);
        }
        List<PaperSubject> subjectList = paperSubjectService.listSubjectByPaperId(id);
        if (CommonUtils.isEmpty(subjectList)){
            throw new PaperException(PaperError.PAPER_SUBJECT_IS_NULL);
        }
        // 根据题目查询答案
        List<Long> subjectIdList = subjectList.stream().map(PaperSubject::getId).collect(Collectors.toList());
        List<PaperSubjectAnswer> answerList = paperSubjectAnswerService.listAnswerBySubjectIdList(subjectIdList);
        if (CommonUtils.isEmpty(answerList)){
            throw new PaperException(PaperError.PAPER_ANSWER_IS_EMPTY);
        }
        List<aaron.paper.api.dto.PaperSubject> subjectDtoList = new ArrayList<>(subjectIdList.size());
        // 类型转换
        subjectList.forEach(subject -> {
            aaron.paper.api.dto.PaperSubject subjectDto = CommonUtils.copyProperties(subject, aaron.paper.api.dto.PaperSubject.class);
            List<PaperSubjectAnswer> subjectAnswers = answerList.stream()
                    .filter(ans -> ans.getPaperSubjectId().equals(subject.getId()))
                    .collect(Collectors.toList());
            List<aaron.paper.api.dto.PaperSubjectAnswer> ansList = CommonUtils.convertList(subjectAnswers, aaron.paper.api.dto.PaperSubjectAnswer.class);
            subjectDto.setSubjectAnswerVoList(ansList);
            // 判断是否为客观题
            subjectDto.setObjectiveSubject(checkObjectiveSubject(subjectDto.getSubjectTypeId()));
            subjectDto.setMark(9999);
            subjectDtoList.add(subjectDto);
        });
        PaperDetail detail = CommonUtils.copyProperties(paper,PaperDetail.class);
        // 这里实际上不是category,category是C++题，而不是试卷类型
        detail.setCategory(paper.getPaperType());
        detail.setCurrentPaperSubjectVOList(subjectDtoList);
        return detail;
    }

    /**
     * 判断是否为客观题
     * @param id
     * @return
     */
    private Boolean checkObjectiveSubject (Long id){
        Long objectiveSubject1 = 616293886733721600L;
        Long objectiveSubject2 = 616293908820926464L;
        Long objectiveSubject3 = 616954547683860480L;
        return (objectiveSubject1.equals(id) || objectiveSubject2.equals(id) || objectiveSubject3.equals(id));
    }

    /**
     * download
     *
     * @param paper the ids of which are downloaded
     * @return the count of downloaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @FullCommonField
    @Override
    public boolean downloadTemplate(PaperDto paper) {
        Paper template = getById(paper.getPreId());
        template.setDownloadTimes(template.getDownloadTimes() + 1);
        Paper newPaper = baseService.setProperties(template,paper,false);
        return baseService.copySubject(template,newPaper,false);
    }

    /**
     * upload
     *
     * @param paper the id of uploaded
     * @return the count of uploaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean uploadTemplate(PaperDto paper) {
        Paper old = getById(paper.getId());
        if (old == null){
            throw new PaperException(PaperError.PAPER_NOT_EXIST);
        }
        Paper newPaper = baseService.setProperties(old,paper,true);
        return baseService.copySubject(old,newPaper,true);
    }

    /**
     * remove batch of templates
     *
     * @param paperTemplateIds the paper id of a template
     * @return the count of removed paper
     */
    @Override
    public boolean deleteTemplate(Long[] paperTemplateIds) {
        List<Paper> templates = listByIds(Arrays.asList(paperTemplateIds));
        // 检查参数是否合法
        templates.forEach(p -> {
            if (p.getTemplate() != 1) {throw new PaperException(StarterError.SYSTEM_PARAMETER_VALUE_INVALID,p.getId(),"不是模板");}
        });
        List<PaperSubject> subjectList = paperSubjectService.listSubjectByPaperIdList(Arrays.asList(paperTemplateIds));
        List<Long> subjectIdList = subjectList.stream().map(PaperSubject::getId).collect(Collectors.toList());
        if (CommonUtils.isEmpty(subjectList)){
            return false;
        }
        PaperServiceImpl service = (PaperServiceImpl) AopContext.currentProxy();
        return service.deleteTemplate(Arrays.asList(paperTemplateIds),subjectIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemplate(List<Long> paperIdList, List<Long> subjectIdList){
        return paperSubjectAnswerService.deleteBySubjectId(subjectIdList) && paperSubjectService.removeByIds(subjectIdList) && removeByIds(paperIdList);
    }

    /**
     * 查询模板
     *
     * @param paperQueryDTO
     * @return
     */
    @Override
    public Map<String, Object> queryTemplate(PaperQueryDto paperQueryDTO) {
        return queryPaper(paperQueryDTO,true);
    }

    /**
     * 发布试卷，也就是将试卷中publishedTime加一
     *
     * @param id
     * @return
     */
    @Override
    public boolean publish(long id) {
        Paper paper = getById(id);
        if (paper == null){
            throw new PaperException(PaperError.PAPER_NOT_EXIST);
        }
        paper.setPublishTimes(paper.getPublishTimes() +  1);
        return updateById(paper);
    }

    /**
     * 列出该公司的所有试卷
     *
     * @param companyId
     * @return
     */
    @Override
    public List<PaperIdWithName> list(long companyId) {
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        wrapper.select(Paper.ID,Paper.NAME);
        wrapper.eq(Paper.COMPANY_ID,companyId);
        List<Paper> paperList = list(wrapper);
        return paperList.stream().map(p ->
            new PaperIdWithName(p.getId(),p.getName())
        ).collect(Collectors.toList());
    }

    /**
     * 通过试卷名模糊搜索
     *
     * @param fuzzySearch
     * @return
     */
    @Override
    public List<PaperIdWithName> listByName(FuzzySearch fuzzySearch) {
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        wrapper.select(Paper.ID,Paper.NAME);
        if (fuzzySearch.getCompanyId() != null){
            wrapper.eq(Paper.COMPANY_ID,fuzzySearch.getCompanyId());
        }
        if (fuzzySearch.getPaperName() != null){
            wrapper.likeRight(Paper.NAME,fuzzySearch.getPaperName());
        }
        List<Paper> paperList = list(wrapper);
        return paperList.stream().map(p ->
                new PaperIdWithName(p.getId(),p.getName())
        ).collect(Collectors.toList());
    }

    /**
     * 获取试卷
     *
     * @param id
     */
    @Override
    public Paper getPaper(long id) {
        Paper paper = getById(id);
        if (paper == null){
            throw new PaperException(PaperError.PAPER_NOT_EXIST);
        }
        return paper;
    }
}
