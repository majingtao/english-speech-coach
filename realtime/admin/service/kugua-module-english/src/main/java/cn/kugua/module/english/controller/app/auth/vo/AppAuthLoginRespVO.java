package cn.kugua.module.english.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "H5 - 登录响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppAuthLoginRespVO {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresTime;

}
