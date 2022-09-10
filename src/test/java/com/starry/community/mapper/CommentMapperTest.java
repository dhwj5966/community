package com.starry.community.mapper;

import com.starry.community.CommunityApplication;
import com.starry.community.bean.Comment;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Starry
 * @create 2022-09-09-3:41 PM
 * @Describe
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    void selectCommentRows() {
        int i = commentMapper.selectCommentRows(1, 280);
        System.out.println(i);
    }

    @Test
    void selectComments() {
        List<Comment> comments = commentMapper.selectComments(2, 12, 1, 10);
    }

    @Test
    void insert(){
        Comment comment = new Comment();
        comment.setUserId(154);
        comment.setEntityType(2);
        comment.setEntityId(236);
        comment.setContent("是不是要打架？");
        comment.setTargetId(1);
        comment.setCreateTime(new Date());
        commentMapper.insertComment(comment);
    }
}