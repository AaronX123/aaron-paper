package aaron.paper.biz.service;

import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.model.Paper;

import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */
public interface TemplatePaperService {
    /**
     * download
     * @param paperDTO the ids of which are downloaded
     * @return the count of downloaded
     * @exception Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    boolean downloadTemplate(PaperDto paperDTO);

    /**
     * upload
     * @param paperDTO the id of uploaded
     * @return the count of uploaded
     * @exception Exception when parse a userPermission if token is invalid of expired and decode unsuccessfully
     */
    boolean uploadTemplate(PaperDto paperDTO);

    /**
     * remove batch of templates
     * @param paperTemplateIds the paper id of a template
     * @return the count of removed paper
     */
    int deleteTemplate(Long[] paperTemplateIds);


    /**
     * 对一张试卷进行深复制并且进行插入
     * @param p
     * @param newPo
     * @return
     */
    boolean deepCopyPaper(Paper p, Paper newPo);

    /**
     * 查询模板
     * @param paperQueryDTO
     * @return
     */
    Map<String,Object> queryTemplate(PaperQueryDto paperQueryDTO);
}
