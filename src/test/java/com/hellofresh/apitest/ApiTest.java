package com.hellofresh.apitest;

//TestNG imports
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

//Logger imports
import org.pmw.tinylog.Logger;

//RestAssured imports
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

//Java Imports
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

//Wiremock import
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

//TypeSafe imports
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApiTest {
	// Config object to read default.properties using typesafe library
	Config config = null;
	List<String> countryList;
	String strResponse = "";
	JsonPath jsonPath;
	Response response;
	WireMockServer wireMockServer;

	/*
	 * Initial Setup before executing any Tests
	 * Read teh default.conf file
	 */
	@BeforeTest(alwaysRun = true)
	public void initialSetup() {
		// Read conf file
		try {
			config = ConfigFactory.load("default.conf");
		} catch (Exception e) {
			Logger.error("default.conf is not in Valid Json format Or File Not Found, Execution will not Proceed");
			System.exit(-1);
		}

		// Get the API baseURI i.e. Server Name
		RestAssured.baseURI = config.getString("baseURI");
		RestAssured.rootPath = "RestResponse.result";
		// Countries List to Verify
		countryList = config.getStringList("countries");

	}

	@Test(description = "Get all countries and validate that US, DE and GB were returned in the response")
	public void getAllRequest() {

		// Fire /GET/ALL method to get countries
		Logger.info("Running GET/all to get List of all countries");
		response = given().when().get("/get/all").then().extract().response();
		strResponse = response.asString();

		assertEquals(response.statusCode(), 200);

		// if Response got generated successfully, Parse the JSonPath
		if (strResponse != null) {
			Logger.info("Response got successfully generated");
			Logger.info("Creating JSOn Object Now");
			jsonPath = new JsonPath(strResponse);
		}

		Logger.info("Searching within the response for the list of countries now");
		for (String strTempVar : countryList) {
			assertTrue(jsonPath.getString("RestResponse.result.alpha2_code").contains(strTempVar));
		}
	}

	@Test(description = "Get each country (US, DE and GB) individually andvalidate the response")
	public void getCountriesOneByOne() {

		// List<String> countries = config.getStringList("countries");

		for (String strTempVar : countryList) {
			Logger.info("Checking Now:" + strTempVar);
			given().when().get("/get/iso2code/" + strTempVar).then().assertThat().statusCode(200).and().assertThat()
					.body("alpha2_code", equalToIgnoringCase(strTempVar));
		}
	}

	@Test(description = "Get information for inexistent countries 'ABC' and validate the response")
	public void getNonExistentCountry() {
		Logger.info("Checking for a Non-existent country ABC");

		Response response = given().when().get("/get/iso2code/abc").then().extract().response();
		strResponse = response.asString();

		// System.out.println("Response Code:"+response.getStatusCode());
		assertEquals(response.getStatusCode(), 200);
		jsonPath = new JsonPath(strResponse);

		assertTrue(jsonPath.getString("RestResponse.messages")
				.contains("No matching country found for requested code [abc]"));
		// System.out.println(jsonPath.getString("RestResponse.messages"));
	}

	@Test(description = "Validate new country addition using POST(")
	public void postAdditionOfNewCountry() throws UnknownHostException, IOException {
		Logger.info("Adding a new Country 'TC'");

		RestAssured.baseURI = "http://localhost:9099";
		RestAssured.rootPath = "";

		wireMockServer = new WireMockServer(9099);
		wireMockServer.start();
		configureFor("localhost", 9099);
		stubFor(post(urlEqualTo("/post"))
				// .withHeader("Content-Type", equalTo("application/json"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
						.withBody("{\"alpha2_code\": \"TC\"}")));

		given().param("name", "Test Country", "alpha2_code", "TC", "alpha3_code", "TCY").when().post("/post").then()
				.assertThat().statusCode(200).body("alpha2_code", org.hamcrest.Matchers.equalTo("TC"));

		wireMockServer.stop();

	}
}
