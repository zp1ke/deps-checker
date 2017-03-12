package com.touwolf.plugin.idea.depschecker.gradle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class GradleBuild
{
    private final List<String> content;

    private String group;

    private String version;

    private final Map<Integer, GradleDependency> dependencies;

    private GradleBuild(@NotNull List<String> content)
    {
        this.content = new LinkedList<>(content);
        dependencies = new HashMap<>();
        parseCoordinates();
        parseDependencies();
    }

    private void parseCoordinates()
    {
        content.forEach(statement ->
        {
            if (statement.contains("group") && group == null)
            {
                group = parseFormalKeyValue("group", statement);
            }
            if (statement.contains("version") && version == null)
            {
                version = parseFormalKeyValue("version", statement);
            }
        });
    }

    private void parseDependencies()
    {
        int startLine = -1;
        int startBlockLine = -1;
        int opened = 0;
        for (int line = 0; line < content.size(); line++)
        {
            String statement = content.get(line);
            if (startLine < 0 && statement.contains("dependencies"))
            {
                startLine = line;
            }
            if (startLine >= 0 && statement.contains("{"))
            {
                if (startBlockLine < 0)
                {
                    startBlockLine = line;
                }
                else
                {
                    opened++;
                }
            }
            if (startBlockLine >= 0 && statement.contains("}"))
            {
                if (opened == 0)
                {
                    parseDependencies(startBlockLine, line);
                    startLine = -1;
                    startBlockLine = -1;
                }
                else
                {
                    opened--;
                }
            }
        }
    }

    private String parseFormalKeyValue(String key, String line)
    {
        String regex = "\\s*" + key + ":*\\s*'(?<value>.*)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        if (!found)
        {
            regex = regex.replaceAll("'", "\"");
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(line);
            found = matcher.find();
        }
        if (found)
        {
            return matcher.group("value");
        }
        return null;
    }

    private void parseDependencies(int fromLine, int toLine)
    {
        for (int line = fromLine; line <= toLine; line++)
        {
            GradleDependency dependency = GradleDependency.of(content.get(line));
            if (dependency != null)
            {
                dependencies.put(line, dependency);
            }
        }
    }

    @NotNull
    public List<String> getContent()
    {
        return Collections.unmodifiableList(content);
    }

    @NotNull
    public List<GradleDependency> getDependencies()
    {
        return new LinkedList<>(dependencies.values());
    }

    public boolean upgradeDependency(@NotNull String group, @NotNull String name, @NotNull String newVersion)
    {
        Set<Integer> lines = dependencies.keySet();
        for (Integer line : lines)
        {
            GradleDependency dependency = dependencies.get(line);
            if (group.equals(dependency.getGroup()) && name.equals(dependency.getName()))
            {
                String contentLine = content.remove(line.intValue());
                contentLine = contentLine.replace(dependency.getVersion(), newVersion);
                content.add(line, contentLine);
                dependency.setVersion(newVersion);
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static GradleBuild read(@NotNull InputStream stream) throws IOException
    {
        BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
        List<String> content = new LinkedList<>();
        String line = buf.readLine();
        while(line != null)
        {
            content.add(line);
            line = buf.readLine();
        }
        return new GradleBuild(content);
    }

    @NotNull
    public static GradleBuild of(@NotNull List<String> content)
    {
        return new GradleBuild(content);
    }

    @NotNull
    public String getGroup()
    {
        if (group != null)
        {
            return group;
        }
        return "";
    }

    @NotNull
    public String getVersion()
    {
        if (version != null)
        {
            return version;
        }
        return "";
    }
}
