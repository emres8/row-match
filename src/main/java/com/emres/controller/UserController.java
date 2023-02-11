package com.emres.controller;

import com.emres.model.User;
import com.emres.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
public class UserController {


    private final UserService userService;

    @Autowired
    UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getUsers(){
        return userService.getUsers();
    }


    @GetMapping("{userId}")
    public User getUserById(@PathVariable(value = "userId") Long userId) {
       return userService.getUserById(userId);
    }



    @PostMapping()
    public User.NewUserResponse createUser(@RequestBody User.NewUserRequest request){
       return userService.createUser(request);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long id){
        userService.deleteUser(id);
    }

    @PostMapping("/levelup/{userId}")
    public User updateLevel(@PathVariable("userId") Long userId){
        return userService.updateLevel(userId);
    }


}