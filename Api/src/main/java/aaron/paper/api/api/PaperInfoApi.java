package aaron.paper.api.api;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.paper.api.dto.FuzzySearch;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.api.dto.PaperIdWithName;

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
    CommonResponse<Boolean> publishPaper(CommonRequest<Long> paperId);

    /**
     * 列出试卷Id和名称
     * @return
     */
    CommonResponse<List<PaperIdWithName>> listPaper();

    /**
     * 通过试卷名称进行模糊搜索
     * @param request
     * @return
     */
    CommonResponse<List<PaperIdWithName>> fuzzySearchByPaperName(CommonRequest<FuzzySearch> request);

    /**
     * 通过试卷Id查询试卷详情
     * @param request
     * @return
     */
    CommonResponse<PaperDetail> queryDetailByPaperId(CommonRequest<Long> request);

    /**
     * 通过试卷id查看试卷发布次数
     * @param request
     * @return
     */
    CommonResponse<Integer> queryPublishedTimesByPaperId(CommonRequest<Long> request);

    /**
     * 获取试卷名称
     * @param request
     * @return
     */
    CommonResponse<String> queryPaperNameByPaperId(CommonRequest<Long> request);

}
