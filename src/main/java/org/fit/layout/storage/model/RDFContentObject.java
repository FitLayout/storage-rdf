/**
 * RDFContentObject.java
 *
 * Created on 15. 11. 2016, 13:49:22 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.model.ContentObject;
import org.openrdf.model.URI;

/**
 * Generic ContentObject implementation.
 *  
 * @author burgetr
 */
public class RDFContentObject implements RDFResource, ContentObject
{
    private URI uri;
    
    public RDFContentObject(URI uri)
    {
        this.uri = uri;
    }

    @Override
    public URI getUri()
    {
        return uri;
    }

    @Override
    public String toString()
    {
        return "(unknown object)";
    }
    
}
