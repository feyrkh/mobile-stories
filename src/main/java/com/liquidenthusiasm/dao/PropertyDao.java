package com.liquidenthusiasm.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.StoryInstance;

@RegisterMapper({ Coven.Mapper.class, StoryInstance.Mapper.class })
public interface PropertyDao {

    @SqlUpdate("merge into intProperties "
        + "( name,  personId, covenId,  propVal) values "
        + "(:name, :personId, :covenId, :propVal)") void updateIntProperty(@Bind("covenId") long covenId, @Bind("personId") long personId,
        @Bind("name") String name, @Bind("propVal") int propVal);

    @SqlQuery("select propVal from intProperties where personId=:personId AND covenId=:covenId AND name=:name") Integer getIntProperty(
        @Bind("covenId") long covenId, @Bind("personId") long personId,
        @Bind("name") String name);

    @SqlUpdate("delete from intProperties where personId=:personId AND covenId=:covenId AND name=:name") void deleteIntProperty(
        @Bind("covenId") long covenId, @Bind("personId") long personId,
        @Bind("name") String name);

    @SqlUpdate("merge into strProperties "
        + "( name,  personId, covenId,  propVal) values "
        + "(:name, :personId, :covenId, :propVal)") void updateStrProperty(@Bind("covenId") long covenId, @Bind("personId") long personId,
        @Bind("name") String name, @Bind("propVal") String propVal);

    @SqlQuery("select propVal from strProperties where personId=:personId AND covenId=:covenId AND name=:name") String getStrProperty(
        @Bind("covenId") long id, @Bind("personId") long personId, @Bind("name") String name);

    @SqlUpdate("delete from strProperties where personId=:personId AND covenId=:covenId AND name=:name") void deleteStrProperty(
        @Bind("covenId") long covenId, @Bind("personId") long personId,
        @Bind("name") String name);

}
