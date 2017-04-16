package com.touwolf.plugin.idea.depschecker.maven;

import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class PomModel
{
    private String groupId;

    private String artifactId;

    private String version;

    @XmlElementWrapper(name = "dependencies")
    @XmlElements(@XmlElement(name = "dependency", type = DependencyModel.class))
    private List<DependencyModel> dependencies;

    public static PomModel parse(String xmlContent) throws JAXBException, XMLStreamException
    {
        JAXBContext ctx = JAXBContext.newInstance(PomModel.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xmlContent));

        Unmarshaller um = ctx.createUnmarshaller();
        um.setListener(new LocationListener(xsr));

        PomModel pomModel = PomModel.class.cast(um.unmarshal(xsr));
        List<String> lines = Arrays.asList(xmlContent.split("\\n"));
        pomModel.getDependencies().forEach(dependency ->
        {
            for (int i = dependency.getStartLine(); i <= lines.size(); i++)
            {
                String line = lines.get(i);
                if (line.contains("</dependency>"))
                {
                    dependency.setEndLine(i);
                    break;
                }
            }
        });
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

    public List<DependencyModel> getDependencies()
    {
        if (dependencies == null)
        {
            dependencies = new LinkedList<>();
        }
        return dependencies;
    }

    public void setDependencies(List<DependencyModel> dependencies)
    {
        this.dependencies = dependencies;
    }
}
