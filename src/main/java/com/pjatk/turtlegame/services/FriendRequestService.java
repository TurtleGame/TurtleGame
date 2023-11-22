package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.FriendRequest;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.FriendRequestRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public void sendFriendRequest(User sender, String receiverUsername) throws Exception {
        User receiver = userRepository.findUserByUsername(receiverUsername);

        if (receiver == null) {
            throw new Exception("Nie znaleziono takiego użytkownika");

        }

        if (sender.getId() == receiver.getId()) {
            throw new Exception("Nie możesz wysłać zaproszenia do samego siebie");
        }

        if (receiver.getAllFriendRequests().stream()
                .anyMatch(request ->
                        (request.getSender().getId() == sender.getId() && request.getReceiver().getId() == receiver.getId())
                )) {
            throw new Exception("Zaproszenie do znajomych zostało już wysłane wcześniej!");
        }

        if (sender.getAllFriendRequests().stream()
                .anyMatch(request ->
                        (request.getSender().getId() == receiver.getId() && request.getReceiver().getId() == sender.getId())
                )) {
            throw new Exception("Zaproszenie do znajomych zostało już wysłane wcześniej!");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setStatus(false);
        friendRequest.setReceiver(receiver);
        friendRequestRepository.save(friendRequest);

    }

    public void deleteFromFriendsList(int friendRequestId, User user) throws Exception {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId).orElseThrow();
        if(friendRequest.getSender().getId() != user.getId() && friendRequest.getReceiver().getId() != user.getId()){
            throw new Exception("Brak autoryzacji");
        }

        friendRequestRepository.delete(friendRequest);
    }

    public void acceptFriendRequest(int friendRequestId, User user) throws Exception {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId).orElseThrow();
        if(friendRequest.getSender().getId() != user.getId() && friendRequest.getReceiver().getId() != user.getId()){
            throw new Exception("Brak autoryzacji");
        }

        friendRequest.setStatus(true);
        friendRequestRepository.save(friendRequest);
    }

}
