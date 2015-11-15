package com.example;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by earlbozarth on 11/13/15.
 */
public interface ArtistRepo extends CrudRepository<Artist, Integer> {

    List<Artist> findByTempGenre(Genre genre);

}
