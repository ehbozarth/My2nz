package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by earlbozarth on 11/13/15.
 */

@Entity
public class Genre {

    @Id
    @GeneratedValue
    Integer id;

    String genreName;
    String genreImage;

    @OneToMany(mappedBy = "tempGenre")
    List<Artist> artistList;
}
