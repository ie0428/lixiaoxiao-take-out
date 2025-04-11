package com.sky.exception;

public class ConnectionLimitExceededException extends RuntimeException {  // 添加继承
    
    public ConnectionLimitExceededException() {
        super();  // 无参构造
    }

    public ConnectionLimitExceededException(String message) {
        super(message);  // 带错误信息的构造
    }
}
