package com.mohan.first.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mohan.first.domain.Author;
import com.mohan.first.repository.AuthorRepository;
import com.mohan.first.web.rest.dto.AuthorDTO;
import com.mohan.first.web.rest.mapper.AuthorMapper;
import com.mohan.first.web.rest.util.HeaderUtil;
import com.mohan.first.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Author.
 */
@RestController
@RequestMapping("/api")
public class AuthorResource {

    private final Logger log = LoggerFactory.getLogger(AuthorResource.class);

    @Inject
    private AuthorRepository authorRepository;

    @Inject
    private AuthorMapper authorMapper;

    /**
     * POST  /authors -> Create a new author.
     */
    @RequestMapping(value = "/authors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuthorDTO> create(@RequestBody AuthorDTO authorDTO) throws URISyntaxException {
        log.debug("REST request to save Author : {}", authorDTO);
        if (authorDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new author cannot already have an ID").body(null);
        }
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        Author result = authorRepository.save(author);
        return ResponseEntity.created(new URI("/api/authors/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("author", result.getId().toString()))
                .body(authorMapper.authorToAuthorDTO(result));
    }

    /**
     * PUT  /authors -> Updates an existing author.
     */
    @RequestMapping(value = "/authors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuthorDTO> update(@RequestBody AuthorDTO authorDTO) throws URISyntaxException {
        log.debug("REST request to update Author : {}", authorDTO);
        if (authorDTO.getId() == null) {
            return create(authorDTO);
        }
        Author author = authorMapper.authorDTOToAuthor(authorDTO);
        Author result = authorRepository.save(author);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("author", authorDTO.getId().toString()))
                .body(authorMapper.authorToAuthorDTO(result));
    }

    /**
     * GET  /authors -> get all the authors.
     */
    @RequestMapping(value = "/authors",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<AuthorDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Author> page = authorRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/authors", offset, limit);
        return new ResponseEntity<>(page.getContent().stream()
            .map(authorMapper::authorToAuthorDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /authors/:id -> get the "id" author.
     */
    @RequestMapping(value = "/authors/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AuthorDTO> get(@PathVariable String id) {
        log.debug("REST request to get Author : {}", id);
        return Optional.ofNullable(authorRepository.findOne(id))
            .map(authorMapper::authorToAuthorDTO)
            .map(authorDTO -> new ResponseEntity<>(
                authorDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /authors/:id -> delete the "id" author.
     */
    @RequestMapping(value = "/authors/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.debug("REST request to delete Author : {}", id);
        authorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("author", id.toString())).build();
    }
}
