package com.wildcodeschool.myProjectWithDB.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.wildcodeschool.myProjectWithDB.entities.School;
import com.wildcodeschool.myProjectWithDB.repositories.SchoolRepository;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
public class SchoolController {

    private final static String DB_URL = "jdbc:mysql://localhost:3306/wild_db_quest?serverTimezone=GMT";
    private final static String DB_USER = "root";
    private final static String DB_PASSWORD = "Imeo17D%";

    @GetMapping("/api/schools")
    public List<School> getWizards(@RequestParam(defaultValue ="%")  String countryParam) {
        try(
            Connection connection = DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
            );
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM school WHERE country LIKE ?"
            );
        ) {
            statement.setString(1, countryParam);

            try(
            		ResultSet resultSet = statement.executeQuery();
        ) {
            List<School> schools = new ArrayList<School>();

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int capacity = resultSet.getInt("capacity");
                String country = resultSet.getString("country");
                schools.add(new School(id, name, capacity, country));
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

    @PostMapping("/api/schools")
    @ResponseStatus(HttpStatus.CREATED)
    public School store(
        @RequestParam String name,
        @RequestParam (required = false, name = "capacity") int capacity,
        @RequestParam String country
    ) {
    	int idGeneratedByInsertion = SchoolRepository.insert(
    		    name,
    		    capacity,
    		    country
    		);
    		return SchoolRepository.selectById(
    		    idGeneratedByInsertion
    		);
    }
    @PutMapping("/api/schools/{id}")
    public School update(
    	    @PathVariable int id,
    	    @RequestParam(required = false) String name,
    	    @RequestParam(required = false) Integer capacity,
    	    @RequestParam(required = false) String country
    	) {
        School school = SchoolRepository.selectById(id);
        SchoolRepository.update(
            id,
            name != null ? name : school.getName(),
            capacity != null ? capacity : school.getCapacity(),
            country != null ? country : school.getCountry()
        );
        return SchoolRepository.selectById(id);    }

}