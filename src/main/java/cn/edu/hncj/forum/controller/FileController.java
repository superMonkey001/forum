package cn.edu.hncj.forum.controller;

import cn.edu.hncj.forum.dto.FileDTO;
import cn.edu.hncj.forum.provider.UCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author FanJian
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private UCloudProvider uCloudProvider;

    /**
     * 上传图片
     * @param request 作用：获取图片文件
     * @return success;message;url;
     */
    @RequestMapping("/upload")
    @ResponseBody
    public FileDTO upload (HttpServletRequest request) {
        // 获得用户上传的图片文件
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("editormd-image-file");

        FileDTO fileDTO;
        try {
            String fileUrl = uCloudProvider.upload(file.getInputStream(),file.getContentType(),file.getOriginalFilename());
            fileDTO = new FileDTO();
            fileDTO.setMessage("成功");
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileUrl);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileDTO = new FileDTO();
        fileDTO.setMessage("失败");
        fileDTO.setSuccess(0);
        return fileDTO;
    }
}
