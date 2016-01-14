/**
 * RDFResource.java
 *
 * Created on 14. 1. 2016, 10:34:45 by burgetr
 */
package org.fit.layout.storage.model;

import org.openrdf.model.URI;

/**
 * A RDF resource with an URI.
 * @author burgetr
 */
public interface RDFResource
{
    
    /**
     * Obtains the URI of the resource in the RDF storage.
     * @return the resource URI
     */
    public URI getUri();

}
