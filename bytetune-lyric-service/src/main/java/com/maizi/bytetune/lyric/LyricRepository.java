package com.maizi.bytetune.lyric;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LyricRepository extends MongoRepository<Lyric, String> {

    Optional<Lyric> findBySongNameAndSinger(String songName, String singer);

}