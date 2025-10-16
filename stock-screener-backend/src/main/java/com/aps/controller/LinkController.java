package com.aps.controller;

import com.aps.entity.Link;
import com.aps.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @PostMapping("/saveLink")
    public String saveLink(@RequestParam String url) {
        try {
            linkService.saveLink(url);
            return "Link saved successfully";
        } catch (IOException e) {
            return "Error saving link: " + e.getMessage();
        }
    }

    @DeleteMapping("/deleteLink/{id}")
    public String deleteLink(@PathVariable Long id) {
        linkService.deleteLink(id);
        return "Link deleted successfully";
    }

    @GetMapping("/getLinks")
    public List<Link> getAllLinks() {
        return linkService.getAllLinks();
    }
}
