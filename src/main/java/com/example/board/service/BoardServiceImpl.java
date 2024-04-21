package com.example.board.service;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.BoardCreate;
import com.example.board.data.requestDto.BoardUpdate;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.data.responseDto.MemberResponse;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.BoardRepository;
import com.example.board.service.impl.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;

@Service("BoardService")
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    private BoardResponse toEntity(Board board) {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setNickname(board.getMember().getNickname());
        boardResponse.setTitle(board.getTitle());
        boardResponse.setContent(board.getContent());
        boardResponse.setCreateAt(board.getCreateAt());
        boardResponse.setUpdateAt(board.getUpdateAt());

        return boardResponse;
    }

    private Page<BoardResponse> toEntity(Page<Board> boards) {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(Board.class, BoardResponse.class)
                .addMappings(mapping -> mapping
                        .map(src -> src.getMember().getNickname(), BoardResponse::setNickname));

        return boards.map(board -> modelMapper.map(board, BoardResponse.class));
    }

    @Override
    public BoardResponse getBoard(int bId) {
        Board board = boardRepository.findById(bId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        return toEntity(board);
    }

    @Override
    @Transactional
    public void createBoard(BoardCreate create, Member member) {
        log.info("Writer Email : {}", member.getEmail());

        try {
            if (create.getTitle() == null) {
                throw new NullPointerException();
            }

            Board board = Board.builder()
                    .createAt(LocalDateTime.now())
                    .title(create.getTitle())
                    .content(create.getContent())
                    .member(member)
                    .build();

            boardRepository.save(board);

        } catch (NullPointerException e) {

            log.error(e.getMessage() + " : Get Title is Null");
            throw new CustomException(ErrorCode.BAD_REQUEST);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }

    }

    @Override
    @Transactional
    public void updateBoard(int bId, BoardUpdate boardUpdate, Member member) {
        try {
            Board board = boardRepository.findById(bId)
                    .orElseThrow(() -> new UsernameNotFoundException(""));

            if (board.getMember().getMId() != member.getMId())
                throw new AccessDeniedException("");

            Board result = Board.builder()
                    .bId(board.getBId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .updateAt(LocalDateTime.now())
                    .build();

            boardRepository.save(result);

        } catch (AccessDeniedException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);

        } catch (UsernameNotFoundException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    @Override
    @Transactional
    public void deleteBoard(int bId, Member member) {
        try {

            Board board = boardRepository.findById(bId)
                    .orElseThrow(() -> new NotFoundException("Member Not Found"));

            if (board.getMember().getMId() != member.getMId())
                throw new AccessDeniedException("No access permission");

        } catch (NotFoundException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD);

        } catch (AccessDeniedException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }

    }

    @Override
    public Page<BoardResponse> boardList(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Board> boards;

        if (keyword == null) {
            boards = boardRepository.findAllByOrderByBIdDesc(pageable);
        } else {
            boards = boardRepository.findBoardByTitleOrContentContainingOrderByBIdDesc(keyword, null, pageable);
        }
        return toEntity(boards);
    }
}
