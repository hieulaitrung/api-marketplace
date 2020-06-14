package com.example.demo.integration;

import com.example.demo.DemoApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes={DemoApplication.class})
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {
}
