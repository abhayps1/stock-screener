package com.aps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "result")
public class Result {

    @Id
    private String gsin;
    private String type;

    @Column(name = "company_short_name")
    private String companyShortName;

    private String oldIsin;
    private String newIsin;
    private String nseSymbol;
    private String bseSymbol;
    private String searchId;
    private String logoUrl;
    private String marketCap;
    private String details;

    private String description;
    private String eventType;

    @Column(name = "primary_date")
    private LocalDate primaryDate;

    private String corporateEventFilter;
    private String instrumentType;

}
