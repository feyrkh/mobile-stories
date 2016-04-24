//package com.liquidenthusiasm.action.coven;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.liquidenthusiasm.dao.CovenDao;
//import com.liquidenthusiasm.domain.Coven;
//import com.liquidenthusiasm.action.story.StoryChoice;
//import com.liquidenthusiasm.domain.StoryInstance;
//import com.liquidenthusiasm.domain.StoryView;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.mock;
//
//public class RecruitNewPersonActionTest {
//
//    RecruitNewPersonAction action;
//
//    StoryInstance instance;
//
//    Coven coven;
//
//    private StoryView view;
//
//    @Before
//    public void setup() {
//        coven = mock(Coven.class);
//        Coven.covenDao = mock(CovenDao.class);
//        action = new RecruitNewPersonAction();
//        instance = new StoryInstance();
//        action.initializeStory(instance);
//        view = new StoryView();
//    }
//
//    @Test
//    public void canRecruit() {
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        action.advanceStory(coven, instance, new StoryChoice(RecruitNewPersonAction.STATE_HIRED, null)
//            .formValue("name", "Kevin")
//            .formValue("focus", "experimentation"));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_HIRED, instance.getStoryPosition());
//        assertTrue("story text should've had 'Kevin': " + view.getStoryText(), view.getStoryText().contains("Kevin"));
//        assertTrue("story text should've had 'experimentation': " + view.getStoryText(), view.getStoryText().contains("experimentation"));
//        assertEquals("no more choices", 0, view.getChoices().size());
//        assertNull(instance.getError());
//    }
//
//    @Test
//    public void canCancel() {
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        action.advanceStory(coven, instance, new StoryChoice(RecruitNewPersonAction.STATE_CANCEL, null));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_CANCEL, instance.getStoryPosition());
//        assertEquals("no more choices", 0, view.getChoices().size());
//        assertNull(instance.getError());
//    }
//
//    @Test
//    public void invalidIdStartsOver() {
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        action.advanceStory(coven, instance, new StoryChoice(5555, null));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        assertEquals("got some choices", 2, view.getChoices().size());
//        assertNull(instance.getError());
//    }
//
//    @Test
//    public void invalidNameStartsOverAndCanRecoverFromErrors() {
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        action.advanceStory(coven, instance, new StoryChoice(RecruitNewPersonAction.STATE_HIRED, null)
//            .formValue("name", "A")
//            .formValue("focus", "experimentation"));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        assertNotNull(instance.getError());
//        action.advanceStory(coven, instance, new StoryChoice(RecruitNewPersonAction.STATE_HIRED, null)
//            .formValue("name", "Kevin")
//            .formValue("focus", "experimentation"));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_HIRED, instance.getStoryPosition());
//        assertNull(instance.getError());
//    }
//
//    @Test
//    public void invalidFocusStartsOver() {
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//        action.advanceStory(coven, instance, new StoryChoice(RecruitNewPersonAction.STATE_HIRED, null)
//            .formValue("name", "Kevin"));
//        action.generateStoryTextAndOptions(view, instance);
//        assertEquals(RecruitNewPersonAction.STATE_START, instance.getStoryPosition());
//
//    }
//}