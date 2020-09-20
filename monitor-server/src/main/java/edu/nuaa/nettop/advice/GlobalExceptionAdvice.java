package edu.nuaa.nettop.advice;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.response.BoRestResObj;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-26
 * Time: 16:02
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = MonitorException.class)
    public BoRestResObj handlerToolException(HttpServletRequest request,
                                             MonitorException ex) {
        BoRestResObj response = new BoRestResObj();
        response.setOptres(0);
        response.setMsg(ex.getMessage());
        return response;
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public BoRestResObj handlerResourceNotFoundException(NoHandlerFoundException e) {
        BoRestResObj response = new BoRestResObj();
        response.setOptres(0);
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public BoRestResObj handlerResourceRunException(RuntimeException e) {
        BoRestResObj response = new BoRestResObj();
        response.setOptres(0);
        response.setMsg(e.getMessage());
        return response;
    }
}
