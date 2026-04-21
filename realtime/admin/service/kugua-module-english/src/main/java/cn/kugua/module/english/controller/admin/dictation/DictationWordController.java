package cn.kugua.module.english.controller.admin.dictation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.*;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import cn.kugua.module.english.service.dictation.DictationWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 听写单词")
@RestController
@RequestMapping("/english/dictation/word")
public class DictationWordController {

    @Resource
    private DictationWordService wordService;

    @PostMapping("/create")
    @Operation(summary = "创建单词")
    @PreAuthorize("@ss.hasPermission('english:dictation-word:create')")
    public CommonResult<Long> createWord(@Valid @RequestBody DictationWordSaveReqVO reqVO) {
        return success(wordService.createWord(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新单词")
    @PreAuthorize("@ss.hasPermission('english:dictation-word:update')")
    public CommonResult<Boolean> updateWord(@Valid @RequestBody DictationWordSaveReqVO reqVO) {
        wordService.updateWord(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除单词")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:dictation-word:delete')")
    public CommonResult<Boolean> deleteWord(@RequestParam("id") Long id) {
        wordService.deleteWord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得单词")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:dictation-word:query')")
    public CommonResult<DictationWordRespVO> getWord(@RequestParam("id") Long id) {
        DictationWordDO word = wordService.getWord(id);
        return success(BeanUtils.toBean(word, DictationWordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得单词分页")
    @PreAuthorize("@ss.hasPermission('english:dictation-word:query')")
    public CommonResult<PageResult<DictationWordRespVO>> getWordPage(@Valid DictationWordPageReqVO reqVO) {
        PageResult<DictationWordDO> pageResult = wordService.getWordPage(reqVO);
        return success(BeanUtils.toBean(pageResult, DictationWordRespVO.class));
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建单词（一行一词）")
    @PreAuthorize("@ss.hasPermission('english:dictation-word:create')")
    public CommonResult<Integer> batchCreateWords(@RequestBody List<String> words) {
        return success(wordService.batchCreateWords(words));
    }

}
