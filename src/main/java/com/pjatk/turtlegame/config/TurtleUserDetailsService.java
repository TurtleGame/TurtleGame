package com.pjatk.turtlegame.config;

import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TurtleUserDetailsService implements UserDetailsService {

    public UserRepository userRepository;
    public HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.isEmailConfirmed()) {
            throw new DisabledException("Konto nieaktywne! Jeżeli nie dostałeś linku aktywacyjnego kliknij w przycisk przypomnij hasło.");
        }
        if (user.isAccountBanned()) {
            throw new LockedException("Konto zablokowane!");
        }
        request.getSession().setAttribute("user", user);

        return new TurtleUserDetails(user);
    }
}
