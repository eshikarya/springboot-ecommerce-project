package com.ecommerce.project.security.response;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfoResponse {

    @Id
    private Long id;
    private String username;
    private String jwtToken;
    private List<String> roles;


    //JWT Token Generation
    public UserInfoResponse(Long id,String username, String jwtToken, List<String> roles) {
        this.id = id;
        this.username = username;
        this.jwtToken = jwtToken;
        this.roles = roles;
    }

    //Cookies
    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
