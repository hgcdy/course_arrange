package cn.netinnet.coursearrange.exception;

import cn.netinnet.coursearrange.model.ResultModel;
import com.sun.istack.internal.NotNull;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局controller异常处理器
 *
 * @author nianzx
 */
@RestControllerAdvice
public class ControllerExceptionHandler {



    /**
     * 捕获所有未知的异常
     */
    @ExceptionHandler({Exception.class})
    public ResultModel globalExceptionHandler(Exception e, HttpServletResponse response) {
        e.printStackTrace();
        response.setStatus(500);
        return ResultModel.error(500, "接口调用失败，请联系管理员！");
    }


    /**
     * 捕获没有权限异常
     */
    @ExceptionHandler({UnauthorizedException.class})
    public ResultModel unauthorizedExceptionHandler(@NotNull UnauthorizedException e, HttpServletResponse response) {
        String message = e.getMessage();
        message = message.replaceAll("(Subject does not have)(.*)(\\[.*])", "当前用户没有$3权限");
        response.setStatus(403);
        return ResultModel.error(403, message);
    }

    /**
     * 捕获服务异常
     */
    @ExceptionHandler({ServiceException.class})
    public ResultModel serviceExceptionHandler(@NotNull ServiceException se) {
        return ResultModel.error(se.getCode(), se.getMessage());
    }


}
