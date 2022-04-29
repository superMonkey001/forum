package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author FanJian
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @RequestMapping("/upload")
    @ResponseBody
    public FileDTO upload () {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setMessage("成功");
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/view.png");
        return fileDTO;
    }
}
