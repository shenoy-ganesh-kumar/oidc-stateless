package com.okta.developer.jugtours.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.okta.developer.jugtours.model.Group;

@RestController
@RequestMapping("/api")
class GroupController {

    private final Logger log = LoggerFactory.getLogger(GroupController.class);

    @GetMapping("/groups")
    public List<Object> groups(Principal principal) {
    	final Group group = new Group();
    	group.setId(System.currentTimeMillis());
    	group.setName("TestGroup");
        return Arrays.asList(group);
    }

    @GetMapping("/group/{id}")
    ResponseEntity<?> getGroup(@PathVariable Long id) {
    	final Group group = new Group();
    	group.setId(System.currentTimeMillis());
    	group.setName("TestGroup");
        return ResponseEntity.ok().body(group);
    }

    @PostMapping("/group")
    ResponseEntity<Group> createGroup(@Valid @RequestBody Group group,
                                      @AuthenticationPrincipal OAuth2User principal) throws URISyntaxException {
        log.info("Request to create group: {}", group);
        group.setId(System.currentTimeMillis());
        return ResponseEntity.created(new URI("/api/group/" + group.getId()))
                .body(group);
    }
}
