package org.example.userapi.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.userapi.Model.SiteUser;
import org.example.userapi.Model.SiteUser.UserRole;
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
        return Optional.ofNullable(userRepo.findByEmail(username));
    }



    public Optional<List<SiteUser>> allUsers(UserRole role) {

        List<SiteUser> users;

        if (role == null) {
            users = userRepo.findAll();
        } else {
            users = userRepo.findAllByRole(role);
        }

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users);
    }



    /// signing up a new user
    public boolean saveUser(SiteUser user){
        if (!checkUserExists(user.getEmail())) {

            user.setRole(UserRole.USER);
            user.setPassword(passwordHasher.hashToString(12, user.getPassword().toCharArray()));
            user.setCreated(Date.from(new Date().toInstant()));
            userRepo.save(user);
            return findUser(user.getEmail()).isPresent();
        }
        return false ;
    }



    /// deleting a username account
    public boolean deleteUser(String username , String user, UserRole requesterRole){
        if(isAdmin(requesterRole) || username.equals(user)) {
            if (checkUserExists(username)) {
                findUser(username).ifPresent(userRepo::delete);
                return true;
            }
        }
        return false;
    }



    /// updating user data
    public boolean updateUser(SiteUser updatedUser ,String username, UserRole requesterRole){
        if(isAdmin(requesterRole) || username.equals(updatedUser.getEmail())) {
            if (checkUserExists(updatedUser.getEmail())) {
                SiteUser oldUser = userRepo.findByEmail(updatedUser.getEmail());
                oldUser.setName(updatedUser.getName());
                oldUser.setBio(updatedUser.getBio());
                oldUser.setPassword(updatedUser.getPassword());
                oldUser.setImage(updatedUser.getImage());
                oldUser.setCareer(updatedUser.getCareer());
                if (isAdmin(requesterRole) && updatedUser.getRole() != null) {
                    oldUser.setRole(updatedUser.getRole());
                }
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

    private boolean isAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }



}
