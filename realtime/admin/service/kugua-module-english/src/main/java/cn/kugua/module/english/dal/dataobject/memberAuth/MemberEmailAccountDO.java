package cn.kugua.module.english.dal.dataobject.memberAuth;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 会员邮箱/账号 扩展账号 DO
 * <p>
 * 与 yudao member_user 通过 user_id 一对一关联。允许一个 member_user 同时拥有
 * 邮箱、账号、手机三种登录方式（任一种登录均得到同一个 member_user.id）。
 */
@TableName("kugua_member_email_account")
@KeySequence("kugua_member_email_account_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmailAccountDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联 member_user.id */
    private Long userId;

    /** 邮箱（uk_email，可空：仅用账号注册的用户可能没有邮箱） */
    private String email;

    /** 用户名/账号（uk_username，可空：仅用邮箱注册可能没有 username） */
    private String username;

    /** 密码 BCrypt（账号密码登录使用；邮箱+验证码登录可不设密码） */
    private String password;

    /** 邮箱是否已验证 */
    private Boolean emailVerified;

    /** 0=启用 1=停用 */
    private Integer status;

}
