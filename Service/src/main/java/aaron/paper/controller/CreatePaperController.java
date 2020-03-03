package aaron.paper.controller;

import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import aaron.common.data.common.CommonState;
import aaron.common.logging.annotation.MethodEnhancer;
import aaron.common.utils.SnowFlake;
import aaron.paper.api.constant.ApiConstant;
import aaron.paper.pojo.vo.CombExamConfigVO;
import aaron.paper.pojo.vo.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-03
 */
@Slf4j
@RestController
public class CreatePaperController {
    @Value("${aaron.version")
    public String version;

    @MethodEnhancer
    @PostMapping(ApiConstant.CREATE_FAST_GEN)
    public CommonResponse fastGen(@RequestBody @Valid CommonRequest<CombExamConfigVO> request){
        return new CommonResponse(version, CommonState.SUCCESS,CommonState.SUCCESS_MSG,null);
    }

    @MethodEnhancer
    @PostMapping(ApiConstant.CREATE_STANDARD_GEN)
    public CommonResponse standardGen(@RequestBody @Valid CommonRequest request){
        return new CommonResponse(version, CommonState.SUCCESS,CommonState.SUCCESS_MSG,null);
    }

    @MethodEnhancer
    @PostMapping("/t")
    public String s(@RequestBody Test test){
        return test.getName();
    }
}
