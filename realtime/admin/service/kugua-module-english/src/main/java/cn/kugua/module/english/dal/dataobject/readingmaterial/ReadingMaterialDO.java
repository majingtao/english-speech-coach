package cn.kugua.module.english.dal.dataobject.readingmaterial;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("esc_reading_material")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReadingMaterialDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String textEn;
    private String textCn;
    private String description;
    private String materialType;
    private String partOfSpeech;
    private String levelCode;
    private String tagsJson;
    private String examplesJson;
    private String wordFormsJson;
    private Integer sort;
    private Integer status;

    @TableField(exist = false)
    private Long vocabId;
    @TableField(exist = false)
    private String vocabWord;
    @TableField(exist = false)
    private String audioUkUrl;
    @TableField(exist = false)
    private String audioUsUrl;
}
