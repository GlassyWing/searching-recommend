package org.manlier.common.parsers;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class SynonymsRecordParser {

    private ObjectMapper mapper = new ObjectMapper();
    private Queue<String> synonyms;
    private Queue<String> antonym;
    private boolean recordValid = false;

    @SuppressWarnings("unchecked")
    public void parse(String value) throws IOException {


        try {
            Map<String, Object> map = mapper.
                    readValue(value, new TypeReference<Map<String, Object>>() {
                    });
            List<String> synonymsList = mapper.convertValue(map.get("synonyms"), LinkedList.class);
            antonym = mapper.convertValue(map.get("antonym"), Queue.class);
            ((LinkedList<String>) synonymsList).addFirst((String) map.get("word"));
            synonyms = (LinkedList<String>) synonymsList;
            recordValid = true;
        } catch (IOException | ClassCastException e) {
            recordValid = false;
            throw new RuntimeException(e);
        }
    }

    public boolean isRecordValid() {
        return recordValid;
    }

    public Queue<String> getAntonym() {
        return antonym;
    }

    public Queue<String> getSynonyms() {
        return synonyms;
    }
}
