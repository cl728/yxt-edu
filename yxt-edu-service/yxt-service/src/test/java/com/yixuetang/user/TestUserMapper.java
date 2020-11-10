package com.yixuetang.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixuetang.YxtServiceApplication;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/10/23 14:36
 */
@SpringBootTest(classes = YxtServiceApplication.class)
@RunWith(SpringRunner.class)
public class TestUserMapper {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testFindAll() {
//        this.userMapper.findAll().forEach( System.out::println );
        this.userMapper.selectList( null ).forEach( System.out::println );
    }

    @Test
    public void testFindByUsernameAndPassword() {
        User user = this.userMapper.findByUsernameAndPassword( "Colin", "ab88ae455430055fe126c4139c6d78c8" );
        System.out.println( user );
    }

    @Test
    public void testFindByCourseId() {
        System.out.println( this.userMapper.findByCourseId( 4L ) );
    }

    @Test
    public void testFindPageByIds() {
//        this.userMapper.findPageByIds( new Page<>( 1L, 5L ), Arrays.asList( 4L, 8L, 1L, 9L ) ).forEach( System.out::println );
    }

}