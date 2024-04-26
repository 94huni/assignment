package com.example.board.data.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Getter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int cId;

    private String comment;

    @JoinColumn
    @ManyToOne
    private Member member;

    @JoinColumn
    @ManyToOne
    private Board board;

    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime createAt;

    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime updateAt;

}
