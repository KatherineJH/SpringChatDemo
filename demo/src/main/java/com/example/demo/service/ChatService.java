package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final PostRepository postRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChattersRepository chattersRepository;

  @Transactional
  public ChatRoom initiateChatFromPost(Long postId, Long requesterId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    User requester =
        userRepository
            .findById(requesterId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // 이미 채팅방이 있는지 확인
    Optional<Chatters> existingChatters =
        chattersRepository.findByUser1IdAndUser2Id(requesterId, post.getAuthor().getId());
    if (existingChatters.isPresent()) {
      return existingChatters.get().getChatRoom();
    }

    // 새로운 채팅방 생성
    ChatRoom chatRoom = ChatRoom.builder().build();
    chatRoom = chatRoomRepository.save(chatRoom);

    // Chatters 생성
    Chatters chatters =
        Chatters.builder().user1(requester).user2(post.getAuthor()).chatRoom(chatRoom).build();
    chattersRepository.save(chatters);

    return chatRoom;
  }

  @Transactional
  public ChatMessage saveMessage(Long chatRoomId, Long senderId, String content, MessageType type) {
    ChatRoom chatRoom =
        chatRoomRepository
            .findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));
    User sender =
        userRepository
            .findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    ChatMessage message =
        ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .messageType(type)
            .build();
    return chatMessageRepository.save(message);
  }

  public List<ChatMessage> getMessages(Long chatRoomId) {
    return chatMessageRepository.findByChatRoomId(chatRoomId);
  }

  public Long getUserIdByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .map(User::getId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
  }
}
