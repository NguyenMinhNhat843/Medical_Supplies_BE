package com.chat.chatservice.controller;

import com.chat.chatservice.entity.ChatEntity;
import com.chat.chatservice.repository.ChatRepository;
import com.chat.chatservice.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final OpenAiService openAiService;
    private final ChatRepository chatRepository;

    public ChatController(OpenAiService openAiService, ChatRepository chatRepository) {
        this.openAiService = openAiService;
        this.chatRepository = chatRepository;
    }


    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askChat(@RequestHeader("X-UserId") Long userId,
                                                       @RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        Map<String, Object> response = openAiService.ask(userMessage, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatEntity>> getChatHistory(@RequestHeader("X-UserId") Long userId) {
        List<ChatEntity> history = chatRepository.findByUserId(userId);
        return ResponseEntity.ok(history);
    }
}

