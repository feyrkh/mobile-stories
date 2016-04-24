package com.liquidenthusiasm.action.story;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertNotNull;

public class StoryOptionTest {

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
    }

    @Test
    public void canDeserializeSimpleOption() throws IOException {
        String json = " {\n"
            + "          \"heading\": \"On second thought, perhaps not...\",\n"
            + "          \"transitions\": [\n"
            + "            {\"target\": 2}\n"
            + "          ]\n"
            + "        }";
        StoryOption deser = mapper.readValue(json, StoryOption.class);
        assertNotNull(deser);
    }

    @Test
    public void canDeserializeComplexOption() throws IOException {
        String json = "  {\n"
            + "          \"heading\": \"Send an acceptance letter\",\n"
            + "          \"fields\": [\n"
            + "            {\n"
            + "              \"name\": \"ss_name\",\n"
            + "              \"label\": \"Name\",\n"
            + "              \"type\": \"text\",\n"
            + "              \"defaultValue\": \"#ss_randomPersonName\",\n"
            + "              \"minLength\": 2,\n"
            + "              \"maxLength\": 30\n"
            + "            },\n"
            + "            {\n"
            + "              \"name\": \"ss_focus\",\n"
            + "              \"label\": \"Focus\",\n"
            + "              \"type\": \"select\",\n"
            + "              \"defaultValue\": \"exper\",\n"
            + "              \"options\": [\n"
            + "                \"Experimentation->exper\",\n"
            + "                \"Administration->admin\"\n"
            + "              ]\n"
            + "            }\n"
            + "          ],\n"
            + "          \"preSubmitCalls\": [\n"
            + "            {\n"
            + "              \"function\": \"randomPersonName\"\n"
            + "            }\n"
            + "          ],\n"
            + "          \"postSubmitCalls\": [\n"
            + "            {\n"
            + "              \"function\": \"registerStudent\",\n"
            + "              \"inputs\": [\n"
            + "                \"ss_name->studentName\",\n"
            + "                \"ss_focus->studentFocus\"\n"
            + "              ]\n"
            + "            }\n"
            + "          ],\n"
            + "          \"transitions\": [\n"
            + "            {\n"
            + "              \"target\": 0,\n"
            + "              \"triggers\": [\"si_registerStudent<1\"]\n"
            + "            },\n"
            + "            {\n"
            + "              \"target\": 1\n"
            + "            }\n"
            + "          ]\n"
            + "        }";
        StoryOption deser = mapper.readValue(json, StoryOption.class);
        assertNotNull(deser);
    }

}