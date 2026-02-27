package com.bytetune.mapper;

import com.bytetune.entity.Song;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
