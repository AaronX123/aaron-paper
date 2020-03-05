package aaron.paper.biz.service.impl;

import aaron.paper.biz.dao.PaperDao;
import aaron.paper.biz.service.PaperService;
import aaron.paper.pojo.model.Paper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class PaperServiceImpl extends ServiceImpl<PaperDao, Paper> implements PaperService {
    public void test(){
        QueryWrapper<Paper> wrapper = new QueryWrapper<>();
        wrapper.eq(Paper.COMPANY_ID,"624197208534945792");
        Paper paper = baseMapper.selectOne(wrapper);
    }
}
