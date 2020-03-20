package aaron.paper.biz.dao;

import aaron.paper.pojo.model.PaperSubject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Mapper
public interface PaperSubjectDao extends BaseMapper<PaperSubject> {
    /**
     * 根据试卷id获取试题
     * @param list
     * @return
     */
    List<PaperSubject> listSubjectByPaperIdList(@Param("ids") List<Long> list);
}
