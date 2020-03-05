package aaron.paper.biz.service.impl;

import aaron.paper.biz.dao.PaperSubjectDao;
import aaron.paper.biz.service.PaperSubjectService;
import aaron.paper.pojo.model.PaperSubject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class PaperSubjectServiceImpl extends ServiceImpl<PaperSubjectDao, PaperSubject> implements PaperSubjectService {
    /**
     * 通过试卷Id获取试题
     *
     * @param paperId
     * @return
     */
    @Override
    public List<PaperSubject> listSubjectByPaperId(Long paperId) {
        QueryWrapper<PaperSubject> wrapper = new QueryWrapper<>();
        wrapper.eq(PaperSubject.PAPER_ID,paperId);
        return list(wrapper);
    }
}
