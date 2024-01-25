package com.pjatk.turtlegame;

import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.RoleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    RoleRepository roleRepository;

    protected User makeUser(String username, boolean confirmed) {
        UserDTO userDto = new UserDTO();
        userDto.setUsername(username);
        userDto.setEmail(username + "@test.pl");
        userDto.setPassword("test123");

        userService.addNewUser(userDto);
        User user = userRepository.findUserByUsername(username);

        if (confirmed) {
            user.setEmailConfirmed(true);
            userRepository.save(user);
        }

        return user;
    }

    protected User makeAdmin(String username, boolean confirmed) {
        UserDTO userDto = new UserDTO();
        userDto.setUsername(username);
        userDto.setEmail(username + "@test.pl");
        userDto.setPassword("test123");

        userService.addNewUser(userDto);
        User user = userRepository.findUserByUsername(username);
        user.setRole(roleRepository.findById(1).orElseThrow());

        if (confirmed) {
            user.setEmailConfirmed(true);
            userRepository.save(user);
        }

        return user;
    }
}
