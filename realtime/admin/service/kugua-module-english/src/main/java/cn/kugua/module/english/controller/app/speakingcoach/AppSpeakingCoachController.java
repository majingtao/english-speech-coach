package cn.kugua.module.english.controller.app.speakingcoach;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.service.speakingcoach.SpeakingCoachService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/english/speaking-coach")
public class AppSpeakingCoachController {
    @Resource private SpeakingCoachService service;
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResult<?>> failure(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode()).body(CommonResult.error(error.getStatusCode().value(), error.getReason()==null?"请求未完成":error.getReason()));
    }
    private long user() { Long id=SecurityFrameworkUtils.getLoginUserId(); if(id==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); return id; }
    @GetMapping("/dashboard") public CommonResult<?> dashboard(@RequestParam(defaultValue="12") int minutes) { return CommonResult.success(service.dashboard(user(),minutes)); }
    @PostMapping("/sessions") public CommonResult<?> start(@RequestBody JsonNode body) { return CommonResult.success(service.start(user(),body)); }
    @GetMapping("/sessions/{id}") public CommonResult<?> session(@PathVariable String id) { return CommonResult.success(service.session(user(),id)); }
    @PostMapping("/sessions/{id}/hint") public CommonResult<?> hint(@PathVariable String id,@RequestBody JsonNode body) { return CommonResult.success(service.hint(user(),id,body.path("taskId").asText())); }
    @PostMapping("/sessions/{id}/attempts") public CommonResult<?> submit(@PathVariable String id,@RequestBody JsonNode body) { return CommonResult.success(service.submit(user(),id,body)); }
    @PostMapping("/sessions/{id}/transfer") public CommonResult<?> transfer(@PathVariable String id,@RequestBody JsonNode body) { return CommonResult.success(service.transfer(user(),id,body.path("parentId").asText())); }
    @PostMapping("/sessions/{id}/finish") public CommonResult<?> finish(@PathVariable String id) { return CommonResult.success(service.finish(user(),id)); }
    @PostMapping("/sessions/{id}/partner") public CommonResult<?> partner(@PathVariable String id,@RequestBody JsonNode body,@RequestHeader("Authorization") String auth) { return CommonResult.success(service.partner(user(),id,body,auth)); }
    @PostMapping("/attempts/{id}/grade") public CommonResult<?> grade(@PathVariable String id,@RequestHeader("Authorization") String auth) { return CommonResult.success(service.grade(user(),id,auth)); }
    @GetMapping("/attempts/{id}/audio/{index}") public ResponseEntity<byte[]> audio(@PathVariable String id,@PathVariable int index) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType("audio/wav")).body(service.audio(user(),id,index));
    }
}
