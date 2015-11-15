package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by earlbozarth on 11/13/15.
 */

@Entity
public class Album {

    @Id
    @GeneratedValue
    Integer id;

    String albumName;
    String albumImage;
    String albumLink;

    @ManyToOne
    Artist tempArtist;

}
