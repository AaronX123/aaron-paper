package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.baseinfo.api.dto.SubjectPackageDto;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.SnowFlake;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Service
public class BaseService {
    @Autowired
    SnowFlake snowFlake;

    @Autowired
    PaperService paperService;

    @Autowired
    PaperSubjectService paperSubjectService;

    @Autowired
    PaperSubjectAnswerService paperSubjectAnswerService;

    public Map<SubjectDto, List<SubjectAnswerDto>> parseSubjectPackage(SubjectPackage subjectPackage){
        List<SubjectPackageDto> dtoList = subjectPackage.getDtoList();
        if (CommonUtils.isEmpty(dtoList)){
            throw new PaperException(PaperError.PAPER_SUBJECT_CANT_BE_NULL);
        }
        // linkedHashMap可以保证进入顺序一致
        Map<SubjectDto,List<SubjectAnswerDto>> map = new LinkedHashMap<>(dtoList.size());
        for (SubjectPackageDto dto : dtoList) {
            SubjectDto subjectDto = CommonUtils.copyProperties(dto,SubjectDto.class);
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
        oldPaper.setDownloadTimes(isDownload ? oldPaper.getDownloadTimes() + 1 : oldPaper.getDownloadTimes());
        //todo
        return true;
    }

    /**
     * 拷贝一份试卷
     * @param old
     * @param newPaper
     * @param isUpload
     * @return
     */
    public boolean copySubject(Paper old, Paper newPaper, boolean isUpload){
        List<PaperSubject> subjectList = paperSubjectService.listSubjectByPaperId(old.getId());
        if (subjectList == null){
            throw new PaperException(PaperError.PAPER_SUBJECT_IS_NULL);
        }
        // 获取试题id，然后去查询这些id对应的答案
        List<Long> subjectIdList = subjectList.stream().map(PaperSubject::getId).collect(Collectors.toList());
        List<PaperSubjectAnswer> paperSubjectAnswerList = paperSubjectAnswerService.listAnswerBySubjectIdList(subjectIdList);
        //todo
        return true;
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
}
