package com.maizi.bytetune.common.service;

import com.maizi.bytetune.common.dto.SongFileInfo;

import java.util.List;

public interface SongExtService {
    /**
     * 第一次启动项目时扫描文件列表并批量入库数据库
     *
     * @param files 待处理的本地文件信息列表
     */
    void loadExistingSongs(List<SongFileInfo> files);
}
