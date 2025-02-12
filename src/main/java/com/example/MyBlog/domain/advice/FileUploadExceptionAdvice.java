package com.example.MyBlog.domain.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice // 전역적으로 예외를 처리하는 클래스임을 선언. Spring MVC의 모든 컨트롤러의 예외를 이 클래스에서 처리할 수 있음
public class FileUploadExceptionAdvice {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    // MaxUploadSizeExceededException 예외가 발생했을 때 실행될 메소드를 지정하는 어노테이션
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(maxSize + "이하의 파일만 업로드할 수 있습니다!"); // 417 메시지
    }
}
