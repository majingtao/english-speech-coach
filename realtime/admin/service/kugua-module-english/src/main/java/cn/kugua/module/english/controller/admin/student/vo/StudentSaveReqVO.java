package cn.kugua.module.english.controller.admin.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学生新增/修改 Request VO")
@Data
public class StudentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7361")
    private Long id;

    @Schema(description = "登录账号（短账号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "登录账号（短账号）不能为空")
    private String username;

    @Schema(description = "密码（BCrypt）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码（BCrypt）不能为空")
    private String password;

    @Schema(description = "昵称 / 真实姓名", example = "李四")
    private String nickname;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "性别 1=男 2=女")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "所属班级 esc_class.id", example = "1060")
    private Long classId;

    @Schema(description = "当前学习级别 flyers/ket/pet")
    private String levelCode;

    @Schema(description = "0=启用 1=停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "0=启用 1=停用不能为空")
    private Integer status;

    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最近登录 IP")
    private String lastLoginIp;

}