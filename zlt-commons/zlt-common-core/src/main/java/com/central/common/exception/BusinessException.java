package com.central.common.exception;

/**
 * 业务异常
 *
 * @author zlt
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 6610083281801529147L;

    public BusinessException(String message) {
        super(message);
    }
}
