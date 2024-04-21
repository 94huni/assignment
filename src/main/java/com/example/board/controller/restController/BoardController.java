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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    @GetMapping("/detail/{bId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable int bId) {
        return ResponseEntity.ok(boardService.getBoard(bId));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<BoardResponse>> getBoards(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(boardService.boardList(keyword, page));
    }

    @PostMapping("/post")
    public ResponseEntity<String> createBoard(@RequestBody BoardCreate boardCreate, Principal principal) {

        Member member = memberService.principalMember(principal);

        boardService.createBoard(boardCreate, member);

        return ResponseEntity.status(HttpStatus.CREATED).body("Post creation was successful");
    }

    @PutMapping("/update/{bId}")
    public ResponseEntity<String> updateBoard(@PathVariable int bId,
                                              @RequestBody BoardUpdate update,
                                              Principal principal) {

        Member member = memberService.principalMember(principal);

        boardService.updateBoard(bId, update, member);

        return ResponseEntity.ok("Update Successful");
    }

    @DeleteMapping("/delete/{bId}")
    public ResponseEntity<String> deleteBoard(@PathVariable int bId,
                                              Principal principal) {

        Member member = memberService.principalMember(principal);

        boardService.deleteBoard(bId, member);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete Successful");
    }
}
