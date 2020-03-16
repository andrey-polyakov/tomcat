package com.codeminders.homework.parser;

public class JavaNoRegexCommentsRemover
{
    public static String RemoveComments(String code)
    {
        StringBuilder output = new StringBuilder();
        boolean lineComment = false;
        boolean blockComment = false;
        boolean stringSwitch = false;
        for (int i = 0; i < code.length(); i++)
        {
            if (lineComment && LineCommentEnds(code, i))
            {
                lineComment = false;
            }
            else if (blockComment && BlockCommentEnds(code, i))
            {
                blockComment = false;
            }
            else if (stringSwitch && StringEnds(code, i))
            {
                stringSwitch = false;
            }
            else if (!blockComment && !stringSwitch && LineCommentStarts(code, i))
            {
                lineComment = true;
            }
            else if (!lineComment && !stringSwitch && BlockCommentStarts(code, i))
            {
                blockComment = true;
            }
            else if (!lineComment && !blockComment && StringStarts(code, i))
            {
                stringSwitch = true;
            }

            if (!lineComment && !blockComment)
            {
                output.append(code.charAt(i));
            }
            else if (blockComment && (code.charAt(i) == '\n' || code.charAt(i) == '\r'))
            {
                output.append(code.charAt(i));
            }
        }

        return output.toString();
    }

    private static boolean StringEnds(String code, int i)
    {
        return code.charAt(i - 1) != '\\' && code.charAt(i) == '"';
    }

    private static boolean StringStarts(String code, int i)
    {
        return code.charAt(i) == '"';
    }

    private static boolean BlockCommentEnds(String code, int i)
    {
        return i - 2 >= 0 && code.charAt(i - 2) == '*' && code.charAt(i - 1) == '/';
    }

    private static boolean LineCommentEnds(String code, int i)
    {
        return code.charAt(i) == '\n' || code.charAt(i) == '\r' && code.length() > i + 1 && code.charAt(i + 1) == '\n';
    }

    private static boolean BlockCommentStarts(String code, int i)
    {
        return code.charAt(i) == '/' && code.length() > i + 1 && code.charAt(i + 1) == '*';
    }

    private static boolean LineCommentStarts(String code, int i)
    {
        return code.charAt(i) == '/' && code.length() > i + 1 && code.charAt(i + 1) == '/';
    }
}