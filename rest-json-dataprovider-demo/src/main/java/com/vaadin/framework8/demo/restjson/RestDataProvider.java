/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.framework8.demo.restjson;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class RestDataProvider
        extends AbstractBackEndDataProvider<JsonObject, Void> {

    private String restApiUrl;

    public RestDataProvider(String restApiUrl) {
        this.restApiUrl = restApiUrl;
    }

    @Override
    public Stream<JsonObject> fetchFromBackEnd(Query<JsonObject, Void> query) {
        URL url;
        try {
            url = new URL(restApiUrl);
            String jsonData = IOUtils.toString(url, StandardCharsets.UTF_8);
            JsonObject json = Json.parse(jsonData);

            JsonArray results = json.getArray("results");
            return stream(results);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error fetching JSON", e);
            // Must return something which matches size, or grid will keep
            // asking and asking...
            return IntStream.range(0, 200).mapToObj(i -> Json.createObject());
        }
    }

    @Override
    public int sizeInBackEnd(Query<JsonObject, Void> query) {
        return 200;
    }

    /**
     * Creates a stream from a JSON array.
     *
     * @param array
     *            the JSON array to create a stream from
     * @return a stream of JSON values
     */
    private static <T extends JsonValue> Stream<T> stream(JsonArray array) {
        assert array != null;
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                return array.get(index);
            }

            @Override
            public int size() {
                return array.length();
            }
        }.stream();
    }
}
