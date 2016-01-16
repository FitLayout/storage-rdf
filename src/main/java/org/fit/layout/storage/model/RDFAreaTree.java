/**
 * RDFAreaTree.java
 *
 * Created on 16. 1. 2016, 20:47:28 by burgetr
 */
package org.fit.layout.storage.model;

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
}
