package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.PrivateMessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateMessageAttachmentRepository extends JpaRepository<PrivateMessageAttachment, Integer> {

    List<PrivateMessageAttachment> findAllByTurtleExpeditionHistoryId(int turtleExpeditionHistoryId);
}
