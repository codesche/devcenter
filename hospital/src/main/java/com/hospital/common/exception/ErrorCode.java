package com.hospital.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션에서 발생할 수 있는 에러 코드를 정의하는 열거형
 * 각 에러 코드는 HTTP 상태코드와 에러 메시지를 포함
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "지원하지 않는 HTTP 메서드입니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "서버 내부 오류가 발생했습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_005", "잘못된 타입 값입니다"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_006", "접근이 거부되었습니다"),

    // 인증/인가 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증에 실패했습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "만료된 토큰입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_004", "접근 권한이 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_005", "리프레시 토큰을 찾을 수 없습니다"),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 일치하지 않습니다"),
    INACTIVE_USER(HttpStatus.FORBIDDEN, "USER_004", "비활성화된 사용자입니다"),

    // 환자 관련 에러
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PATIENT_001", "환자 정보를 찾을 수 없습니다"),
    PATIENT_REGISTRATION_NUMBER_EXISTS(HttpStatus.CONFLICT, "PATIENT_002", "이미 등록된 환자 등록번호입니다"),

    // 의사 관련 에러
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCTOR_001", "의사 정보를 찾을 수 없습니다"),
    DOCTOR_LICENSE_NUMBER_EXISTS(HttpStatus.CONFLICT, "DOCTOR_002", "이미 등록된 의사 면허번호입니다"),

    // 진료과 관련 에러
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT_001", "진료과를 찾을 수 없습니다"),
    DEPARTMENT_CODE_EXISTS(HttpStatus.CONFLICT, "DEPARTMENT_002", "이미 존재하는 진료과 코드입니다"),

    // 예약 관련 에러
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPOINTMENT_001", "예약 정보를 찾을 수 없습니다"),
    APPOINTMENT_TIME_CONFLICT(HttpStatus.CONFLICT, "APPOINTMENT_002", "이미 예약된 시간입니다"),
    INVALID_APPOINTMENT_TIME(HttpStatus.BAD_REQUEST, "APPOINTMENT_003", "유효하지 않은 예약 시간입니다"),

    // 진료기록 관련 에러
    MEDICAL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "MEDICAL_001", "진료기록을 찾을 수 없습니다"),
    PRESCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEDICAL_002", "처방전을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
