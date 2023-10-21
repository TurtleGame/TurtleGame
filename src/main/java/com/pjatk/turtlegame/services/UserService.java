package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.ItemRepository;
import com.pjatk.turtlegame.repositories.RoleRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    ItemService itemService;

    @Transactional
    public void addNewUser(UserDTO userDTO) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail().trim());
        user.setUsername(userDTO.getUsername().trim());
        user.setGold(0);
        user.setRegistrationDate(LocalDateTime.now());
        user.setUserItemList(null);
        user.setRole(roleRepository.findById(2).orElseThrow(null));
        userRepository.save(user);
        Item item = itemRepository.findById(9).orElseThrow(null);
        Item egg = itemRepository.findById(21).orElseThrow(null);
        itemService.addItem(user, item, 5);
        itemService.addItem(user, egg, 1);
    }

    public boolean isUsernameAlreadyTaken(String username) {
        User existingUser = userRepository.findUserByUsername(username);
        return existingUser != null;
    }

    public boolean isEmailAlreadyTaken(String email) {
        User existingUser = userRepository.findUserByEmail(email);
        return existingUser != null;
    }

    public List<String> searchUsers(String keyword, String username) {
        return userRepository.searchUserByKeyword(keyword)
                .stream()
                .filter(username1 -> !username1.equals(username))
                .collect(Collectors.toList());

    }

    @Transactional
    public void editAbout(String about, User user) {
        user.setAbout(about);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserActivity(User user) {
        user.setLastActivity(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean isUserOnline(User user) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(user.getLastActivity(), currentTime);
        long timeSinceLastActivity = duration.toMillis();
        int onlineThresholdInMilliseconds = 5 * 6 * 1000;

        return timeSinceLastActivity <= onlineThresholdInMilliseconds;

    }

    public void changePassword(User user, String oldPassword, String newPassword) throws Exception {
        final BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();

        if (!passwordEncoder1.matches(oldPassword, user.getPassword())) {
           throw  new Exception("Stare hasło nie pasuje");
        }
        if (newPassword.length() < 6) {
            throw  new Exception("Hasło jest za krótkie!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


    }

    public void changeUsername(User user, String username) throws Exception {
        if(username.length() < 2 || username.length() > 15){
            throw new Exception("Zła długość nicku");
        }

        if(userRepository.findUserByUsername(username) != null){
           throw new Exception("Nick jest już zajęty");
        }
        if(user.getGold() < 100){
            throw new Exception("Nie masz wystarczającej ilości golda!");
        }
        user.setGold(user.getGold() - 100);
        user.setUsername(username);
        userRepository.save(user);

    }

    public void changeAvatar(User user, MultipartFile avatar) throws IOException {
        String uploadDir = "src\\main\\media\\avatars\\";
        Path uploadPath = Paths.get(uploadDir);
        String extension = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1);
        ArrayList<String> acceptedExtensions = new ArrayList<>();
        acceptedExtensions.add("jpg");
        acceptedExtensions.add("jpeg");
        acceptedExtensions.add("png");

        if (!acceptedExtensions.contains(extension)) {
            throw new IOException("Obsługiwane formaty plików to: " + String.join(", ", acceptedExtensions));
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = user.getId() + "." + extension; //

        try (InputStream inputStream = avatar.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            // Zapisujemy obraz jako plik PNG
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Nie udało się zapisać pliku!");
        }
    }
}

