package com.example;

import javax.persistence.*;
import java.util.List;

/**
 * Created by earlbozarth on 11/13/15.
 */

@Entity
public class Artist {

    @Id
    @GeneratedValue
    Integer id;

    String artistName;
    String artistImage;
    String artistLink;

    @ManyToOne
    Genre tempGenre;

    @OneToMany(mappedBy = "tempArtist")
    List<Album> albumList;

}
