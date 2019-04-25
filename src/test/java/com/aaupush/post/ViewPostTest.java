package com.aaupush.post;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ViewPostTest {

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
	public void viewPost() {

		/* sending the post to be viewed */
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/view/addPost.json")).post("/PostServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		Assert.assertEquals("OK", status.getAsString());

		/* sending the post view request */
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/view/viewPost.json")).post("/PostServlet");
		jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat("2", Matchers.equalTo(jobj.getAsJsonObject("body").get("id").getAsString()));
		errorCollector.checkThat("test content", Matchers.equalTo(jobj.getAsJsonObject("body").get("content").getAsString()));
		errorCollector.checkThat("12", Matchers.equalTo(jobj.getAsJsonObject("body").get("type").getAsString()));
		errorCollector.checkThat("01-01-1970 01:00", Matchers.equalTo(jobj.getAsJsonObject("body").get("pubdate").getAsString()));

	}

}
