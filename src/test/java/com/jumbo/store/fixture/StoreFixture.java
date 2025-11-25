package com.jumbo.store.fixture;

import com.jumbo.store.domain.model.Store;
import java.math.BigDecimal;

public class StoreFixture {

    public static Store createAmsterdamStore() {
        return Store.builder()
                .uuid("amsterdam-uuid-1")
                .addressName("Jumbo Amsterdam Centrum")
                .city("Amsterdam")
                .postalCode("1011 AB")
                .street("Damrak")
                .street2("1")
                .latitude(new BigDecimal("52.3676"))
                .longitude(new BigDecimal("4.9041"))
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("22:00")
                .build();
    }

    public static Store createRotterdamStore() {
        return Store.builder()
                .uuid("rotterdam-uuid-1")
                .addressName("Jumbo Rotterdam Centrum")
                .city("Rotterdam")
                .postalCode("3011 AA")
                .street("Coolsingel")
                .street2("42")
                .latitude(new BigDecimal("51.9225"))
                .longitude(new BigDecimal("4.4777"))
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("21:00")
                .build();
    }

    public static Store createUtrechtStore() {
        return Store.builder()
                .uuid("utrecht-uuid-1")
                .addressName("Jumbo Utrecht Centrum")
                .city("Utrecht")
                .postalCode("3511 AA")
                .street("Oudegracht")
                .street2("123")
                .latitude(new BigDecimal("52.0907"))
                .longitude(new BigDecimal("5.1214"))
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("21:00")
                .build();
    }

    public static Store createHaarlemStore() {
        return Store.builder()
                .uuid("haarlem-uuid-1")
                .addressName("Jumbo Haarlem Centrum")
                .city("Haarlem")
                .postalCode("2011 AA")
                .street("Grote Houtstraat")
                .street2("50")
                .latitude(new BigDecimal("52.3792"))
                .longitude(new BigDecimal("4.6368"))
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("21:00")
                .build();
    }

    public static Store createTheHagueStore() {
        return Store.builder()
                .uuid("thehague-uuid-1")
                .addressName("Jumbo Den Haag Centrum")
                .city("Den Haag")
                .postalCode("2511 AA")
                .street("Spui")
                .street2("25")
                .latitude(new BigDecimal("52.0705"))
                .longitude(new BigDecimal("4.3007"))
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("21:00")
                .build();
    }

    public static Store createStoreWithCoordinates(
            String uuid, String city, BigDecimal latitude, BigDecimal longitude) {
        return Store.builder()
                .uuid(uuid)
                .addressName("Jumbo " + city)
                .city(city)
                .postalCode("1000 AA")
                .street("Main Street")
                .street2("1")
                .latitude(latitude)
                .longitude(longitude)
                .showWarningMessage(false)
                .locationType("Supermarkt")
                .todayOpen("08:00")
                .todayClose("21:00")
                .build();
    }
}
