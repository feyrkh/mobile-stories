package com.liquidenthusiasm.dao;

import java.util.Collection;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.domain.Person;

@RegisterMapper({ Person.Mapper.class })
public interface PersonDao {

    @SqlQuery("select * from people where id=:id") Person findById(@Bind("id") long id);

    @SqlQuery("select * from people") Collection<Person> list();

    @SqlUpdate("insert into people "
        + "( name,  covenId,  activeActionCategoryId,  activeStoryId,  timeRemaining) values "
        + "(:name, :covenId, :activeActionCategoryId, :activeStoryId, :timeRemaining)") @GetGeneratedKeys long insert(
        @BindBean Person person);

    @SqlUpdate("merge into people "
        + "( id,  name,  activeStoryId,  activeActionCategoryId,  timeRemaining) values "
        + "(:id, :name, :activeStoryId, :activeActionCategoryId, :timeRemaining)") void update(@BindBean Person person);

    @SqlQuery("select * from people where covenId=:covenId") ImmutableList<Person> listByCovenId(@Bind("covenId") long covenId);
}
