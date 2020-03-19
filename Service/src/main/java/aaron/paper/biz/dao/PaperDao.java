package aaron.paper.biz.dao;

import aaron.paper.pojo.model.Paper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Mapper
public interface PaperDao extends BaseMapper<Paper> {
    int func();
}
