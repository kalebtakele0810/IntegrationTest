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

public class AddStudentTest {

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
	int entryyear;
	int sectionnumber;
	String registrationid = null;
	String phone;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setUp() {

	}

	@Test
	public void addUser1() {
		RestAssured.baseURI = this.url;
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/User/signup.json")).post("/SignUpServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		this.getStudent();
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat(3, Matchers.equalTo(this.id));
		errorCollector.checkThat("test", Matchers.equalTo(this.Firstname));
		errorCollector.checkThat("test", Matchers.equalTo(this.Lastname));
		errorCollector.checkThat("test", Matchers.equalTo(this.Password));
		errorCollector.checkThat("test@test.com", Matchers.equalTo(this.Email));
		errorCollector.checkThat(2009, Matchers.equalTo(this.entryyear));
		errorCollector.checkThat(3, Matchers.equalTo(this.sectionnumber));
		errorCollector.checkThat("GR6546543", Matchers.equalTo(this.registrationid));
		errorCollector.checkThat("0911111111", Matchers.equalTo(this.phone));

	}

	public void getStudent() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.user where aaupush.user.Id='3' ;");

			while (rs.next()) {
				if (!rs.getString("email").isEmpty()) {
					this.id = rs.getInt("id");
					this.Email = rs.getString("email");
					this.Firstname = rs.getString("firstname");
					this.Lastname = rs.getString("lastname");
					this.Password = rs.getString("password");
					this.entryyear = rs.getInt("entryyear");
					this.sectionnumber = rs.getInt("sectionnumber");
					this.registrationid = rs.getString("registrationid");
					this.phone = rs.getString("phone");
				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
	}

}
