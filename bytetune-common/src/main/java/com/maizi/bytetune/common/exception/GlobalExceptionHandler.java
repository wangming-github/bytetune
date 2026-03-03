package com.maizi.bytetune.common.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 统一处理 Controller 层抛出的异常，返回自定义统一响应对象 R<T>。
 * - IllegalArgumentException → 返回 400 错误码
 * - 其他 Exception → 返回 500 错误码
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // /**
    //  * 处理请求参数或业务校验错误
    //  *
    //  * @param e 捕获的 IllegalArgumentException
    //  * @return R<Void> 包含 400 状态码和错误信息
    //  */
    // @ExceptionHandler(IllegalArgumentException.class)
    // public R<Void> handleBadRequest(IllegalArgumentException e) {
    //     return R.error(e.getMessage(), 400);
    // }
    //
    // /**
    //  * 处理服务器内部异常
    //  *
    //  * @param e 捕获的 Exception
    //  * @return R<Void> 包含 500 状态码和固定错误信息
    //  */
    // @ExceptionHandler(Exception.class)
    // public R<Void> handleServerError(Exception e) {
    //     return R.error("服务器内部错误", 500);
    // }

}