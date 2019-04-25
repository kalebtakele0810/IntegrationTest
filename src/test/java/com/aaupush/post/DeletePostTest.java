package com.aaupush.post;

import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

public class DeletePostTest {
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
	public void deletePostTest() {
		/* sending the post to be deleted */
		RestAssured.baseURI = this.url;
		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/delete/addPost.json")).post("/PostServlet");

		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		Assert.assertEquals("OK", status.getAsString());

		/* sending the post delete request */
		response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/delete/deletePost.json")).post("/PostServlet");
		jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		status = jobj.get("status");
		errorCollector.checkThat(200, Matchers.equalTo(response.getStatusCode()));
		errorCollector.checkThat("OK", Matchers.equalTo(status.getAsString()));

		this.getPost();
		errorCollector.checkThat(0, Matchers.equalTo(this.id));
		errorCollector.checkThat(null, Matchers.equalTo(this.content));
		errorCollector.checkThat(null, Matchers.equalTo(this.type));
		errorCollector.checkThat(null, Matchers.equalTo(this.pubdate));
	}

	public void getPost() {
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.post where aaupush.post.id='4' ;");

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
