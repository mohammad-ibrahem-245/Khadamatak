package org.example.userapi.Controllers;

import org.example.userapi.Model.SiteUser;
import org.example.userapi.Model.SiteUser.UserRole;
import org.example.userapi.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getall")
    public ResponseEntity<List<SiteUser>> random(@RequestParam(required = false) UserRole role,
                                                 @RequestHeader("X-Role") UserRole requesterRole){
        if (requesterRole != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        return userService.allUsers(role)
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
    public ResponseEntity<Void> update(@RequestBody SiteUser user , @RequestHeader("X-User-Name") String username, @RequestHeader("X-Role") UserRole requesterRole){
        if (userService.updateUser(user ,username, requesterRole)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email ,  @RequestHeader("X-User-Name") String user, @RequestHeader("X-Role") UserRole requesterRole){
        if (userService.deleteUser(email,user,requesterRole)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }









}
