/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redislabs.redisearch.jdbc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Internal utility methods for Elasticsearch tests.
 */
public class RediSearchChecker {

  private static final ObjectMapper MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES) // user-friendly settings to
      .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES); // avoid too much quoting

  private RediSearchChecker() {
  }


  /**
   * Returns a function that checks that a particular Elasticsearch pipeline is
   * generated to implement a query.
   *
   * @param expected expected expression
   * @return validation function
   */
  public static Consumer<List> rediSearchChecker(final String... expected) {
    Objects.requireNonNull(expected, "string");
    return a -> {
      String actual = a == null || a.isEmpty() ? null : ((String) a.get(0));
      if (!expected[0].equals(actual)) {
        assertEquals(expected[0], actual, "expected and actual RediSearch queries do not match");
      }
    };
  }

  /**
   * Expands attributes with dots ({@code .}) into sub-nodes.
   * Use for more friendly JSON format:
   * <pre>
   *   {'a.b.c': 1}
   *   expanded to
   *   {a: { b: {c: 1}}}}
   * </pre>
   *
   * @param parent current node
   * @param <T>    type of node (usually JsonNode).
   * @return copy of existing node with field {@code a.b.c} expanded.
   */
  @SuppressWarnings("unchecked")
  private static <T extends JsonNode> T expandDots(T parent) {
    Objects.requireNonNull(parent, "parent");

    if (parent.isValueNode()) {
      return parent.deepCopy();
    }

    // ArrayNode
    if (parent.isArray()) {
      ArrayNode arr = (ArrayNode) parent;
      ArrayNode copy = arr.arrayNode();
      arr.elements().forEachRemaining(e -> copy.add(expandDots(e)));
      return (T) copy;
    }

    // ObjectNode
    ObjectNode objectNode = (ObjectNode) parent;
    final ObjectNode copy = objectNode.objectNode();
    objectNode.fields().forEachRemaining(e -> {
      final String property = e.getKey();
      final JsonNode node = e.getValue();

      final String[] names = property.split("\\.");
      ObjectNode copy2 = copy;
      for (int i = 0; i < names.length - 1; i++) {
        copy2 = copy2.with(names[i]);
      }
      copy2.set(names[names.length - 1], expandDots(node));
    });

    return (T) copy;
  }

}
