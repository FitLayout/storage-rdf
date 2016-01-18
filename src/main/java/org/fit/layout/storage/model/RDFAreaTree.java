/**
 * RDFAreaTree.java
 *
 * Created on 16. 1. 2016, 20:47:28 by burgetr
 */
package org.fit.layout.storage.model;

import java.util.Map;

import org.fit.layout.impl.DefaultAreaTree;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Page;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFAreaTree extends DefaultAreaTree implements RDFResource
{
    protected URI uri;
    protected Map<URI, RDFArea> areaUris;
    protected Map<URI, RDFLogicalArea> logicalAreaUris;


    public RDFAreaTree(Page page, URI uri)
    {
        super(page);
        this.uri = uri;
    }
    
    public RDFAreaTree(AreaTree src, URI uri)
    {
        super(src);
        this.uri = uri;
    }

    @Override
    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    public Map<URI, RDFArea> getAreaUris()
    {
        return areaUris;
    }

    public void setAreaUris(Map<URI, RDFArea> areaUris)
    {
        this.areaUris = areaUris;
    }
    
    public RDFArea findAreaByUri(URI uri)
    {
        if (areaUris != null)
            return areaUris.get(uri);
        else
            return null;
    }

    public Map<URI, RDFLogicalArea> getLogicalAreaUris()
    {
        return logicalAreaUris;
    }

    public void setLogicalAreaUris(Map<URI, RDFLogicalArea> logicalAreaUris)
    {
        this.logicalAreaUris = logicalAreaUris;
    }
    
    public RDFLogicalArea findLogicalAreaByUri(URI uri)
    {
        if (logicalAreaUris != null)
            return logicalAreaUris.get(uri);
        else
            return null;
    }
    
}
