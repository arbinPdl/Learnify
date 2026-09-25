package com.learnify.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=VGhpcy1pcy1hLXRlc3Qtc2VjcmV0LXdoaWNoLWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=")
class LearnifyBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
