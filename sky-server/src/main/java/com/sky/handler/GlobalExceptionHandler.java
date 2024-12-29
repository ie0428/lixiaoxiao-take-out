package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
    * 处理SQL异常
    * @param ex
    * @return
    */

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //Duplicate entry '小智' for key 'employee.idx_username
        String message = ex.getMessage();
        /**
         * getMessage 方法的功能是返回当前 Throwable 实例的详细消息字符串。具体如下：
         * 方法签名：public String getMessage()
         * 功能：返回 detailMessage 字段的值，该字段存储了异常的详细信息。
         * 返回值：返回 detailMessage 字符串，可能为 null
         */
        if (message.contains("Duplicate entry")) {//检查是否包含
            String[] split = message.split(" ");//将字符串以空格分成数组
            String username = split[2];//提取数组中索引为 2 的元素作为用户名
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);

        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
