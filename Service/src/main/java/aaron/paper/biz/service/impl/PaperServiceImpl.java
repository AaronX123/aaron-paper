package aaron.paper.biz.service.impl;

import aaron.baseinfo.api.dto.BaseDataDto;
import aaron.baseinfo.api.dto.CombExamConfigItemDto;
import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.common.aop.annotation.FullCommonField;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.utils.CommonUtils;
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
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.dto.SubjectAnswerDto;
import aaron.paper.pojo.dto.SubjectDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    UserApi userApi;

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
