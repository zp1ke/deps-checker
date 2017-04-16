package com.touwolf.plugin.idea.depschecker.maven;

import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

public class LocationListener extends Unmarshaller.Listener
{
    private XMLStreamReader xsr;

    public LocationListener(XMLStreamReader xsr)
    {
        this.xsr = xsr;
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent)
    {
        if (target instanceof DependencyModel)
        {
            ((DependencyModel) target).setStartLine(xsr.getLocation().getLineNumber() - 1);
        }
    }
}
