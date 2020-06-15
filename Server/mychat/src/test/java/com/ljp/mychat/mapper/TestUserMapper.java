package com.ljp.mychat.mapper;

import com.ljp.mychat.MychatApplication;
import com.ljp.mychat.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TestUserMapper {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectAll(){
        User user = userMapper.getUserByMychatId("lip");
        System.out.println(user);
    }

}
