package com.aps.controller;

import com.aps.entity.Link;
import com.aps.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping("/saveLink")
    public String saveLink(@RequestBody Link link) {
        linkService.saveLink(link);
        return "Link saved successfully";
    }

    @GetMapping("/getLinks")
    public List<Link> getAllLinks() {
        return linkService.getAllLinks();
    }
}
