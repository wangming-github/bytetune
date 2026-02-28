package com.maizi.bytetune.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maizi.bytetune.common.entity.Song;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 歌曲信息表 Mapper 接口
 * </p>
 *
 * @author maizi
 * @since 2026-02-26
 */
@Mapper
public interface SongMapper extends BaseMapper<Song> {

}
