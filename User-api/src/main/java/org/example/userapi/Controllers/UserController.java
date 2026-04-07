package org.example.userapi.Controllers;

import org.example.userapi.Model.SiteUser;
import org.example.userapi.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserService userService;



    @GetMapping("/search/{email}")
    public ResponseEntity<SiteUser> search(@PathVariable String email){
       Optional<SiteUser> user = userService.findUser(email);
        if(user.isPresent()){
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getall/{isProvider}")
    public ResponseEntity<List<SiteUser>> random(@PathVariable boolean isProvider){
        Optional<List<SiteUser>> allUsers = userService.allUsers(isProvider);
        if(allUsers.isPresent()){
            return ResponseEntity.ok(allUsers.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/signup")
    public ResponseEntity save(@RequestBody SiteUser user) {

        if (userService.saveUser(user)) {
            return ResponseEntity.ok().build();
        }else  {
            return ResponseEntity.badRequest().build();
        }

    }


    @PutMapping("/update")
    public ResponseEntity update(@RequestBody SiteUser user , @RequestHeader("X-User-Name") String username){
        if (userService.updateUser(user ,username)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity delete(@PathVariable String email ,  @RequestHeader("X-User-Name") String user){
        if (userService.deleteUser(email,user)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }









}
