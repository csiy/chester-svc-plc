package com.chester.svc.plc.web.model.res;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code = 200;
    private T data;

    public Result(Integer code,T data){
        this.code = code;
        this.data = data;
    }

    public Result(T data){
        this.data = data;
    }
}
