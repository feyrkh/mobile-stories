package com.liquidenthusiasm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.action.vars.VarRepo;

public class Daos {

    private static final Logger log = LoggerFactory.getLogger(Daos.class);

    public static StoryDao storyDao;

    public static CovenDao covenDao;

    public static PersonDao personDao;

    public static PropertyDao propertyDao;

    public static ActionRepo actionRepo;

    public static StoryFunctionRepo functionRepo;

    public static VarRepo varRepo;
}
