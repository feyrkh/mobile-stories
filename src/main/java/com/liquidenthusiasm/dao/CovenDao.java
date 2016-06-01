package com.liquidenthusiasm.dao;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.StoryInstance;

@RegisterMapper({ Coven.Mapper.class, StoryInstance.Mapper.class })
public interface CovenDao {

    @SqlUpdate("insert into covens "
        + "( name,  password,  displayName,  admin,  activeStoryId,   focusedPersonId) values "
        + "(:name, :password, :displayName, :admin, :activeStoryId,  :focusedPersonId)")
    @GetGeneratedKeys long insert(@BindBean Coven coven);

    @SqlUpdate("merge into covens "
        + "( id,  name,  password,  displayName,  admin,  activeStoryId,   focusedPersonId) values "
        + "(:id, :name, :password, :displayName, :admin, :activeStoryId,  :focusedPersonId)") void update(
        @BindBean Coven coven);

    @SqlQuery("select * from covens where id=:id") Coven findById(@Bind("id") long id);

    @SqlQuery("select * from covens") ImmutableList<Coven> findAll();

    @SqlQuery("select * from covens where name=:name") Coven findByName(@Bind("name") String name);

    @SqlUpdate("merge into sessions "
        + "( covenId, loginCookie) values "
        + "(:covenId, :loginCookie)") void login(@Bind("covenId") long id, @Bind("loginCookie") String loginCookie);

    @SqlQuery("select * from covens, sessions where covens.id=covenId AND sessions.loginCookie=:loginCookie") Coven findByLoginCookie(
        @Bind("loginCookie") String loginCookie);

}
