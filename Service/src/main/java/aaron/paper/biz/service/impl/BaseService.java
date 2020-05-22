package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.baseinfo.api.dto.SubjectPackageDto;
import aaron.common.data.common.CacheConstants;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.RPCUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.common.constant.EnumRPCType;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.manager.baseinfo.BaseInfoApi;
import aaron.paper.manager.exam.ExamInfoApi;
import aaron.paper.manager.user.UserInfoApi;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Service
public class BaseService {
    public static final int COMPANY = 1;
    public static final int USER = 2;
    public static final int CATEGORY = 3;
    public static final int DICTIONARY = 4;
    public static final int SUBJECT_TYPE = 5;

    @Autowired
    SnowFlake snowFlake;

    @Autowired
    PaperService paperService;

    @Autowired
    PaperSubjectService paperSubjectService;

    @Autowired
    PaperSubjectAnswerService paperSubjectAnswerService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    BaseInfoApi baseInfoApi;

    @Autowired
    UserInfoApi userInfoApi;

    @Autowired
    CommonState commonState;

    @Autowired
    ExamInfoApi examInfoApi;


    public Map<SubjectDto, List<SubjectAnswerDto>> parseSubjectPackage(SubjectPackage subjectPackage){
        List<SubjectPackageDto> dtoList = subjectPackage.getDtoList();
        if (CommonUtils.isEmpty(dtoList)){
            throw new PaperException(PaperError.PAPER_SUBJECT_CANT_BE_NULL);
        }
        // linkedHashMap可以保证进入顺序一致
        Map<SubjectDto,List<SubjectAnswerDto>> map = new LinkedHashMap<>(dtoList.size());
        for (SubjectPackageDto dto : dtoList) {
            SubjectDto subjectDto = CommonUtils.copyProperties(dto.getSubjectDTO(),SubjectDto.class);
            List<SubjectAnswerDto> subjectAnswerDtoList = CommonUtils.convertList(dto.getSubjectAnswerDtoList(),SubjectAnswerDto.class);
            map.put(subjectDto,subjectAnswerDtoList);
        }
        return map;
    }

    /**
     * 插入试卷
     * @param paper 试卷属性
     * @param map 试卷里面的题目和对应答案
     * @return
     */
    public boolean insertNewPaper(PaperDto paper, Map<SubjectDto,List<SubjectAnswerDto>> map){
        if (map == null){
            throw new PaperException(PaperError.PAPER_CANT_BE_NULL);
        }
        Paper paperModel = CommonUtils.copyProperties(paper,Paper.class);
        List<PaperSubject> paperSubjectList = new ArrayList<>(64);
        List<PaperSubjectAnswer> paperSubjectAnswerList = new ArrayList<>(256);
        map.forEach((subject,subjectAnswer) -> {
            // 类型转换下
            PaperSubject paperSubject = CommonUtils.copyProperties(subject,PaperSubject.class);
            // 副本设置新Id
            paperSubject.setId(snowFlake.nextId());
            // 设置试卷Id
            paperSubject.setPaperId(paper.getId());
            // 设置下题目
            paperSubject.setSubject(subject.getName());
            paperSubjectList.add(paperSubject);
            // 处理试题答案
            subjectAnswer.forEach((answer -> {
                PaperSubjectAnswer ans = CommonUtils.copyProperties(answer,PaperSubjectAnswer.class);
                ans.setId(snowFlake.nextId());
                ans.setPaperSubjectId(paperSubject.getId());
                paperSubjectAnswerList.add(ans);
            }));
        });
        // 计算试卷总分
        paperModel.setScore(countScore(paperSubjectList));
        // 插入
        return paperService.insertPaper(paperModel,paperSubjectList,paperSubjectAnswerList);
    }

    /**
     * 下载试卷，模板组卷和它类似
     * @param paperDto
     * @param isDownload
     * @return
     */
    public boolean downLoad(PaperDto paperDto, boolean isDownload){
        Paper oldPaper = paperService.getBaseMapper().selectById(paperDto.getId());
        if (oldPaper == null){
            throw new PaperException(PaperError.PAPER_NOT_EXIST);
        }
        Paper newPaper = setProperties(oldPaper,paperDto,false);
        // 模板的下载次数加一
        oldPaper.setDownloadTimes(isDownload ? oldPaper.getDownloadTimes() + 1 : oldPaper.getDownloadTimes());
        return copySubject(oldPaper,newPaper,false);
    }

    /**
     * 拷贝一份试卷
     * @param old
     * @param newPaper
     * @param isUpload
     * @return
     */
    public boolean copySubject(Paper old, Paper newPaper, boolean isUpload){
        // 查询原试卷的题目
        List<PaperSubject> subjectList = paperSubjectService.listSubjectByPaperId(old.getId());
        if (subjectList == null){
            throw new PaperException(PaperError.PAPER_SUBJECT_IS_NULL);
        }
        // 获取试题id，然后去查询这些id对应的答案
        List<Long> subjectIdList = subjectList.stream().map(PaperSubject::getId).collect(Collectors.toList());
        List<PaperSubjectAnswer> paperSubjectAnswerList = paperSubjectAnswerService.listAnswerBySubjectIdList(subjectIdList);
        // 复制答案
        // 创建复制的答案集合
        List<PaperSubjectAnswer> copiedAnswerList = new ArrayList<>(paperSubjectAnswerList.size());
        List<PaperSubject> copiedSubjectList = new ArrayList<>(subjectList.size());
        for (PaperSubject subject : subjectList) {
            PaperSubject copiedSubject = CommonUtils.copyProperties(subject,PaperSubject.class);
            copiedSubject.setPaperId(newPaper.getId());
            copiedSubject.setId(snowFlake.nextId());
            for (PaperSubjectAnswer answer : paperSubjectAnswerList) {
                if (answer.getPaperSubjectId().equals(subject.getId())){
                    PaperSubjectAnswer copiedAns = CommonUtils.copyProperties(answer,PaperSubjectAnswer.class);
                    copiedAns.setPaperSubjectId(copiedSubject.getId());
                    copiedAns.setId(snowFlake.nextId());
                    copiedAnswerList.add(copiedAns);
                }
            }
            copiedSubjectList.add(copiedSubject);
        }
        BaseService baseService = (BaseService) AopContext.currentProxy();
        return baseService.insertPaper(old,newPaper,copiedSubjectList,copiedAnswerList,isUpload);
    }

    /**
     * 插入试卷
     * @param old
     * @param newPaper
     * @param subjectList
     * @param answerList
     * @param isUpload 上传模板时为true
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertPaper(Paper old, Paper newPaper, List<PaperSubject> subjectList, List<PaperSubjectAnswer> answerList, boolean isUpload){
        try {
            if (isUpload){
                return paperService.save(newPaper) && paperSubjectService.saveBatch(subjectList)
                        && paperSubjectAnswerService.saveBatch(answerList);
            }else {
                // 不是上传需要修改原试卷状态，下载次数
                return paperService.updateById(old) && paperService.save(newPaper)
                        && paperSubjectService.saveBatch(subjectList)
                        && paperSubjectAnswerService.saveBatch(answerList);
            }
        }catch (Exception e){
            if (e instanceof DuplicateKeyException){
                throw new PaperException(PaperError.PAPER_REPEATED_PAPER);
            }else {
                throw new PaperException(PaperError.PAPER_INSERT_FAILURE);
            }
        }

    }

    public Paper setProperties(Paper old,PaperDto paperDto, boolean isTemplate){
        UserPermission userPermission = TokenUtils.getUser();
        Paper newPaper = CommonUtils.copyProperties(old,Paper.class);
        newPaper.setId(snowFlake.nextId());
        newPaper.setName(paperDto.getName());
        newPaper.setDifficulty(paperDto.getDifficulty());
        newPaper.setDescription(paperDto.getDescription());
        newPaper.setCompanyId(userPermission.getCompanyId());
        newPaper.setUpdatedBy(userPermission.getId());
        newPaper.setPaperCreator(userPermission.getUserName());
        newPaper.setPublishTimes(0);
        newPaper.setCreatedTime(new Date());
        newPaper.setCreatedBy(userPermission.getId());
        newPaper.setVersion(newPaper.getCreatedTime().getTime());
        newPaper.setCombExamTime(newPaper.getCreatedTime());
        newPaper.setUpdatedTime(newPaper.getCreatedTime());
        newPaper.setPaperType(paperDto.getPaperType());
        newPaper.setStatus((byte) 1);
        newPaper.setTemplate((byte) (isTemplate ? 1 : 0));
        return newPaper;
    }

    private double countScore(List<PaperSubject> list){
        double res = 0;
        for (PaperSubject subject : list) {
            res += subject.getScore();
        }
        return res;
    }

    /**
     * 清除缓存的试卷详情
     * 从redis中获取值Cache不可能为空因为allowInFlightCacheCreation默认为true会在不存在cache时主动创建
     * @param id
     * @return
     */
    @SuppressWarnings("all")
    @Nullable
    public String getBaseInfoCache(long id, int type){
        if (type == CATEGORY){
            Cache cache = cacheManager.getCache(CacheConstants.CATEGORY_VAL);
            Cache.ValueWrapper valueWrapper = cache.get(id);
            if (valueWrapper == null){
                CommonResponse response = baseInfoApi.getCategory(id);
                String value = RPCUtils.parseResponse(response,String.class,RPCUtils.BASEINFO);
                cache.put(id,value);
                return value;
            }
            return (String) valueWrapper.get();
        }
        if (type == DICTIONARY){
            Cache cache = cacheManager.getCache(CacheConstants.DICTIONARY_VAL);
            Cache.ValueWrapper wrapper = cache.get(id);
            if (wrapper == null){
                CommonResponse response = baseInfoApi.getBaseData(new CommonRequest<>(commonState.getVersion(),TokenUtils.getToken(),id));
                String val = RPCUtils.parseResponse(response,String.class,RPCUtils.BASEINFO);
                cache.put(id,val);
                return val;
            }
            return (String) wrapper.get();
        }
        if (type == SUBJECT_TYPE){
            Cache cache = cacheManager.getCache(CacheConstants.SUBJECT_TYPE_VAL);
            Cache.ValueWrapper wrapper = cache.get(id);
            if (wrapper == null){
                CommonResponse response = baseInfoApi.getSubjectType(id);
                String val = RPCUtils.parseResponse(response,String.class,RPCUtils.BASEINFO);
                cache.put(id,val);
                return val;
            }
            return (String) wrapper.get();
        }
        return null;
    }

    @Nullable
    public String getUserInfoCache(long id, int type){
        if (type == COMPANY){
            Cache companyCache = cacheManager.getCache(CacheConstants.COMPANY_VAL);
            Cache.ValueWrapper wrapper = companyCache.get(id);
            if (wrapper != null){
                return (String) wrapper.get();
            }else {
                CommonResponse response = userInfoApi.getCompanyById(new CommonRequest<>(commonState.getVersion(),TokenUtils.getToken(),id));
                String val = RPCUtils.parseResponse(response,String.class, RPCUtils.USER);
                companyCache.put(id,val);
                return val;
            }
        }
        if (type == USER){
            Cache userCache = cacheManager.getCache(CacheConstants.USER_VAL);
            Cache.ValueWrapper wrapper = userCache.get(id);
            if (wrapper != null){
                return (String) wrapper.get();
            }else {
                CommonResponse response = userInfoApi.getUserNameById(new CommonRequest<>(commonState.getVersion(),TokenUtils.getToken(),id));
                String val = RPCUtils.parseResponse(response,String.class,RPCUtils.USER);
                userCache.put(id,val);
                return val;
            }
        }
        return null;
    }

    /**
     * 清除缓存的试卷详情
     * 从redis中获取值Cache不可能为空因为allowInFlightCacheCreation默认为true会在不存在cache时主动创建
     * @param id
     * @return
     */
    @SuppressWarnings("all")
    public void evictPaper(Long[] paperIdList){
        for (Long id : paperIdList) {
            Cache cache = cacheManager.getCache(CacheConstants.PAPER_DETAIL);
            cache.evict(id);
        }
    }
    @SuppressWarnings("all")
    public void evictPaper(long id){
        Cache cache = cacheManager.getCache(CacheConstants.PAPER_DETAIL);
        cache.evict(id);
    }

    /**
     * 判断是否已经发布过试卷，发布过则不能变更
     * @param id
     */
    public void published(Long id){
        CommonResponse response = examInfoApi.checkEditable(id);
        if (!RPCUtils.parseResponse(response,Boolean.class,RPCUtils.EXAM)){
            throw new PaperException(PaperError.PAPER_PUBLISHED_CANT_DELETE);
        }
    }
}
