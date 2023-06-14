package com.demo.app.repository;

import com.demo.app.ProjectDesignIApplication;
import com.demo.app.model.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProjectDesignIApplication.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername(){
        var expectUser = User.builder()
                .username("admin")
                .email("knkuro00@gmail.com")
                .build();
        var actualUser = userRepository.findByUsername("admin").get();
        Assertions.assertThat(actualUser.equals(expectUser)).isTrue();
    }

    @Test
    public void testFindByUsernameAndEnabledIsTrue(){
        var expectUser = User.builder()
                .username("admin")
                .email("knkuro00@gmail.com")
                .enabled(true)
                .build();
        var actualUser = userRepository.findByUsernameAndEnabledIsTrue("admin").get();
        Assertions.assertThat(actualUser.equals(expectUser)).isTrue();
    }

    @Test
    public void testExistsByUsername(){
        boolean existed = userRepository.existsByUsername("admin");
        Assertions.assertThat(existed).isTrue();
    }

    @Test
    public void testExistsByEmail(){
        boolean existed = userRepository.existsByEmailAndEnabledTrue("knkuro00@gmail.com");
        Assertions.assertThat(existed).isTrue();
    }

    @Test
    public void testFindByEnabledFalseAndCreatedAtBefore(){
        var waitedTime = LocalDateTime.now().minusMinutes(5);
        var expectUser = userRepository.findByEnabledFalseAndCreatedAtBefore(waitedTime);
        Assertions.assertThat(expectUser).isNotNull();
    }

    @Test
    public void testExistByEmailOrUsername(){

    }

}
