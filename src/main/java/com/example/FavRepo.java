package com.example;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by earlbozarth on 11/13/15.
 */
public interface FavRepo extends CrudRepository<Favorite, Integer> {
}
