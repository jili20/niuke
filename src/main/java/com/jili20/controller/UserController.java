package com.jili20.controller;

import com.jili20.annotation.LoginRequired;
import com.jili20.entity.User;
import com.jili20.service.UserService;
import com.jili20.util.CommunityUtil;
import com.jili20.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author bing  @create 2020/11/5-3:29 下午
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired // 自定义的拦截器注解，必须登录才能访问此方法
    @GetMapping("/setting")
    private String getSettingPage() {
        return "/site/setting";
    }

    // 上传头像
    @LoginRequired // 自定义的拦截器注解，必须登录才能访问此方法
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model, RedirectAttributes attr) {
        // 如果上传的文件为空
        if (headerImage == null) {
            model.addAttribute("error", "你还没有选择图片！");
            return "/site/setting";
        }
        // 获取原始文件名
        String fileName = headerImage.getOriginalFilename();
        // 获取后缀，从最后一个点的索引往后截取
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件，把头像写入 dest
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }
        // 更新当前用户的头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;

        // 使用下面的方法，给模板传提示信息
        int rows = userService.updateHeader(user.getId(), headerUrl);
        if (rows > 0) {
            // 重定向消息提示
            attr.addFlashAttribute("uploadHeaderSuccess", "上传头像成功");
        }
         return "redirect:/";
    }

    // 获取头像
    // 向网页响应的是一个二进制数据，通过流手动向浏览器输出
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }


    // 修改密码
    @PostMapping("/updatePassword")
    public String updatePassword(Model model,String oldPassword,String newPassword,String confirmPassword){
        User user = hostHolder.getUser();

        if (StringUtils.isBlank(oldPassword) ) {
            model.addAttribute("oldPasswordMsg", "原密码不能为空！");
            return "site/setting";
        }

        if (StringUtils.isBlank(newPassword) ) {
            model.addAttribute("newPasswordMsg", "新密码不能为空！");
            return "site/setting";
        }

        if (StringUtils.isBlank(confirmPassword) ) {
            model.addAttribute("confirmPasswordMsg", "确认密码不能为空！");
            return "site/setting";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("newPasswordMsg","新密码和确认密码不一致！");
            return "site/setting";
        }

        // 处理密码加密
        String u = user.getPassword() ;
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        if (!oldPassword.equals(u)){
            model.addAttribute("oldPasswordMsg","原密码错误！！");
            return "site/setting";
        }
        if (oldPassword.equals(u)){
            user.setPassword(newPassword);
//            userService.updatePassword(user.getId(),user.getPassword());
            int rows = userService.updatePassword(user.getId(),user.getPassword());
            if (rows > 0) {
                model.addAttribute("updatePasswordMsg","密码更新成功！");
            }
        }
        return "site/setting";
    }
}
