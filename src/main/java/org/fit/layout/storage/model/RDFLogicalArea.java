/**
 * RDFLogicalArea.java
 *
 * Created on 18. 1. 2016, 13:16:22 by burgetr
 */
package org.fit.layout.storage.model;

import org.eclipse.rdf4j.model.IRI;
import org.fit.layout.impl.DefaultLogicalArea;

/**
 * 
 * @author burgetr
 */
public class RDFLogicalArea extends DefaultLogicalArea implements RDFResource
{
    protected IRI iri;

    public RDFLogicalArea(IRI iri)
    {
        super();
        setIri(iri);
    }

    @Override
    public IRI getIri()
    {
        return iri;
    }

    public void setIri(IRI uri)
    {
        this.iri = uri;
    }

}
