package cn.kugua.module.english.controller.admin.quota;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import cn.kugua.module.english.controller.admin.quota.vo.*;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaDefaultDO;
import cn.kugua.module.english.dal.dataobject.quota.EscQuotaUsageDailyDO;
import cn.kugua.module.english.dal.dataobject.quota.EscUserQuotaDO;
import cn.kugua.module.english.service.quota.EscQuotaService;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 英语课配额")
@RestController
@RequestMapping("/esc/quota")
@Validated
public class EscQuotaAdminController {

    @Resource
    private EscQuotaService quotaService;

    @Resource
    private MemberUserService memberUserService;

    // ========== 全局默认 ==========

    @GetMapping("/default")
    @Operation(summary = "查看全局默认配额")
    @PreAuthorize("@ss.hasPermission('esc:quota:query')")
    public CommonResult<EscQuotaDefaultRespVO> getDefault() {
        EscQuotaDefaultDO d = quotaService.getDefault();
        return success(BeanUtils.toBean(d, EscQuotaDefaultRespVO.class));
    }

    @PutMapping("/default")
    @Operation(summary = "更新全局默认配额")
    @PreAuthorize("@ss.hasPermission('esc:quota:update')")
    public CommonResult<Boolean> updateDefault(@Valid @RequestBody EscQuotaDefaultUpdateReqVO reqVO) {
        quotaService.updateDefault(reqVO);
        return success(true);
    }

    // ========== 单用户覆盖 ==========

    @GetMapping("/user/page")
    @Operation(summary = "单用户配额分页")
    @PreAuthorize("@ss.hasPermission('esc:quota:query')")
    public CommonResult<PageResult<EscUserQuotaRespVO>> getUserQuotaPage(@Valid EscUserQuotaPageReqVO reqVO) {
        // nickname 模糊搜索：先用 yudao 查到匹配 userId，再按第一个灌回 reqVO（简单场景足够）
        if (reqVO.getNickname() != null && !reqVO.getNickname().isBlank() && reqVO.getUserId() == null) {
            java.util.List<MemberUserDO> hits = memberUserService.getUserListByNickname(reqVO.getNickname());
            if (hits.isEmpty()) {
                return success(new PageResult<>(java.util.List.of(), 0L));
            }
            reqVO.setUserId(hits.get(0).getId());
        }
        PageResult<EscUserQuotaDO> page = quotaService.getUserQuotaPage(reqVO);
        PageResult<EscUserQuotaRespVO> resp = BeanUtils.toBean(page, EscUserQuotaRespVO.class);

        // 补昵称
        if (resp.getList() != null && !resp.getList().isEmpty()) {
            java.util.List<Long> userIds = resp.getList().stream()
                    .map(EscUserQuotaRespVO::getUserId).distinct().toList();
            Map<Long, MemberUserDO> userMap = memberUserService.getUserList(userIds).stream()
                    .collect(Collectors.toMap(MemberUserDO::getId, u -> u, (a, b) -> a));
            for (EscUserQuotaRespVO vo : resp.getList()) {
                MemberUserDO u = userMap.get(vo.getUserId());
                if (u != null) vo.setNickname(u.getNickname());
            }
        }
        return success(resp);
    }

    @GetMapping("/user/get")
    @Operation(summary = "查看某用户配额覆盖")
    @Parameter(name = "userId", required = true)
    @PreAuthorize("@ss.hasPermission('esc:quota:query')")
    public CommonResult<EscUserQuotaRespVO> getUserQuota(@RequestParam("userId") Long userId) {
        EscUserQuotaDO d = quotaService.getUserQuota(userId);
        if (d == null) return success(null);
        EscUserQuotaRespVO vo = BeanUtils.toBean(d, EscUserQuotaRespVO.class);
        MemberUserDO u = memberUserService.getUser(userId);
        if (u != null) vo.setNickname(u.getNickname());
        return success(vo);
    }

    @PostMapping("/user/save")
    @Operation(summary = "新增或更新单用户配额（按 userId upsert）")
    @PreAuthorize("@ss.hasPermission('esc:quota:update')")
    public CommonResult<Boolean> saveUserQuota(@Valid @RequestBody EscUserQuotaSaveReqVO reqVO) {
        quotaService.saveUserQuota(reqVO);
        return success(true);
    }

    @DeleteMapping("/user/delete")
    @Operation(summary = "删除单用户覆盖（回归默认）")
    @Parameter(name = "userId", required = true)
    @PreAuthorize("@ss.hasPermission('esc:quota:update')")
    public CommonResult<Boolean> deleteUserQuota(@RequestParam("userId") Long userId) {
        quotaService.deleteUserQuota(userId);
        return success(true);
    }

    @PostMapping("/user/reset-usage")
    @Operation(summary = "重置某用户今日已用量（LLM/ASR/TTS 全部清零）")
    @Parameter(name = "userId", required = true)
    @PreAuthorize("@ss.hasPermission('esc:quota:update')")
    public CommonResult<Boolean> resetUserUsage(@RequestParam("userId") Long userId) {
        quotaService.resetTodayUsage(userId);
        return success(true);
    }

    // ========== 用量归档 / 报表 ==========

    @GetMapping("/usage/page")
    @Operation(summary = "每日用量归档分页")
    @PreAuthorize("@ss.hasPermission('esc:quota:query')")
    public CommonResult<PageResult<EscQuotaUsageRespVO>> getUsagePage(@Valid EscQuotaUsagePageReqVO reqVO) {
        if (reqVO.getNickname() != null && !reqVO.getNickname().isBlank() && reqVO.getUserId() == null) {
            List<MemberUserDO> hits = memberUserService.getUserListByNickname(reqVO.getNickname());
            if (hits.isEmpty()) {
                return success(new PageResult<>(List.of(), 0L));
            }
            reqVO.setUserId(hits.get(0).getId());
        }
        PageResult<EscQuotaUsageDailyDO> page = quotaService.getUsagePage(reqVO);
        PageResult<EscQuotaUsageRespVO> resp = BeanUtils.toBean(page, EscQuotaUsageRespVO.class);
        if (resp.getList() != null && !resp.getList().isEmpty()) {
            List<Long> userIds = resp.getList().stream()
                    .map(EscQuotaUsageRespVO::getUserId).distinct().toList();
            Map<Long, MemberUserDO> userMap = memberUserService.getUserList(userIds).stream()
                    .collect(Collectors.toMap(MemberUserDO::getId, u -> u, (a, b) -> a));
            for (EscQuotaUsageRespVO vo : resp.getList()) {
                MemberUserDO u = userMap.get(vo.getUserId());
                if (u != null) vo.setNickname(u.getNickname());
            }
        }
        return success(resp);
    }

    @PostMapping("/usage/archive")
    @Operation(summary = "手动触发某日归档（yyyy-MM-dd，留空=昨天）")
    @PreAuthorize("@ss.hasPermission('esc:quota:update')")
    public CommonResult<Integer> archiveUsage(@RequestParam(value = "date", required = false) String date) {
        int n;
        if (date == null || date.isBlank()) {
            n = quotaService.archiveYesterdayUsage();
        } else {
            n = quotaService.archiveUsageForDate(LocalDate.parse(date));
        }
        return success(n);
    }

}
