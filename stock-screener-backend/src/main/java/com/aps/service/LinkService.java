package com.aps.service;

import com.aps.entity.Link;
import com.aps.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public Link saveLink(Link link) {
        return linkRepository.save(link);
    }

    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }
}
