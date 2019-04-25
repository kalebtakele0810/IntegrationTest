package com.aaupush.post;

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

public class AddPostTest {

	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";

	int id;
	String content = null;
	String type = null;
	String pubdate = null;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setUp() {

	}

	@Test
	public void addPost() {
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/sendPost.json")).post("/PostServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		this.getPost();
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat(1, Matchers.equalTo(this.id));
		errorCollector.checkThat("test content", Matchers.equalTo(this.content));
		errorCollector.checkThat("12", Matchers.equalTo(this.type));
		errorCollector.checkThat("01-01-1970 01:00", Matchers.equalTo(this.pubdate));
	}

	public void getPost() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.post where aaupush.post.id='1' ;");

			while (rs.next()) {
				if (!rs.getString(2).isEmpty()) {
					this.id = rs.getInt(1);
					this.content = rs.getString(2);
					this.pubdate = rs.getString(3);
					this.type = rs.getString(4);
					
				}

			}
			rs.close();
			con.close();
		} catch (Exception e) {
			fail("could not connect to the database!!");
		}
	}

}
