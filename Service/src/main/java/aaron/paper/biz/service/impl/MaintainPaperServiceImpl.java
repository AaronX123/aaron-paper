package aaron.paper.biz.service.impl;

import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.service.MaintainPaperService;
import aaron.paper.pojo.dto.PaperQueryDto;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class MaintainPaperServiceImpl implements MaintainPaperService {
    /**
     * 通过查询参数查询试卷
     *
     * @param paperQueryDTO 查询试卷参数
     * @return 返回满足此参数的试卷列表
     */
    @Override
    public Map<String, Object> queryPaper(PaperQueryDto paperQueryDTO) {
        return null;
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
}
