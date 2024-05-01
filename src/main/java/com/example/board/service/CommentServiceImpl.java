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
import org.webjars.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public Page<CommentResponse> toEntity(Page<Comment> boards) {
        ModelMapper modelMapper = new ModelMapper();
        return boards.map(comment -> {
            CommentResponse response = modelMapper.map(comment, CommentResponse.class);
            response.setWriter(comment.getMember().getNickname());
            response.setBId(comment.getBoard().getBId());
            return response;
        });
    }

    @Override
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

    @Override
    public Page<CommentResponse> getCommentsByBoard(int b_id, int page) {

        Board board = boardRepository.findById(b_id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        Pageable pageable = PageRequest.of(page, 10);

        Page<Comment> comments = commentRepository.findCommentByBoardOrderByCreateAtDesc(board, pageable);

        if (comments.getTotalPages() <= page || comments.getTotalPages() < 0)
            throw new CustomException(ErrorCode.BAD_REQUEST_PAGE);

        return toEntity(comments);
    }

    @Override
    public Page<CommentResponse> getCommentsByMember(String email, int page) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Pageable pageable = PageRequest.of(page, 10);

        Page<Comment> comments = commentRepository.findCommentByMemberOrderByCreateAtDesc(member, pageable);

        return toEntity(comments);
    }

    @Override
    public void updateComment(int cId, CommentWrite create, Member member) {
        try {

            Comment comment = commentRepository.findById(cId)
                    .orElseThrow(() -> new NullPointerException("Not found Comment"));

            if (!comment.getMember().equals(member))
                throw new AccessDeniedException("FORBIDDEN");

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
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);

        } catch (AccessDeniedException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    @Override
    public void deleteComment(int cId, Member member) {
        try {

            Comment comment = commentRepository.findById(cId)
                    .orElseThrow(() -> new NotFoundException("Comment Not found"));

            if (!comment.getMember().equals(member))
                throw new AccessDeniedException("FORBIDDEN");

            commentRepository.delete(comment);

        } catch (NotFoundException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_COMMENT);

        }
    }
}
