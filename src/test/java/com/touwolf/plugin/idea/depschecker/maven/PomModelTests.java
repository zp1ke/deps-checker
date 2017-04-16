package com.touwolf.plugin.idea.depschecker.maven;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.junit.Assert;
import org.junit.Test;

public class PomModelTests
{
    @Test
    public void parseXmlContentTest() throws JAXBException, XMLStreamException
    {
        PomModel pomModel = PomModel.parse(POM_XML);
        Assert.assertNotNull(pomModel);
        Assert.assertEquals("com.touwolf.pass2word", pomModel.getGroupId());
        Assert.assertEquals(2, pomModel.getDependencies().size());
        for (DependencyModel dependency : pomModel.getDependencies())
        {
            int lineIndex = "bridje-orm".equals(dependency.getArtifactId()) ? 88 : 93;
            Assert.assertTrue(dependency.getStartLine() == lineIndex);
            Assert.assertTrue(dependency.getEndLine() > dependency.getStartLine());
        }
    }

    private static final String POM_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
        "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
        "    <modelVersion>4.0.0</modelVersion>\n" +
        "\n" +
        "    <groupId>com.touwolf.pass2word</groupId>\n" +
        "    <artifactId>pass2word-parent</artifactId>\n" +
        "    <version>1.0.0-SNAPSHOT</version>\n" +
        "    <packaging>pom</packaging>\n" +
        "\n" +
        "    <properties>\n" +
        "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
        "        <maven.compiler.source>1.8</maven.compiler.source>\n" +
        "        <maven.compiler.target>1.8</maven.compiler.target>\n" +
        "        <bridje.version>0.3.0</bridje.version>\n" +
        "    </properties>\n" +
        "\n" +
        "    <modules>\n" +
        "        <module>pass2word-core</module>\n" +
        "        <module>pass2word-rest</module>\n" +
        "    </modules>\n" +
        "\n" +
        "    <build>\n" +
        "        <pluginManagement>\n" +
        "            <plugins>\n" +
        "                <plugin>\n" +
        "                    <groupId>org.bridje</groupId>\n" +
        "                    <artifactId>bridje-maven-plugin</artifactId>\n" +
        "                    <version>${bridje.version}</version>\n" +
        "                    <executions>\n" +
        "                        <execution>\n" +
        "                            <id>generate-entitys</id>\n" +
        "                            <goals>\n" +
        "                                <goal>generate-sources</goal>\n" +
        "                            </goals>\n" +
        "                            <phase>generate-sources</phase>\n" +
        "                        </execution>\n" +
        "                    </executions>\n" +
        "                </plugin>\n" +
        "                <plugin>\n" +
        "                    <groupId>org.apache.maven.plugins</groupId>\n" +
        "                    <artifactId>maven-compiler-plugin</artifactId>\n" +
        "                    <version>3.6.0</version>\n" +
        "                    <configuration>\n" +
        "                        <source>${maven.compiler.source}</source>\n" +
        "                        <target>${maven.compiler.target}</target>\n" +
        "                        <encoding>${project.build.sourceEncoding}</encoding>\n" +
        "                        <compilerArgs>\n" +
        "                            <arg>-Xlint:deprecation</arg>\n" +
        "                        </compilerArgs>\n" +
        "                    </configuration>\n" +
        "                </plugin>\n" +
        "                <plugin>\n" +
        "                    <groupId>org.apache.maven.plugins</groupId>\n" +
        "                    <artifactId>maven-jar-plugin</artifactId>\n" +
        "                    <version>3.0.2</version>\n" +
        "                    <configuration>\n" +
        "                        <archive>\n" +
        "                            <manifest>\n" +
        "                                <addClasspath>true</addClasspath>\n" +
        "                                <classpathLayoutType>custom</classpathLayoutType>\n" +
        "                                <customClasspathLayout>${artifact.artifactId}.${artifact.extension}</customClasspathLayout>\n" +
        "                            </manifest>\n" +
        "                        </archive>\n" +
        "                    </configuration>\n" +
        "                </plugin>\n" +
        "            </plugins>\n" +
        "        </pluginManagement>\n" +
        "    </build>\n" +
        "\n" +
        "    <dependencyManagement>\n" +
        "        <dependencies>\n" +
        "            <dependency>\n" +
        "                <groupId>commons-codec</groupId>\n" +
        "                <artifactId>commons-codec</artifactId>\n" +
        "                <version>1.10</version>\n" +
        "            </dependency>\n" +
        "            <dependency>\n" +
        "                <groupId>junit</groupId>\n" +
        "                <artifactId>junit</artifactId>\n" +
        "                <scope>test</scope>\n" +
        "                <version>4.12</version>\n" +
        "            </dependency>\n" +
        "        </dependencies>\n" +
        "    </dependencyManagement>\n" +
        "\n" +
        "    <dependencies>\n" +
        "        <dependency>\n" +
        "            <groupId>org.bridje</groupId>\n" +
        "            <artifactId>bridje-orm</artifactId>\n" +
        "            <version>${bridje.version}</version>\n" +
        "        </dependency>\n" +
        "        <dependency>\n" +
        "            <groupId>org.bridje</groupId>\n" +
        "            <artifactId>bridje-http</artifactId>\n" +
        "            <version>${bridje.version}</version>\n" +
        "        </dependency>\n" +
        "    </dependencies>\n" +
        "</project>\n";
}
