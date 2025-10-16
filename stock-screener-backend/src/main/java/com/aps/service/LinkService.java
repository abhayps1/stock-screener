package com.aps.service;

import com.aps.entity.Link;
import com.aps.repository.LinkRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public Link saveLink(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String description = doc.title();
        Link link = new Link();
        link.setUrl(url);
        link.setDescription(description);
        return linkRepository.save(link);
    }

    public void deleteLink(Long id) {
        linkRepository.deleteById(id);
    }

    public List<Link> getAllLinks() {
        return linkRepository.findAll();
    }
}
