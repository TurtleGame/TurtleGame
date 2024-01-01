package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RoleRepository roleRepository;
    private final FriendRequestRepository friendRequestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ItemService itemService;
    private final EmailService emailService;

    @Transactional
    public void addNewUser(UserDTO userDTO) {
        String token = UUID.randomUUID().toString();
        User user = new User();
        String link = "http://localhost:8080/registration/confirm?token=" + token;
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail().trim());
        user.setUsername(userDTO.getUsername().trim());
        user.setGold(0);
        user.setShells(0);
        user.setRegistrationDate(LocalDateTime.now());
        user.setActivationToken(token);
        user.setTurtleLimit(5);
        user.setActivationTokenExpireAt(LocalDateTime.now().plusWeeks(1));
        user.setUserItemList(null);
        user.setRole(roleRepository.findById(2).orElseThrow(null));
        userRepository.save(user);
        Item item = itemRepository.findById(9).orElseThrow(null);
        Item egg = itemRepository.findById(21).orElseThrow(null);
        List<UserItem> newList = new ArrayList<>();
        user.setUserItemList(newList);
        itemService.addItem(user, item, 5);
        itemService.addItem(user, egg, 1);
        emailService.send(userDTO.getEmail().trim(), buildActivationEmail(userDTO.getUsername(), link), "Potwierdź swój adres mailowy");
    }

    public void sendChangePasswordMail(String email) {
        User user = userRepository.findUserByEmail(email);

        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        user.setActivationTokenExpireAt(LocalDateTime.now().plusWeeks(1));
        userRepository.save(user);
        String link = "http://localhost:8080/change-password?token=" + token;
        emailService.send(user.getEmail(), buildChangePasswordEmail(user.getUsername(), link), "Zmień hasło");

    }

    @Transactional
    public void changePasswordFromMail(String token, String newPassword, String confirmPassword) throws Exception {
        User user = userRepository.findByActivationToken(token);
        if (user.getActivationTokenExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token wygasł");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Hasła nie pasują");
        }
        if (newPassword.length() < 6) {
            throw new Exception("Hasło jest za krótkie!");
        }
        if (!user.isEmailConfirmed()) {
            user.setEmailConfirmed(true);
        }
        user.setActivationTokenExpireAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    @Transactional
    public void confirmToken(String token) {
        User user = userRepository.findByActivationToken(token);
        if (user.getActivationTokenExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token wygasł");
        }
        user.setActivationTokenExpireAt(LocalDateTime.now());
        user.setEmailConfirmed(true);
        userRepository.save(user);
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
            throw new Exception("Stare hasło nie pasuje");
        }
        if (newPassword.length() < 6) {
            throw new Exception("Hasło jest za krótkie!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void changeUsername(User user, String username) throws Exception {
        int changeUsernamePrice = 1;

        if (username.length() < 2 || username.length() > 15) {
            throw new Exception("Zła długość nicku");
        }

        if (userRepository.findUserByUsername(username) != null) {
            throw new Exception("Nick jest już zajęty");
        }
        if (user.getShells() < changeUsernamePrice) {
            throw new Exception("Nie masz wystarczającej ilości muszelek!");
        }
        user.setShells(user.getShells() - changeUsernamePrice);
        user.setUsername(username);
        userRepository.save(user);

    }

    @Transactional
    public void increaseLimit(User user) throws Exception {
        int increaseLimitPrice = 10;
        if (user.getShells() < increaseLimitPrice) {
            throw new Exception("Nie masz wystarczającej ilości muszelek!");
        }

        user.setShells(user.getShells() - increaseLimitPrice);
        user.setTurtleLimit(user.getTurtleLimit() + 1);
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

        String fileName = user.getId() + ".png";

        try (InputStream inputStream = avatar.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            if (extension.equals("jpg") || extension.equals("jpeg")) {

                BufferedImage image = ImageIO.read(inputStream);
                if (image != null) {
                    ImageIO.write(image, "png", filePath.toFile());
                } else {
                    throw new IOException("Nie udało się przekonwertować obrazu.");
                }
            } else {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IOException("Nie udało się zapisać pliku!");
        }
    }

    public Map<Integer, User> getFriends(User user) {
        List<FriendRequest> friendRequestList = friendRequestRepository.findBySenderOrReceiver(user, user);

        return friendRequestList.stream()
                .filter(FriendRequest::isStatus)
                .collect(Collectors.toMap(
                        FriendRequest::getId,
                        friendRequest -> {
                            if (Objects.equals(friendRequest.getSender().getUsername(), user.getUsername())) {
                                return friendRequest.getReceiver();
                            } else {
                                return friendRequest.getSender();
                            }
                        }
                ));
    }

    public boolean isUserOnFriendList(User loggedUser, User userToCheck) {

        return getFriends(loggedUser).containsValue(userToCheck);

    }

    private String buildActivationEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String buildChangePasswordEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Zmień hasło</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Oto twój link do zmiany hasła: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Zmień hasło</a> </p></blockquote>\n <p>Do zobaczenia w turtle blast!</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}

