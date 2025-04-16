package com.example.demo.repository;

import com.example.demo.entity.Chatters;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattersRepository extends JpaRepository<Chatters, Long> {
  Optional<Chatters> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
}
