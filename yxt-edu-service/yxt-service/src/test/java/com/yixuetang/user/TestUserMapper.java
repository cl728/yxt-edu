package com.yixuetang.user;

import com.yixuetang.YxtServiceApplication;
import com.yixuetang.entity.user.User;
import com.yixuetang.user.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}