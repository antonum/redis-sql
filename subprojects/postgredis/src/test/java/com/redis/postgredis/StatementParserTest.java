package com.redis.postgredis;

import org.junit.Assert;
import org.junit.Test;

import com.redis.postgredis.utils.StatementParser;

public class StatementParserTest {

  @Test
  public void testRemoveCommentsAndTrim() {

    String sqlStatement = "-- This is a one line comment\nSELECT * FROM FOO";
    String expectedResult = "SELECT * FROM FOO";
    String result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    sqlStatement = "/* This is a simple multi line comment */\nSELECT * FROM FOO";
    expectedResult = "SELECT * FROM FOO";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    sqlStatement = "/* This is a \nmulti line comment */\nSELECT * FROM FOO";
    expectedResult = "SELECT * FROM FOO";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    sqlStatement = "/* This\nis\na\nmulti\nline\ncomment */\nSELECT * FROM FOO";
    expectedResult = "SELECT * FROM FOO";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    sqlStatement =
        "/*\n"
            + " * Script for testing invalid/unrecognized statements\n"
            + " */\n"
            + "\n"
            + "-- MERGE into test comment MERGE -- \n"
            + "@EXPECT EXCEPTION INVALID_ARGUMENT 'INVALID_ARGUMENT: Unknown statement'\n"
            + "MERGE INTO Singers s\n"
            + "/*** test ****/"
            + "USING (VALUES (1, 'John', 'Doe')) v\n"
            + "ON v.column1 = s.SingerId\n"
            + "WHEN NOT MATCHED \n"
            + "  INSERT VALUES (v.column1, v.column2, v.column3)\n"
            + "WHEN MATCHED\n"
            + "  UPDATE SET FirstName = v.column2,\n"
            + "             LastName = v.column3;";
    expectedResult =
        "@EXPECT EXCEPTION INVALID_ARGUMENT 'INVALID_ARGUMENT: Unknown statement'\n"
            + "MERGE INTO Singers s\n"
            + "USING (VALUES (1, 'John', 'Doe')) v\n"
            + "ON v.column1 = s.SingerId\n"
            + "WHEN NOT MATCHED \n"
            + "  INSERT VALUES (v.column1, v.column2, v.column3)\n"
            + "WHEN MATCHED\n"
            + "  UPDATE SET FirstName = v.column2,\n"
            + "             LastName = v.column3;";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    // Dollar Quoted
    sqlStatement = "$$--foo$$";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "$$\nline 1\n--line2$$";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "$bar$--foo$bar$";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "SELECT FOO$BAR FROM SOME_TABLE";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "SELECT FOO$BAR -- This is a comment\nFROM SOME_TABLE";
    expectedResult = "SELECT FOO$BAR \nFROM SOME_TABLE";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    // Embedded Comments
    sqlStatement =
        "/* This is a comment /* This is an embedded comment */ This is after the embedded comment */ SELECT 1";
    expectedResult = "SELECT 1";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, expectedResult);

    // No effect HashTag Comment
    sqlStatement = "# this is a comment\nselect * from foo";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "select *\nfrom foo # this is a comment\nwhere bar=1";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    // When parameters are mixed with dollar-quoted string
    sqlStatement = "$1$$?it$?s$$$2";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "$1$tag$?it$$?s$tag$$2";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);

    sqlStatement = "$1$$?it\\'?s \n ?it\\'?s$$$2";
    result = StatementParser.removeCommentsAndTrim(sqlStatement);
    Assert.assertEquals(result, sqlStatement);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveCommentsAndTrimWithUnterminatedComment() {
    String sqlStatement =
        "/* This is a comment /* This is still a comment */ this is unterminated SELECT 1";
    StatementParser.removeCommentsAndTrim(sqlStatement);
  }

  @Test
  public void testEscapes() {
    String sql = "Bobby\\'O\\'Bob'; DROP TABLE USERS; select'";
    String expectedSql = "Bobby\\'O\\'Bob\\'; DROP TABLE USERS; select\\'";
    Assert.assertEquals(StatementParser.singleQuoteEscape(sql), expectedSql);
  }
}
