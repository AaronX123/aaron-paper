package aaron.paper.biz.service.impl;

import aaron.paper.biz.service.TemplatePaperService;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.model.Paper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@Service
public class TemplatePaperServiceImpl implements TemplatePaperService {
    /**
     * download
     *
     * @param paperDTO the ids of which are downloaded
     * @return the count of downloaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean downloadTemplate(PaperDto paperDTO) {
        return false;
    }

    /**
     * upload
     *
     * @param paperDTO the id of uploaded
     * @return the count of uploaded
     * @throws Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    @Override
    public boolean uploadTemplate(PaperDto paperDTO) {
        return false;
    }

    /**
     * remove batch of templates
     *
     * @param paperTemplateIds the paper id of a template
     * @return the count of removed paper
     */
    @Override
    public int deleteTemplate(Long[] paperTemplateIds) {
        return 0;
    }

    /**
     * 对一张试卷进行深复制并且进行插入
     *
     * @param p
     * @param newPo
     * @return
     */
    @Override
    public boolean deepCopyPaper(Paper p, Paper newPo) {
        return false;
    }

    /**
     * 查询模板
     *
     * @param paperQueryDTO
     * @return
     */
    @Override
    public Map<String, Object> queryTemplate(PaperQueryDto paperQueryDTO) {
        return null;
    }
}
