package com.wildcodeschool.myProjectWithDB.repositories;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.wildcodeschool.myProjectWithDB.entities.School;

public class SchoolRepository {

    private final static String DB_URL = "jdbc:mysql://localhost:3306/wild_db_quest?serverTimezone=GMT";
    private final static String DB_USER = "root";
    private final static String DB_PASSWORD = "Imeo17D%";

    public static List<School> selectByLastname(String lastname) {
        try(
            Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
            );
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM school WHERE lastname LIKE ?"
            );
        ) {
            statement.setString(1, lastname);

            try(
                ResultSet resulSet = statement.executeQuery();
            ) {
                List<School> schools = new ArrayList<School>();

                while(resulSet.next()){
                    int id = resulSet.getInt("id");
                    String name = resulSet.getString("name");
                    int capacity = resulSet.getInt("capacity");
                    String country = resulSet.getString("country");

                    schools.add(new School(id, name, capacity,country));
                }

                return schools;
            }
        }
        catch (SQLException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "", e
            );
        }
    }

    public static School selectById(int id) {
        try(
            Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
            );
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM school WHERE id=?"
            );
        ) {
            statement.setInt(1, id);

            try(
                ResultSet resulSet = statement.executeQuery();
            ) {
                if(resulSet.next()){
                    String name = resulSet.getString("name");
                    int capacity = resulSet.getInt("capacity");
                    String country = resulSet.getString("country");

                    return new School(id, name, capacity, country);
                }
                else {
                    throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "unknown id in table school"
                    );
                }
            }
        }
        catch (SQLException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "", e
            );
        }
    }
    
    public static int insert(
    	    String name,
    	    int capacity,
    	    String country
    	) {
    	try(
    		    Connection connection = DriverManager.getConnection(
    		        DB_URL, DB_USER, DB_PASSWORD
    		    );
    		    PreparedStatement statement = connection.prepareStatement(
    		        "INSERT INTO school (name, capacity, country) VALUES (?, ?, ?)",
    		        Statement.RETURN_GENERATED_KEYS
    		    );
    		) {
    		statement.setString(1, name);
    		statement.setInt(2, capacity);
    		statement.setString(3, country);
    		
    		if(statement.executeUpdate() != 1) {
    		    throw new ResponseStatusException(
    		        HttpStatus.INTERNAL_SERVER_ERROR, "failed to insert data"
    		    );
    		}

    		try(
    			    ResultSet generatedKeys = statement.getGeneratedKeys();
    			) {
    			    if(generatedKeys.next()) {
    			        return generatedKeys.getInt(1);
    			    }
    			    else {
    			        throw new ResponseStatusException(
    			            HttpStatus.INTERNAL_SERVER_ERROR, "failed to get inserted id"
    			        );
    			    }
    			}
    		}
    		catch (SQLException e) {
    		    throw new ResponseStatusException(
    		        HttpStatus.INTERNAL_SERVER_ERROR, "", e
    		    );
    		}
    	}

}


