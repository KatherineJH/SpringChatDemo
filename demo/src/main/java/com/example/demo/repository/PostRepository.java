package com.example.demo.repository;

import com.example.demo.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findByAuthorId(Long userId);
}
