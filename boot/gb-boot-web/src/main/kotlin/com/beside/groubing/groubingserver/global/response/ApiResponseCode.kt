package com.beside.groubing.groubingserver.global.response

enum class ApiResponseCode(val message: String) {
    OK("요청이 성공하였습니다."),
    BAD_PARAMETER("요청 파라미터가 잘못되었습니다."),
    MISSING_REQUEST_HEADER("필수 Header 정보가 누락되었습니다."),
    MISSING_REQUEST_PARAMETER("필수 parameter 정보가 누락되었습니다."),
    NOT_READABLE_REQUEST_BODY("Request Body 정보를 읽을 수 없습니다."),
    BAD_REQUEST_HEADER("요청 Header 정보가 누락되었습니다."),
    BAD_REQUEST_BODY("요청 Body 정보가 누락되었습니다."),
    NOT_FOUND("리소스를 찾지 못했습니다."),
    UNAUTHORIZED("인증에 실패하였습니다."),
    SERVER_ERROR("서버 에러입니다."),

    BAD_MEMBER_INPUT("사용자 입력값이 잘못 되었습니다.")
}
