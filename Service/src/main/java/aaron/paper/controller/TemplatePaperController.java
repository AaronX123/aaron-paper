package aaron.paper.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.data.exception.StarterError;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.biz.service.PaperService;
import aaron.paper.biz.service.impl.BaseService;
import aaron.paper.common.constant.ControllerConstant;
import aaron.paper.common.exception.PaperError;
import aaron.paper.common.exception.PaperException;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.dto.PaperQueryDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.vo.PaperQueryVo;
import aaron.paper.pojo.vo.PaperVo;
import autoconfigure.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
public class TemplatePaperController {

    @Autowired
    PaperService paperService;

    @Autowired
    CommonState state;

    @Autowired
    BaseService baseService;

    /**
     * 根据模板下载试卷
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.TEMPLATE_DOWNLOAD_PAPER)
    public CommonResponse<Boolean> downloadPaper(@RequestBody @Valid CommonRequest<PaperVo> request){
        PaperVo paperVo = request.getData();
        checkCompany();
        PaperDto paper = CommonUtils.copyProperties(paperVo,PaperDto.class);
        paper.setPreId(paper.getId());
        if (paperService.downloadTemplate(paper)){
            return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(state.getVersion(),state.FAIL,state.FAIL_MSG,false);
    }

    /**
     * 上传模板
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.TEMPLATE_UPLOAD_PAPER)
    public CommonResponse<Boolean> uploadPaper(@RequestBody @Valid CommonRequest<PaperVo> request){
        PaperVo paperVo = request.getData();
        checkCompany();
        PaperDto paper = CommonUtils.copyProperties(paperVo, PaperDto.class);
        if (paperService.uploadTemplate(paper)){
            return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(state.getVersion(),state.FAIL,state.FAIL_MSG,false);
    }

    /**
     * 删除模板
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.TEMPLATE_DELETE_PAPER)
    public CommonResponse deleteTemplate(@RequestBody @Valid CommonRequest<Long[]> request){
        Long[] del = request.getData();
        if (paperService.deleteTemplate(del)) {
            baseService.evictPaper(del);
            return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,true);
        }
        return new CommonResponse<>(state.getVersion(),state.FAIL,state.FAIL_MSG,false);
    }

    @MethodEnhancer
    @PostMapping(ControllerConstant.TEMPLATE_QUERY)
    public CommonResponse<Map> queryTemplate(@RequestBody @Valid CommonRequest<PaperQueryVo> request){
        Map map = paperService.queryTemplate(CommonUtils.copyProperties(request.getData(), PaperQueryDto.class));
        return new CommonResponse<>(state.getVersion(),state.SUCCESS,state.SUCCESS_MSG,map);
    }

    private void checkCompany(){
        UserPermission userPermission = TokenUtils.getUser();
        if (userPermission.getCompanyId() == null){
            throw new PaperException(StarterError.SYSTEM_ACCESS_INVALID);
        }
    }
}
