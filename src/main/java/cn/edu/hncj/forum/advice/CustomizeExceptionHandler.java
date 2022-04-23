package cn.edu.hncj.forum.advice;

import cn.edu.hncj.forum.dto.ResultDTO;
import cn.edu.hncj.forum.exception.CustomizeErrorCode;
import cn.edu.hncj.forum.exception.CustomizeException;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author FanJian
 */
@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable ex,
                        Model model, HttpServletResponse response) {
        final String jsonType = "application/json";
        String contentType = request.getContentType();
        // 如果是ajax请求，用户从页面正常的请求，但发生自定义的异常，那么不进行跳转，而是在用户当前页面返回一些自定义异常的信息{code,message}
        if (jsonType.equals(contentType)) {
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException) {
                // [2002,"未选中任何问题或评论进行回复"]
                resultDTO = ResultDTO.errorOf((CustomizeException)ex);
            } else {
                // [2004,"服务器冒烟了~~~"]
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            // 由于本方法上面不能加@ResponseBody注解，因此只能通过如下方法返回JSON串给页面
            try {
                // 设置相应格式
                response.setContentType("application/json;charset=utf-8");
                // 获取字符输出流
                PrintWriter writer = response.getWriter();
                // 返回JSON串给前端页面
                writer.write(JSON.toJSONString(resultDTO));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 返回null相当于不跳转页面
            return null;
        }// 如果是路径的非法请求，就直跳转自定义的错误页面
        else {
            if (ex instanceof CustomizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", "服务器冒烟了~~~");
            }
            return new ModelAndView("error");
        }
    }

}