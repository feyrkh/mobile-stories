package com.liquidenthusiasm.dao;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AbstractStoreTest {

    @Before
    public void setup() {
        AbstractStore.clearStores();
    }

    @Test
    public void canRegisterAndRemoveStore() {
        AbstractStore s1 = mock(AbstractStore.class);
        AbstractStore s2 = mock(AbstractStore.class);
        AbstractStore.setStore(Object.class, s1);
        assertEquals(s1, AbstractStore.getStore(Object.class));
        AbstractStore.setStore(Object.class, s2);
        assertEquals(s2, AbstractStore.getStore(Object.class));
        verify(s1).shutdownStore();
        verify(s2, never()).shutdownStore();
    }

    @Test(expected = RuntimeException.class)
    public void missingStoreThrowsException() {
        assertNull(AbstractStore.getStore(Object.class));
    }
    
    @Test
    public void canDetectMissingStore() {
        AbstractStore s1 = mock(AbstractStore.class);
        assertFalse(AbstractStore.hasStore(Object.class));
        AbstractStore.setStore(Object.class, s1);
        
    }

}