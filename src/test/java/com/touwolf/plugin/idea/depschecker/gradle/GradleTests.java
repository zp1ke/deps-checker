package com.touwolf.plugin.idea.depschecker.gradle;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class GradleTests
{
    @Test
    public void dependenciesTest()
    {
        String group = "dep.group";
        String name = "dep.name";
        String version = "dep.version";
        String content = "dependencies {\n" +
            "   compile group: '" + group + "', name: '" + name + "', version: '" + version + "'\n" +
            "}";
        GradleBuild build = new GradleBuild(Arrays.asList(content.split("\n")));
        Assert.assertEquals(1, build.getDependencies().size());
        GradleDependency dependency = build.getDependencies().get(0);
        Assert.assertEquals("compile", dependency.getType());
        Assert.assertEquals(group, dependency.getGroup());
        Assert.assertEquals(name, dependency.getName());
        Assert.assertEquals(version, dependency.getVersion());
    }
}
