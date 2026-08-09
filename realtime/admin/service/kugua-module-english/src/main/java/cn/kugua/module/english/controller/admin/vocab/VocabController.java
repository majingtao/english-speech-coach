package cn.kugua.module.english.controller.admin.vocab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.vocab.vo.*;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.service.vocab.VocabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 词汇")
@RestController
@RequestMapping("/english/vocab")
public class VocabController {

    @Resource
    private VocabService vocabService;

    @PostMapping("/create")
    @Operation(summary = "创建词条")
    @PreAuthorize("@ss.hasPermission('english:vocab:create')")
    public CommonResult<Long> create(@Valid @RequestBody VocabSaveReqVO reqVO) {
        return success(vocabService.createVocab(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新词条")
    @PreAuthorize("@ss.hasPermission('english:vocab:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody VocabSaveReqVO reqVO) {
        vocabService.updateVocab(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除词条")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('english:vocab:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        vocabService.deleteVocab(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取词条")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('english:vocab:query')")
    public CommonResult<VocabRespVO> get(@RequestParam("id") Long id) {
        VocabDO vocab = vocabService.getVocab(id);
        VocabRespVO resp = BeanUtils.toBean(vocab, VocabRespVO.class);
        if (resp != null) {
            resp.setThemeIds(vocabService.getThemeIdsByVocabId(id));
        }
        return success(resp);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询词条")
    @PreAuthorize("@ss.hasPermission('english:vocab:query')")
    public CommonResult<PageResult<VocabRespVO>> page(@Valid VocabPageReqVO reqVO) {
        PageResult<VocabDO> page = vocabService.getVocabPage(reqVO);
        return success(BeanUtils.toBean(page, VocabRespVO.class));
    }

    @PostMapping("/regen-content/{id}")
    @Operation(summary = "重新生成 content_json")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('english:vocab:regen')")
    public CommonResult<Boolean> regenContent(@PathVariable("id") Long id) {
        vocabService.regenerateContent(id);
        return success(true);
    }

    @PostMapping("/batch-import")
    @Operation(summary = "批量导入词条（JSON 粘贴）")
    @PreAuthorize("@ss.hasPermission('english:vocab:create')")
    public CommonResult<Integer> batchImport(@Valid @RequestBody VocabBatchImportReqVO reqVO) {
        return success(vocabService.batchImport(reqVO));
    }

}
