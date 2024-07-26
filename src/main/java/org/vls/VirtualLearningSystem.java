package org.vls;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class User{
    private int loginId;
    private String password;

    public User(int loginId,String password)
    {
        this.loginId=loginId;
        this.password=password;
    }
    public int getLoginId()
    {
        return loginId;
    }
    public String getPassword()
    {
        return password;
    }
}

class Course{
    private int courseId;
    private String courseName;
    private String authorName;
    private int duration;
    private boolean availability;

    public Course(int courseId, String courseName, String authorName, int duration, boolean availability){
        this.courseId=courseId;
        this.courseName=courseName;
        this.authorName=authorName;
        this.duration=duration;
        this.availability=availability;

    }
    @Override
    public String toString(){
        return "Course ID: "+courseId+", Name: "+ courseName+",Author: "+authorName+ ", Duration: "+ duration+ " hours, Available: " + (availability ? "Yes":"No");

    }
}
class VLSException extends Exception{
    public VLSException(String message){
        super(message);
    }
}

public class VirtualLearningSystem {
    private static final String db_url = "jdbc:mysql://localhost:3306/VLSDB";
    private static final String db_username = "root";
    private static final String db_password = "mysql";
    private static User loggedInUser;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if(loggedInUser==null){
                System.out.println("1. Login");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if(choice==1){
                    System.out.println("Login ID: ");
                    int loginId=scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Password: ");
                    String password= scanner.nextLine();
                    try{
                        loggedInUser=login(loginId,password);
                        System.out.println("You have successfully logged in!👌👌");
                    }
                    catch(SQLException | VLSException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            else{
                System.out.println("1. Search Courses");
                System.out.println("2. View course Details");
                System.out.println("3. Add Course to Cart");
                System.out.println("4. Remove course from Cart");
                System.out.println("5. Logout me");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                try{
                    switch (choice){
                        case 1:
                            System.out.println("Enter course name or author: ");
                            String query = scanner.nextLine();
                            List<Course> courses =searchCourses(query);
                            for (Course course : courses){
                                System.out.println(course);
                            }
                            break;
                        case 2:
                            System.out.println("Enter course Id: ");
                            int courseId=scanner.nextInt();
                            scanner.nextLine();
                            Course course = getCourse(courseId);
                            System.out.println("The details of the course is listed below:");
                            System.out.println(course);
                            break;
                        case 3:
                            System.out.println("Enter course Id to add: ");
                            int addCourseId=scanner.nextInt();
                            scanner.nextLine();
                            addCurseToCart(addCourseId);
                            System.out.println("Course added to cart successfully!!");
                            break;
                        case 4:
                            System.out.println("Enter course Id to remove: ");
                            int removeCourseId = scanner.nextInt();
                            scanner.nextLine();
                            removeCourseFromCart(removeCourseId);
                            System.out.println("Course removed from your cart..");
                            break;
                        case 5:
                            loggedInUser=null;
                            System.out.println("Logged out successfully<>");
                            break;
                        default:
                            System.out.println("Invalid Option, try again with corect options??");
                            break;
                    }
                }catch(SQLException | VLSException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(db_url,db_username,db_password);
    }
    static User login(int loginId, String password) throws SQLException, VLSException{
        Connection conn=getConnection();
        String query = "select * from login where login_id=? and password=?";
        PreparedStatement stmt =conn.prepareStatement(query);
        stmt.setInt(1,loginId);
        stmt.setString(2,password);
        ResultSet rs=stmt.executeQuery();

        if(rs.next()){
        return new User(rs.getInt("login_id"),rs.getString("password"));
        }
        else {
            throw new VLSException("Invalid login credentials.");
        }
    }
    static List<Course> searchCourses(String query) throws SQLException{
        Connection conn = getConnection();
        String sql = "select * from courses where course_name like ? or author_name like ?";
        PreparedStatement stmt=conn.prepareStatement(sql);
        stmt.setString(1,"%"+query+"%");
        stmt.setString(2,"%"+query+"%");
        ResultSet rs= stmt.executeQuery();

        List<Course> courses = new ArrayList<>();
        while(rs.next()){
            courses.add(new Course(rs.getInt("course_id"),rs.getString("course_name"),rs.getString("author_name"),rs.getInt("duration"),rs.getBoolean("availability")));
        }
        return courses;
    }
    static Course getCourse(int courseId) throws SQLException,VLSException{
        Connection conn=getConnection();
        String sql="select * from courses where course_id= ?";
        PreparedStatement stmt=conn.prepareStatement(sql);
        stmt.setInt(1,courseId);
        ResultSet rs= stmt.executeQuery();

        if(rs.next()){
            return new Course(rs.getInt("course_id"),rs.getString("course_name"),rs.getString("author_name"),rs.getInt("duration"),rs.getBoolean("availability"));
        }
        else {
            throw new VLSException("Course has not found: ");
        }
    }
    private static void addCurseToCart(int courseId) throws SQLException{
        System.out.println("Course with ID"+ courseId+", is added to cart. ");

    }
    public static void removeCourseFromCart(int courseId) throws SQLException {
        System.out.println("Course with ID "+courseId+", has been removed from the cart.");

    }
}