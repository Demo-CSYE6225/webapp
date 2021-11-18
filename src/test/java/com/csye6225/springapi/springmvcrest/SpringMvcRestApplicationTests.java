package com.csye6225.springapi.springmvcrest;

import com.csye6225.springapi.springmvcrest.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SpringMvcRestApplicationTests {
// Testing
    @Test
    public void testUserName() {
        User user = new User("Naveen","Kumar","naveen@gmail.com","123456",java.time.Clock.systemUTC().instant().toString(),java.time.Clock.systemUTC().instant().toString());
        assertEquals("naveen@gmail.com",user.getUsername());
    }

}
