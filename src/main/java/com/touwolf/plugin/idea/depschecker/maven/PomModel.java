package com.touwolf.plugin.idea.depschecker.maven;

import java.io.StringReader;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class PomModel extends DependenciesHolder
{
    private String groupId;

    private String artifactId;

    private String version;

    private DependenciesHolder dependencyManagement;

    @XmlElementWrapper(name = "properties")
    @XmlAnyElement(lax = true)
    private List<Element> nodeProperties;

    @XmlTransient
    private Map<String, String> properties;

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
        if (pomModel.nodeProperties != null)
        {
            pomModel.nodeProperties
                .parallelStream()
                .filter(element -> element.getChildNodes().getLength() > 0)
                .forEach(element ->
                {
                    String value = element.getChildNodes().item(0).getNodeValue();
                    pomModel.getProperties().put(element.getTagName(), value);
                });
        }
        return pomModel;
    }

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

    public DependenciesHolder getDependencyManagement()
    {
        return dependencyManagement;
    }

    public Map<String, String> getProperties()
    {
        if (properties == null)
        {
            properties = new HashMap<>();
        }
        return properties;
    }
}
