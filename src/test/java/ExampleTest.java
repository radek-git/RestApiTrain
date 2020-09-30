import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.Headers;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExampleTest {

    @Test
    public void checkErrorAndResponse() {
        get("https://sv443.net/jokeapi/v2/joke/Any")
                .then()
                .body("flags.nsfw", is(false))
        .and()
        .body("id", is(143));
    }

    @Test
    public void checkSalary() {
        get("http://dummy.restapiexample.com/api/v1/employees")
                .then()
                .body("data[4].id", is("5"));
    }

    @Test
    public void checkEmployeeName() {
        get("http://dummy.restapiexample.com/api/v1/employees")
                .then()
                .body("data[3].employee_name", is("Cedric Kelly"))
                .and()
                .body("data[3].employee_salary", is("433060"))
                .and()
                .body("data[4].employee_name", is("Airi Satou"))
                .and()
                .body("data[4].employee_salary", is("162700"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", "John");
        jsonObject.put("lastName", "Smith");
        jsonObject.put("age", 25);

        Map<String, java.io.Serializable> map = new LinkedHashMap<String, java.io.Serializable>(4);
        map.put("streetAddress", "21 2nd Street");
        map.put("city", "New York");
        map.put("state", "NY");
        map.put("postalCode", 10021);

        // putting address to JSONObject
        jsonObject.put("address", map);

        JSONArray jsonArray = new JSONArray();

        map = new LinkedHashMap(2);
        map.put("type", "home");
        map.put("number", "212 555-1234");

        // adding map to list
        jsonArray.add(map);

        map = new LinkedHashMap(2);
        map.put("type", "fax");
        map.put("number", "212 555-1234");

        // adding map to list
        jsonArray.add(map);

        // putting phoneNumbers to JSONObject
        jsonObject.put("phoneNumbers", jsonArray);

        // writing JSON to file:"JSONExample.json" in cwd
        PrintWriter pw = null;
        try {
            pw = new PrintWriter("JSONExample.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.write(jsonObject.toJSONString());

        pw.flush();
        pw.close();

    }



    @Test
    public void checkNumber() {
        get("https://www.metaweather.com/api/location/search/?query=warsaw")
                .then()
                .body("[0].woeid", is(523920));
    }

    @Test
    public void checkFloat() {
        get("https://www.metaweather.com/api/location/search/?query=warsaw")
                .then()
                .body("[0].woeid", is(not(523920f)));
    }

    @Test
    public void checkBigDecimal() {
        given().config(RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .when()
                .get("https://www.metaweather.com/api/location/search/?query=warsaw")
                .then()
                .body("[0].woeid", is(not(BigDecimal.valueOf(523920))));
    }



    @Test
    public void checkJson() {
        get("http://dummy.restapiexample.com/api/v1/employees")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("file.json"));
    }

    @Test
    public void checkParsedJson() {
        String json = "[1, 2, 3]";
        List<Object> list = from(json).getList("$");
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
    }

    @Test
    public void getSingleHeader() {
        Response response = get("http://dummy.restapiexample.com/api/v1/employees");
        Headers headers = response.getHeaders();

        assertEquals(headers.get("X-Sol").getValue(), "pub_site");
        assertEquals(headers.get("Response").getValue(), "200");
        assertEquals(headers.get("X-Middleton-Display").getValue(), "staticcontent_sol");
        assertEquals(headers.get("X-Ezoic-Cdn").getValue(), "Hit ds;mm;64e5dcd8fd074fe044e20470a9643699;2-133674-2;4c3a9bc1-6d23-4e92-60b4-69a3167ccb07");

    }

    @Test
    public void getHeaderWithMultipleValues() {
        Response response = get("http://dummy.restapiexample.com/api/v1/employees");
        Headers headers = response.getHeaders();

        List<String> values = Arrays.asList(headers.getValues("X-Ezoic-Cdn").get(0).split(";").clone());
        assertEquals(values.get(0), "Hit ds");
        assertThat(values.get(1), is("mm")) ;
    }


    @Test
    public void checkEmployee() {
        get("http://dummy.restapiexample.com/api/v1/employees")
                .then()
                .body("data[0].id", is("1"))
                .and()
                .body("data[0].employee_name", equalTo("Tiger Nixon"))
                .and()
                .body("data[0].employee_salary", is("320800"))
                .and()
                .body("data[0].employee_age", is("61"))
                .and()
                .body("data[0].profile_image", is(""));
    }





}
