package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.BaseDataDto;
import aaron.baseinfo.api.dto.CombExamConfigItemDto;
import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.common.aop.annotation.FullCommonField;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.dao.PaperDao;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.common.constant.EnumRPCType;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.manager.baseinfo.BaseInfoApi;
import aaron.paper.pojo.dto.*;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import aaron.paper.pojo.vo.PaperVo;
import aaron.user.api.api.UserApi;
import aaron.user.api.dto.UserInfoDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    UserApi userApi;

    @Autowired
    SnowFlake snowFlake;

    @Override
    public int func(){
        return baseMapper.func();
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
        // 从基础数据获取题目
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
        // 从基础数据获取题目
        CommonResponse<SubjectPackage> response = baseInfoApi.getSubjectAndAnswerCustomized(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),dtoList));
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
            }
        }
        if (paperQueryDTO.getDifficulty() != null){
            wrapper.eq(Paper.DIFFICULTY,paperQueryDTO.getDifficulty());
        }
        wrapper.likeRight(Paper.PAPER_CREATOR,paperQueryDTO.getCreatedBy());
        wrapper.between(Paper.COMB_EXAM_TIME,paperQueryDTO.getStart(),paperQueryDTO.getEnd());
        wrapper.likeRight(Paper.NAME,paperQueryDTO.getName());
        wrapper.orderByDesc(Paper.UPDATE_TIME);
        page = baseMapper.selectPage(page,wrapper);
        List<Paper> paperList = page.getRecords();
        return convertId(paperList,page.getTotal());
    }

    private Map<String,Object> convertId(List<Paper> paperList,long total){
        /**
         * 缓存单次请求中的RPC请求数据,Long型是因为系统多个微服务中的ID必然不同。设计这个Cache是为了
         * 减少多次重复的请求，加快速度，设计成局部变量是因为如果是全局那么在其他服务修改后会失效，
         * 为了减去每次校验是否为过期数据的性能消耗。另外由于服务器内存只有1GB，为了节约空间，不让
         * 变量长时间驻留内存中。
         */
        Map<Long,String> cache = new HashMap<>(8);
        Map<String,Object> map = new HashMap<>(2);
        List<PaperVo> paperVoList = new ArrayList<>(paperList.size());
        for (Paper paper : paperList) {
            PaperVo vo = CommonUtils.copyProperties(paper,PaperVo.class);
            boolean flag = false;
            if (flag = cache.containsKey(paper.getCompanyId())){
                vo.setCompanyValue(cache.get(paper.getCompanyId()));
            }
            if (flag &= cache.containsKey(paper.getUpdatedBy())){
                vo.setUpdatedByValue(cache.get(paper.getUpdatedBy()));
            }
            if (!flag){
                List<Long> list = new ArrayList<>(2);
                list.add(paper.getCompanyId());
                list.add(paper.getUpdatedBy());
                UserInfoDto userInfoDto = userApi.getUserInfo(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),list)).getData();
                cache.put(paper.getCompanyId(),userInfoDto.getNameMap().get(paper.getCompanyId()));
                cache.put(paper.getUpdatedBy(),userInfoDto.getNameMap().get(paper.getUpdatedBy()));
                vo.setCompanyValue(userInfoDto.getNameMap().get(paper.getCompanyId()));
                vo.setCompanyValue(userInfoDto.getNameMap().get(paper.getUpdatedBy()));
            }
            if (flag = cache.containsKey(paper.getPaperType())){
                vo.setPaperTypeValue(cache.get(paper.getPaperType()));
            }
            if (flag &= cache.containsKey(paper.getDifficulty())){
                vo.setDifficultyValue(cache.get(paper.getDifficulty()));
            }
            if (!flag){
                BaseDataDto dataDto = new BaseDataDto();
                Map<Long,String> baseDataMap = new HashMap<>(2);
                baseDataMap.put(paper.getPaperType()," ");
                baseDataMap.put(paper.getDifficulty()," ");
                dataDto.setBaseInfoMap(baseDataMap);
                BaseDataDto baseDataDto = baseInfoApi.getBaseDataS(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),dataDto)).getData();
                cache.put(paper.getPaperType(),baseDataDto.getBaseInfoMap().get(paper.getPaperType()));
                cache.put(paper.getDifficulty(),baseDataDto.getBaseInfoMap().get(paper.getDifficulty()));
                vo.setPaperTypeValue(baseDataDto.getBaseInfoMap().get(paper.getPaperType()));
                vo.setDifficultyValue(baseDataDto.getBaseInfoMap().get(paper.getDifficulty()));
            }
        }
        map.put("PaperVO",paperVoList);
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
                deletedScore = deletedPaperSubject.stream().map(PaperSubject::getScore).count();
            }
        }

        // 处理新添加的试题
        List<ModifyPaperSubjectDto> modifyPaperDtoList = paperDto.getCurrentPaperSubjectDtoList();
        if (!CommonUtils.isEmpty(modifyPaperDtoList)){
            // 获取mark不为9999的试题Id,9999是存在的试题
            List<Long> addedSubjectIdList = modifyPaperDtoList.stream().filter(s-> 9999 != (s.getMark()))
                    .map(ModifyPaperSubjectDto::getId).collect(Collectors.toList());
            double addedScore = 0;
            if (!CommonUtils.isEmpty(addedSubjectIdList)){
                // 从基础数据服务中获取新添加的试题并进行拷贝
                CommonResponse<SubjectPackage> response = baseInfoApi.getSubjectById(new CommonRequest<>(state.getVersion(),TokenUtils.getToken(),addedSubjectIdList));
                Map<SubjectDto, List<SubjectAnswerDto>> newSubjectMap = baseService.parseSubjectPackage(response.getData());
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
            return paperSubjectAnswerService.deleteBySubjectId(deletedIdList) &&
                    paperSubjectService.removeByIds(deletedIdList) &&
                    updateById(paper) &&
                    paperSubjectService.saveBatch(addedSubject) &&
                    paperSubjectAnswerService.saveBatch(addedAnswer);
        }else if (hasDel){
            return paperSubjectAnswerService.deleteBySubjectId(deletedIdList) &&
                    paperSubjectService.removeByIds(deletedIdList) &&
                    updateById(paper);
        }else if (hasNew){
            return updateById(paper) && paperSubjectService.saveBatch(addedSubject) &&
                    paperSubjectAnswerService.saveBatch(addedAnswer);
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
    public PaperDetail getPaperInfo(Long id) {
        return null;
    }

    /**
     * download
     *
     * @param paper the ids of which are downloaded
     * @return the count of downloaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean downloadTemplate(Paper paper) {
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
