package aaron.paper.manager.baseinfo;

import aaron.baseinfo.api.constant.ApiConstant;
import aaron.baseinfo.api.dto.BaseDataDto;
import aaron.baseinfo.api.dto.CombExamConfigItemDto;
import aaron.baseinfo.api.dto.SubjectPackage;
import aaron.common.data.common.CommonRequest;
import aaron.common.data.common.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@FeignClient(ApiConstant.SERVICE_NAME)
public interface BaseInfoApi {
    @PostMapping({"/baseinfo/list/category"})
    CommonResponse listCategory(CommonRequest<BaseDataDto> request);

    @PostMapping({"/baseinfo/get/base/datas"})
    CommonResponse getBaseDataS(CommonRequest<BaseDataDto> request);

    @PostMapping({"/baseinfo/get/base/data"})
    CommonResponse getBaseData(CommonRequest<Long> request);

    @PostMapping({"/baseinfo/get/subject/and/answer"})
    CommonResponse getSubjectAndAnswer(CommonRequest<Long> request);

    @PostMapping({"/baseinfo/get/subject/customized"})
    CommonResponse getSubjectAndAnswerCustomized(CommonRequest<List<CombExamConfigItemDto>> request);

    @PostMapping({"/baseinfo/get/subject/by/id"})
    CommonResponse getSubjectById(CommonRequest<List<Long>> request);

    @PostMapping({"/baseinfo/list/subject/type"})
    CommonResponse getSubjectType(CommonRequest<BaseDataDto> request);

    @PostMapping({"/baseinfo/get/category/val"})
    CommonResponse getCategory(Long id);

    @PostMapping({"/baseinfo/get/subject/type"})
    CommonResponse getSubjectType(Long id);
}
