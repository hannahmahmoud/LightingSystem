package com.lightingsystem.lightingsystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lightingsystem.lightingsystem.Model.AuthUser;
import com.lightingsystem.lightingsystem.Repository.userRepo;
import com.lightingsystem.lightingsystem.Response.ResponseMessage;
import com.lightingsystem.lightingsystem.Validators.signupValidator;
import com.lightingsystem.lightingsystem.config.jwtUtil;
import com.lightingsystem.lightingsystem.Validators.loginValidator;;

@Service

public class userServics {

    private ResponseMessage message;

    @Autowired
    private userRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private jwtUtil jwtUtil;
    private AuthUser authUser;

    public ResponseEntity<Object> signup(signupValidator newUser) {
        boolean oldUser = userRepository.existsByEmail(newUser.getEmail());
        if (oldUser == true) {
            message = new ResponseMessage("Failed", "Email is alr used!");
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }

        authUser = new AuthUser(
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getEmail(),
                this.passwordEncoder.encode(newUser.getPassword()),
                newUser.getPhoneNumber()

        );
        userRepository.save(authUser);
        newUser.setPassword(null);

        ResponseMessage message = new ResponseMessage();
        message.setStatus("Success");
        message.setUser(newUser);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> login(loginValidator user)

    {
        authUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (authUser == null) {
            message = new ResponseMessage("Failed!", "Incorrect  Email ");
            return new ResponseEntity(message, HttpStatus.NOT_FOUND);
        }
        String password = user.getPassword();
        String hashedPassword = authUser.getPassword();
        if (!passwordEncoder.matches(password, hashedPassword)) {
            message = new ResponseMessage("Failed!", "Incorrect Password!");
            return new ResponseEntity(message, HttpStatus.NOT_FOUND);

        }
        // matnsesh t3mli hwar token dah mohem
        String token = jwtUtil.generateToken(user.getEmail());

        message = new ResponseMessage("Success", "Login successful", token);
        return new ResponseEntity(message, HttpStatus.OK);

    }

}