package cn.kugua.module.english.service.writing;

import cn.kugua.module.english.dal.dataobject.writing.KetWritingTaskDO;
import cn.kugua.module.english.dal.mysql.writing.KetWritingTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KetWritingTaskServiceImpl implements KetWritingTaskService {

    @Resource
    private KetWritingTaskMapper taskMapper;

    @Override
    public List<KetWritingTaskDO> getPublishedTasks(String levelCode) {
        String level = levelCode == null || levelCode.trim().isEmpty() ? "ket" : levelCode.trim().toLowerCase();
        return taskMapper.selectPublished(level);
    }
}
