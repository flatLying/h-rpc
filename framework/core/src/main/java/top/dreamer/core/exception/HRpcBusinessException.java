package top.dreamer.core.exception;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 21:39
 * @description: 业务代码出错
 */
public class HRpcBusinessException extends RuntimeException {
    public HRpcBusinessException (String message) {
        super(message);
    }
    public HRpcBusinessException () {}
}
