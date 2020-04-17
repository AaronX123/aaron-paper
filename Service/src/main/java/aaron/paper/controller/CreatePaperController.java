package aaron.paper.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.data.exception.StarterError;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.CommonUtils;
import aaron.common.utils.TokenUtils;
import aaron.common.utils.jwt.UserPermission;
import aaron.paper.api.constant.ApiConstant;
import aaron.paper.biz.service.PaperService;
import aaron.paper.common.constant.ControllerConstant;
import aaron.paper.common.exception.PaperException;
import aaron.paper.pojo.dto.PaperDto;
import aaron.paper.pojo.model.Paper;
import aaron.paper.pojo.vo.CombExamConfigVo;
import aaron.paper.pojo.vo.CustomizedCombExamConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.CREATE)
public class CreatePaperController {
    @Autowired
    CommonState state;

    @Autowired
    PaperService paperService;


    /**
     * 快速组卷，应用场景：前端选择组卷配置后，将组卷配置
     * @param request
     * @return
     */
    @MethodEnhancer
    @PostMapping(ControllerConstant.CREATE_FAST_GEN)
    public CommonResponse<String> fastGen(@RequestBody @Valid CommonRequest<CombExamConfigVo> request){
        UserPermission userPermission = checkAccessAuthority();
        CombExamConfigVo vo = request.getData();
        PaperDto paperDto = CommonUtils.copyProperties(vo,PaperDto.class);
        paperDto.setConfigId(vo.getId());
        paperDto.setDescription(vo.getRemark());
        paperDto.setPaperCreator(userPermission.getUserName());
        paperDto.setStatus((byte) 1);
        boolean res = paperService.generateFastMode(paperDto);
        if (res){
            return new CommonResponse<>(state.getVersion(), state.SUCCESS,state.SUCCESS_MSG,state.SUCCESS_MSG);
        }
        return new CommonResponse<>(state.getVersion(), state.FAIL,state.FAIL,state.FAIL_MSG);
    }

    @MethodEnhancer
    @PostMapping(ControllerConstant.CREATE_STANDARD_GEN)
    public CommonResponse standardGen(@RequestBody @Valid CommonRequest<CustomizedCombExamConfigVo> request){
        UserPermission userPermission = checkAccessAuthority();
        CustomizedCombExamConfigVo vo = request.getData();
        PaperDto paperDto = new PaperDto();
        paperDto.setPaperCreator(userPermission.getUserName());
        boolean res = paperService.generateNormalMode(paperDto,vo);
        if (res){
            return new CommonResponse<>(state.getVersion(), state.SUCCESS,state.SUCCESS_MSG,state.SUCCESS_MSG);
        }
        return new CommonResponse<>(state.getVersion(), state.FAIL,state.FAIL,state.FAIL_MSG);
    }


    @MethodEnhancer
    @PostMapping(ControllerConstant.CREATE_TEMPLATE_GEN)
    public CommonResponse templateGen(@RequestBody @Valid CommonRequest<Long> request){
        checkAccessAuthority();
        Long templateId = request.getData();
        PaperDto paperDto = new PaperDto();
        paperDto.setId(templateId);
        boolean res = paperService.generateTemplateMode(paperDto);
        if (res){
            return new CommonResponse<>(state.getVersion(), state.SUCCESS,state.SUCCESS_MSG,state.SUCCESS_MSG);
        }
        return new CommonResponse<>(state.getVersion(), state.FAIL,state.FAIL,state.FAIL_MSG);
    }

    private UserPermission checkAccessAuthority(){
        UserPermission userPermission = TokenUtils.getUser();
        if (userPermission.getCompanyId() == null){
            throw new PaperException(StarterError.SYSTEM_ACCESS_INVALID);
        }
        return userPermission;
    }

    @GetMapping("/he")
    public String test(){
        return "Hello";
    }
}
