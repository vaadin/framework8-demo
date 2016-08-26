package com.vaadin.framework8.demo.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GOTService {

    private static GOTService instance = new GOTService();

    private static Map<Integer, GOTCharacter> characters = new HashMap<>();

    private GOTService() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static GOTService getInstance() {
        return instance;
    }

    public Stream<GOTCharacter> fetchCharacters(int offset, int num) {
        Stream.Builder<GOTCharacter> sb = Stream.builder();
        try {
            for (int i = offset; i < offset + num; i++) {
                if (!characters.containsKey(i)) {
                    GOTCharacter character = Unirest
                            .get("http://anapioficeandfire.com/api/characters/{id}")
                            .routeParam("id", String.valueOf(i))
                            .asObject(GOTCharacter.class).getBody();
                    characters.put(i, character);
                }
                sb.accept(characters.get(i));
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return sb.build();
    }
}
