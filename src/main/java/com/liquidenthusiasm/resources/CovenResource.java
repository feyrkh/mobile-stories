package com.liquidenthusiasm.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.BCryptUtil;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.domain.Coven;
import io.dropwizard.auth.Auth;

@Path("/coven")
@Produces(MediaType.APPLICATION_JSON)
public class CovenResource {

    private static final Logger log = LoggerFactory.getLogger(CovenResource.class);

    private final CovenDao covenDao;

    public CovenResource(CovenDao covenDao) {
        this.covenDao = covenDao;
        Coven.covenDao = covenDao;
    }

    public CovenDao getCovenDao() {
        return covenDao;
    }

    @Path("{id}")
    @GET
    public Coven get(@Auth Coven currentCoven, @PathParam("id") long id) {
        log.info("Coven with requestingId={} is asking for coven with requestedId={}", currentCoven.getId(), id);
        Coven c = covenDao.findById(id);
        if (c != null) {
            if (c.getId() != currentCoven.getId()) {
                throw new ForbiddenException();
            }
            // If you're not an admin, you don't get sensitive info back
            if (!c.isAdmin()) {
                c.sanitize();
            }
        }
        return c;
    }

    @Path("intAttr/{attrName}")
    @GET
    public int getIntAttr(@Auth Coven currentCoven, @PathParam("attrName") String attrName) {
        return currentCoven.getIntProperty(attrName);
    }

    @GET
    @Timed
    public List<Coven> getAll() {
        log.info("Fetching list of all covens");
        ImmutableList<Coven> covens = covenDao.findAll();
        covens.parallelStream().forEach(Coven::sanitize);
        return covens;
    }

    @POST
    public Coven create(@Valid Coven coven) {
        if (covenDao.findByName(coven.getName()) != null) {
            log.info("Tried to create coven with name={} but it already exists.", coven.getName());
            throw new BadRequestException("User with that email address already exists.");
        }

        coven.setPassword(BCryptUtil.hashpw(coven.getPassword()));
        log.info("Creating new coven {}", coven);
        long id = covenDao.insert(coven);
        coven.setId(id);
        log.info("Created new coven with id={}, name={}", coven.getId(), coven.getName());
        return coven;
    }
}
