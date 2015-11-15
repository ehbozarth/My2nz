package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by earlbozarth on 11/13/15.
 */
@Entity
public class Favorite {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    User tempUser;

    @ManyToOne
    Artist tempArtist;
}
