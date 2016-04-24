package com.liquidenthusiasm.story;

import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.domain.*;

public interface StoryGenerator {

    StoryInstance getOrGenerateStoryInstance(Coven coven);

    StoryInstance getOrGenerateStoryInstance(Coven coven, Person person);

    void initializeStory(StoryInstance story);

    void advanceStory(Coven coven, StoryInstance story, StoryChoice choice);

    StoryView getStoryView(StoryInstance instance);

    void generateStoryTextAndOptions(StoryView view, StoryInstance instance);

    boolean isValidChoice(StoryInstance instance, StoryChoice choice);
}
