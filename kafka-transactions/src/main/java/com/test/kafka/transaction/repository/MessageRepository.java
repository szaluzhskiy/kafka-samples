package com.test.kafka.transaction.repository;

import com.test.kafka.transaction.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
