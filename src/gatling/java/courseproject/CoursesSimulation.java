package courseproject;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * This sample is based on our official tutorials:
 * <ul>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/quickstart">Gatling quickstart tutorial</a>
 *   <li><a href="https://gatling.io/docs/gatling/tutorials/advanced">Gatling advanced tutorial</a>
 * </ul>
 */
public class CoursesSimulation extends Simulation {

    FeederBuilder<String> feeder = csv("search.csv").random();
    ChainBuilder list =
            exec(http("Home").get("/swagger-ui.html"))
                    .pause(1)
                    .exec(http("Get all courses")
                            .get("/api/v1/courses"));
    ChainBuilder add =
        feed(feeder)
            .exec(
                http("Add courses")
                    .post("/api/v1/courses")
                        .body(StringBody("{ \"title\": \"#{title}\", \"description\": \"#{description}\" }"))
            );

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
                .contentTypeHeader("application/json")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
            );

    ScenarioBuilder openSwaggerAndAdd = scenario("Home").exec(add, list);
    {
        setUp(
                openSwaggerAndAdd.injectOpen(rampUsers(1000).during(10))
        ).protocols(httpProtocol);
    }
}
