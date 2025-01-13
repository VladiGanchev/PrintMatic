package com.example.printmatic;


import com.example.printmatic.init.DbInit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class PrintmaticApplicationTests {

	@MockBean
	DbInit dbInit;

	@Test
	void contextLoads() {
	}

}
