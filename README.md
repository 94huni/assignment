 # board project

 ## 사용된 기술

### - Backend : 
 - Java : 사용된 버전 17
 - Spring Boot : 사용된 버전 2.6.1
 - Spring Data JPA 

### - 인증과 보안 :
 - Spring Security5
 - JWT 

### - API 관리 : 
 - Swagger 3.0


### - Database : 
 - MariaDB
 - H2 Database

## 유저 기능 설명

### 로그인
- **요청**
  - Request Method: `POST`
  - URL: `/member/login`
- **응답**
  - Success Response Status Code: `200 OK`
  - Response Format: JSON

### 회원가입
- **요청**
  - Request Method: `POST`
  - URL: `/member/register`
- **응답**
  - Success Response Status Code: `201 Created`
  - Response Format: JSON

### 회원 정보 수정 
- **요청**
  - Request Method: `PUT`
  - URL: `/member/update`
- **응답**
  - Success Response Status Code: `200 OK`
  - Response Format: JSON

## 게시판 기능 설명

### 게시글 작성
- **요청**
  - Request Method: `POST`
  - URL: `/board/post`
- **응답**
  - Success Response Status Code: `201 Create`
  - Response Format: JSON

### 게시글 정보 조회

- **요청**
  - Request Method: `GET`
  - URL: `/board/detail/{board_id}`
- **응답**
  - Success Response Status Code: `200 ok`
  - Response Format: JSON

### 게시글 리스트 조회

- **요청**
  - Request Method: `GET`
  - URL: `/board/list?page=value1&keyword=value2`
- **응답**
  - Success Response Status Code: `200 ok`
  - Response Format: JSON

### 게시글 수정

- **요청**
  - Request Method: `PUT`
  - URL: `/board/update/{board_id}`
- **응답**
  - Success Response Status Code: `200 OK`
  - Response Format: JSON

### 게시글 삭제

- **요청**
  - Request Method: `DELETE`
  - URL: `/board/delete/{board_id}`
- **응답**
  - Success Response Status Code: `204 No Content`
  - Response Format: JSON

