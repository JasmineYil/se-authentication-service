package org.example.dto;

public class LoginResponseDto {
    private String jwtToken;
    private long jwtExpirationInMilliseconds;

    public LoginResponseDto() {}

    public LoginResponseDto(String jwtToken, long jwtExpirationInMilliseconds) {
        this.jwtToken = jwtToken;
        this.jwtExpirationInMilliseconds = jwtExpirationInMilliseconds;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public long getJwtExpirationInMilliseconds() {
        return jwtExpirationInMilliseconds;
    }

    public void setJwtExpirationInMilliseconds(long jwtExpirationInMilliseconds) {
        this.jwtExpirationInMilliseconds = jwtExpirationInMilliseconds;
    }
}
