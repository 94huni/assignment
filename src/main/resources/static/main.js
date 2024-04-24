// GET /api/v1/board/list
$(document).ready(function () {
    let pageNumber = 0;

    $(document).on('click', `#login-request`, function () {
        $('#continueButton').hide();
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        const loginHtml = `
            
                <div class="form-floating">
                    <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com">
                    <label for="floatingInput">Email address</label>
                </div>
                <div class="form-floating">
                    <input type="password" class="form-control" id="floatingPassword" placeholder="Password">
                    <label for="floatingPassword">Password</label>
                </div>
                <button class="btn btn-primary w-100 py-2" id="login-submit">Sign in</button>
                <p class="mt-5 mb-3 text-body-secondary">&copy; 2017–2024</p>
          
        `;
        console.log(loginHtml);
        $(`.login-page`).append(loginHtml);
    });

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


            },
            error: function (xhr, textStatus, errorThrown){
                const errorResponse = xhr.responseJSON;
                alert(errorResponse.code + " " + errorResponse.message);
            }
        });

    });

    $(document).on('click', '#pageContinueButton', function () {
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        pageNumber ++
        console.log("pageContinue : " + pageNumber);
        loadBoardList(pageNumber);
    });

    $(document).on('click', '.page-back-button', function () {
        console.log("page back button : " + pageNumber)
        if (pageNumber > 0) {
            $(`.card-body`).remove();
            $(`.pageRequest`).remove();
            pageNumber--;
            loadBoardList(pageNumber);
        } else {
            alert("First Page");
        }
    });

    $(document).on('click', 'page-link', function () {
        pageNumber = parseInt($(this).attr('id'));
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
        loadBoardList(pageNumber);
    })

    $(document).on('click', `#writeForm`, function () {
        $('#continueButton').hide();
        $(`.card-body`).remove();
        $(`.pageRequest`).remove();
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

    function loadBoardList(pageNumber) {
        $.ajax({
            url: `/api/v1/board/list?page=${pageNumber}`,
            type: "GET",
            dataType: 'json',
            success: function (response) {
                const board = response.content;
                console.log(response.content);

                let cardHtml;
                for (const result of board) {
                    const dateString = result.createAt;

                    const date = new Date(dateString);

                    const formattedDate =
                        date.toLocaleDateString('ko-KR',
                            { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });

                    const bId = result.bid;
                    console.log(bId);
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
                const pageHtml = `
                    <nav aria-label="Page navigation" class="pageRequest">
                            <ul class="pagination">
                                <li class="page-item">
                                    <a class="page-link page-back-button" href="#" id="pageBackButton" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                    <span class="sr-only">Previous</span>
                                    </a>
                            </li>
                            <li class="page-item"><a class="page-link" href="#" id="${pageNumber}">${pageNumber + 1}</a></li>
                            <li class="page-item"><a class="page-link" href="#" id="${pageNumber+1}">${pageNumber + 2}</a></li>
                            <li class="page-item"><a class="page-link" href="#" id="${pageNumber+2}">...</a></li>
                            <li class="page-item">
                                <a class="page-link" href="#" id="pageContinueButton" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                                <span class="sr-only">Next</span>
                                </a>
                                </li>
                            </ul>
                        </nav>`;
                $('.pageContainer').append(pageHtml);
            },
            error: function (xhr, status, error) {

                alert(status + error);
                console.error('Error:', error);
            }
        });
    }

    $('#continueButton').click(loadBoardList(pageNumber));

    $(document).on('click', '#backButton', function (event) {
        event.preventDefault();

        loadBoardList(getPageNumberFromCookie());
        $('#details').remove();


    });

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
                                <h5 class="card-title" id="postTitle">${board.title}</h5>
                                <p class="card-text" id="postContent">${board.content}</p>
                                <small class="text-body-secondary">${formattedDate}</small>
                            </div>
                        </div>
                        <a id="backButton" class="btn btn-primary mt-3">목록으로</a>
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

});

