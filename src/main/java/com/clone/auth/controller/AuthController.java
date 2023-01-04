package com.clone.auth.controller;

import com.clone.auth.model.User;
import com.clone.auth.payload.request.LoginRequest;
import com.clone.auth.payload.request.SignupRequest;
import com.clone.auth.payload.response.UserInfoResponse;
import com.clone.auth.repository.UserRepository;
import com.clone.auth.security.jwt.JwtUtils;
import com.clone.auth.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/auth")
public class AuthController {

   @Autowired
   private AuthenticationManager authenticationManager;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private PasswordEncoder passwordEncoder;

   @Autowired
   private JwtUtils jwtUtils;


   @PostMapping("/signin")
   public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

      Authentication authentication = authenticationManager
              .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
              .body(new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),userDetails.getEmail()));
   }

   @PostMapping("/signup")
   public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
         return ResponseEntity.badRequest().body("Error: Username is already taken!");
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
         return ResponseEntity.badRequest().body("Error: Email is already in use!");
      }

      // Create new user's account
      User user = new User(signUpRequest.getName(),signUpRequest.getSurname(),signUpRequest.getUsername(),signUpRequest.getEmail(),
              passwordEncoder.encode(signUpRequest.getPassword()));

      userRepository.save(user);

      return ResponseEntity.ok("User registered successfully!");
   }
}
