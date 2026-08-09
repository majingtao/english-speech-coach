package cn.kugua.module.english.controller.app.vocab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.vocab.vo.AppVocabListItemVO;
import cn.kugua.module.english.controller.app.vocab.vo.AppWordbookAddReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabListDO;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.service.vocab.UserVocabListService;
import cn.kugua.module.english.service.vocab.UserVocabProgressService;
import cn.kugua.module.english.service.vocab.VocabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.kugua.module.english.enums.ErrorCodeConstants.VOCAB_NOT_EXISTS;

/**
 * H5 学员端 - 我的生词本（单一默认词库，家长手动挑词的待学池）
 */
@Tag(name = "H5 - 生词本")
@RestController
@RequestMapping("/english/vocab/wordbook")
@Validated
public class AppWordbookController {

    @Resource
    private UserVocabListService listService;

    @Resource
    private VocabService vocabService;

    @Resource
    private UserVocabProgressService progressService;

    @GetMapping
    @Operation(summary = "我的生词本（含待学/学习进度状态，最新加入在前）")
    public CommonResult<List<AppVocabListItemVO>> list() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<Long> ids = listService.getWordbookVocabIds(userId); // FIFO 升序
        if (ids.isEmpty()) return success(List.of());
        List<VocabDO> vocabs = vocabService.getVocabListByIds(ids);
        Map<Long, VocabDO> vocabMap = new HashMap<>();
        for (VocabDO v : vocabs) vocabMap.put(v.getId(), v);
        // 展示按最新加入在前
        List<Long> ordered = new ArrayList<>(ids);
        Collections.reverse(ordered);
        List<AppVocabListItemVO> result = new ArrayList<>(ordered.size());
        for (Long id : ordered) {
            VocabDO v = vocabMap.get(id);
            if (v == null) continue;
            UserVocabProgressDO p = progressService.getProgress(userId, id);
            result.add(toItem(v, p));
        }
        return success(result);
    }

    @PostMapping("/add")
    @Operation(summary = "加词到生词本（仅接受词库中已发布的词；重复加入返回 0）")
    public CommonResult<Integer> add(@Valid @RequestBody AppWordbookAddReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        VocabDO vocab = vocabService.getVocab(reqVO.getVocabId());
        if (vocab == null || vocab.getStatus() == null || vocab.getStatus() != 1) {
            throw exception(VOCAB_NOT_EXISTS);
        }
        UserVocabListDO wordbook = listService.getOrCreateDefaultWordbook(userId);
        int added = listService.addItems(userId, wordbook.getId(), List.of(reqVO.getVocabId()));
        return success(added);
    }

    @DeleteMapping("/{vocabId}")
    @Operation(summary = "从生词本移除词条")
    public CommonResult<Boolean> remove(@PathVariable("vocabId") Long vocabId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        UserVocabListDO wordbook = listService.getOrCreateDefaultWordbook(userId);
        listService.removeItem(userId, wordbook.getId(), vocabId);
        return success(true);
    }

    private AppVocabListItemVO toItem(VocabDO v, UserVocabProgressDO p) {
        AppVocabListItemVO item = new AppVocabListItemVO();
        item.setId(v.getId());
        item.setWord(v.getWord());
        item.setLevelCode(v.getLevelCode());
        item.setPos(v.getPos());
        item.setDifficulty(v.getDifficulty());
        if (p != null) {
            item.setProgressStatus(p.getStatus());
            item.setRepetitions(p.getRepetitions());
        }
        return item;
    }

}
