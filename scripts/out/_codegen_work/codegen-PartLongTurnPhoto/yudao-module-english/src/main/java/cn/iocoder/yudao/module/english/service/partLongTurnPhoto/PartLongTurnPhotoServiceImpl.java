package cn.iocoder.yudao.module.english.service.partLongTurnPhoto;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.english.controller.admin.partLongTurnPhoto.vo.*;
import cn.iocoder.yudao.module.english.dal.dataobject.partLongTurnPhoto.PartLongTurnPhotoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.english.dal.mysql.partLongTurnPhoto.PartLongTurnPhotoMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.english.enums.ErrorCodeConstants.*;

/**
 * 独立长答 - 图片描述 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class PartLongTurnPhotoServiceImpl implements PartLongTurnPhotoService {

    @Resource
    private PartLongTurnPhotoMapper partLongTurnPhotoMapper;

    @Override
    public Long createPartLongTurnPhoto(PartLongTurnPhotoSaveReqVO createReqVO) {
        // 插入
        PartLongTurnPhotoDO partLongTurnPhoto = BeanUtils.toBean(createReqVO, PartLongTurnPhotoDO.class);
        partLongTurnPhotoMapper.insert(partLongTurnPhoto);

        // 返回
        return partLongTurnPhoto.getId();
    }

    @Override
    public void updatePartLongTurnPhoto(PartLongTurnPhotoSaveReqVO updateReqVO) {
        // 校验存在
        validatePartLongTurnPhotoExists(updateReqVO.getId());
        // 更新
        PartLongTurnPhotoDO updateObj = BeanUtils.toBean(updateReqVO, PartLongTurnPhotoDO.class);
        partLongTurnPhotoMapper.updateById(updateObj);
    }

    @Override
    public void deletePartLongTurnPhoto(Long id) {
        // 校验存在
        validatePartLongTurnPhotoExists(id);
        // 删除
        partLongTurnPhotoMapper.deleteById(id);
    }

    @Override
        public void deletePartLongTurnPhotoListByIds(List<Long> ids) {
        // 删除
        partLongTurnPhotoMapper.deleteByIds(ids);
        }


    private void validatePartLongTurnPhotoExists(Long id) {
        if (partLongTurnPhotoMapper.selectById(id) == null) {
            throw exception(PART_LONG_TURN_PHOTO_NOT_EXISTS);
        }
    }

    @Override
    public PartLongTurnPhotoDO getPartLongTurnPhoto(Long id) {
        return partLongTurnPhotoMapper.selectById(id);
    }

    @Override
    public PageResult<PartLongTurnPhotoDO> getPartLongTurnPhotoPage(PartLongTurnPhotoPageReqVO pageReqVO) {
        return partLongTurnPhotoMapper.selectPage(pageReqVO);
    }

}