package com.aaupush.com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserTest {
	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";

	@Before
	public void setUp() throws Exception {
		RestAssured.baseURI = this.url;
	}

	@Test
	public void addUser() {

		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/User/addUser.json")).post("/UserServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		assertEquals(200, response.getStatusCode());
		assertEquals("OK", status.getAsString());
		assertTrue(this.getUser("3"));

	}

	@Test
	public void signUp() {

		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/User/signup.json")).post("/StudentServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		assertEquals(200, response.getStatusCode());
		assertEquals("OK", status.getAsString());
		assertTrue(this.getStudent("3"));

	}

	public boolean getUser(String userID) {
		boolean result = false;
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.user where aaupush.user.Id='" + userID + "' ;");

			while (rs.next()) {
				if (!rs.getString("Id").isEmpty()) {
					result = true;
				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
		return result;
	}

	public boolean getStudent(String studentID) {
		boolean result = false;
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st
					.executeQuery("select * from aaupush.student where aaupush.student.Id='" + studentID + "' ;");

			while (rs.next()) {
				if (!rs.getString("Id").isEmpty()) {
					result = true;
				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
		return result;
	}

}
