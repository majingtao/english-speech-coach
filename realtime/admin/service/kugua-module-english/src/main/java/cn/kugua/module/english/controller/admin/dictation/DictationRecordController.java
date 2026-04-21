package cn.kugua.module.english.controller.admin.dictation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationRecordPageReqVO;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationRecordRespVO;
import cn.kugua.module.english.dal.dataobject.dictationRecord.DictationRecordDO;
import cn.kugua.module.english.service.dictation.DictationRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 听写记录")
@RestController
@RequestMapping("/english/dictation/record")
public class DictationRecordController {

    @Resource(name = "dictationRecordService")
    private DictationRecordService recordService;

    @GetMapping("/page")
    @Operation(summary = "获得听写记录分页")
    @PreAuthorize("@ss.hasPermission('english:dictation-record:query')")
    public CommonResult<PageResult<DictationRecordRespVO>> getRecordPage(@Valid DictationRecordPageReqVO reqVO) {
        PageResult<DictationRecordDO> pageResult = recordService.getRecordPage(reqVO);
        return success(BeanUtils.toBean(pageResult, DictationRecordRespVO.class));
    }

}


