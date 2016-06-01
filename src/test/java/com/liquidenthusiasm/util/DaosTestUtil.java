package com.liquidenthusiasm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.dao.*;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DaosTestUtil {

    private static final Logger log = LoggerFactory.getLogger(DaosTestUtil.class);

    public static void setupMockDaos() {
        Daos.actionRepo = mock(ActionRepo.class, RETURNS_DEEP_STUBS);
        Daos.covenDao = mock(CovenDao.class, RETURNS_DEEP_STUBS);
        Daos.functionRepo = mock(StoryFunctionRepo.class, RETURNS_DEEP_STUBS);
        Daos.personDao = mock(PersonDao.class, RETURNS_DEEP_STUBS);
        Daos.propertyDao = mock(PropertyDao.class, RETURNS_DEEP_STUBS);
        when(Daos.propertyDao.getIntProperty(anyLong(), anyLong(), anyString())).thenReturn(null);
        when(Daos.propertyDao.getStrProperty(anyLong(), anyLong(), anyString())).thenReturn(null);
        Daos.storyDao = mock(StoryDao.class, RETURNS_DEEP_STUBS);
    }
}
