package org.example.userapi.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.userapi.Model.SiteUser;
import org.example.userapi.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {



    @Autowired
    UserRepo userRepo;
    @Autowired
    private final BCrypt.Hasher passwordHasher;




    public UserService(BCrypt.Hasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }



    /// searching for user
    public Optional<SiteUser> findUser(String username) {
        Optional<SiteUser> user = Optional.ofNullable(userRepo.findByEmail(username));
        return user;
    }



    public Optional<List<SiteUser>> allUsers(boolean isProvider) {

        List<SiteUser> users;


            users = userRepo.findAllByIsProvider(isProvider);

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users);
    }



    /// signing up a new user
    public boolean saveUser(SiteUser user){
        if (!checkUserExists(user.getEmail())) {

            user.setPassword(passwordHasher.hashToString(12, user.getPassword().toCharArray()));
            user.setCreated(Date.from(new Date().toInstant()));
            userRepo.save(user);
            if (findUser(user.getEmail()).isPresent()) {
                return true;
            }
        }
        return false ;
    }



    /// deleting a username account
    public boolean deleteUser(String username , String user){
        if(username.equals(user)) {
            if (checkUserExists(username)) {
                findUser(username).ifPresent(userRepo::delete);
                return true;
            }
        }
        return false;
    }



    /// updating user data
    public boolean updateUser(SiteUser updatedUser ,String username){
        if(username.equals(updatedUser.getEmail())) {
            if (checkUserExists(updatedUser.getEmail())) {
                SiteUser oldUser = userRepo.findByEmail(updatedUser.getEmail());
                oldUser.setName(updatedUser.getName());
                oldUser.setBio(updatedUser.getBio());
                oldUser.setPassword(updatedUser.getPassword());
                oldUser.setImage(updatedUser.getImage());
                oldUser.setCareer(updatedUser.getCareer());
                userRepo.save(oldUser);
                return true;
            }
        }
        return false;

    }





    /// internal usage only !! used to check the existence of a user
    private boolean checkUserExists(String user){

        return userRepo.existsByEmail(user);

    }



}
