package com.example.board.service;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Comment;
import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.CommentWrite;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.data.responseDto.CommentResponse;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.service.impl.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // Comment 로 받아온 Page 객체를 CommentResponse 변경하는 Mapper
    private Page<CommentResponse> toEntity(Page<Comment> boards) {
        // 새로운 ModelMapper 생성
        ModelMapper modelMapper = new ModelMapper();

        return boards.map(comment -> {
            CommentResponse response = modelMapper.map(comment, CommentResponse.class);
            response.setWriter(comment.getMember().getNickname()); // Comment 에 저장된 Member 의 닉네임만 반환
            response.setBId(comment.getBoard().getBId()); // Comment 에 저장된 게시판의 ID 만 반환
            return response;
        });
    }

    // 댓글 생성
    @Override
    @Transactional
    public void createComment(CommentWrite create, Member member, Board board) {
        if (create == null)
            throw new NullPointerException("Comment is null");

        try {
            if (member == null)
                throw new UsernameNotFoundException("Member not found");

            if (board == null)
                throw new NullPointerException("Board not found");

            Comment comment = Comment.builder()
                    .member(member)
                    .board(board)
                    .comment(create.getComment())
                    .createAt(LocalDateTime.now())
                    .build();
            commentRepository.save(comment);

        } catch (NullPointerException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD);

        } catch (UsernameNotFoundException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    // 게시글에 등록된 모든 댓글 조회
    @Override
    public Page<CommentResponse> getCommentsByBoard(int b_id, int page) {

        // 컨트롤러에서 받아온 게시글 ID
        // select * from BOARD where b_id = {b_id}
        Board board = boardRepository.findById(b_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        // 불러올 페이지 번호를 컨트롤러에서 받아와서 Pageable 객체 생성
        // LIMIT {page}, 10
        Pageable pageable = PageRequest.of(page, 10);

        // 게시글에 등록된 댓글정보를 조회
        // select * from COMMENT where b_id = b_id limit {page}, 10;
        Page<Comment> comments = commentRepository.findCommentByBoardOrderByCreateAtDesc(board, pageable);

        // 만약 전체페이지 보다 큰 페이지 요청이 오거나 0보다 작은 요청이 왔을 때의 예외처리
        if (comments.getTotalPages() <= page || comments.getTotalPages() < 0)
            throw new CustomException(ErrorCode.BAD_REQUEST_PAGE);

        return toEntity(comments);
    }

    // 해당 email 의 유저가 등록한 모든댓글 조회
    @Override
    public Page<CommentResponse> getCommentsByMember(String email, int page) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Pageable pageable = PageRequest.of(page, 10);

        Page<Comment> comments = commentRepository.findCommentByMemberOrderByCreateAtDesc(member, pageable);

        return toEntity(comments);
    }

    // 댓글수정
    @Override
    @Transactional
    public void updateComment(int cId, CommentWrite create, Member member) {
        try {
            // 변경 될 Comment 의 정보를 컨트롤러에서 받아와 생성
            Comment comment = commentRepository.findById(cId)
                    .orElseThrow(() -> new NullPointerException("Not found Comment"));

            // 만약 Comment 에 저장된 Member 와 현재 접속된 Member 가 다르면 권한 없음 예외처리
            if (!comment.getMember().equals(member))
                throw new AccessDeniedException("FORBIDDEN");

            // toBuilder 패턴을 이용해 필요한 부분만 교체
            Comment result = comment.toBuilder()
                    .cId(comment.getCId())
                    .board(comment.getBoard())
                    .updateAt(LocalDateTime.now())
                    .member(comment.getMember())
                    .comment(create.getComment())
                    .build();

            commentRepository.save(result);


        } catch (NullPointerException e) {

            log.error(e.getMessage());
            // Comment 정보를 받아오지 못했으면 예외처리
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);

        } catch (AccessDeniedException e) {

            log.error(e.getMessage());
            // 현재 접속된 정보와 Comment 에 저장된 정보가 다를시 예외처리
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);

        } catch (Exception e) {

            log.error(e.getMessage());
            // 예상못한 에러가 발생시 500에러 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    // 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(int cId, Member member) {
        try {

            // 변경 될 Comment 의 정보를 컨트롤러에서 받아와 생성
            Comment comment = commentRepository.findById(cId)
                    .orElseThrow(() -> new NotFoundException("Comment Not found"));

            // 만약 Comment 에 저장된 Member 와 현재 접속된 Member 가 다르면 권한 없음 예외처리
            if (!comment.getMember().equals(member))
                throw new AccessDeniedException("FORBIDDEN");

            commentRepository.delete(comment);

        } catch (NotFoundException e) {

            log.error(e.getMessage());
            // Comment 정보가 없을 시 예외처리
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);

        } catch (Exception e) {
            log.error(e.getMessage());
            // 예상못한 에러들 예외 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
