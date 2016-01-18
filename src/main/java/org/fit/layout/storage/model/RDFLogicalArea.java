/**
 * RDFLogicalArea.java
 *
 * Created on 18. 1. 2016, 13:16:22 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultLogicalArea;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFLogicalArea extends DefaultLogicalArea implements RDFResource
{
    protected URI uri;

    public RDFLogicalArea(URI uri)
    {
        super();
        setUri(uri);
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

}
