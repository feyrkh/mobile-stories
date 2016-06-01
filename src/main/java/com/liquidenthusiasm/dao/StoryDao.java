package com.liquidenthusiasm.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.liquidenthusiasm.domain.StoryInstance;

@RegisterMapper({ StoryInstance.Mapper.class })
public interface StoryDao {

    @SqlUpdate("merge into stories "
        + "( covenId,  personId, actionId, storyPosition, stateJson) values "
        + "(:covenId, :personId, :actionId, :storyPosition, :stateJson)") void saveRunningStory(@BindBean StoryInstance story);

    @SqlUpdate("delete from stories where actionId=:actionId AND covenId=:covenId AND personId=:personId") void deleteStory(
        @BindBean StoryInstance storyInstance);

    @SqlQuery("select * from stories where covenId=:covenId AND personId=:personId AND actionId=:actionId") StoryInstance findRunningStory(
        @Bind("covenId") long covenId, @Bind("personId") long personId, @Bind("actionId") long actionId);

}
