package com.starry.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.starry.community.annotation.CheckLogin;
import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Page;
import com.starry.community.bean.User;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.FollowService;
import com.starry.community.service.LikeService;
import com.starry.community.service.UserService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import com.starry.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-06-10:03 AM
 * @Describe
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //文件上传的路径，这里不要写死，因为开发是在windows环境下，部署是在linux环境，最终部署的时候只需要改配置文件就可以了。
    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @Value(value="${qiniu.ak}")
    private String accessKey;

    @Value(value="${qiniu.sk}")
    private String secretKey;

    @Value(value="${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value(value="${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(value = "/postrecord/{userId}", method = RequestMethod.GET)
    public String getDiscussPostByUser(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        page.setLimit(5);
        page.setRows(discussPostService.findDiscussPostsPostRows(userId));
        page.setPath("/user/postrecord/" + userId);
        if (page.getCurrent() > page.getTotal()) {
            page.setCurrent(page.getTotal());
        }
        List<DiscussPost> discussPosts2 =
                discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPosts2 != null) {
            for (DiscussPost post : discussPosts2) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("user", user);
        return "site/my-post";
    }


    /**
     * 获取个人设置页面
     * @return
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        //上传文件名称
        String fileName = CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJsonString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    /**
     * 修改密码
     *
     * @return 如果修改成功，则清空登录状态并跳转到登录页面，如果修改失败，跳回到setting页面
     */
    @RequestMapping(value = "/setting/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String curPassword, String targetPassword,
                                 Model model, @CookieValue("ticket") String ticket) {
        //检查旧密码格式
        if (StringUtils.isBlank(targetPassword)) {
            model.addAttribute("newerror", "新密码不能为空");
            return "/site/setting";
        }
        //检查登录状态
        User user = hostHolder.getUser();
        if (user == null) {
            model.addAttribute("error", "请先登录！");
            return "/site/setting";
        }
        //检查原始密码是否正确
        String password = user.getPassword();
        curPassword = CommunityUtil.md5(curPassword + user.getSalt());
        if (StringUtils.isBlank(password) || !password.equals(curPassword)) {
            model.addAttribute("olderror", "原始密码错误！");
            return "/site/setting";
        }
        //通过所有验证，则修改password并跳转到登录页面
        targetPassword = CommunityUtil.md5(targetPassword + user.getSalt());
        userService.updatePasswordById(user.getId(), targetPassword);
        //将登录凭证无效化
        userService.logOut(ticket);
        return "redirect:/login";
    }

    /**
     *
     * @param userId 个人主页所属的用户的id
     */
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在，无法查看个人主页");
        }
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("user",user);
        model.addAttribute("likeCount",likeCount);
        //粉丝数量
        long followersCount = followService.getFollowersCount(userId);
        //关注者数量
        long followeeCount = followService.getFolloweeCount(userId, ENTITY_TYPE_USER);
        boolean followStatus = false;
        if (hostHolder.getUser() != null) {
            followStatus = followService.getFollowStatus(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        boolean isUserself = false;
        if (hostHolder.getUser() != null) {
            isUserself = hostHolder.getUser().getId() == user.getId();
        }
        model.addAttribute("followersCount",followersCount);
        model.addAttribute("followeeCount",followeeCount);
        model.addAttribute("followStatus",followStatus);
        model.addAttribute("self", isUserself);
        return "/site/profile";
    }

    /**
     * 更新当前用户的headerUrl
     * @param fileName
     * @return
     */
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName,@CookieValue(value = "ticket", defaultValue = "") String ticket) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJsonString(1, "文件名不能为空");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeaderUrl(hostHolder.getUser().getId(), url, ticket);
        return CommunityUtil.getJsonString(0);
    }



//    /**
//     * 接收用户上传头像的请求，已经废弃
//     */
//    @RequestMapping(value = "/setting/upload", method = RequestMethod.POST)
//    public String uploadPhoto(MultipartFile multipartFile, Model model) {
//        /*
//            怎么实现功能呢？
//            将用户上传的文件按一定的命名规范，存到硬盘里
//         */
//        if (multipartFile == null) {
//            model.addAttribute("error", "您还没有选择文件！");
//            return "/site/setting";
//        }
//        try {
//            if (multipartFile.getBytes().length >= 20 * 1024 * 1024) {
//                model.addAttribute("error", "上传文件的大小不能超过20M");
//                return "/site/setting";
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        String originalFilename = multipartFile.getOriginalFilename();
//        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        if (StringUtils.isBlank(suffix) ||
//                (!suffix.equals(".png") && !suffix.equals(".jpg") && !suffix.equals(".jfif"))) {
//            model.addAttribute("error",
//                    "上传的文件格式错误，请上传【png】、【jpg】或【jfif】格式的文件");
//            return "/site/setting";
//        }
//        //如果已经通过所有验证
//        originalFilename = CommunityUtil.generateUUID() + suffix;
//        try {
//            multipartFile.transferTo(new File(uploadPath + "/" + originalFilename));
//        } catch (IOException e) {
//            logger.error("上传文件失败" + e.getMessage());
//            throw new RuntimeException("上传文件失败");
//        }
//
//        User currentUser = hostHolder.getUser();
//        if (currentUser == null) {
//            model.addAttribute("error", "请先登录");
//            return "/site/setting";
//        }
//        userService.updateHeaderUrl(currentUser.getId(),
//                domain + contextPath + "/user/header/" + originalFilename);
//        //http://localhost:8080/community/user/header/xxx.png
//        return "redirect:/index";
//    }

    /**
     * 根据文件名获取用户头像,废弃
     */
    @GetMapping("/header/{filename}")
    public void getHeader(HttpServletResponse response, @PathVariable("filename") String filename) {
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            outputStream = response.getOutputStream();
            fileInputStream = new FileInputStream(filename);
            byte[] buffer = new byte[10 * 1024];
            int l = 0;
            while ((l = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
