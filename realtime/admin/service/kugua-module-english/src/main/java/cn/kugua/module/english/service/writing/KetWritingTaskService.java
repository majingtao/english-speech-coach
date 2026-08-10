package cn.kugua.module.english.service.writing;

import cn.kugua.module.english.dal.dataobject.writing.KetWritingTaskDO;

import java.util.List;

public interface KetWritingTaskService {

    List<KetWritingTaskDO> getPublishedTasks(String levelCode);
}
