package aaron.paper.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import aaron.paper.api.api.PaperInfoApi;
import aaron.paper.api.dto.PaperDetail;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.impl.BaseService;
import aaron.paper.common.constant.ControllerConstant;
import aaron.paper.manager.baseinfo.BaseInfoApi;
import aaron.paper.pojo.dto.ModifyPaperDto;
import aaron.paper.pojo.dto.ModifyPaperSubjectDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.vo.ModifyPaperVo;
import aaron.paper.pojo.vo.PaperQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    BaseInfoApi baseInfoApi;

    @Autowired
    BaseService baseService;

    @Autowired
    PaperInfoApi paperInfoApi;

    /**
     * 查询试卷表单
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_QUERY_PAPER)
    public CommonResponse<Map> queryPaper(@RequestBody @Valid CommonRequest<PaperQueryVo> request){
        Map res = paperService.queryPaper(CommonUtils.copyProperties(request.getData(), PaperQueryDto.class),false);
        return new CommonResponse<>(commonState.getVersion(),commonState.SUCCESS,commonState.SUCCESS_MSG,res);
    }

    /**
     * 修改试卷
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_MODIFY_PAPER)
    public CommonResponse modifyPaper(@RequestBody @Valid CommonRequest<ModifyPaperVo> request){
        ModifyPaperVo vo = request.getData();
        ModifyPaperDto detail = CommonUtils.copyProperties(vo,ModifyPaperDto.class);
        List<ModifyPaperSubjectDto> currentPaperSubjectList = vo.getCurrentPaperSubjectVoList().stream().map(
                subject -> CommonUtils.copyComplicateObject(subject,ModifyPaperSubjectDto.class)
        ).collect(Collectors.toList());
        detail.setCurrentPaperSubjectDtoList(currentPaperSubjectList);
        if (paperService.paperModify(detail)){
            baseService.evictPaper(vo.getId());
            return new CommonResponse<>(commonState.getVersion(),commonState.SUCCESS,commonState.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(commonState.getVersion(),commonState.FAIL,commonState.FAIL_MSG,false);
    }

    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_DELETE_PAPER)
    public CommonResponse deletePaper(@RequestBody @Valid CommonRequest<Long[]> request){
        Long[] deletedIdArray = request.getData();
        if (paperService.paperDelete(deletedIdArray)){
            baseService.evictPaper(deletedIdArray);
            return new CommonResponse<>(commonState.getVersion(),commonState.SUCCESS,commonState.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(commonState.getVersion(),commonState.FAIL,commonState.FAIL_MSG,false);
    }


    @MethodEnhancer
    @PostMapping(ControllerConstant.MAINTAIN_PAPER_DETAIL)
    public CommonResponse<PaperDetail> paperDetail(@RequestBody @Valid CommonRequest<Long> request){
        return paperInfoApi.queryDetailByPaperId(request);
    }

}
