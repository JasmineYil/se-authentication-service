package org.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.CustomerDto;
import org.example.dto.CustomerLoginDto;
import org.example.dto.LoginResponseDto;
import org.example.exception.EmailAlreadyInUseException;
import org.example.model.Customer;
import org.example.service.CustomerService;
import org.example.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
//@CrossOrigin(origins = "http://localhost:9090") // The URL of frontend
public class CustomerController {

    private final JwtService jwtService;
    private final CustomerService customerService;
    private final HttpServletResponse httpServletResponse;

    public CustomerController(JwtService jwtService, CustomerService customerService, HttpServletResponse httpServletResponse) {
        this.jwtService = jwtService;
        this.customerService = customerService;
        this.httpServletResponse = httpServletResponse;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerDto customerDto) {
        try {
            customerService.registerCustomer(customerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer registered successfully");
        } catch (EmailAlreadyInUseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The information provided cannot be used to create a new account. Please double-check your details or try different ones.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Customer registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody CustomerLoginDto loginCustomerDto) {
        try {
            Customer loginCustomer = customerService.authenticateCustomer(loginCustomerDto);
            String jwtToken = jwtService.generateToken(loginCustomer);

            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setJwtToken(jwtToken);
            loginResponseDto.setJwtExpirationInMilliseconds(jwtService.getExpirationTime());

            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) jwtService.getExpirationTime());
            httpServletResponse.addCookie(cookie);

            return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Customer login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutCustomer(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().body("Customer logged out successfully");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkCustomerSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName()) && jwtService.validateToken(cookie.getValue())) {
                    return ResponseEntity.ok().body("Session is valid.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session is not valid.");
    }
}
