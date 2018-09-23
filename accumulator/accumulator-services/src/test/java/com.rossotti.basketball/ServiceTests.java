package com.rossotti.basketball;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.rossotti.basketball.config.SpringConfig.class)
public class ServiceTests {

    @Test
    public void contextLoads() {
    }
}
