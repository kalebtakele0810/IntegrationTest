package com.aaupush.user;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddUserTest {

	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";
	Response response = null;
	JsonElement status = null;
	String Email = null;
	int id;
	String Firstname = null;
	String Lastname = null;
	String Password = null;
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setUp() {

	}

	@Test
	public void addUser() {
		RestAssured.baseURI = this.url;
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/User/addUser.json")).post("/UserServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		this.getUser();
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat(3, Matchers.equalTo(this.id));
		errorCollector.checkThat("test", Matchers.equalTo(this.Firstname));
		errorCollector.checkThat("test", Matchers.equalTo(this.Lastname));
		errorCollector.checkThat("test", Matchers.equalTo(this.Password));
		errorCollector.checkThat("test@test.com", Matchers.equalTo(this.Email));
	}

	public void getUser() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.user where aaupush.user.id='3' ;");

			while (rs.next()) {

				this.id = Integer.parseInt(rs.getString(1));
				this.Email = rs.getString(2);
				this.Firstname = rs.getString(3);
				this.Lastname = rs.getString(4);
				this.Password = rs.getString(5);

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
	}
}
