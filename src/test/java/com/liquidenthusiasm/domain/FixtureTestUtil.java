package com.liquidenthusiasm.domain;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

import static org.assertj.core.api.Assertions.assertThat;

import static io.dropwizard.testing.FixtureHelpers.fixture;

public class FixtureTestUtil {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public static <T> void verifyAgainstFixture(String fixtureFilename, Class<T> fixtureClass, T compareWith) {
        try {
            final String expected = MAPPER.writeValueAsString(loadFixture(fixtureFilename, fixtureClass));
            assertThat(MAPPER.writeValueAsString(compareWith)).isEqualTo(expected).describedAs("deserialized/reserialized fixtured");

            T deserializedFixture = MAPPER.readValue(fixture(fixtureFilename), fixtureClass);
            assertThat(deserializedFixture).describedAs("deserialized fixture")
                .isEqualToIgnoringGivenFields(compareWith);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadFixture(String fixtureFilename, Class<T> fixtureClass) {
        try {
            return MAPPER.readValue(fixture(fixtureFilename), fixtureClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
