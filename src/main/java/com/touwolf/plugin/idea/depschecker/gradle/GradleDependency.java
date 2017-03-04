package com.touwolf.plugin.idea.depschecker.gradle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradleDependency
{
    private static final String TYPE_REGEX = "(?<type>compile|runtime|testCompile|testRuntime)";

    private static final String GROUP_REGEX = "group:\\s*'(?<group>.*)'";

    private static final String NAME_REGEX = "name:\\s*'(?<name>.*)'";

    private static final String VERSION_REGEX = "version:\\s*'(?<version>.*)'";

    private static final Pattern FORMAL_PATTERN = Pattern.compile(TYPE_REGEX + "\\s+" + GROUP_REGEX + "\\s*,\\s*" + NAME_REGEX + "\\s*,\\s*" + VERSION_REGEX);

    private final String type;

    private final String group;

    private final String name;

    private final String version;

    private GradleDependency(String type, String group, String name, String version)
    {
        this.type = type;
        this.group = group;
        this.name = name;
        this.version = version;
    }

    @Nullable
    public static GradleDependency of(@NotNull String line)
    {
        Matcher matcher = FORMAL_PATTERN.matcher(line);
        if (matcher.find())
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
}
