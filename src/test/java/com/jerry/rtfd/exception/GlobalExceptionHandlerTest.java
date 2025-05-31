package com.jerry.rtfd.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleJsonParseError_ReturnsBadRequest() {
        // 模拟异常
        HttpMessageNotReadableException ex = 
            new HttpMessageNotReadableException("Invalid JSON");

        // 调用处理器
        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleJsonParseError(ex);

        // 验证状态码
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // 验证响应体内容
        Map<String, Object> body = response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Invalid JSON format", body.get("error"));
        assertEquals("Please check your request body format", body.get("message"));
    }

    @Test
    void handleValidationExceptions_ReturnsFieldErrors() {
        // 模拟字段错误
        FieldError fieldError = new FieldError(
            "object", "username", "must not be empty"
        );
        
        // 配置Mock行为
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // 调用处理器
        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // 验证状态码
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // 验证响应体结构
        Map<String, Object> body = response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Validation error", body.get("error"));
        assertEquals("Request validation failed", body.get("message"));
        
        // 验证字段级错误
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertEquals("must not be empty", errors.get("username"));
    }

    @Test
    void handleIllegalArgument_ReturnsCustomMessage() {
        // 模拟带自定义消息的异常
        IllegalArgumentException ex = 
            new IllegalArgumentException("Invalid parameter value");

        // 调用处理器
        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleIllegalArgument(ex);

        // 验证状态码和消息传递
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter value", response.getBody().get("message"));
    }

    @Test
    void handleAllExceptions_ReturnsInternalServerError() {
        // 模拟通用异常
        Exception ex = new Exception("Database connection failed");

        // 调用处理器
        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleAllExceptions(ex);

        // 验证状态码
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        // 验证错误详情
        Map<String, Object> body = response.getBody();
        assertEquals(500, body.get("status"));
        assertEquals("Database connection failed", body.get("details"));
    }
}