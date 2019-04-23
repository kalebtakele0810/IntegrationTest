
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

public class PostTest {
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
	public void test() {

		Response response = RestAssured.given().contentType("application/json")
				.body(new File("src/main/resources/Post/sendPost.json")).post("/PostServlet");
		JsonObject jobj = new Gson().fromJson(response.asString(), JsonObject.class);
		JsonElement status = jobj.get("status");
		assertEquals(200, response.getStatusCode());
		assertEquals("OK", status.getAsString());
		assertTrue(this.getPost("3"));

	}

	public boolean getPost(String postID) {
		boolean result = false;
		try {
			Class.forName(this.driver);
			this.con = DriverManager.getConnection(this.databaseURL, this.userName, this.dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from aaupush.post where aaupush.post.Id='" + postID + "' ;");

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
