package cn.edu.hncj.forum.advice;

import cn.edu.hncj.forum.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {

	@ExceptionHandler(Exception.class)
	ModelAndView handle(HttpServletRequest request, Throwable ex,
										   Model model) {
        if(ex instanceof CustomizeException) {
        	model.addAttribute("message",ex.getMessage());
		}else {
			model.addAttribute("message","服务器冒烟了~~~");
		}
		return new ModelAndView("error");
	}

}