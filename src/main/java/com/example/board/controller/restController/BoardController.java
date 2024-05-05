package com.example.board.controller.restController;

import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.BoardCreate;
import com.example.board.data.requestDto.BoardUpdate;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.data.responseDto.MemberResponse;
import com.example.board.service.impl.BoardService;
import com.example.board.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/detail/{bId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable int bId) {
        // 상세 페이지
        return ResponseEntity.ok(boardService.getBoard(bId));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<BoardResponse>> getBoards(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(required = false) String keyword) {
        // 리스트 페이지 검색어가 있으면 검색어도 같이 검색
        return ResponseEntity.ok(boardService.boardList(keyword, page));
    }

    @PostMapping("/post")
    public ResponseEntity<Map<String, String>> createBoard(@RequestBody BoardCreate boardCreate, Principal principal) {

        // 현재 로그인된 정보
        Member member = memberService.principalMember(principal);

        // 새로운 게시글 생성 DTO 와 현재 접속정보
        boardService.createBoard(boardCreate, member);
        
        //성공시 message 반환
        Map<String, String> result = new HashMap<>();
        result.put("message", "Post creation was successful");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 게시글 업데이트
    @PutMapping("/update/{bId}")
    public ResponseEntity<Map<String, String>> updateBoard(@PathVariable int bId,
                                              @RequestBody BoardUpdate update,
                                              Principal principal) {

        Member member = memberService.principalMember(principal);

        boardService.updateBoard(bId, update, member);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Update Successful");
        return ResponseEntity.ok(result);
    }
    
    // 게시글 삭제
    @DeleteMapping("/delete/{bId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int bId,
                                              Principal principal) {

        Member member = memberService.principalMember(principal);

        boardService.deleteBoard(bId, member);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
