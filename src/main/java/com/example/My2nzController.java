package com.example;

import com.theironyard.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by earlbozarth on 11/13/15.
 */
@Controller
public class My2nzController {


    @Autowired
    UserRepo userRepo;
    @Autowired
    GenreRepo genreRepo;
    @Autowired
    ArtistRepo artistRepo;
    @Autowired
    AlbumRepo albumRepo;
    @Autowired
    FavRepo favRepo;


    @PostConstruct
    public void init() throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userRepo.findOneByUserName("Default name");
        if(user == null){
            user = new User();
            user.userName = "Default Name";
            user.password = PasswordHash.createHash("hunter2");
            userRepo.save(user);
        }

        if (genreRepo.count() == 0) {
            String fileContent = readFile("genres.csv");
            String [] lines = fileContent.split("\n");
            for(String line : lines){
                if(line == lines[0]){
                    continue;
                }
                Genre tempGenre = new Genre();
                String [] columns = line.split(",");
                tempGenre.genreName = columns[0].trim();
                tempGenre.genreImage = columns[1].trim();
                genreRepo.save(tempGenre);
            }
        }//End of Reading Genres File into GenreRepo

        if (artistRepo.count() == 0) {
            String fileContent = readFile("artists.csv");
            String [] lines = fileContent.split("\n");
            for(String line : lines){
                if(line == lines[0]){
                    continue;
                }
                String [] columns = line.split(",");
                Artist tempArtist = new Artist();
                Genre tempGenre1 = genreRepo.findOne(Integer.valueOf(columns[0]));
                tempArtist.tempGenre = tempGenre1;
                tempArtist.artistName = columns[1].trim();
                tempArtist.artistImage = columns[2].trim();
                tempArtist.artistLink = columns[3].trim();
                artistRepo.save(tempArtist);
            }
        }//End of Reading Artists File into ArtistRepo

        if (albumRepo.count() == 0) {
            String fileContent = readFile("albums.csv");
            String [] lines = fileContent.split("\n");
            for(String line : lines){
                if(line == lines[0]){
                    continue;
                }
                String [] columns = line.split(",");
                Album tempAlbum = new Album();
                Artist tempArtist1 = artistRepo.findOne(Integer.valueOf(columns[0]));
                tempAlbum.tempArtist = tempArtist1;
                tempAlbum.albumName = columns[1].trim();
                tempAlbum.albumImage = columns[2].trim();
                tempAlbum.albumLink = columns[3].trim();
                albumRepo.save(tempAlbum);
            }
        }//End of Reading Albums File into AlbumRepo


    }//End of init Method


    @RequestMapping("/")
    public String home(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("userName");

        if(username == null){
            return "login";
        }

        model.addAttribute("genres", genreRepo.findAll());
        model.addAttribute("artists", artistRepo.findAll());
        model.addAttribute("albums", albumRepo.findAll());
        return "home";
    }


    @RequestMapping("/login")
    public String login(HttpServletRequest request, String username, String password) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepo.findOneByUserName(username);
        if(user == null){
            user = new User();
            user.userName = username;
            user.password = PasswordHash.createHash(password); //hashing the password
            userRepo.save(user);
        }
        else if(!PasswordHash.validatePassword(password, user.password)){ //user input password checked against the database password
            throw new Exception("Wrong password");
        }
        session.setAttribute("userName", username);
        return "redirect:/";
    }

    @RequestMapping("/add-genre")
    public String addGenre(String genreName, String genreImage, HttpSession session) throws Exception {
        //create and use session to help join values from different tables
        String username = (String) session.getAttribute("userName");
        if(username == null){
            throw new Exception("You are not logged in here");
        }
        User user = userRepo.findOneByUserName(username);
        Genre tempGenre = new Genre();
        tempGenre.genreName = genreName;
        tempGenre.genreImage = genreImage;

        genreRepo.save(tempGenre);
        return "redirect:/";
    }//End of add-genre


    @RequestMapping("/add-artist")
    public String addArtist(String artistName, String artistImage, String artistLink, int genreId, HttpSession session) throws Exception {
        //create and use session to help join values from different tables
        String username = (String) session.getAttribute("userName");
        if(username == null){
            throw new Exception("You are not logged in here");
        }
        User user = userRepo.findOneByUserName(username);
        Artist tempArtist = new Artist();
        tempArtist.artistName = artistName;
        tempArtist.artistImage = artistImage;
        tempArtist.artistLink = artistLink;
        tempArtist.tempGenre = genreRepo.findOne(genreId);
        artistRepo.save(tempArtist);
        return "redirect:/artists?genreId=" + genreId;
    }//End of add-artist

    @RequestMapping("/add-album")
    public String addAlbum(String albumName, String albumImage, String albumLink,int artistId, HttpSession session) throws Exception {
        //create and use session to help join values from different tables
        String username = (String) session.getAttribute("userName");
        if(username == null){
            throw new Exception("You are not logged in here");
        }
        User user = userRepo.findOneByUserName(username);
        Album tempAlbum = new Album();
        tempAlbum.albumName = albumName;
        tempAlbum.albumImage = albumImage;
        tempAlbum.albumLink = albumLink;
        tempAlbum.tempArtist = artistRepo.findOne(artistId);
        albumRepo.save(tempAlbum);
        return "redirect:/albums?artistId=" + artistId;
    }//End of add-album


    @RequestMapping("/artists")
    public String artists(Model model, HttpSession session, int genreId){
        String username = (String) session.getAttribute("userName");
        if(username == null){
            return "login";
        }
        Genre tempGenre = genreRepo.findOne(genreId);
        model.addAttribute("genreId",genreId);
        model.addAttribute("artists", artistRepo.findByTempGenre(tempGenre));
        return "artists";
    }

    @RequestMapping("/albums")
    public String albums(Model model, HttpSession session, int artistId){
        String username = (String) session.getAttribute("userName");
        if(username == null){
            return "login";
        }
        Artist tempArtist = artistRepo.findOne(artistId);
        model.addAttribute("albums", albumRepo.findByTempArtist(tempArtist));
        model.addAttribute("artistId", artistId);
        return "albums";
    }

    @RequestMapping("/editp-artist")
    public String editArtist(int artistId, String artistName, String artistImage, String artistLink, HttpSession session) throws Exception {
        if(session.getAttribute("userName") == null){
            throw new Exception("Not Logged In");
        }
        Artist tempArtist = artistRepo.findOne(artistId);
        tempArtist.artistName = artistName;
        tempArtist.artistImage = artistImage;
        tempArtist.artistLink = artistLink;
        artistRepo.save(tempArtist);
        return "redirect:/";
    }

    @RequestMapping("/editg-artist")
    public String editArtist(Model model, HttpSession session, int artistId) throws Exception {
        if(session.getAttribute("userName") == null){
            throw new Exception("Not Logged In");
        }
        model.addAttribute("artistId", artistId);
        return "editp-artist";
    }

    @RequestMapping("/delete-artist")
    public String deleteArtist(Model model, HttpSession session, int artistId) throws Exception {
        if(session.getAttribute("userName") == null){
            throw new Exception("Not Logged In");
        }
        artistRepo.delete(artistId);
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    static String readFile(String fileName) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            int fileSize = (int) f.length();
            char[] fileContent = new char[fileSize];
            fr.read(fileContent);
            return new String(fileContent);
        } catch (Exception e) {
            return null;
        }
    }//End of readFile()

}//End of My2nz Controller
