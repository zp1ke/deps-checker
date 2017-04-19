package com.touwolf.plugin.idea.depschecker.maven;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class PomModel extends DependenciesHolder
{
    private String groupId;

    private String artifactId;

    private String version;

    private DependenciesHolder dependencyManagement;

    public static PomModel parse(String xmlContent) throws JAXBException, XMLStreamException
    {
        JAXBContext ctx = JAXBContext.newInstance(PomModel.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xmlContent));

        Unmarshaller um = ctx.createUnmarshaller();
        um.setListener(new LocationListener(xsr));

        PomModel pomModel = PomModel.class.cast(um.unmarshal(xsr));
        List<String> lines = Arrays.asList(xmlContent.split("\\n"));
        pomModel.updateDependenciesLines(lines);
        if (pomModel.dependencyManagement != null)
        {
            pomModel.dependencyManagement.updateDependenciesLines(lines);
        }
        return pomModel;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public DependenciesHolder getDependencyManagement()
    {
        return dependencyManagement;
    }

    public void setDependencyManagement(DependenciesHolder dependencyManagement)
    {
        this.dependencyManagement = dependencyManagement;
    }
}
