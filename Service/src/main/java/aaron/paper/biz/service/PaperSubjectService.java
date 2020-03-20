package aaron.paper.biz.service;

import aaron.paper.biz.dao.PaperSubjectDao;
import aaron.paper.pojo.model.PaperSubject;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
public interface PaperSubjectService extends IService<PaperSubject> {
    /**
     * 通过试卷Id获取试题
     * @param paperId
     * @return
     */
    List<PaperSubject> listSubjectByPaperId(Long paperId);

    /**
     * 通过试卷d集合来获取一批试题
     * @param list
     * @return
     */
    List<PaperSubject> listSubjectByPaperIdList(List<Long> list);
}
