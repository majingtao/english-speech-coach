package cn.kugua.module.english.dal.dataobject.student;

import lombok.*;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学生 DO
 *
 * @author 苦瓜
 */
@TableName("esc_student")
@KeySequence("esc_student_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 登录账号（短账号）
     */
    private String username;
    /**
     * 密码（BCrypt）
     */
    private String password;
    /**
     * 昵称 / 真实姓名
     */
    private String nickname;
    /**
     * 头像 URL
     */
    private String avatar;
    /**
     * 性别 1=男 2=女
     */
    private Integer gender;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 所属班级 esc_class.id
     */
    private Long classId;
    /**
     * 当前学习级别 flyers/ket/pet
     */
    private String levelCode;
    /**
     * 0=启用 1=停用
     */
    private Integer status;
    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 最近登录 IP
     */
    private String lastLoginIp;


}
