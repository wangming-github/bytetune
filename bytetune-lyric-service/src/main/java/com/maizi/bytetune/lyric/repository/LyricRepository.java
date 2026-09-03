package com.maizi.bytetune.lyric.repository;

import com.maizi.bytetune.lyric.entity.Lyric;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LyricRepository extends MongoRepository<Lyric, String> {

    Optional<Lyric> findBySongNameAndSinger(String songName, String singer);

}