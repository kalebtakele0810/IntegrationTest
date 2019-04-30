package com.aaupush.forum;

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

public class AddForumTest {

	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";

	int id;
	String name = null;
	String joincode = null;
	boolean privacy;
	String description = null;
	String forumid = null;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setUp() {

	}

	@Test
	public void addForum() {
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Forum/addForum.json")).post("/ForumServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		this.getForum();
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat(1, Matchers.equalTo(this.id));
		errorCollector.checkThat("test name", Matchers.equalTo(this.name));
		errorCollector.checkThat("test code", Matchers.equalTo(this.joincode));
		errorCollector.checkThat(true, Matchers.equalTo(this.privacy));
		errorCollector.checkThat("test contet", Matchers.equalTo(this.description));
		errorCollector.checkThat("test id", Matchers.equalTo(this.forumid));
	}

	public void getForum() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.forum where aaupush.forum.id='1' ;");

			while (rs.next()) {
				if (!rs.getString(2).isEmpty()) {
					this.id = rs.getInt(1);
					this.description = rs.getString(2);
					this.forumid = rs.getString(3);
					this.joincode = rs.getString(4);
					this.name = rs.getString(5);
					this.privacy = Boolean.parseBoolean(rs.getString(6));

				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
	}

}
