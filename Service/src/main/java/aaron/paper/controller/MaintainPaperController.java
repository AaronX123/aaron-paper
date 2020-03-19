package aaron.paper.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.service.PaperService;
import aaron.paper.common.constant.ControllerConstant;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.vo.ModifyPaperVo;
import aaron.paper.pojo.vo.PaperQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-04
 */
@RestController
public class MaintainPaperController {

    @Autowired
    PaperService paperService;

    @Autowired
    CommonState commonState;

    @Autowired
    CacheManager cacheManager;

    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_QUERY_PAPER)
    public CommonResponse<Map> queryPaper(@RequestBody @Valid CommonRequest<PaperQueryVo> request){
        Map res = paperService.queryPaper(CommonUtils.copyProperties(request.getData(), PaperQueryDto.class),false);
        return new CommonResponse<>(commonState.getVersion(),commonState.SUCCESS,commonState.SUCCESS_MSG,res);
    }


    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_MODIFY_PAPER)
    public CommonResponse modifyPaper(@RequestBody @Valid CommonRequest<ModifyPaperVo> request){
        PaperDetail detail = CommonUtils.copyProperties(request.getData(),PaperDetail.class);
        return new CommonResponse();
    }
}
