package com.homework.common.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result(){
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result(Integer code, String message, Object data)
    {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static Result success(Object data)
    {
        return new Result(200, "success", data);
    }

    public static Result error(String data)
    {
        return new Result(500, data, null);
    }
    public static Result error(String message, Object data){
        return new Result(500, message, data);
    }


    public boolean isSuccess()
    {
        return this.code == 200;
    }

    public boolean isError()
    {
        return this.code != 200;
    }
}
