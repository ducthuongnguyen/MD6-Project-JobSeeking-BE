package com.codegym.controller;


import com.codegym.advice.CommonException;
import com.codegym.advice.ValidationErrorResponse;
import com.codegym.advice.Violation;
import com.codegym.constant.Constant;
import com.codegym.model.dto.request.SignInForm;
import com.codegym.model.dto.request.SignUpCompanyForm;
import com.codegym.model.dto.request.SignUpForm;
import com.codegym.model.dto.response.*;
import com.codegym.model.entity.Company;
import com.codegym.model.entity.Role;
import com.codegym.model.entity.User;
import com.codegym.security.jwt.JwtProvider;
import com.codegym.security.jwt.JwtTokenFilter;
import com.codegym.security.userprincal.UserPrinciple;
import com.codegym.service.company.CompanyService;
import com.codegym.service.role.RoleServiceImpl;
import com.codegym.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@CrossOrigin("*")
@RequestMapping("")
@RestController
@Slf4j
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    CompanyService companyService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    JwtTokenFilter jwtTokenFilter;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpForm signUpForm) {
        if (userService.existsByUsername(signUpForm.getUsername())) {
            return new ResponseEntity<>(new ResponMessage("no_user"), HttpStatus.OK);
        }
        if (userService.existsByEmail(signUpForm.getEmail())) {
            return new ResponseEntity<>(new ResponMessage("no_email"), HttpStatus.OK);
        }

        if (signUpForm.getAvatar() == null || signUpForm.getAvatar().trim().isEmpty()) {
            signUpForm.setAvatar("https://firebasestorage.googleapis.com/v0/b/blog-eab4c.appspot.com/o/images%2Fth%20(1).jpg?alt=media&token=aff3ee5b-f7c2-419a-98bb-9dd3e48041bd");
        }
        User user = new User(signUpForm.getName(), signUpForm.getUsername(), signUpForm.getEmail(), passwordEncoder.encode(signUpForm.getPassword()));
        Set<String> strRoles = signUpForm.getRoles();
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleService.findByName(Constant.RoleName.ADMIN).orElseThrow(
                            () -> new RuntimeException("Role not found")
                    );
                    roles.add(adminRole);
                    break;
                case "company":
                    Role companyRole = roleService.findByName(Constant.RoleName.COMPANY).orElseThrow(
                            () -> new RuntimeException("Role not found")
                    );
                    roles.add(companyRole);
                    break;
                default:
                    Role userRole = roleService.findByName(Constant.RoleName.USER).orElseThrow(
                            () -> new RuntimeException("Role not found"));
                    roles.add(userRole);

            }
        });
        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(new ResponMessage("Create User Account Success!"), HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@Valid @RequestBody SignInForm signInForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token, userPrinciple.getId(), userPrinciple.getName(), userPrinciple.getUsername(), userPrinciple.getEmail(), userPrinciple.getAvatar(), userPrinciple.getAuthorities()));
    }

    @PostMapping("/sign-in-company")
    public ResponseEntity<?> login1(@Valid @RequestBody SignUpCompanyForm signUpCompanyForm) {
        Optional<Company> company = companyService.findByEmail(signUpCompanyForm.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpCompanyForm.getEmail(), signUpCompanyForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        JwtResponse jwtResponse;
        if (company.isPresent()){
            jwtResponse = new JwtResponse(token, company.get().getId(), userPrinciple.getName(), userPrinciple.getUsername(), userPrinciple.getEmail(), userPrinciple.getAvatar(), userPrinciple.getAuthorities());
        } else {
            jwtResponse = new JwtResponse(token, userPrinciple.getId(), userPrinciple.getName(), userPrinciple.getUsername(), userPrinciple.getEmail(), userPrinciple.getAvatar(), userPrinciple.getAuthorities());
        }
        return ResponseEntity.ok(jwtResponse);

    }

    @PostMapping(value = "/sign-up-company", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyResponseBody<?>> register(@Valid @RequestPart(value = "company") SignUpCompanyForm signUpCompanyForm, @RequestPart(value = "image", required = false) MultipartFile image) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        try {
            return new ResponseEntity<>(new MyResponseBody(Response.SUCCESS, companyService.create(signUpCompanyForm, image )), HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation c : e.getConstraintViolations()) {
                error.getViolations().add(new Violation(c.getPropertyPath().toString(), c.getMessage()));
            }
            return new ResponseEntity<>(new MyResponseBody(Response.OBJECT_INVALID, error), HttpStatus.BAD_REQUEST);
        } catch (CommonException e) {
            return new ResponseEntity<>(new MyResponseBody(e.getResponse(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new MyResponseBody(Response.SYSTEM_ERROR, e.getMessage()), HttpStatus.OK);
        }
    }
}
