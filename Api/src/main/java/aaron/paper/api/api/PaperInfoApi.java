package aaron.paper.api.api;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.paper.api.constant.ApiConstant;
import aaron.paper.api.dto.FuzzySearch;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.api.dto.PaperIdWithName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */

public interface PaperInfoApi {
    /**
     * 发布试卷
     * @param paperId
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_PUBLISH_PAPER)
    CommonResponse<Boolean> publishPaper(CommonRequest<Long> paperId);

    /**
     * 列出试卷Id和名称
     * @return
     */
    @GetMapping(ApiConstant.PAPER_INFO_LIST_PAPER)
    CommonResponse<List<PaperIdWithName>> listPaper();

    /**
     * 通过试卷名称进行模糊搜索
     * @param request
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_FUZZY_SEARCH)
    CommonResponse<List<PaperIdWithName>> fuzzySearchByPaperName(CommonRequest<FuzzySearch> request);

    /**
     * 通过试卷Id查询试卷详情
     * @param request
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_DETAIL)
    CommonResponse<PaperDetail> queryDetailByPaperId(CommonRequest<Long> request);

    /**
     * 通过试卷id查看试卷发布次数
     * @param request
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_PUBLISHED_TIME)
    CommonResponse<Integer> queryPublishedTimesByPaperId(CommonRequest<Long> request);

    /**
     * 获取试卷名称
     * @param request
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_PAPER_NAME)
    CommonResponse<String> queryPaperNameByPaperId(CommonRequest<Long> request);

    /**
     * 查询试卷分数
     * @param request
     * @return
     */
    @PostMapping(ApiConstant.PAPER_INFO_QUERY_PAPER_SCORE)
    CommonResponse<Double> queryPaperScore(CommonRequest<Long> request);
}
