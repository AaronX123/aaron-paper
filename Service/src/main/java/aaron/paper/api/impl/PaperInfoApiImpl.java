package aaron.paper.api.impl;

import aaron.common.data.common.CacheConstants;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.api.api.PaperInfoApi;
import aaron.paper.api.constant.ApiConstant;
import aaron.paper.api.dto.FuzzySearch;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.api.dto.PaperIdWithName;
import aaron.paper.api.dto.PaperSubject;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.impl.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@RestController
@Slf4j
public class PaperInfoApiImpl implements PaperInfoApi {
    @Autowired
    PaperService paperService;

    @Autowired
    CommonState state;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    BaseService baseService;

    /**
     * 发布试卷
     *
     * @param paperId
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_PUBLISH_PAPER)
    @Override
    public CommonResponse<Boolean> publishPaper(@RequestBody CommonRequest<Long> paperId) {
        if(paperService.publish(paperId.getData())){
            return new CommonResponse<>(state.getVersion(), state.SUCCESS,state.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(state.getVersion(), state.FAIL,state.FAIL,false);
    }

    /**
     * 列出试卷Id和名称
     *
     * @return
     */
    @MethodEnhancer
    @GetMapping(ApiConstant.PAPER_INFO_LIST_PAPER)
    @Override
    public CommonResponse<List<PaperIdWithName>> listPaper() {
        UserPermission userPermission = TokenUtils.getUser();
        List<PaperIdWithName> res = paperService.list(userPermission.getCompanyId());
        return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,res);
    }

    /**
     * 通过试卷名称进行模糊搜索
     *
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_FUZZY_SEARCH)
    @Override
    public CommonResponse<List<PaperIdWithName>> fuzzySearchByPaperName(@RequestBody CommonRequest<FuzzySearch> request) {
        List<PaperIdWithName> res = paperService.listByName(request.getData());
        return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,res);
    }

    /**
     * 通过试卷Id查询试卷详情
     *
     * @param request
     * @return
     */
    @SuppressWarnings("all")
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_DETAIL)
    @Override
    public CommonResponse<PaperDetail> queryDetailByPaperId(@RequestBody CommonRequest<Long> request) {
        long id = request.getData();
        Cache cache = cacheManager.getCache(CacheConstants.PAPER);
        if (cache != null && cache.get(id) != null){
            Cache.ValueWrapper wrapper = cache.get(id);
            PaperDetail detail = CommonUtils.copyComplicateObject(wrapper.get(),PaperDetail.class);
            return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,detail);
        }else {
            PaperDetail detail = paperService.getPaperInfo(id);
            // 需要将类型和难度转换下
            detail.setDifficultyValue(baseService.getBaseInfoCache(detail.getDifficulty(),BaseService.DICTIONARY));
            // 这里实际上是字典值
            detail.setCategoryValue(baseService.getBaseInfoCache(detail.getCategory(),BaseService.DICTIONARY));
            for (PaperSubject subject : detail.getCurrentPaperSubjectVOList()) {
                subject.setCategoryValue(baseService.getBaseInfoCache(subject.getCategoryId(),BaseService.CATEGORY));
                subject.setDifficultyValue(baseService.getBaseInfoCache(subject.getDifficulty(),BaseService.DICTIONARY));
                subject.setSubjectTypeName(baseService.getBaseInfoCache(subject.getSubjectTypeId(),BaseService.SUBJECT_TYPE));
            }
            cache.put(id,detail);
            return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,detail);
        }
    }

    /**
     * 通过试卷id查看试卷发布次数
     *
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_PUBLISHED_TIME)
    @Override
    public CommonResponse<Integer> queryPublishedTimesByPaperId(@RequestBody CommonRequest<Long> request) {
        return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,paperService.getPaper(request.getData()).getPublishTimes());
    }

    /**
     * 获取试卷名称
     *
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_PAPER_NAME)
    @Override
    public CommonResponse<String> queryPaperNameByPaperId(@RequestBody CommonRequest<Long> request) {
        return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,paperService.getPaper(request.getData()).getName());
    }
}
