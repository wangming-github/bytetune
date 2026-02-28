package com.maizi.bytetune.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchDuplicateException extends RuntimeException {

    public BatchDuplicateException(String message) {
        super(message);
    }

    public BatchDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    private void handleBatchException(Throwable e) {
        if (e instanceof java.sql.BatchUpdateException && e.getMessage().contains("Duplicate entry")) {
            log.warn("重复主键，忽略: {}", e.getMessage());
            // 抛出自定义异常或不抛出，取决于业务
            throw new BatchDuplicateException("保存数据时出现重复主键", e);
        } else {
            throw new RuntimeException(e);
        }
    }
}