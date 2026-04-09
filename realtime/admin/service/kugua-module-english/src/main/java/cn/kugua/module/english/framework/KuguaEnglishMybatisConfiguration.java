package cn.kugua.module.english.framework;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * kugua-module-english 的 MyBatis Mapper 扫描配置
 * <p>
 * 原因：yudao 的 {@code YudaoMybatisAutoConfiguration} 仅扫描 {@code ${yudao.info.base-package}}
 * ( = cn.iocoder.yudao )，kugua 自研模块的 Mapper 在 cn.kugua 下，需要单独 @MapperScan 才能被注册为 Bean。
 */
@AutoConfiguration
@MapperScan(value = "cn.kugua.module.english.dal.mysql", annotationClass = Mapper.class)
public class KuguaEnglishMybatisConfiguration {
}
