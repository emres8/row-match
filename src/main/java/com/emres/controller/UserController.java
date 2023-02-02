package com.emres.controller;

import com.emres.model.User;
import com.emres.repository.UserRepository;
import com.emres.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public List<User> index(){
        return userRepository.findAll();
    }



    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable(value = "id") Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }


    public static final String FIND_USERS = "SELECT id,level,coin FROM user";
    @PostMapping("/user")
    @Query(value = FIND_USERS, nativeQuery = true)
    public User create(@RequestBody Map<String, String> body){
        String name = body.get("name");
        String lastName = body.get("lastName");
        Integer level = Integer.parseInt(body.get("level"));
        Integer coin = Integer.parseInt(body.get("coin"));
        return userRepository.save(new User(name, lastName, level, coin));
    }

    /*
    @PutMapping("/user/{id}")
    public User update(@PathVariable String id, @RequestBody Map<String, String> body){
        Long userId = Long.parseLong(id);
        Integer coin = Integer.parseInt(body.get("coin"));
        Integer level = Integer.parseInt(body.get("level"));

        User user = userRepository.findOne(userId);
        user.setCoin(coin);
        user.setLevel(level);
        return userRepository.save(user)
    }
*/


}