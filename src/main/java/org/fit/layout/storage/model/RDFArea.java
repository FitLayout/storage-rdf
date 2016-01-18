/**
 * RDFArea.java
 *
 * Created on 17. 1. 2016, 16:31:01 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultArea;
import org.fit.layout.model.Rectangular;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFArea extends DefaultArea implements RDFResource
{
    protected URI uri;

    public RDFArea(Rectangular r, URI uri)
    {
        super(r);
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
        setId(Integer.parseInt(uri.getLocalName().substring(1))); //skip 'a' prefix
    }


}
