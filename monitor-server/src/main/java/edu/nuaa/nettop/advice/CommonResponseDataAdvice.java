package edu.nuaa.nettop.advice;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.annotation.IgnoreResponseAdvice;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.vo.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-26
 * Time: 15:49
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 响应是否应该拦截
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        //类上注解
        if (methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )) {
            return false;
        }
        //方法上注解
        if (Objects.requireNonNull(methodParameter.getMethod()).isAnnotationPresent(
                IgnoreResponseAdvice.class
        )) {
            return false;
        }
        return true;
    }

    /**
     * 写入响应之前做的事
     */
    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        BoRestResObj response = new BoRestResObj();
        response.setOptres(1);
        if (null == o) {
            return response;
        } else if (o instanceof BoRestResObj) {
            response = (BoRestResObj) o;
        } else if (o instanceof Response) {
            response.setMsg(((Response) o).getMessage());
        } else if (o instanceof LinkedHashMap) {
            response.setOptres(0);
            String error = ((LinkedHashMap<?, ?>) o).get("status") + " " + ((LinkedHashMap<?, ?>) o).get("error");
            response.setMsg(error);
        } else {
            response.setResObj(JSON.toJSONString(o));
        }
        return response;
    }
}
