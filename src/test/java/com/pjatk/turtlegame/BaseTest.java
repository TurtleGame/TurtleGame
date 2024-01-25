package com.pjatk.turtlegame;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.*;
import com.pjatk.turtlegame.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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

    @Autowired
    TurtleRepository turtleRepository;

    @Autowired
    TurtleTypeRepository turtleTypeRepository;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    TurtleEggRepository turtleEggRepository;

    @Autowired
    UserItemRepository userItemRepository;

    @Autowired
    ItemRepository itemRepository;

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

    protected Turtle makeTurtle(User owner, int energy, int level) {
        Turtle turtle = new Turtle();
        turtle.setEnergy(energy);
        turtle.setGender(0);
        turtle.setHowMuchFood(0);
        turtle.setAvailable(true);
        turtle.setFed(true);
        turtle.setLevel(level);
        turtle.setOwner(owner);
        turtle.setRankingPoints(0);
        turtle.setTurtleType(turtleTypeRepository.findById(1));
        turtle.setName("teścik");

        turtleRepository.save(turtle);
        return turtle;
    }

    protected News makeNews(User author){
        News news = new News();
        news.setContent("testuje");
        news.setTitle("testuje");
        news.setAuthor(author);
        news.setReleaseDate(LocalDateTime.now());

        newsRepository.save(news);
        return news;
    }

    protected UserItem makeUserItem(User owner, int itemId, int quantity) {
        UserItem userItem = new UserItem();
        userItem.setItem(itemRepository.getById(itemId));
        userItem.setUser(owner);
        userItem.setQuantity(quantity);
        userItem.setTurtle(null);

        userItemRepository.save(userItem);
        return userItem;
    }

    protected TurtleEgg makeTurtleEgg(User owner) {
        TurtleEgg turtleEgg = new TurtleEgg();
        turtleEgg.setHatchingAt(LocalDateTime.of(2024, 3, 3, 3, 33));
        turtleEgg.setUser(owner);
        turtleEgg.setTurtleType(turtleTypeRepository.findById(1));
        turtleEgg.setName("Teścik");
        turtleEgg.setGender(0);
        turtleEgg.setWarming(2);

        turtleEggRepository.save(turtleEgg);
        return turtleEgg;
    }
}
