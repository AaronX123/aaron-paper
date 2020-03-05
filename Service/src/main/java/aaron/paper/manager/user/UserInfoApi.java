package aaron.paper.manager.user;

import aaron.user.api.api.UserApi;
import aaron.user.api.constant.ApiConstant;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@FeignClient(name = ApiConstant.SERVICE_NAME)
public interface UserInfoApi extends UserApi {
}