package org.example.service;

import org.example.dto.CustomerDto;
import org.example.dto.CustomerLoginDto;
import org.example.exception.EmailAlreadyInUseException;
import org.example.model.Customer;
import org.example.repository.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public CustomerService(CustomerRepository customerRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    @Transactional
    public void registerCustomer(CustomerDto customerDto) {
        if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use");
        }
        Customer customer = convertCustomerDtoToEntity(customerDto);
        customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(String jwtToken) {
        String email = jwtService.extractUsername(jwtToken);
        customerRepository.findByEmail(email).ifPresent(customerRepository::delete);
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerInfo(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Customer not found")
        );

        return convertCustomerToDto(customer);
    }

    public Customer authenticateCustomer(CustomerLoginDto customerLoginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        customerLoginDto.getEmail(),
                        customerLoginDto.getPassword()
                )
        );
        return customerRepository.findByEmail(customerLoginDto.getEmail()).orElse(null);
    }

    private Customer convertCustomerDtoToEntity(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setLicenceNumber(customerDto.getLicenceNumber());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(this.passwordEncoder.encode(customerDto.getPassword()));

        return customer;
    }

    private CustomerDto convertCustomerToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setEmail(customer.getEmail());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(String.valueOf(String.valueOf(customer.getPhoneNumber())));
        customerDto.setLicenceNumber(customer.getLicenceNumber());

        return customerDto;
    }
}
