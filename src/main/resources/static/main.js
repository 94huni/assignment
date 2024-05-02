// GET /api/v1/board/list
$(document).ready(function () {
    $('#writeForm').hide();
    let pageNumber = 0;

    // 로그인페이지
    $(document).on('click', `#login-request`, function () {
        // 쓰지않는 페이지들 보이지 않게
        $('#continueButton').hide();
        $(`#details`).remove();
        $(`#login-request`).hide();
        $('#signUp-form').remove();
        $(`#signUp-button`).show(); // 로그인 페이지에선 회원가입 버튼이 보이게
        deleteDiv();
        const loginHtml = `
            <div id="login-request-div">
                <div class="form-floating" >
                    <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com">
                    <label for="floatingInput">Email address</label>
                </div>
                <div class="form-floating">
                    <input type="password" class="form-control" id="floatingPassword" placeholder="Password">
                    <label for="floatingPassword">Password</label>
                </div>
                <button class="btn btn-primary w-100 py-2" id="login-submit">Sign in</button>
                <p class="mt-5 mb-3 text-body-secondary">&copy; 2017–2024</p>
          </div>
        `;
        console.log(loginHtml);
        $(`.login-page`).append(loginHtml); // 먼저 main.html 에 만들어둔 login-page class 에 현재 만든 html 생성
    });

    // 로그인 입력후 실행
    $(document).on('click', `#login-submit`, function () {

        const email = $('#floatingInput').val();
        const password = $('#floatingPassword').val();

        console.log("email: " + email + "password : " + password)
        $.ajax({
            url: "/api/v1/member/login",
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({
                email: email,
                password: password
            }),
            success: function (data){
                const token = data.token;

                const expiresDate = new Date();
                expiresDate.setTime(expiresDate.getTime() + (60 * 60 * 1000)); // 1시간 후


                const expires = `expires=${expiresDate.toUTCString()}`;

                document.cookie = `token=${token}; expires=${expires}; path=/`;

                console.log("token : ", token);

                $(`#sign`).remove();

                $(`.login-page`).remove();

                const pageNum = getPageNumberFromCookie();
                if (pageNum) {
                    loadBoardList(pageNum);
                } else {
                    loadBoardList(0)
                }

                $(`#writeForm`).show();


            },
            error: function (xhr, textStatus, errorThrown){
                const errorResponse = xhr.responseJSON;
                alert(errorResponse.code + " " + errorResponse.message);
            }
        });

    });

    //회원가입 버튼
    $(document).on('click', `#signUp-button`, function () {
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        $('#login-request-div').remove();
        $(`#comment_`).remove();
        $('#details').remove();
        $('#write').remove();
        $(`#login-request`).show();
        const signUpHtml = `
        <div class="container mt-5" id="signUp-form">
        <h2>Member SignUp Form</h2>
            <hr>
            <div class="mb-3">
                <label for="userName" class="form-label">User Name</label>
                <input type="text" class="form-control" id="userName" placeholder="Enter user name" required>
            </div>
            <div class="mb-3">
                <label for="nickName" class="form-label">Nick Name</label>
                <input type="text" class="form-control" id="nickName" placeholder="Enter nick name" required>
                <button type="submit" id="checkNickname">Check</button>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" placeholder="Enter password" required>
            </div>
            <div class="mb-3">
                <label for="validPassword" class="form-label">Confirm Password</label>
                <input type="password" class="form-control" id="validPassword" placeholder="Enter password again" required>
                <span id="passwordMatch" style="color:#ff0000;">Passwords do not match</span>
            </div>
            <div class="mb-3">
                <label for="phone" class="form-label">Phone</label>
                <input type="text" class="form-control" id="phone" placeholder="Enter phone number" required>
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" placeholder="Enter email" required>
                <button type="submit" id="checkEmail">Check</button>
            </div>
            <button type="submit" class="btn btn-primary" id="signUp-submit">Submit</button>
        </div>
        `;

        $('.signUp-page').append(signUpHtml);
        $('#signUp-button').hide();
    });

    //회원가입 POST 요청
    $(document).on('click', '#signUp-submit', function () {
        const username = $('#userName').val();
        const password = $('#password').val();
        const validPassword = $('#validPassword').val();
        const nickname = $('#nickName').val();
        const email = $('#email').val();
        const phone = $('#phone').val();

        $.ajax({
            url: "/api/v1/member/register",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                userName: username,
                email: email,
                password: password,
                validPassword: validPassword,
                nickName: nickname,
                phone: phone
            }),
            success: function (data) {
                alert(data.message);
                $('#signUp-form').remove();
                loadBoardList(0);
            },
            error: function (xhr, textStatus, errorThrown){
                if (xhr.responseJSON){
                    const errorResponse = xhr.responseJSON;
                    console.log(errorResponse);
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert(textStatus+ " " + errorThrown);
                }
            }
        });
    });

    // 비밀번호 검증시 같은값이 입력되면 Input 태그 잠구는 기능
    $(document).on('input', '#validPassword', function() {
        const password = $('#password').val();
        const validPassword = $(this).val();

        if (password === validPassword) {
            $('#passwordMatch').html('<span style="color:green;">Passwords match</span>');
            $('#password').prop('disabled', true);
            $('#validPassword').prop('disabled', true);
        } else {
            $('#passwordMatch').html('<span style="color:red;">Passwords do not match</span>');
        }
    });

    // 이메일 중복확인시 사용할 수 있으면 태그 잠구는 기능
    $(document).on('click', '#checkEmail', function() {
        const email = $('#email').val(); // 아이디 입력값 가져오기

        $.ajax({
            url: '/api/v1/member/validEmail',
            type: 'POST',
            data: { email: email },
            success: function(response) {
                if (response === 1) {
                    alert('Email available'); // 사용 가능한 아이디인 경우
                    $('#email').prop('disabled', true);
                    $('#checkEmail').prop('disabled', true);
                } else {
                    alert('Email not available'); // 사용 중인 아이디인 경우
                    $('#email').val(''); // 아이디 입력 필드 초기화
                }
            },
            error: function(xhr) {
                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert("An error occurred while processing your request.");
                } // 오류가 발생한 경우
            }
        });
    });

    // 닉네임 중복확인시 사용할 수 있으면 태그 잠구는 기능
    $(document).on('click', '#checkNickname', function() {
        const nickname = $('#nickName').val(); // 아이디 입력값 가져오기

        $.ajax({
            url: '/api/v1/member/validNickname',
            type: 'POST',
            data: { nickname: nickname },
            success: function(response) {
                if (response === 1) {
                    alert('Nickname available'); // 사용 가능한 아이디인 경우
                    $('#nickName').prop('disabled', true);
                    $('#checkNickname').prop('disabled', true);
                } else {
                    alert('Nickname not available'); // 사용 중인 아이디인 경우
                    $('#nickName').val(''); // 아이디 입력 필드 초기화
                }
            },
            error: function(xhr) {
                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert("An error occurred while processing your request.");
                } // 오류가 발생한 경우
            }
        });
    });

    //마지막 페이지 버튼
    $(document).on('click', '#pageContinueButton', function () {
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        const totalPages = $(this).data('total-pages');
        const keywordElement = document.getElementById('keyword');
        const keyword = keywordElement ? keywordElement.value : null;

        console.log("pageContinue : " + totalPages);
        const result = parseInt(totalPages);
        loadBoardList(result - 1, keyword);
    });

    //뒤로가기 페이지 버튼
    $(document).on('click', '.page-back-button', function () {
        console.log("page back button : " + pageNumber)
        const keywordElement = document.getElementById('keyword');
        const keyword = keywordElement ? keywordElement.value : null;

        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        loadBoardList(0, keyword);
    });

    //페이지 이동 버튼
    $(document).on('click', '.page-select', function () {
        pageNumber = parseInt($(this).attr('id'));
        const keywordElement = document.getElementById('keyword');
        const keyword = keywordElement ? keywordElement.value : null;

        console.log("page link page : " + pageNumber)
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        loadBoardList(pageNumber, keyword);
    })

    //글쓰기 페이지 호출
    $(document).on('click', `#writeForm`, function () {
        $('#continueButton').hide();
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        $(`#details`).remove();
        $(`#login-request`).remove();
        $(`#writeForm`).hide();
        const writeFormHtml = `
            <div class="container" id="write" xmlns="http://www.w3.org/1999/html">
                        <h1 class="mt-5">글 쓰기 페이지</h1>
                        <hr>
                        <div class="card mt-4">
                            <div class="card-body">
                                <input type="text" id="title"><br />
                                <textarea type="text" id="content"></textarea>
                            </div>
                        </div>
                        <a id="writeButton" class="btn btn-primary mt-3">글쓰기</a>
            </div>
        `;

        $(`.card-container`).append(writeFormHtml);

    })

    //글쓰기 POST 요청
    $(document).on('click', `#writeButton`, function () {
        const title = $('#title').val();
        const content = $('#content').val();
        const token = getJWTFromCookie();

        if (token === null) {
            alert("Not Login");
            return;
        }

        if (title.trim() === '') {
            alert("title not found");
            return;
        }

        $.ajax({
            url: "/api/v1/board/post",
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            headers: {
                "Authorization": "Bearer " + token
            },
            data: JSON.stringify({
                title: title,
                content: content
            }),
            success: function (data) {
                const message = data.message;
                console.log(typeof(data.message));
                alert(message);
                $(`#write`).remove()
                loadBoardList(0);
                $(`#writeForm`).show();
            },
            error: function (xhr, textStatus, errorThrown){
                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert("An error occurred while processing your request.");
                }
            }
        });
    });

    $(document).on('click', '#searchSubmit', function () {
        const keyword = document.getElementById('searchInput').value;
        deleteDiv();
        loadBoardList(0, keyword);
    });
    //리스트 페이지 불러오기
    function loadBoardList(pageNumber, keyword) {
        $.ajax({
            url: `/api/v1/board/list`,
            type: "GET",
            dataType: 'json',
            data: {
                page: pageNumber,
                keyword: keyword
            },
            success: function (response) {
                const board = response.content;
                console.log(response.content);
                console.log(response.totalPages);

                let cardHtml;
                for (const result of board) {
                    const dateString = result.createAt;

                    const date = new Date(dateString);

                    const formattedDate =
                        date.toLocaleDateString('ko-KR',
                            { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });

                    const bId = result.bid;
                    cardHtml = `
                        <div class="card-body">
                            <p class="bid">${bId}</p>
                            <h5 class="card-title"><a class="post-link">${result.title}</a></h5>
                            <p class="card-text">${result.content}</p>
                             <div class="d-flex justify-content-between align-items-center">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-sm btn-outline-secondary">View</button>               
                                </div>
                                <small class="text-body-secondary">${formattedDate}</small>
                            </div>
                        </div>`;

                    $('.card-container').append(cardHtml);

                    $('#continueButton').hide();
                }
                let pageHtml = `
                                <nav aria-label="Page navigation" class="pageRequest">
                                    <ul class="pagination">
                                `;

                if (pageNumber === 0) {
                    pageHtml += `
                              
                                <li class="page-item"><a class="page-link page-select bg-primary text-white" href="#" id="0">1</a></li>
                                <li class="page-item"><a class="page-link page-select" href="#" id="1">2</a></li>
                                <li class="page-item">
                                    <a class="page-link last-page" href="#" id="pageContinueButton" aria-label="Next" data-total-pages="${response.totalPages}">
                                    <span aria-hidden="true">&raquo;</span>
                                    <span class="sr-only">Last</span>
                                    </a>
                                </li>
                                `;
                } else if (pageNumber === response.totalPages - 1) { // 현재 페이지가 마지막 페이지인 경우
                    pageHtml += `
                                <li class="page-item">
                                    <a class="page-link page-back-button" href="#" id="pageBackButton" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                    <span class="sr-only">First</span>
                                    </a>
                                </li>
                                <li class="page-item"><a class="page-link page-select" href="#" id="${pageNumber - 2}">${pageNumber - 1}</a></li>
                                <li class="page-item"><a class="page-link page-select" href="#" id="${pageNumber - 1}">${pageNumber}</a></li>
                                <li class="page-item"><a class="page-link page-select bg-primary text-white" href="#" id="${pageNumber}">${pageNumber + 1}</a></li>
                                `;
                } else { // 그 외의 경우
                    pageHtml += `
                                <li class="page-item">
                                    <a class="page-link page-back-button" href="#" id="pageBackButton" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                    <span class="sr-only">First</span>
                                    </a>
                                </li>
                                <li class="page-item"><a class="page-link page-select" href="#" id="${pageNumber - 1}">${pageNumber}</a></li>
                                <li class="page-item"><a class="page-link page-select bg-primary text-white" href="#" id="${pageNumber}">${pageNumber + 1}</a></li>
                                <li class="page-item"><a class="page-link page-select" href="#" id="${pageNumber + 1}">${pageNumber + 2}</a></li>
                                <li class="page-item">
                                    <a class="page-link last-page" href="#" id="pageContinueButton" aria-label="Next" data-total-pages="${response.totalPages}">
                                    <span aria-hidden="true">&raquo;</span>
                                    <span class="sr-only">Last</span>
                                    </a>
                                </li>
                                `;
                }

                pageHtml += `
                                </ul>
                            </nav>
                            <input type="hidden" id="keyword" value="${keyword}">`;

                $('.pageContainer').append(pageHtml);
            },
            error: function (xhr, status, error) {

                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                    location.reload();
                } else {
                    alert("An error occurred while processing your request.");
                }
            }
        });
    }

    // $(document).on('click', '#continueButton', loadBoardList(pageNumber))
    $('#continueButton').click(loadBoardList(pageNumber));

    //리스트로 돌아가기 버튼
    $(document).on('click', '#backButton', function (event) {
        event.preventDefault();

        loadBoardList(getPageNumberFromCookie());
        $('#details').remove();


    });

    //상세페이지 버튼
    $(document).on('click', '.post-link', function (event) {
        event.preventDefault();
        const bId = $(this).closest('.card-body').find('.bid').text();
        $.ajax({
            url: `/api/v1/board/detail/${bId}`,
            type: "GET",
            dataType: "json",
            success: function (response) {
                const board = response;
                const dateString = board.createAt;
                const date = new Date(dateString);
                const formattedDate = date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
                console.log(board);
                const cardHtml = `
                    <div class="container" id="details">
                        <h1 class="mt-5">게시물 상세 페이지</h1>
                        <hr>
                        <div class="card mt-4">
                            <div class="card-body">
                                <input type="hidden" id="boardId" value="${bId}">
                                <h5 class="card-title" id="postTitle">${board.title}</h5>
                                <p class="card-text" id="postContent">${board.content}</p>
                                <small class="text-body-secondary">${formattedDate}</small>
                            </div>
                        </div>
                        <a id="backButton" class="btn btn-primary mt-3">List</a>
                        <a id="updateButton" class="btn btn-primary mt-3">Update</a>
                        <a id="commentButton" class="btn btn-primary mt-3">Comment List</a>
                        <a id="commentWriteButton" class="btn btn-primary mt-3">Comment Write</a>
                        <br />
                        <div id="comment-write-box"></div>
                        <div id="comment-box"></div>
                    </div>`;
                $('.card-container').html(cardHtml);
                $('.pageRequest').remove();

            },
            error: function (xhr, status, error) {
                alert(status + error);
                console.error('Error:', error);
            }
        });
    });

    $(document).on('click', '#commentWriteButton', function () {
        $(`#commentWriteButton`).hide();

        const commentInput = `
        <div class="commentWriteInput">
        <input type="text" id="commentInput" placeholder="comment write .."/>
        <button type="button" id="commentWriteSubmit">Write</button>
        <button type="button" id="commentWriteCancel">Cancel</button>
        </div>
        `;

        $('#comment-write-box').html(commentInput);

    });

    // 댓글 생성 취소시
    $(document).on('click', '#commentWriteCancel', function () {
        $(`.commentWriteInput`).remove();
        $(`#commentWriteButton`).show();
    });

    $(document).on('click', '#commentWriteSubmit', function () {
        const comment = $(`#commentInput`).val();
        const b_id = $(`#boardId`).val();

        const token = getJWTFromCookie();

        if (token === null) {
            alert("Not Login");
            return;
        }

        $.ajax({
            url: `/api/v1/comment/write/board/${b_id}`,
            type: 'POST',
            contentType: 'application/json',
            headers: {
                "Authorization": "Bearer " + token
            },
            data: JSON.stringify({
                comment: comment
            }),
            success: function (data) {
                alert(data.result);
                $(`.comment-container`).remove();
                $(`.commentWriteInput`).remove();
                $(`#commentButton`).hide();
                $(`#commentWriteButton`).show();
                loadComment(0, b_id, token);
            },
            error: function (xhr, textStatus, errorThrown){
                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert("An error occurred while processing your request.");
                }
            }
        })

    });

    //댓글 보기 버튼
    $(document).on('click', '#commentButton', function () {
        const boardId = document.getElementById("boardId").value;
        const token = getJWTFromCookie();

        if (token === null) {
            alert("Not Login");
            return;
        }

        loadComment(0, boardId, token);
    })

    //댓글 다음페이지
    $(document).on('click', '#continueComment', function (event) {
        event.preventDefault();
        const pageNumber = document.getElementById("currentPage").value;
        const boardId = document.getElementById("boardId").value;

        const nextPage = parseInt(pageNumber) + 1;

        const token = getJWTFromCookie();

        if (token === null) {
            alert("Not Login");
            return;
        }

        loadComment(nextPage, boardId, token);
    })

    //댓글 이전페이지
    $(document).on('click', '#backComment', function (event) {
        event.preventDefault();
        const pageNumber = document.getElementById("currentPage").value;
        const boardId = document.getElementById("boardId").value;

        const backPage = parseInt(pageNumber) - 1;

        const token = getJWTFromCookie();

        if (token === null) {
            alert("Not Login");
            return;
        }

        loadComment(backPage, boardId, token);

    })

    // 댓글 HTML 불러오는 기능
    function loadComment(commentPageNum, bId, token) {
        $.ajax({
            url: `/api/v1/comment/board/${bId}?page=${commentPageNum}`,
            type: 'GET',
            dataType: 'json',
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function (data) {
                const comments = data.content;
                console.log(comments);

                let commentHtml =``;
                for (const comment of comments) {
                    const dateString = comment.createAt;
                    const date = new Date(dateString);
                    const formattedDate = date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });

                    commentHtml += `
                    <div id="comment_" style="position: relative">
                        <span style="font-weight: bold">${comment.writer}</span>
                        <input type="hidden" name="cno" value="${comment.cId}">   
                        <button style="position:absolute; top:0px;right:10px;margin: 0 auto; padding: 2px;" type="submit">x</button>
                        <small class="text-body-secondary">${formattedDate}</small>
                        <p>
                            <span style="font-size: 12px;">${comment.comment}</span>
                        </p>
                        <br>
                    </div>
                `;

                }

                commentHtml += `<a id="continueComment" class="btn btn-primary mt-3">Continue</a>

                                <a id="backComment" class="btn btn-primary mt-3">Back</a>
                                <input type="hidden" id="currentPage" value="${commentPageNum}">`;

                $('#comment-box').html(commentHtml);
                $('#commentButton').remove();
                console.log(commentPageNum)
            },
            error: function (xhr, textStatus, errorThrown){
                if (xhr.responseJSON) {
                    const errorResponse = xhr.responseJSON;
                    alert(errorResponse.code + " " + errorResponse.message);
                } else {
                    alert("An error occurred while processing your request.");
                }
            }
        })
    }

    // 게시판 페이지 넘버 쿠키에 저장하는 기능
    function savePageNumberToCookie(pageNumber) {

        const existingPageNumber = getPageNumberFromCookie();


        const expiresDate = new Date();
        expiresDate.setTime(expiresDate.getTime() + (60 * 60 * 1000)); // 1시간 후


        const expires = `expires=${expiresDate.toUTCString()}`;


        if (existingPageNumber !== null) {
            document.cookie = `pageNumber=${pageNumber}; ${expires}; path=/`;
        } else {

            document.cookie = `pageNumber=${pageNumber}; ${expires}; path=/`;
        }
    }

    // 쿠키에저장된 페이지 넘버 받아오는 기능
    function getPageNumberFromCookie() {
        const cookies = document.cookie.split(';');
        for (const cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'pageNumber') {
                return parseInt(value);
            }
        }
        return 0;
    }

    //쿠키 저장된 JWT 불러오기
    function getJWTFromCookie() {
        const cookies = document.cookie.split(';');
        for (const cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'token') {
                return value
            }
        }
        return null;
    }

    // HTML 삭제
    function deleteDiv() {
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        $(`#comment_`).remove();
    }

});

