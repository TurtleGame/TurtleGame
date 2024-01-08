package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RoleRepository roleRepository;
    private final FriendRequestRepository friendRequestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ItemService itemService;
    private final EmailService emailService;
    private final UserStatusRepository userStatusRepository;
    private final PrivateMessageService privateMessageService;
    private Environment envi;


    @Transactional
    public void addNewUser(UserDTO userDTO) {
        String token = UUID.randomUUID().toString();
        User user = new User();
        String link = "turtleblast.pl/registration/confirm?token=" + token;
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail().trim());
        user.setUsername(userDTO.getUsername().trim());
        user.setGold(1000);
        user.setShells(0);
        user.setRegistrationDate(LocalDateTime.now());
        user.setActivationToken(token);
        user.setTurtleLimit(5);
        user.setActivationTokenExpireAt(LocalDateTime.now().plusWeeks(1));
        user.setUserItemList(null);
        user.setRole(roleRepository.findById(2).orElseThrow(null));
        userRepository.save(user);
        Item item = itemRepository.findById(10).orElseThrow(null);
        Item stormEgg = itemRepository.findById(1).orElseThrow(null);
        Item magmaEgg = itemRepository.findById(2).orElseThrow(null);
        Item tornadoEgg = itemRepository.findById(3).orElseThrow(null);
        List<UserItem> newList = new ArrayList<>();
        user.setUserItemList(newList);
        itemService.addItem(user, item, 5);
        itemService.addItem(user, stormEgg, 1);
        itemService.addItem(user, magmaEgg, 1);
        itemService.addItem(user, tornadoEgg, 1);
        emailService.send(userDTO.getEmail().trim(), buildActivationEmail(userDTO.getUsername(), link), "Potwierdź swój adres mailowy");
        privateMessageService.sendWelcomeMessage(user);

    }

    public void sendChangePasswordMail(String email) {
        User user = userRepository.findUserByEmail(email);

        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        user.setActivationTokenExpireAt(LocalDateTime.now().plusWeeks(1));
        userRepository.save(user);
        String link = "turtleblast.pl/change-password?token=" + token;
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
        String regex = "^[a-zA-Z0-9]+$";

        if (username.length() < 2 || username.length() > 15) {
            throw new Exception("Zła długość nicku");
        }
        if (!Pattern.matches(regex, username)) {
            throw new Exception("Nick musi się składać z samych liter i liczb");
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

    public void banUser(User giver, String playerUsername, String reason, LocalDateTime banExpireAt) {
        User playerToBan = userRepository.findUserByUsername(playerUsername);

        UserStatus userStatus = new UserStatus();
        userStatus.setGiver(giver);
        userStatus.setUser(playerToBan);
        userStatus.setReason(reason);
        userStatus.setStartAt(LocalDateTime.now());
        userStatus.setEndAt(banExpireAt);
        userStatusRepository.save(userStatus);

        emailService.send(playerToBan.getEmail().trim(), buildBlockAccountEmail(reason, LocalDate.from(banExpireAt)), "Twoje konto zostało zablokowane!");

    }


    public void changeAvatar(User user, MultipartFile avatar) throws IOException {
        String avatarUploadDirectory = envi.getProperty("upload.dir");
        try {
            String userId = String.valueOf(user.getId());
            String filename = userId + ".png";

            String filePath = avatarUploadDirectory + File.separator + filename;

            byte[] bytes = avatar.getBytes();
            Files.write(Paths.get(filePath), bytes);


        } catch (IOException e) {
            throw new IOException("Failed to save avatar on the server: " + e.getMessage());
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

    private String buildCommonEmailHeader(String title) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + title + "</span>\n" +
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
                "  </tbody></table>\n";
    }

    private String buildActivationEmail(String name, String link) {
        String header = buildCommonEmailHeader("Confirm your email");
        return header +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "          <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>\n" +
                "          <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p>\n" +
                "          <blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">\n" +
                "              <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p>\n" +
                "          </blockquote>\n" +
                "          Link will expire in 15 minutes. <p>See you soon</p>\n" +
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
        String header = buildCommonEmailHeader("Zmień hasło");
        return header +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "          <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>\n" +
                "          <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Oto twój link do zmiany hasła: </p>\n" +
                "          <blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">\n" +
                "              <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Zmień hasło</a> </p>\n" +
                "          </blockquote>\n" +
                "          <p>Do zobaczenia w turtle blast!</p>\n" +
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

    private String buildBlockAccountEmail(String reason, LocalDate expireAt) {
        String header = buildCommonEmailHeader("Zablokowane konto");
        return header +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Drogi graczu,</p>\n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Twoje konto zostało zablokowane. Powód: " + reason + ". Ban kończy się: " + expireAt + "</p>\n" +
                "            <p>Do zobaczenia w Turtle Blast!</p>\n" +
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

