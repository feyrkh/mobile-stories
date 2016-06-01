package com.liquidenthusiasm.action.vars;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.domain.FixtureTestUtil;

public class VarDescriptionTest {

    @Before
    public void setup() {
    }

    @Test
    public void canDeserialize() {
        VarDescription vd = new VarDescription();
        vd.name = "ci_members";
        vd.desc = "How many individuals reside in your covenant.";
        vd.label = "Covenant Members";
        vd.image = "assets/icon/ci_members.png";
        vd.hideStoryOption = false;
        vd.hideChanges = false;
        FixtureTestUtil.verifyAgainstFixture("fixtures/varDescription.json", VarDescription.class, vd);
    }

}