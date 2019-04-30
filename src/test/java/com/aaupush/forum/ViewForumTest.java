package com.aaupush.forum;

import java.io.File;
import java.sql.Connection;

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

public class ViewForumTest {

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

	@Test
	public void viewForum() {

		/* sending the Forum to be viewed */
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Forum/view/addForum.json")).post("/ForumServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		Assert.assertEquals("OK", status.getAsString());

		/* sending the Forum view request */
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Forum/view/viewForum.json")).post("/ForumServlet");
		jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat("2", Matchers.equalTo(jobj.getAsJsonObject("body").get("id").getAsString()));
		errorCollector.checkThat("test name", Matchers.equalTo(jobj.getAsJsonObject("body").get("name").getAsString()));
		errorCollector.checkThat("test code",
				Matchers.equalTo(jobj.getAsJsonObject("body").get("joincode").getAsString()));
		errorCollector.checkThat("1", Matchers.equalTo(jobj.getAsJsonObject("body").get("privacy").getAsString()));
		errorCollector.checkThat("test contet",
				Matchers.equalTo(jobj.getAsJsonObject("body").get("description").getAsString()));
		errorCollector.checkThat("test id",
				Matchers.equalTo(jobj.getAsJsonObject("body").get("forumid").getAsString()));

	}

}
