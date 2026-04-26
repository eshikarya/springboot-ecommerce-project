package com.ecommerce.project.utils;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public String loggedInEmail() {
        User user = retrieveUser();
        return user.getEmail();
    }

    public User loggedInUser() {
        User user = retrieveUser();
        return user;
    }

    public Long loggedInUserId(){
        User user = retrieveUser();
        return user.getUserId();
    }

    private User retrieveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("User "+authentication.getName()+" not found!"));
        return user;
    }
}
