package com.aps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aps.dto.IpoDTO;
import com.aps.service.IpoService;

@RestController
@RequestMapping("/api/ipos")
public class IpoController {

	@Autowired
	private IpoService ipoService;

	@GetMapping
	public List<IpoDTO> getAllIpos() {
		return ipoService.getAllIpos();
	}
}