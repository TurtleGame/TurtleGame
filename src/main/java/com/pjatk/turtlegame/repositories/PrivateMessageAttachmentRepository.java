package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.PrivateMessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateMessageAttachmentRepository extends JpaRepository<PrivateMessageAttachment, Integer> {
}