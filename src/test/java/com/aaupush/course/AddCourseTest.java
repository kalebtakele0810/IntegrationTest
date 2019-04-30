package com.aaupush.course;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddCourseTest {

	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";

	int id;
	String name = null;
	String coursecode = null;
	String ects = null;
	float credithour;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Test
	public void addCourse() {
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Course/addCourse.json")).post("/CourseServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		this.getCourse();
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat(1, Matchers.equalTo(this.id));
		errorCollector.checkThat("Test12", Matchers.equalTo(this.coursecode));
		errorCollector.checkThat(5.6f, Matchers.equalTo(this.credithour));
		errorCollector.checkThat("3.4", Matchers.equalTo(this.ects));
		errorCollector.checkThat("test", Matchers.equalTo(this.name));
	}

	public void getCourse() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.course where aaupush.course.id='1' ;");

			while (rs.next()) {
				if (!rs.getString(2).isEmpty()) {
					this.id = rs.getInt(1);
					this.coursecode = rs.getString(2);
					this.credithour = Float.parseFloat(rs.getString(3));
					this.ects = rs.getString(4);
					this.name = rs.getString(5);

				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
	}

}
