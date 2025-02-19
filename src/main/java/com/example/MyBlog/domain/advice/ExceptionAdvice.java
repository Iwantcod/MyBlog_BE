package com.example.MyBlog.domain.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice // 전역적으로 예외를 처리하는 클래스임을 선언. Spring MVC의 모든 컨트롤러의 예외를 이 클래스에서 처리할 수 있음
public class ExceptionAdvice {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    // MaxUploadSizeExceededException 예외가 발생했을 때 실행될 메소드를 지정하는 어노테이션
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(maxSize + "이하의 파일만 업로드할 수 있습니다!"); // 417 메시지
    }

    // 외래키 참조 무결성 제약조건이 위배되어 데이터베이스에서 에러 메시지를 반환받을 경우의 전역적인 예외처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FK integrity violation: " + ex.getMessage());
    }
}
