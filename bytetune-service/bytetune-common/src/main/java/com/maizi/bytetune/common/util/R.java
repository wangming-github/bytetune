package com.maizi.bytetune.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "统一返回对象", description = "接口统一返回结构，包含状态码、消息和数据")
public class R<T> {

    @Schema(description = "状态码，200 表示成功，其它表示失败", example = "200")
    private int code;

    @Schema(description = "返回消息说明", example = "success")
    private String msg;

    @Schema(description = "返回数据，泛型", example = "{}")
    private T data;

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> error(String msg, int code) {
        return new R<>(code, msg, null);
    }
}