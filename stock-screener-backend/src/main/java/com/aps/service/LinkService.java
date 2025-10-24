package com.aps.service;

import com.aps.entity.Link;
import com.aps.repository.LinkRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class LinkService {

    private static final Logger logger = LoggerFactory.getLogger(LinkService.class);

    @Autowired
    private LinkRepository linkRepository;

    public Link saveLink(String url) throws IOException {
        try {
            if (linkRepository.existsByUrl(url)) {
                throw new RuntimeException("Link already exists");
            }
            logger.info("Fetching document from URL: {}", url);
            Document doc = Jsoup.connect(url).get();
            String description = doc.title();
            logger.info("Successfully fetched title: {} for URL: {}", description, url);
            Link link = new Link();
            link.setUrl(url);
            link.setDescription(description);
            return linkRepository.save(link);
        } catch (IOException e) {
            logger.error("IOException occurred while fetching URL: {}. Error: {}", url, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing URL: {}. Error: {}", url, e.getMessage(), e);
            throw new RuntimeException("Failed to save link for URL: " + url, e);
        }
    }

    public void deleteLink(Long id) {
        try {
            logger.info("Deleting link with ID: {}", id);
            linkRepository.deleteById(id);
            logger.info("Successfully deleted link with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while deleting link with ID: {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete link with ID: " + id, e);
        }
    }

    public void updateLink(Long id, String description) {
        try {
            logger.info("Updating link with ID: {} to description: {}", id, description);
            Link link = linkRepository.findById(id).orElseThrow(() -> new RuntimeException("Link not found"));
            link.setDescription(description);
            linkRepository.save(link);
            logger.info("Successfully updated link with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while updating link with ID: {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update link with ID: " + id, e);
        }
    }

    public List<Link> getAllLinks() {
        try {
            logger.info("Fetching all links");
            List<Link> links = linkRepository.findAll();
            logger.info("Successfully fetched {} links", links.size());
            return links;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all links. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch all links", e);
        }
    }
}
