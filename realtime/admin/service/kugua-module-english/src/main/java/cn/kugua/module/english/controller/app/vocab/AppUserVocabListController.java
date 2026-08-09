package cn.kugua.module.english.controller.app.vocab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.vocab.vo.AppUserVocabListItemsAddReqVO;
import cn.kugua.module.english.controller.app.vocab.vo.AppUserVocabListRespVO;
import cn.kugua.module.english.controller.app.vocab.vo.AppUserVocabListSaveReqVO;
import cn.kugua.module.english.controller.app.vocab.vo.AppVocabListItemVO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListItemDO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.service.vocab.UserVocabListService;
import cn.kugua.module.english.service.vocab.UserVocabListService.UserVocabListWithCount;
import cn.kugua.module.english.service.vocab.UserVocabProgressService;
import cn.kugua.module.english.service.vocab.VocabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * H5 学员端 - 用户自建词库
 */
@Tag(name = "H5 - 用户词库")
@RestController
@RequestMapping("/english/user-vocab-list")
@Validated
public class AppUserVocabListController {

    @Resource
    private UserVocabListService listService;

    @Resource
    private VocabService vocabService;

    @Resource
    private UserVocabProgressService progressService;

    @PostMapping
    @Operation(summary = "创建词库")
    public CommonResult<Long> create(@Valid @RequestBody AppUserVocabListSaveReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(listService.createList(userId, reqVO.getName(), reqVO.getDescription()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新词库")
    public CommonResult<Boolean> update(@PathVariable("id") Long id,
                                        @Valid @RequestBody AppUserVocabListSaveReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        listService.updateList(userId, id, reqVO.getName(), reqVO.getDescription());
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除词库")
    public CommonResult<Boolean> delete(@PathVariable("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        listService.deleteList(userId, id);
        return success(true);
    }

    @GetMapping("/mine")
    @Operation(summary = "我的词库列表（含词数）")
    public CommonResult<List<AppUserVocabListRespVO>> mine() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<UserVocabListWithCount> rows = listService.getMyLists(userId);
        List<AppUserVocabListRespVO> result = new ArrayList<>(rows.size());
        for (UserVocabListWithCount r : rows) {
            AppUserVocabListRespVO vo = new AppUserVocabListRespVO();
            vo.setId(r.list.getId());
            vo.setName(r.list.getName());
            vo.setDescription(r.list.getDescription());
            vo.setSource(r.list.getSource());
            vo.setWordCount(r.wordCount);
            vo.setCreateTime(r.list.getCreateTime());
            result.add(vo);
        }
        return success(result);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "批量加词")
    public CommonResult<Integer> addItems(@PathVariable("id") Long id,
                                          @Valid @RequestBody AppUserVocabListItemsAddReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(listService.addItems(userId, id, reqVO.getVocabIds()));
    }

    @DeleteMapping("/{id}/items/{vocabId}")
    @Operation(summary = "从词库移除词条")
    public CommonResult<Boolean> removeItem(@PathVariable("id") Long id,
                                            @PathVariable("vocabId") Long vocabId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        listService.removeItem(userId, id, vocabId);
        return success(true);
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "分页查询词库词条")
    public CommonResult<PageResult<AppVocabListItemVO>> items(
            @PathVariable("id") Long id,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        PageResult<UserVocabListItemDO> page = listService.getItemPage(userId, id, pageParam);
        if (page.getList().isEmpty()) {
            PageResult<AppVocabListItemVO> empty = new PageResult<>();
            empty.setList(List.of());
            empty.setTotal(page.getTotal());
            return success(empty);
        }
        List<Long> vocabIds = page.getList().stream().map(UserVocabListItemDO::getVocabId).toList();
        List<VocabDO> vocabs = vocabService.getVocabListByIds(vocabIds);
        Map<Long, VocabDO> vocabMap = new HashMap<>();
        for (VocabDO v : vocabs) vocabMap.put(v.getId(), v);

        List<AppVocabListItemVO> items = new ArrayList<>(page.getList().size());
        for (UserVocabListItemDO rel : page.getList()) {
            VocabDO v = vocabMap.get(rel.getVocabId());
            if (v == null) continue;
            AppVocabListItemVO item = new AppVocabListItemVO();
            item.setId(v.getId());
            item.setWord(v.getWord());
            item.setLevelCode(v.getLevelCode());
            item.setPos(v.getPos());
            item.setDifficulty(v.getDifficulty());
            UserVocabProgressDO p = progressService.getProgress(userId, v.getId());
            if (p != null) {
                item.setProgressStatus(p.getStatus());
                item.setRepetitions(p.getRepetitions());
            }
            items.add(item);
        }
        PageResult<AppVocabListItemVO> result = new PageResult<>();
        result.setList(items);
        result.setTotal(page.getTotal());
        return success(result);
    }

}
