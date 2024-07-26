package org.vls;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vls.User;
import org.vls.VLSException;
import org.vls.VirtualLearningSystem;

public class VirtualLearningSystemTest {
    private VirtualLearningSystem system;

    @BeforeEach
    public void setUp() {
        system = new VirtualLearningSystem();
    }

    @Test
    public void testLogin_ValidCredentials() {
        try {
            User user = system.login(1, "password");
            assertNotNull(user);
            assertEquals(1, user.getLoginId());
        } catch (SQLException | VLSException e) {
            fail("Exception should not be thrown for valid credentials.");
        }
    }

    @Test
    public void testInvalidCredentials() {
        Exception exception = assertThrows(VLSException.class, () -> {
            system.login(1, "wrongpassword");
        });
        assertEquals("Invalid login credentials.", exception.getMessage());
    }
    @Test
    public void testLoginFailure() {
        try {
            VirtualLearningSystem.login(1, "wrongpassword");
            fail("Expected VLSException to be thrown");
        } catch (VLSException | SQLException e) {
            assertTrue(e instanceof VLSException);
        }
    }
    @Test
    public void testSearchCoursesByName() throws SQLException {
        List<Course> courses = VirtualLearningSystem.searchCourses("Java");
        assertEquals(1, courses.size());
        assertTrue(courses.get(0).toString().contains("Java Full Stack"));
    }

    @Test
    public void testGetCourse() throws SQLException, VLSException {
        Course course = VirtualLearningSystem.getCourse(1);
        assertNotNull(course);
        assertTrue(course.toString().contains("Java Full Stack"));
    }
}
