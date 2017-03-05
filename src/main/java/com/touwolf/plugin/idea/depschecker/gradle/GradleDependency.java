package com.touwolf.plugin.idea.depschecker.gradle;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradleDependency
{
    private static final String TYPE_REGEX = "\\s*(?<type>compile|runtime|testCompile|testRuntime|classpath)";

    private static final String FORMAL_REGEX = "\\s+group:\\s*'(?<group>.*)'\\s*,\\s*name:\\s*'(?<name>.*)'\\s*,\\s*version:\\s*'(?<version>.*)'";

    private static final String COMPACT_REGEX = "\\s+'(?<group>.*):(?<name>.*):(?<version>.*)'";

    private static final List<String> REGEX_LIST = Arrays.asList(
        TYPE_REGEX + FORMAL_REGEX,
        TYPE_REGEX + FORMAL_REGEX.replaceAll("'", "\""),
        TYPE_REGEX + COMPACT_REGEX,
        TYPE_REGEX + COMPACT_REGEX.replaceAll("'", "\"")
    );

    private final String type;

    private final String group;

    private final String name;

    private String version;

    private GradleDependency(@NotNull String type, @NotNull String group,
                             @NotNull String name, @NotNull String version)
    {
        this.type = type;
        this.group = group;
        this.name = name;
        this.version = version;
    }

    @Nullable
    public static GradleDependency of(@NotNull String line)
    {
        Matcher matcher = null;
        boolean found = false;
        for (String regex : REGEX_LIST)
        {
            Pattern pattern = Pattern.compile(regex);
            matcher = pattern.matcher(line);
            found = matcher.find();
            if (found)
            {
                break;
            }
        }
        if (found)
        {
            String type = matcher.group("type");
            String group = matcher.group("group");
            String name = matcher.group("name");
            String version = matcher.group("version");
            if (type != null && group != null && name != null && version != null)
            {
                return new GradleDependency(type, group, name, version);
            }
        }
        return null;
    }

    public String getType()
    {
        return type;
    }

    public String getGroup()
    {
        return group;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
