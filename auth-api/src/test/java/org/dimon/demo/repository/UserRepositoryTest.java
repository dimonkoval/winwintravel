package org.dimon.demo.repository;

import org.dimon.demo.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.datasource.url=spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = User.builder()
                .username("test@example.com")
                .password("hashedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("test@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getUsername());
        assertEquals("hashedPassword", foundUser.get().getPassword());
    }

    @Test
    void findByUsername_WhenUserNotExists_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByUsername_ShouldBeCaseSensitive() {
        // Arrange
        User user = User.builder()
                .username("Test@Example.com")
                .password("hashedPassword")
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundUserLowercase = userRepository.findByUsername("test@example.com");
        Optional<User> foundUserUppercase = userRepository.findByUsername("TEST@EXAMPLE.COM");
        Optional<User> foundUserExact = userRepository.findByUsername("Test@Example.com");

        // Assert
        assertFalse(foundUserLowercase.isPresent());
        assertFalse(foundUserUppercase.isPresent());
        assertTrue(foundUserExact.isPresent());
    }

    @Test
    void saveUser_ShouldPersistCorrectly() {
        // Arrange
        User user = User.builder()
                .username("newuser@example.com")
                .password("newPassword")
                .build();

        // Act
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("newuser@example.com", savedUser.getUsername());
        assertEquals("newPassword", savedUser.getPassword());

        // Verify it can be retrieved
        Optional<User> foundUser = userRepository.findByUsername("newuser@example.com");
        assertTrue(foundUser.isPresent());
    }

    @Test
    void findByUsername_WithMultipleUsers_ShouldReturnCorrectOne() {
        // Arrange
        User user1 = User.builder()
                .username("user1@example.com")
                .password("pass1")
                .build();

        User user2 = User.builder()
                .username("user2@example.com")
                .password("pass2")
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Act
        Optional<User> foundUser1 = userRepository.findByUsername("user1@example.com");
        Optional<User> foundUser2 = userRepository.findByUsername("user2@example.com");

        // Assert
        assertTrue(foundUser1.isPresent());
        assertEquals("user1@example.com", foundUser1.get().getUsername());

        assertTrue(foundUser2.isPresent());
        assertEquals("user2@example.com", foundUser2.get().getUsername());
    }
}