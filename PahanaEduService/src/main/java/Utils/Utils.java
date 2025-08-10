/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

/**
 *
 * @author Malindu Dilshan
 */
public class Utils {
    static final String DB_URL = "jdbc:mysql://localhost/pahana";

    static final String USER = "root";
    static final String PASS = "";
   
    
    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
         
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
