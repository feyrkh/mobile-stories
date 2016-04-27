package com.liquidenthusiasm.action.story;

import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

public interface StoryGenerator {

    StoryInstance getOrGenerateStoryInstance(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person);

    void initializeStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story);

    void advanceStory(StoryFunctionRepo repo, Coven coven, Person person, StoryInstance story, StoryChoice choice);

    StoryView getStoryView(StoryInstance instance, Coven coven, Person person);

    void generateStoryTextAndOptions(StoryView view, StoryInstance instance, Coven coven, Person person);

    boolean isValidChoice(StoryInstance instance, StoryChoice choice);
}
