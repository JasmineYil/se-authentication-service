package org.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.CustomerDto;
import org.example.dto.CustomerLoginDto;
import org.example.dto.LoginResponseDto;
import org.example.model.Customer;
import org.example.service.CustomerService;
import org.example.service.JwtService;
import org.example.util.CookieUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
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
        customerService.registerCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody CustomerLoginDto loginCustomerDto, HttpServletResponse response) {
        Customer loginCustomer = customerService.authenticateCustomer(loginCustomerDto);
        String jwtToken = jwtService.generateToken(loginCustomer);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setJwtToken(jwtToken);
        loginResponseDto.setJwtExpirationInMilliseconds(jwtService.getExpirationTime());

        CookieUtils.setJwtCookie(httpServletResponse, jwtToken, (int) jwtService.getExpirationTime());

        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutCustomer(HttpServletResponse response) {
        CookieUtils.clearJwtCookie(response);
        return ResponseEntity.ok().body("Customer logged out successfully");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkCustomerSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie jwtCookie = CookieUtils.findJwtCookie(cookies);

        if (jwtCookie != null && jwtService.validateToken(jwtCookie.getValue())) {
            logger.info("Session is valid for jwtToken: {}", jwtCookie.getValue());
            return ResponseEntity.ok().body("Session is valid.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session is not valid.");
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        logger.info("Received token for validation: {}", token);
        try {
            boolean isValid = jwtService.validateToken(token);
            logger.info("Token validation result: {}", isValid);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCustomer(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies =  request.getCookies();
        Cookie jwtCookie = CookieUtils.findJwtCookie(cookies);

        if (jwtCookie != null && jwtService.validateToken(jwtCookie.getValue())) {
            customerService.deleteCustomer(jwtCookie.getValue());
            CookieUtils.clearJwtCookie(response);
            return ResponseEntity.ok().body("Customer deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerDto> getProfile(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie jwtCookie = CookieUtils.findJwtCookie(cookies);

        if (jwtCookie != null && jwtService.validateToken(jwtCookie.getValue())) {
            CustomerDto customer = customerService.getCustomerInfo(jwtService.extractUsername(jwtCookie.getValue()));
            return ResponseEntity.ok(customer);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
