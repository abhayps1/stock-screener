package com.aps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aps.entity.Announcement;
import com.aps.service.AnnouncementService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

	@Autowired
	private AnnouncementService announcementService;

	@GetMapping("/latest")
	public List<Announcement> getLatestAnnouncements() {
		return announcementService.fetchLatestAnnouncements();
	}
}
