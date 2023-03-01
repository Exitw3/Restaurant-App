package com.codegym.controller.api;


import com.codegym.exception.DataInputException;
import com.codegym.exception.EmailExistsException;
import com.codegym.model.JwtResponse;
import com.codegym.model.Role;
import com.codegym.model.User;
import com.codegym.model.dto.RoleDTO;
import com.codegym.model.dto.UserDTO;
import com.codegym.service.jwt.JwtService;
import com.codegym.service.role.IRoleService;
import com.codegym.service.user.IUserService;
import com.codegym.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private AppUtils appUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return appUtils.mapErrorToResponse(bindingResult);

        Boolean existsByUsername = userService.existsByUsername(userDTO.getUsername());

        if (existsByUsername) {
            throw new EmailExistsException("Tài khoản đã tồn tại");
        }

        Optional<Role> optRole = roleService.findById(userDTO.getRole().getId());

        if (!optRole.isPresent()) {
            throw new DataInputException("Vai trò không hợp lệ");
        }

        try {
            userService.save(userDTO.toUser());

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            throw new DataInputException("Thông tin tài khoản không đúng, vui lòng kiểm tra lại");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return appUtils.mapErrorToResponse(bindingResult);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.getByUsername(user.getUsername());

            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    currentUser.getId(),
                    userDetails.getUsername(),
                    currentUser.getUsername(),
                    userDetails.getAuthorities()
            );

            ResponseCookie springCookie = ResponseCookie.from("JWT", jwt)
                    .httpOnly(false)
                    .secure(false)
                    .path("/")
                    .maxAge(60 * 1000)
                    .domain("localhost")
                    .build();

            System.out.println(jwtResponse);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                    .body(jwtResponse);
        } catch (Exception e) {
            throw new DataInputException("Tài khoản không tồn tại");
        }
    }
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRole() {

        List<RoleDTO> roleDTOS = roleService.getAllRoleDTO();

        if (roleDTOS.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(roleDTOS, HttpStatus.OK);
    }

}