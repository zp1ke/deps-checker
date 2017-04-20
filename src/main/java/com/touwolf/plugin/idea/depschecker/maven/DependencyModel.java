package com.touwolf.plugin.idea.depschecker.maven;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dependency")
@XmlAccessorType(XmlAccessType.FIELD)
public class DependencyModel
{
    private int startLine;

    private int endLine;

    private String groupId;

    private String artifactId;

    private String version;

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public int getStartLine()
    {
        return startLine;
    }

    public void setStartLine(int startLine)
    {
        this.startLine = startLine;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public void setEndLine(int endLine)
    {
        this.endLine = endLine;
    }
}
