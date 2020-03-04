package aaron.paper.biz.service;

import aaron.paper.api.dto.PaperDetail;
import aaron.paper.pojo.dto.PaperQueryDto;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */
public interface MaintainPaperService {

    /**
     * 通过查询参数查询试卷
     * @param paperQueryDTO 查询试卷参数
     * @return 返回满足此参数的试卷列表
     *
     */
    Map<String,Object> queryPaper(PaperQueryDto paperQueryDTO);

    /**
     * 准备要删除的试卷数据
     * @param paperIds 试卷id数组
     * @return 删除成功的条数
     */
    int prepareDelete(Long[] paperIds);

    /**
     * 准备修改的资源
     * @param paperDetail
     * @return
     */
    boolean prepareModify(PaperDetail paperDetail);

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    PaperDetail getPaperInfo(Long id);
}
