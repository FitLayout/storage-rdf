/**
 * RDFArea.java
 *
 * Created on 17. 1. 2016, 16:31:01 by burgetr
 */
package org.fit.layout.storage.model;

import java.util.Collections;
import java.util.Comparator;

import org.fit.layout.impl.DefaultArea;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFArea extends DefaultArea implements RDFResource
{
    protected URI uri;
    protected int documentOrder;

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

    public int getDocumentOrder()
    {
        return documentOrder;
    }

    public void setDocumentOrder(int documentOrder)
    {
        this.documentOrder = documentOrder;
    }
    
    /**
     * Sorts contained boxes in the document order
     */
    public void sortBoxes()
    {
        Collections.sort(getBoxes(), new Comparator<Box>(){
            @Override
            public int compare(Box box1, Box box2)
            {
                if (box1 instanceof RDFBox && box2 instanceof RDFBox)
                    return ((RDFBox) box1).getDocumentOrder() - ((RDFBox) box2).getDocumentOrder();
                else
                    return 0;
            }
        });
    }

}
