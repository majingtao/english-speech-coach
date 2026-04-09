# kugua-module-english

English Speech Coach 业务模块（kugua 自研，独立包名 `cn.kugua.module.english`）。
对应数据库表前缀：`esc_`（共 24 张表，见 `sql/kugua-module-english/esc_init.sql`）。

## 目录结构

```
kugua-module-english/
├── pom.xml
└── src/main/
    ├── java/cn/kugua/module/english/
    │   ├── controller/admin/                  # 管理后台 REST
    │   │   └── exam/                          #   试卷 CRUD（样板）
    │   │       ├── ExamController.java
    │   │       └── vo/
    │   │           ├── ExamPageReqVO.java
    │   │           ├── ExamRespVO.java
    │   │           └── ExamSaveReqVO.java
    │   ├── dal/
    │   │   ├── dataobject/exam/ExamDO.java    # MyBatis-Plus DO
    │   │   └── mysql/exam/ExamMapper.java     # Mapper
    │   ├── service/exam/                      # 业务 Service
    │   │   ├── ExamService.java
    │   │   └── ExamServiceImpl.java
    │   ├── enums/
    │   │   ├── ErrorCodeConstants.java        # 错误码 1-040-xxx-xxx
    │   │   └── DictTypeConstants.java
    │   └── framework/
    │       └── KuguaEnglishMybatisConfiguration.java  # Mapper 扫描
    └── resources/META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 与 yudao 的集成点

kugua 使用独立包名 `cn.kugua`，必须在 yudao 侧追加 4 处配置才能被容器扫描到：

| 文件 | 改动 |
|---|---|
| `pom.xml`（根） | 追加 `<module>kugua-module-english</module>` |
| `yudao-server/pom.xml` | 追加 `<dependency>` 引 `kugua-module-english` |
| `yudao-server/.../YudaoServerApplication.java` | `scanBasePackages` 追加 `"cn.kugua.module"` |
| `yudao-server/.../application.yaml` | `mybatis.type-aliases-package` 追加 `,cn.kugua.module.*.dal.dataobject` |

Mapper 扫描通过本模块内置的 `KuguaEnglishMybatisConfiguration`（auto-configuration）完成，无需再改 yudao。

## 样板的复制模式

`esc_exam` 已作为完整样板实现。其他 23 张表按以下 6 文件套路复制：

1. `dal/dataobject/<group>/XxxDO.java`    继承 `TenantBaseDO`
2. `dal/mysql/<group>/XxxMapper.java`      继承 `BaseMapperX<XxxDO>`
3. `controller/admin/<group>/vo/XxxPageReqVO.java` 继承 `PageParam`
4. `controller/admin/<group>/vo/XxxRespVO.java`
5. `controller/admin/<group>/vo/XxxSaveReqVO.java`
6. `service/<group>/XxxService.java` + `XxxServiceImpl.java`
7. `controller/admin/<group>/XxxController.java`

权限码命名：`english:<resource>:<action>`，例：`english:exam:create`。

> 规模：24 张表 × 6~7 文件 ≈ 150 个文件。建议：
>  - 先用本样板跑通 `esc_exam` 的 create / update / page
>  - 后续用 yudao 自带的 **代码生成器**（`yudao-module-infra` 的 codegen）扫库自动生成
>  - 或按需手写：题库侧只做 CRUD，交互复杂点是 `esc_exam_part`（多态）和 `esc_practice_session`（嵌套存储）

## 错误码段

`1-040-xxx-xxx`（见 `ErrorCodeConstants.java`），与 yudao 其它模块不冲突：
- system: 1-002-xxx
- infra:  1-001-xxx
- ai:     1-040-xxx  ← 注意：yudao-module-ai 也用 1-040-xxx，如有冲突调整本模块为 `1-050-xxx-xxx`

## 本模块没有做的事

- 多态 `esc_exam_part` 的子表分发（需要结合 `part_type` 再路由到子表 mapper）
- 学生登录 JWT Provider（复用 yudao 的 JWT，但 `userType=student` 需要额外 Provider）
- AI 调用配额拦截（由 `realtime/web/server.py` 侧承担，kugua 只提供配置与记录）

以上留到需求对齐后再补。
