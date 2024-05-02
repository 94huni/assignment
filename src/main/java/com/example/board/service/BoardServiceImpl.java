package com.example.board.service;

import com.example.board.data.entity.Board;
import com.example.board.data.entity.Member;
import com.example.board.data.requestDto.BoardCreate;
import com.example.board.data.requestDto.BoardUpdate;
import com.example.board.data.responseDto.BoardResponse;
import com.example.board.exception.CustomException;
import com.example.board.exception.ErrorCode;
import com.example.board.repository.BoardRepository;
import com.example.board.service.impl.BoardService;
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

@Service("BoardService")
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    // Board 객체를 BoardResponse 로 바꿔주는 메서드
    private BoardResponse toEntity(Board board) {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setNickname(board.getMember().getNickname());
        boardResponse.setTitle(board.getTitle());
        boardResponse.setContent(board.getContent());
        boardResponse.setCreateAt(board.getCreateAt());
        boardResponse.setUpdateAt(board.getUpdateAt());

        return boardResponse;
    }

    // ID 값으로 Board 객체를 반환하는 메서드
    @Override
    public Board findBoard(int bId) {
        return boardRepository.findById(bId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
    }

    // 받아온 Page 객체를 Board 에서 BoardResponse 로 바꿔주는 Mapper
    public Page<BoardResponse> toEntity(Page<Board> boards) {
        ModelMapper modelMapper = new ModelMapper();
        return boards.map(board -> {
            BoardResponse response = modelMapper.map(board, BoardResponse.class);
            response.setNickname(board.getMember().getNickname());
            return response;
        });
    }

    // 게시글의 상세정보를 ID 값으로 받아와 Response 객체로 반환
    @Override
    public BoardResponse getBoard(int bId) {
        Board board = boardRepository.findById(bId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        return toEntity(board);
    }

    // 게시글 생성
    @Override
    @Transactional
    public void createBoard(BoardCreate create, Member member) {
        log.info("Writer Email : {}", member.getEmail());

        try {
            // 제목은 빈값이 될 수 없음
            if (create.getTitle() == null) {
                throw new NullPointerException();
            }

            // 새로운 게시글을 생성
            Board board = Board.builder()
                    .createAt(LocalDateTime.now())
                    .title(create.getTitle())
                    .content(create.getContent())
                    .member(member)
                    .build();

            boardRepository.save(board);

        } catch (NullPointerException e) {

            log.error(e.getMessage() + " : Get Title is Null");
            throw new CustomException(ErrorCode.BAD_REQUEST_TITLE);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }

    }

    @Override
    @Transactional
    public void updateBoard(int bId, BoardUpdate boardUpdate, Member member) {
        try {
            // 게시글 정보 가져오기
            Board board = boardRepository.findById(bId)
                    .orElseThrow(() -> new NotFoundException("Board Not found"));

            // 현재 접속정보와 게시글의 작성자 정보 비교
            if (board.getMember().getMId() != member.getMId())
                throw new AccessDeniedException("No access permission");

            // toBuilder 사용하여 변경사항 적용
            Board result = board.toBuilder()
                    .updateAt(LocalDateTime.now())
                    .content(boardUpdate.getContent())
                    .title(boardUpdate.getTitle())
                    .build();

            boardRepository.save(result);

        } catch (AccessDeniedException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FORBIDDEN_MEMBER);

        } catch (NotFoundException e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_BOARD);

        } catch (Exception e) {

            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);

        }
    }

    @Override
    @Transactional
    public void deleteBoard(int bId, Member member) {
        try {

            // 게시글 정보 찾아오기
            Board board = boardRepository.findById(bId)
                    .orElseThrow(() -> new NotFoundException("Member Not Found"));

            // 현재 접속정보와 게시글의 작성자 정보 비교
            if (board.getMember().getMId() != member.getMId())
                throw new AccessDeniedException("No access permission");

            boardRepository.delete(board);

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
        // 현재 불러올 페이지 번호를 입력받아 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, 10);

        // 조건에 따라 다르게 받아오기 위해 새로운 객체 생성
        Page<Board> boards;

        if (keyword == null) {
            // 만약 검색어가 없으면 select * from BOARD order by create_at desc limit {page}, 10;
            boards = boardRepository.findAllByOrderByCreateAtDesc(pageable);
        } else {
            // 만약 검색어가 존재하면 select * from BOARD where title={keyword} order by create_at desc limit {page}, 10;
            boards = boardRepository.findBoardByTitleContainingOrderByCreateAtDesc(keyword, pageable);
        }

        // 받아온 페이지에서 최대 페이지 보다 입력받은 값이 크거나 0보다 작으면 예외처리
        if (boards.getTotalPages() <= page || boards.getTotalPages() < 0)
            throw new CustomException(ErrorCode.BAD_REQUEST_PAGE);

        return toEntity(boards);
    }
}
