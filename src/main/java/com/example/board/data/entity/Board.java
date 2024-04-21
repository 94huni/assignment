package com.example.board.data.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter @Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bId;

    @Column
    private String title;

    @Column
    private String content;

    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime createAt;

    @DateTimeFormat(fallbackPatterns = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "m_id")
    private Member member;

}
