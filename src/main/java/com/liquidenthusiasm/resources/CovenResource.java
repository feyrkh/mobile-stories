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
import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import io.dropwizard.auth.Auth;

@Path("/coven")
@Produces(MediaType.APPLICATION_JSON)
public class CovenResource {

    private static final Logger log = LoggerFactory.getLogger(CovenResource.class);

    public CovenResource() {
    }

    @Path("{id}")
    @GET
    public Coven get(@Auth Coven currentCoven, @PathParam("id") long id) {
        log.info("Coven with requestingId={} is asking for coven with requestedId={}", currentCoven.getId(), id);
        Coven c = Daos.covenDao.findById(id);
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
    public Integer getIntAttr(@Auth Coven currentCoven, @PathParam("attrName") String attrName) {
        return currentCoven.getIntProperty(attrName);
    }

    @GET
    @Timed
    public List<Coven> getAll() {
        log.info("Fetching list of all covens");
        ImmutableList<Coven> covens = Daos.covenDao.findAll();
        covens.parallelStream().forEach(Coven::sanitize);
        return covens;
    }

    @POST
    public Coven create(@Valid Coven coven) {
        if (Daos.covenDao.findByName(coven.getName()) != null) {
            log.info("Tried to create coven with name={} but it already exists.", coven.getName());
            throw new BadRequestException("User with that email address already exists.");
        }

        coven.setPassword(BCryptUtil.hashpw(coven.getPassword()));
        log.info("Creating new coven {}", coven);
        long id = Daos.covenDao.insert(coven);
        coven.setId(id);
        log.info("Created new coven with id={}, name={}", coven.getId(), coven.getName());

        // Create the first member
        Person person = new Person();
        person.setCovenId(coven.getId());
        long personId = Daos.personDao.insert(person);
        person.setId(personId);

        // Create the prologue story
        StoryInstance prologueStory =
            Daos.actionRepo.getAction(0).getOrGenerateStoryInstance(coven, person);
        person.setActiveStoryId(prologueStory.getActionId());
        Daos.personDao.update(person);

        coven.setFocusedPersonId(personId);
        coven.addMember(person);
        Daos.covenDao.update(coven);

        return coven;
    }

    public ActionRepo getActionRepo() {
        return Daos.actionRepo;
    }
}
