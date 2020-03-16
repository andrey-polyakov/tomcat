package com.codeminders.homework;

import com.codeminders.homework.parser.JavaNoRegexCommentsRemover;
import org.junit.Assert;
import org.junit.Test;

public class JavaNoRegexCommentsRemoverTests
{

    public static final String X_FIRE_X_FLOOD_X_PLAGUE = "String x=\"XXX\\\"//Amsterdam\";";
    /**
     * These are language-specific comment to cover more Unicode characters.
     */
    private static final String FRENCH_COMMENT = "annulé sans préjudice";
    private static final String RUSSIAN_COMMENT = "Комментарий";

    @Test
    public void carriageReturnInsideComment() {
        run("/*\r\n \n \r*/", "\r\n\n\r", "CRs & NL expected");
    }

    @Test
    public void blockCommentPrimitive() {
        run("/*\n\n*/", "\n\n", "Multi line comment was not taken out");
    }

    @Test
    public void empty() {
        run("", "", "Empty must yield empty");
    }

    @Test
    public void noComments() {
        run("long x = 220l", "long x = 220l", "No change expected");
    }

    @Test
    public void singleComment() {
        run("//Bedenke, dass du sterben musst.", "", "Must produce an empty string");
    }

    @Test
    public void codeAndCommentsMixedUp() {
        run("int x = 0;//\"int x = 0;", "int x = 0;", "The line has to be twice shorter");
    }

    @Test
    public void mixedUpLines() {
        run("//this is a comment\nint x = 0;", "\nint x = 0;", "Has to be a one liner");
    }

    @Test
    public void finickyMultiLineComment() {
        run("/* //" + FRENCH_COMMENT + " */i++;", "i++;", "Expected i++;");
    }

    @Test
    public void multiLineComment() {
        run("/* God keep our land glorious and free! */i++;", "i++;", "Expected i++;");
    }

    @Test
    public void slash() {
        run("//line/*this is not block comment\nFoo ter = bar;", "\nFoo ter = bar;", "Only variable assignment should remain");
    }

    @Test
    public void doubleForwardSlashString() {
        run("String x=\"//\";", "String x=\"//\";", "No change expected");
    }

    @Test
    public void emptyAgain() {
        run("//Vouloir, c'est pouvoir\r", "", "Empty string expected");
    }

    @Test
    public void carriageReturnNewLine() {
        run("//God save our gracious Queen!\r\nint x = 0;", "\r\nint x = 0;", "Has to be a one liner");
    }

    @Test
    public void blockCommentOpeningInString() {
        run("String x=\"/*\";", "String x=\"/*\";", "BlockCommentOpeningInString_InNotAComment");
    }

    @Test
    public void baseCaseLineComment() {
        run("String x=\"A Mari Usque Ad Mare\";//!", "String x=\"A Mari Usque Ad Mare\";", "Expected a line 2 characters shorter");
    }

    @Test
    public void doubleQuoteInLineComment() {
        run("//Commentaire\"\nThread.yield();//to be removed", "\nThread.yield();", "Three lines expected to turn into one liner");
    }

    @Test
    public void doubleQuoteInBlockComment() {
        run("/*" + RUSSIAN_COMMENT +"\"*/\ndouble pi=3.14;//remove this", "\ndouble pi=3.14;", "");
    }

    @Test
    public void doubleQuotes() {
        run(X_FIRE_X_FLOOD_X_PLAGUE, X_FIRE_X_FLOOD_X_PLAGUE, "No change expected");
    }


    /**
     * Invoke unit under test
     * @param testInput
     * @param expected
     * @param message
     */
    public void run(String testInput, String expected, String message)
    {
        String actual = JavaNoRegexCommentsRemover.RemoveComments(testInput);
        Assert.assertEquals(message, expected, actual);
    }
}