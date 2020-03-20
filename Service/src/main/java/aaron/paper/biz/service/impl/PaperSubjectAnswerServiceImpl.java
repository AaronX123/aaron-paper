package aaron.paper.biz.service.impl;

import aaron.paper.biz.dao.PaperSubjectAnswerDao;
import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.pojo.model.PaperSubjectAnswer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class PaperSubjectAnswerServiceImpl extends ServiceImpl<PaperSubjectAnswerDao, PaperSubjectAnswer> implements PaperSubjectAnswerService {
    /**
     * 通过试题id集合来获取一批答案
     *
     * @param list
     * @return
     */
    @Override
    public List<PaperSubjectAnswer> listAnswerBySubjectIdList(List<Long> list) {
        return baseMapper.batchQueryAnswerBySubjectId(list);
    }

    /**
     * 通过试题Id删除答案
     *
     * @param idList
     * @return
     */
    @Override
    public boolean deleteBySubjectId(List<Long> idList) {
        return baseMapper.deleteBySubjectIdList(idList);
    }
}
