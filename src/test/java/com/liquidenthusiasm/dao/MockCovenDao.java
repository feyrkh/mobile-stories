package com.liquidenthusiasm.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.BCryptUtil;
import com.liquidenthusiasm.domain.Coven;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockCovenDao {

    private static AtomicLong nextId = new AtomicLong();

    public static CovenDao buildMock() {
        CovenDao dao = mock(CovenDao.class);

        when(dao.insert(any(Coven.class))).thenAnswer(
            invoc -> {
                long curId = nextId.getAndIncrement();
                addCovenToDao(dao, (Coven) invoc.getArguments()[0]);
                return curId;
            }
        );


        return dao;
    }

    private static void addCovenToDao(CovenDao dao, Coven coven) {
        List<Coven> allList = dao.findAll();
        if (allList == null) {
            allList = new ArrayList<>();
        } else {
            allList = new ArrayList<>(allList);
        }
        allList.add(coven);

        when(dao.findAll()).thenReturn(ImmutableList.copyOf(allList));

    }

    public static Coven addMockCovenToDao(CovenDao dao, long id, String name) {
        Coven c = mock(Coven.class);
        when(c.getId()).thenReturn(id);
        when(c.getName()).thenReturn(name);
        when(c.getDisplayName()).thenReturn("Display: " + name);
        when(c.getPassword()).thenReturn(BCryptUtil.hashpw(name + "_password"));
        addCovenToDao(dao, c);

        nextId.set(Math.max(nextId.get(), id + 1));

        when(dao.findById(id)).thenReturn(c);
        when(dao.findByName(name)).thenReturn(c);
        return c;
    }
}
