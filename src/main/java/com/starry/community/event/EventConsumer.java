package com.starry.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.starry.community.bean.DiscussPost;
import com.starry.community.bean.Event;
import com.starry.community.bean.Message;
import com.starry.community.mapper.MessageMapper;
import com.starry.community.service.DiscussPostService;
import com.starry.community.service.ElasticSearchService;
import com.starry.community.service.MessageService;
import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Starry
 * @create 2022-09-18-10:26 AM
 * @Describe
 */
@Component
public class EventConsumer implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private DiscussPostService discussPostService;

    @Value("${wk.image.storage}")
    private String storage;

    @Value("${wk.image.command}")
    private String command;


    @Value(value="${qiniu.ak}")
    private String accessKey;

    @Value(value="${qiniu.sk}")
    private String secretKey;

    @Value(value="${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Value(value="${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    /**
     * 异步 生成 长图,将生成的图片存放到七牛云服务器里
     * @param record
     */
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShare(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        //通过record读取所需数据
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        Map<String, Object> data = event.getData();
        String htmlUrl = data.get("htmlUrl").toString();
        String fileName = data.get("fileName").toString();
        String suffix = data.get("suffix").toString();
        String exec = command + " " + htmlUrl + " " + storage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(exec);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
            采用定时线程池，将生成的图片存入云服务器，为什么要采用定时线程池呢?
            因为Runtime.getRuntime().exec(exec); 是另一个线程去执行的，而且生成图片
            相对耗时，而我们需要等图片生成完再上传。
            因此可以用定时任务，每过一段时间看一眼生成完毕了没有，一旦生成完毕就上传。
         */
        UploadImage task = new UploadImage(fileName, suffix);
        Future future = threadPoolTaskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    class UploadImage implements Runnable {
        //文件名
        private String fileName;
        //文件后缀
        private String suffix;
        private Future future;
        //开始时间
        private long startTime;
        //上传次数
        private int count;

        public void setFuture(Future future) {
            this.future = future;
        }

        public UploadImage(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            //生成,时间太长
            if (System.currentTimeMillis() - startTime > 10_000) {
                logger.error("执行超时" + fileName);
                future.cancel(true);
            }
            if (count > 1) {
                future.cancel(true);
            }
            String path = storage + "/" + fileName + suffix;
            //尝试上传
            File file = new File(path);
            if (file.exists()) {
                count++;
                logger.info("文件上传开始:" + fileName);
                //设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJsonString(0));
                //生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                //指定上传的机房
                UploadManager manager = new UploadManager(new Configuration(Region.region0()));
                try {
                    Response response = manager.put
                        (path, fileName, uploadToken, null, "image/" + suffix, false);
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info("上传失败！" + fileName);
                    } else {
                        logger.info("上传成功！");
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    System.out.println(e.getMessage());
                    logger.error("上传异常！！！" + fileName);
                }
            }
        }

    }

    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDelete(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        String value = record.value().toString();
        Event event = JSONObject.parseObject(value, Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        //删除帖子
        elasticSearchService.deleteDiscussPostById(event.getEntityId());
    }

    /**
     * Kafka消费者，消费topic为 publish
     * 监听publish 事件，根据事件，修改ElasticSearch中"discusspost"index的数据
     * @param record
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublish(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        String value = record.value().toString();
        Event event = JSONObject.parseObject(value, Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        /*
         * 想想这里面的逻辑，是根据event里的数据再去查数据库，再存到ES中，为什么要这样？
         * 为什么不是事件里直接包含discussPost对象，然后不用IO、直接存到ES中呢？
         */
        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(discussPost);
    }

    /**
     * Kafka的消费者，消费topic为 comment,follow,like
     * 监听comment,follow,like事件，生成Message对象并存入数据库，作为对用户的系统通知。
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        //增强健壮性以及日志记录
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        //将record中的value取出并转换为Event对象
        String value = record.value().toString();
        JSONObject jsonObject = JSONObject.parseObject(value);
        Event event = jsonObject.to(Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        //
        Message message = convertEventToMessage(event);
        messageService.addMessage(message);
    }

    /**
     * 将event对象转换为Message对象
     * @param event
     * @return
     */
    private Message convertEventToMessage(Event event) {
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());

        //构建map，转化成Json字符串，存入content
        Map<String, Object> map = new HashMap<>();
        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            map.putAll(event.getData());
        }
        message.setContent(JSONObject.toJSONString(map));
        return message;
    }
}
