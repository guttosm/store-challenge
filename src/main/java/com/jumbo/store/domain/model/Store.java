package com.jumbo.store.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "stores",
        indexes = {@Index(name = "idx_latitude_longitude", columnList = "latitude,longitude")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String uuid;

    @Column(nullable = false, length = 200)
    private String addressName;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(length = 50)
    private String street2;

    @Column(length = 50)
    private String street3;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(length = 20)
    private String complexNumber;

    @Column(nullable = false)
    private Boolean showWarningMessage;

    @Column(length = 10)
    private String todayOpen;

    @Column(length = 10)
    private String todayClose;

    @Column(length = 50)
    private String locationType;

    private Boolean collectionPoint;

    @Column(length = 20)
    private String sapStoreID;
}
