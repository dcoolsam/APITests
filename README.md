# Project 2 - API Testing
*** Tools Used:
-----
- `Rest-Assured` libraries which in turn helps to write api-tests functions in BDD format
- `Wiremock`: Mocking post request for Stubbing
- `ReportNG`: to create nicer reports
- `TestNG`: for test annotations
- `TypeSafe`: for reading JSon conf file
- `JSonPath`: to validate the response present in JSon format

*** How to Run ***
-----
* Using IDE like Eclipse: 
  * Import the project in IDE
  * Right click `pom.xml` and click `Run As` , Maven >> `Generate sources`. After dependencies are downloaded.
  * Right click again, `pom.xml` and `Run As`, Maven >> `Test`. If it asks to specify the goal, choose as 'test'   

* Another option is to go to the root of the folder and run maven from command line using: `mvn compile generate-resources` and then followed by `mvn test`

*** Technical Know Hows ***
-----
* TypeSafe: Reads the default.conf file which is JSon format using typesafe. Gets the baseURI and countries to test list from this conf file.
*  Logic behind Testing each functionality:
 * Check all 3 countries are present: Generate the response, In the response check each country code is present using JSonPath. You can add more countries to default.conf and still the logic works flowlessly.
 * Check each of 3 countries one by one: Read default.conf to know which countries. Run the /GET request for each country by generating customer URI. Check in the response using assertThat()
 * Check non-existent country: Check the country code "ABC" and generate ther response. Assert using JSonPath "No matching country found" message is present.
 * Post Request: Create a Wiremock server programmatically on localhost:9099 and Send post request. Assert the country code is present in response and response code is 200.

*** Report(s) to check ***
-----
* Main Report: Created by ReportNG is available at: `./target/surefire-reports/html/index.html`. It shows both the statistics and which tests passed/failed.
* TestNG emailable report: available at: `./target/surefire-reports/emailable-report.html`

*** Dependencies used and usage
-----
* For Rest: Assured: rest-assured, hamcrest-all, 
* TestNG: For test assertions like @Test
* TinyLog: For Logging
* ReportNG: For Reporting
* Wiremock


Thanks for your time :-)
