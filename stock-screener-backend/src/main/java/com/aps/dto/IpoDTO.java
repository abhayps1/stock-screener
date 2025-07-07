package com.aps.dto;

import java.time.LocalDate;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class IpoDTO {

	@Id
	private String companyShortName;
	private String issueSize;
	private LocalDate ipoOpenDate;
	private LocalDate ipoClosedDate;
	private String ipoCategory;
	private Boolean isOpen;
}