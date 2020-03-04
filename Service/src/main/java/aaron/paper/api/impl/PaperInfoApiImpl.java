package aaron.paper.api.impl;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.paper.api.api.PaperInfoApi;
import aaron.paper.api.constant.ApiConstant;
import aaron.paper.api.dto.FuzzySearch;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.api.dto.PaperIdWithName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    /**
     * 发布试卷
     *
     * @param paperId
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_PUBLISH_PAPER)
    @Override
    public CommonResponse<Boolean> publishPaper(CommonRequest<Long> paperId) {
        return null;
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
        return null;
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
    public CommonResponse<List<PaperIdWithName>> fuzzySearchByPaperName(CommonRequest<FuzzySearch> request) {
        return null;
    }

    /**
     * 通过试卷Id查询试卷详情
     *
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_DETAIL)
    @Override
    public CommonResponse<PaperDetail> queryDetailByPaperId(CommonRequest<Long> request) {
        return null;
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
    public CommonResponse<Integer> queryPublishedTimesByPaperId(CommonRequest<Long> request) {
        return null;
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
    public CommonResponse<String> queryPaperNameByPaperId(CommonRequest<Long> request) {
        return null;
    }
}
