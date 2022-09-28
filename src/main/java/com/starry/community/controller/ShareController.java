package com.starry.community.controller;

import com.starry.community.bean.Event;
import com.starry.community.event.EventProducer;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-27-9:04 PM
 * @Describe 处理分享请求，生成长图。
 */
@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    //项目的域名
    @Value(value = "${community.path.domain}")
    private String domain;

    @Value(value = "${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String storage;


    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl) {
        //随机生成文件名
        String fileName = CommunityUtil.generateUUID();
        //异步生成长图
        Event event = new Event()
                        .setTopic(TOPIC_SHARE)
                        .setData("htmlUrl", htmlUrl)
                        .setData("fileName", fileName)
                        .setData("suffix", ".png");
        eventProducer.fireEvent(event);
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);

        return CommunityUtil.getJsonString(0, null, map);
    }

    @RequestMapping(path = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }
        response.setContentType("image/png");
        File file = new File(storage + "/" + fileName + ".png");
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = response.getOutputStream();
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 2];
            int l;
            while ((l = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, l);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
