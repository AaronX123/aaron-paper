package aaron.paper.manager.exam;

import aaron.common.data.common.CommonResponse;
import api.ExamApi;
import constant.ApiConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-04-24
 */
@FeignClient(ApiConstant.SERVICE_NAME)
public interface ExamInfoApi{
    @PostMapping({"/exam/checkEditable"})
    CommonResponse<Boolean> checkEditable(@RequestBody Long var1);
}
