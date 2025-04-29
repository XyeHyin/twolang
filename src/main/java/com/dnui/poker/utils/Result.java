package com.dnui.poker.utils;

import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.success = true;
        r.data = data;
        return r;
    }
    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.success = false;
        r.message = msg;
        return r;
    }
}