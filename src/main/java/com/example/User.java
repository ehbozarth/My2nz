package com.example;

import javax.persistence.*;
import java.util.List;

/**
 * Created by earlbozarth on 11/13/15.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    Integer id;

    String userName;
    String password;

    @OneToMany(mappedBy = "tempUser")
    List<Favorite> favList;


}
