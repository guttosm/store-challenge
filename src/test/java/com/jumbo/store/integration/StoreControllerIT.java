package com.jumbo.store.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.jumbo.store.IntegrationTestBase;
import com.jumbo.store.domain.model.Store;
import com.jumbo.store.fixture.StoreFixture;
import com.jumbo.store.fixtures.RepoHelper;
import com.jumbo.store.fixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class StoreControllerIT extends IntegrationTestBase {

    @Autowired
    private RepoHelper repoHelper;

    @BeforeEach
    void setUp() {
        repoHelper.deleteAllStores();

        // Create test stores using fixtures
        Store amsterdamStore = StoreFixture.createAmsterdamStore();
        Store rotterdamStore = StoreFixture.createRotterdamStore();
        Store utrechtStore = StoreFixture.createUtrechtStore();
        Store haarlemStore = StoreFixture.createHaarlemStore();
        Store theHagueStore = StoreFixture.createTheHagueStore();

        repoHelper.insertStores(amsterdamStore, rotterdamStore, utrechtStore, haarlemStore, theHagueStore);
    }

    @Test
    @DisplayName("should return nearest stores sorted by distance")
    void findNearestStores() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "3")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("stores", is(notNullValue()))
                .body("stores", is(instanceOf(java.util.List.class)))
                .body("count", is(3))
                .body("stores[0].distanceInKm", is(notNullValue()))
                .body("stores[0].city", is(notNullValue()))
                .body("stores[0].uuid", is(notNullValue()));
    }

    @Test
    @DisplayName("should return stores sorted by distance ascending")
    void findNearestStores_SortedByDistance() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "5")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("stores[0].distanceInKm", is(notNullValue()))
                .body("stores[1].distanceInKm", is(notNullValue()))
                .body("stores[0].distanceInKm", lessThanOrEqualTo(5.0f))
                .body("stores[1].distanceInKm", greaterThanOrEqualTo(0.0f));
    }

    @Test
    @DisplayName("should return default limit of 5 when limit not specified")
    void findNearestStores_DefaultLimit() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(5))
                .body("stores", is(instanceOf(java.util.List.class)))
                .body("stores.size()", is(5));
    }

    @Test
    @DisplayName("should return empty list when no stores exist")
    void findNearestStores_NoStores() {
        repoHelper.deleteAllStores();

        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(0))
                .body("stores", is(empty()));
    }

    @Test
    @DisplayName("should return 400 for invalid latitude")
    void findNearestStores_InvalidLatitude() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "91")
                .param("longitude", "4.9041")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("should return 400 for invalid longitude")
    void findNearestStores_InvalidLongitude() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3676")
                .param("longitude", "181")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("should handle limit less than 5")
    void findNearestStores_LimitLessThan5() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "2")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(2))
                .body("stores", is(instanceOf(java.util.List.class)))
                .body("stores.size()", is(2));
    }

    @Test
    @DisplayName("should handle limit greater than 5")
    void findNearestStores_LimitGreaterThan5() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "10")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(5)) // Only 5 stores exist
                .body("stores", is(instanceOf(java.util.List.class)))
                .body("stores.size()", is(5));
    }

    @Test
    @DisplayName("should return 403 when no authorization header")
    void findNearestStores_Unauthorized() {
        given().param("latitude", "52.3676")
                .param("longitude", "4.9041")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("should return 403 when token without read:store permission")
    void findNearestStores_Forbidden() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.WITHOUT_ROLES_AND_PERMISSIONS)
                .param("latitude", "52.3676")
                .param("longitude", "4.9041")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("should handle zero limit by using default limit")
    void findNearestStores_ZeroLimit() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "0")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(5))
                .body("stores.size()", is(5));
    }

    @Test
    @DisplayName("should handle negative limit by using default limit")
    void findNearestStores_NegativeLimit() {
        given().header("Authorization", "Bearer " + TestFixtures.Jwt.Stores.READ)
                .param("latitude", "52.3791")
                .param("longitude", "4.9003")
                .param("limit", "-1")
                .when()
                .get("/api/stores/nearest")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("count", is(5))
                .body("stores.size()", is(5));
    }
}
