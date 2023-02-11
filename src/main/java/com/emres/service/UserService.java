package com.emres.service;

import com.emres.exception.ResourceNotFoundException;
import com.emres.model.User;
import com.emres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }


    public User.NewUserResponse createUser(User.NewUserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setCoin(5000);
        user.setLevel(1);
        userRepository.save(user);
        User.NewUserResponse response = new User.NewUserResponse(user.getId(),user.getLevel(),user.getCoin());
        return response;
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }


    public User updateLevel(Long userId){
        int coinPerLevel = 25;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setLevel(user.getLevel() + 1);
        user.setCoin((user.getCoin() + coinPerLevel));
        userRepository.save(user);
        return user;
    }


}
