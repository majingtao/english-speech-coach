package cn.kugua.module.english.controller.admin.student.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 学生分页 Request VO")
@Data
public class StudentPageReqVO extends PageParam {

    @Schema(description = "登录账号（短账号）", example = "王五")
    private String username;

    @Schema(description = "密码（BCrypt）")
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

    @Schema(description = "0=启用 1=停用", example = "2")
    private Integer status;

    @Schema(description = "最近登录时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] lastLoginTime;

    @Schema(description = "最近登录 IP")
    private String lastLoginIp;

    @Schema(description = "添加时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}