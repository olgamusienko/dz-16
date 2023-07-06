import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.LocalDate;



public class RestAssuredTests {
//    https://restful-booker.herokuapp.com/
    private int bookingid;
    private ResponseBookingId bookingid2;

    @BeforeMethod
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    @Test(priority = 1)
    public void createNewBooking() {

        BookingDates bookingdates= new BookingDates()
                .builder()
                .checkin(LocalDate.of(2022, 01,01))
                .checkout(LocalDate.of(2023,12,31))
                .build();
        Booking booking = new Booking()
                .builder()
                .firstname("Olha")
                .lastname("Musienko")
                .totalprice(150)
                .depositpaid(true)
                .additionalneeds("Breakfast")
                .bookingdates(bookingdates)
                .build();

        Response response = RestAssured
                .given()
                .body(booking)
                .when()
                .post("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
        bookingid = response.as(ResponseBooking.class).getBookingid();
    }

    @Test(priority = 2)
    public void getAllBookingIds(){
        Response response = RestAssured.given().log().all().get("/booking");
        response.then().statusCode(200);
        response.prettyPrint();
        ResponseBookingId[] bookingList = RestAssured.given().when().get("booking/").as(ResponseBookingId[].class);
        bookingid2 = bookingList[0];
    }


    @Test(priority = 3)
    public void updateTotalPriceBooking() {
        JSONObject totalPriceUpdate = new JSONObject();
        totalPriceUpdate.put("totalprice", 300);
        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .body(totalPriceUpdate.toString())
                .when()
                .patch("/booking/" + bookingid);

        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 4)
    public void updateFirstNameAndAdditionalNeeds(){
        Booking booking = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingid2.getBookingid())
                .as(Booking.class);
        booking.setFirstname("Mary");
        booking.setAdditionalneeds("Dinner");

        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .body(booking)
                .when()
                .put("/booking/" + bookingid2.getBookingid());

        response.prettyPrint();
        response.then().statusCode(200);
    }

    @Test(priority = 5)
    public void deleteBooking(){
        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .delete("/booking/" + bookingid);
        response.prettyPrint();
        response.then().statusCode(201);

    }
}

