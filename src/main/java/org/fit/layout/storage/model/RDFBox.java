/**
 * RDFBox.java
 *
 * Created on 14. 1. 2016, 10:31:25 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultBox;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFBox extends DefaultBox implements RDFResource
{
    protected URI uri;

    public RDFBox(URI uri)
    {
        super();
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
    
}
