package com.example.demo.controller;

import com.example.demo.GetSelf;
import com.example.demo.User;
import com.example.demo.exception.EmailExistException;
import com.example.demo.exception.NotValidEmailException;
import com.example.demo.repository.UserRepository;
import org.apache.coyote.Response;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
//    @Autowired
//    PasswordEncoder passwordEncoder;

    @GetMapping("/healthz")
    public String test () {
        return "";
    }

    @GetMapping("/v1/user/self")
    public GetSelf getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());

        GetSelf getSelf = new GetSelf();

        getSelf.setId(userData.getId());
        getSelf.setUsername(userData.getEmailAddress());
        getSelf.setFirst_name(userData.getFirstName());
        getSelf.setLast_name(userData.getLastName());
        getSelf.setAccount_created(userData.getAccount_created());
        getSelf.setAccount_updated(userData.getAccount_updated());

        return getSelf;
//        return userData;
    }

    @PostMapping("/v1/user")
    public User createUser(@Valid @RequestBody User providedUser) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(providedUser.getEmailAddress());
        if (!mat.matches()) {
            throw new NotValidEmailException();
        }
        if (userRepository.findByEmailAddress(providedUser.getEmailAddress())!=null) {
            throw new EmailExistException();
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        providedUser.setPassword(bCryptPasswordEncoder.encode(providedUser.getPassword()));
        return userRepository.save(providedUser);
    }

    @PutMapping("/v1/user/self")
    public User updateUser(@Valid @RequestBody User userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userData = userRepository.findByEmailAddress(auth.getName());

        userData.setFirstName(userDetails.getFirstName());
        userData.setLastName(userDetails.getLastName());
        userData.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(userData);
    }
}
