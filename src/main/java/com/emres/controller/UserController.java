package com.emres.controller;

import com.emres.model.User;
import com.emres.repository.UserRepository;
import com.emres.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
public class UserController {


    private final UserRepository userRepository;

    @Autowired
    UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping()
    public List<User> getUsers(){
        return userRepository.findAll();
    }


    @GetMapping("{userId}")
    public User getUserById(@PathVariable(value = "userId") Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    record NewUserRequest(
            String name,
            String email){
    }
    record NewUserResponse(
            Long id,
            Integer level,
            Integer coin
    ){}

    @PostMapping()
    public NewUserResponse createUser(@RequestBody NewUserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setCoin(5000);
        user.setLevel(1);
        userRepository.save(user);
        NewUserResponse response = new NewUserResponse(user.getId(),user.getLevel(),user.getCoin());
        return response;
    }
    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") Long id){
        userRepository.deleteById(id);
    }

    @PostMapping("/levelup/{userId}")
    public User updateLevel(@PathVariable("userId") Long userId){
        int coinPerLevel = 25;
         User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setLevel(user.getLevel() + 1);
        user.setCoin((user.getCoin() + coinPerLevel));
        userRepository.save(user);
        return user;
    }


}