package com.chat.chatservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.util.Date;

@Entity
@Table(name = "chat")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private Long userId;
    @Column(name = "question")
    private String question;
    @Column(name = "answer")
    private String answer;

    @Column(name = "createdat")
    @CurrentTimestamp
    private Date createdAt;
}
