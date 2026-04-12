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
       return userService.findUser(email)
               .map(ResponseEntity::ok)
               .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getall/{isProvider}")
    public ResponseEntity<List<SiteUser>> random(@PathVariable boolean isProvider, @RequestHeader("X-Is-Admin") boolean isAdmin){
        if (!isAdmin) {
            return ResponseEntity.status(403).build();
        }
        return userService.allUsers(isProvider)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> save(@RequestBody SiteUser user) {

        if (userService.saveUser(user)) {
            return ResponseEntity.ok().build();
        }else  {
            return ResponseEntity.badRequest().build();
        }

    }


    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody SiteUser user , @RequestHeader("X-User-Name") String username, @RequestHeader("X-Is-Admin") boolean isAdmin){
        if (userService.updateUser(user ,username, isAdmin)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email ,  @RequestHeader("X-User-Name") String user, @RequestHeader("X-Is-Admin") boolean isAdmin){
        if (userService.deleteUser(email,user,isAdmin)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }









}
