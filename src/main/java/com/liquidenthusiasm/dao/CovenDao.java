package com.liquidenthusiasm.dao;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.StoryInstance;

@RegisterMapper({ Coven.Mapper.class, StoryInstance.Mapper.class })
public interface CovenDao {

    @SqlUpdate("insert into covens "
        + "( name,  password,  displayName,  admin,  activeStoryId) values "
        + "(:name, :password, :displayName, :admin, :activeStoryId)")
    @GetGeneratedKeys long insert(@BindBean Coven coven);

    @SqlUpdate("merge into stories "
        + "( covenId,  personId, actionId, storyPosition, stateJson) values "
        + "(:covenId, :personId, :actionId, :storyPosition, :stateJson)") void saveRunningStory(@BindBean StoryInstance story);

    @SqlUpdate("delete from stories where actionId=:actionId AND covenId=:covenId AND personId=:personId") void deleteStory(
        @BindBean StoryInstance storyInstance);

    @SqlQuery("select * from covens where id=:id") Coven findById(@Bind("id") long id);

    @SqlQuery("select * from covens") ImmutableList<Coven> findAll();

    @SqlQuery("select * from covens where name=:name") Coven findByName(@Bind("name") String name);

    @SqlUpdate("merge into covenIntProperties "
        + "( name,  covenId,  propVal) values "
        + "(:name, :covenId, :propVal)") void updateIntProperty(@Bind("covenId") long covenId,
        @Bind("name") String name, @Bind("propVal") int propVal);

    @SqlQuery("select propVal from covenIntProperties where covenId=:covenId AND name=:name") int getIntProperty(
        @Bind("covenId") long covenId,
        @Bind("name") String name);

    @SqlUpdate("delete from covenIntProperties where covenId=:covenId AND name=:name") void deleteIntProperty(@Bind("covenId") long covenId,
        @Bind("name") String name);

    @SqlQuery("select * from stories where covenId=:covenId AND personId=:personId AND actionId=:actionId") StoryInstance findRunningStory(
        @Bind("covenId") long covenId, @Bind("personId") long personId, @Bind("actionId") long actionId);

    @SqlQuery("select * from stories where covenId=:covenId AND personId=0 AND actionId=:actionId") StoryInstance findRunningStory(
        @Bind("covenId") long covenId, @Bind("actionId") long actionId);

}
