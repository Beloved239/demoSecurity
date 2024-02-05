package com.demoProject.config;

import com.demoProject.model.User;
import com.demoProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//       UserEntity user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException(String.format("No such user with %s found", username)));

//        return new PassportUserDetails(user);
        Optional<User> user = userRepository.findByUsernameOrEmail(username, username);

        return user.map(AppUserDetails::new).orElseThrow(() -> new UsernameNotFoundException(String.format("No such user with %s found", username)));
    }
}
