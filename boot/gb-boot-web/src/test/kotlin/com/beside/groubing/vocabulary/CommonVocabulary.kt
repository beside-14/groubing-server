package com.beside.groubing.vocabulary

import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseType

// --- 공통 식별자 ---

fun id(fieldName: String = "id", description: String = "ID", example: String = "1") =
    fieldName responseType NUMBER means description example example

fun idPath(fieldName: String = "id", description: String = "ID") =
    fieldName requestParam description

// --- 페이징 파라미터 ---

fun pageParam(fieldName: String = "page") =
    fieldName requestParam "페이지 번호 (0부터 시작)"

fun sizeParam(fieldName: String = "size") =
    fieldName requestParam "페이지 크기"

fun bingoBoardIdParam(fieldName: String = "bingoBoardId") =
    fieldName requestParam "빙고 ID"

// --- 공통 메시지 / 코드 ---

fun message(fieldName: String = "message", description: String = "응답 메시지") =
    fieldName responseType STRING means description example "요청이 완료되었습니다."
