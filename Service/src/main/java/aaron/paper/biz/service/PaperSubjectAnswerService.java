package aaron.paper.biz.service;

import aaron.paper.biz.dao.PaperSubjectAnswerDao;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public interface PaperSubjectAnswerService extends IService<PaperSubjectAnswer> {
    /**
     * 通过试题id集合来获取一批答案
     * @param list
     * @return
     */
    List<PaperSubjectAnswer> listAnswerBySubjectIdList(List<Long> list);

    /**
     * 通过试题Id删除答案
     * @param idList
     * @return
     */
    boolean deleteBySubjectId(List<Long> idList);
}
