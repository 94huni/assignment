package com.example.board.controller.restController;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.CommentWrite;
import com.example.board.data.responseDto.CommentResponse;
import com.example.board.service.impl.BoardService;
import com.example.board.service.impl.CommentService;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final BoardService boardService;

    // 게시글에 댓글 생성
    @PostMapping("/write/board/{bId}")
    public ResponseEntity<Map<String, String>> writeComment(@RequestBody CommentWrite commentWrite,
                                                            Principal principal,
                                                            @PathVariable int bId) {
        Member member = memberService.principalMember(principal);
        Board board = boardService.findBoard(bId);
        commentService.createComment(commentWrite, member, board);

        Map<String, String> map = new HashMap<>();
        map.put("result", "Write Success");

        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }

    // 현재 접속된 회원의 댓글정보
    @GetMapping("/member")
    public ResponseEntity<Page<CommentResponse>> getCommentByMember(Principal principal,
                                                                    @RequestParam int page) {

        Member member = memberService.principalMember(principal);

        return ResponseEntity.ok(commentService.getCommentsByMember(member.getEmail(), page));
    }

    
    // 게시글에 등록된 댓글 전체 조회
    @GetMapping("/board/{bId}")
    public ResponseEntity<Page<CommentResponse>> getCommentByBoard(@PathVariable int bId,
                                                                   @RequestParam(required = false, defaultValue = "0") int page) {

        return ResponseEntity.ok(commentService.getCommentsByBoard(bId, page));
    }

    // 해당 댓글의 수정
    @PutMapping("/update/{cId}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable int cId,
                                                             @RequestBody CommentWrite write,
                                                             Principal principal) {
        Member member = memberService.principalMember(principal);

        commentService.updateComment(cId, write, member);

        Map<String, String> result = new HashMap<>();
        result.put("result", "Comment Update Success");

        return ResponseEntity.ok(result);
    }

    // 해당댓글의 삭제
    @DeleteMapping("/delete/{cId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable int cId,
                                                             Principal principal) {
        Member member = memberService.principalMember(principal);

        commentService.deleteComment(cId, member);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
