package com.redis.postgredis.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.redis.postgredis.metadata.CommandMetadataParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class CommandMetadataParserTest {

  private CommandMetadataParser parser;

  @Before
  public void setUp() {
    parser = new CommandMetadataParser();
  }

  @Test
  public void testReturnsEmptyCommands() throws IOException, ParseException {
    final JSONObject expected = new JSONObject(ImmutableMap.of("commands", new JSONArray()));

    final JSONObject actual = parser.emptyCommands();

    assertEquals(expected, actual);
  }

  @Test
  public void testReturnsNonNullDefaultCommands() throws IOException, ParseException {
    final JSONObject actual = parser.defaultCommands();

    assertNotNull(actual);
  }

  @Test
  public void testReturnsCommandsFromMetadataFile() throws IOException, ParseException {
    final File tempFile = File.createTempFile("pgadapter-", ".json");
    tempFile.deleteOnExit();

    final JSONArray commands = new JSONArray();
    commands.add(
        ImmutableMap.of(
            "input_pattern", "SELECT 1",
            "output_pattern", "/*GSQL*/ SELECT 1",
            "matcher_array", new JSONArray(),
            "blurb", "test command"));
    final JSONObject expected = new JSONObject(ImmutableMap.of("commands", commands));
    try (final BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(tempFile), Charsets.UTF_8.name()))) {
      writer.write(expected.toJSONString());
    }

    final JSONObject actual = parser.parse(tempFile.getAbsolutePath());

    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThrowsIllegalArgumentExceptionWhenJsonIsNotAnObject()
      throws IOException, ParseException {
    final File tempFile = File.createTempFile("pgadapter-", ".json");
    tempFile.deleteOnExit();

    try (final BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(tempFile), Charsets.UTF_8.name()))) {
      writer.write("[]");
    }

    parser.parse(tempFile.getAbsolutePath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testThrowsIllegalArgumentExceptionWhenJsonIsNull()
      throws IOException, ParseException {
    final File tempFile = File.createTempFile("pgadapter-", ".json");
    tempFile.deleteOnExit();

    try (final BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(tempFile), Charsets.UTF_8.name()))) {
      writer.write("null");
    }

    parser.parse(tempFile.getAbsolutePath());
  }
}
