package com.chester.svc.plc.web.model.res;

import lombok.Data;

@Data
public class Result<T> {
    private Boolean success = true;
    private String error;
    private T data;

    public Result(){

    }

    public static <T> Result<T> error(String error){
        Result<T> result = new Result<>();
        result.success = false;
        result.error = error;
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }
}
