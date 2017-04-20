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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class PomModel extends DependenciesHolder
{
    private String groupId;

    private String artifactId;

    private String version;

    private DependenciesHolder dependencyManagement;

    private List<String> content;

    @XmlElementWrapper(name = "properties")
    @XmlAnyElement(lax = true)
    private List<Element> nodeProperties;

    @XmlTransient
    private Map<String, String> properties;

    @NotNull
    public static PomModel parse(@NotNull String xmlContent) throws JAXBException, XMLStreamException
    {
        JAXBContext ctx = JAXBContext.newInstance(PomModel.class);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xmlContent));

        Unmarshaller um = ctx.createUnmarshaller();
        um.setListener(new LocationListener(xsr));

        PomModel pomModel = PomModel.class.cast(um.unmarshal(xsr));
        List<String> lines = Arrays.asList(xmlContent.split("\\n"));
        pomModel.content = new LinkedList<>(lines);
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

    @NotNull
    public Map<String, String> getProperties()
    {
        if (properties == null)
        {
            properties = new HashMap<>();
        }
        return properties;
    }

    @NotNull
    public List<String> getContent()
    {
        if (content == null)
        {
            content = new LinkedList<>();
        }
        return content;
    }

    public boolean upgradeDependency(@NotNull String groupId,
                                     @NotNull String artifactId,
                                     @NotNull String newVersion)
    {
        boolean upgraded = false;
        for (DependencyModel dependency : getDependencies())
        {
            boolean depUpgrade = upgradeDependency(dependency, groupId, artifactId, newVersion);
            upgraded = upgraded || depUpgrade;
        }
        if (dependencyManagement != null)
        {
            for (DependencyModel dependency : dependencyManagement.getDependencies())
            {
                boolean depUpgrade = upgradeDependency(dependency, groupId, artifactId, newVersion);
                upgraded = upgraded || depUpgrade;
            }
        }
        return upgraded;
    }

    private boolean upgradeDependency(@NotNull DependencyModel dependency,
                                      @NotNull String groupId,
                                      @NotNull String artifactId,
                                      @NotNull String newVersion)
    {
        if (groupId.equals(dependency.getGroupId()) &&
            artifactId.equals(dependency.getArtifactId()) &&
            dependency.getVersion() != null)
        {
            String version = dependency.getVersion();
            boolean isPropertyVersion = version.startsWith("${");
            if (isPropertyVersion)
            {
                version = version.substring(2, version.length() - 1);
            }
            for (int i = dependency.getStartLine(); i <= dependency.getEndLine(); i++)
            {
                String line = content.get(i);
                if (!isPropertyVersion &&
                    line.contains(version))
                {
                    replaceLine(i, version, newVersion);
                    dependency.setVersion(newVersion);
                    return true;
                }
                else if (line.contains("${") && isPropertyVersion &&
                         getProperties().containsKey(version))
                {
                    int startIndex = line.indexOf("${");
                    int endIndex = line.indexOf("}", startIndex);
                    if (startIndex >= 0 && endIndex > startIndex)
                    {
                        String propertyValue = getProperties().get(version);
                        Integer lineIndex = findPropertyLine(version);
                        if (propertyValue != null && lineIndex != null)
                        {
                            replaceLine(lineIndex, propertyValue, newVersion);
                            getProperties().put(version, newVersion);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void replaceLine(int index, String oldValue, String newValue)
    {
        String contentLine = content.remove(index);
        contentLine = contentLine.replace(oldValue, newValue);
        content.add(index, contentLine);
    }

    @Nullable
    private Integer findPropertyLine(String propertyName)
    {
        for (int i = 0; i < getContent().size(); i++)
        {
            String line = content.get(i);
            if (line.contains("<" + propertyName + ">"))
            {
                return i;
            }
        }
        return null;
    }
}
