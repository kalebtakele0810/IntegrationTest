package com.aaupush.reminder;

import java.io.File;
import java.sql.Connection;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ViewReminderTest {

	String url = "http://localhost:8080/server";
	Connection con;
	String databaseURL = "jdbc:mysql://localhost:3306/aaupush";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String dbPassword = "";

	int id;
	String duedate = null;
	String title = null;
	String place = null;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Test
	public void viewReminder() {

		/* sending the Reminder to be viewed */
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Reminder/view/addReminder.json")).post("/ReminderServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		Assert.assertEquals("OK", status.getAsString());

		/* sending the Reminder view request */
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Reminder/view/viewReminder.json")).post("/ReminderServlet");
		jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));
		errorCollector.checkThat("2", Matchers.equalTo(jobj.getAsJsonObject("body").get("id").getAsString()));
		errorCollector.checkThat("01-01-1970 01:00",
				Matchers.equalTo(jobj.getAsJsonObject("body").get("duedate").getAsString()));
		errorCollector.checkThat("test", Matchers.equalTo(jobj.getAsJsonObject("body").get("title").getAsString()));
		errorCollector.checkThat("test", Matchers.equalTo(jobj.getAsJsonObject("body").get("place").getAsString()));

	}

}
