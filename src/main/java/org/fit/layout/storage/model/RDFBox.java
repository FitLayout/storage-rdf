/**
 * RDFBox.java
 *
 * Created on 14. 1. 2016, 10:31:25 by burgetr
 */
package org.fit.layout.storage.model;

import org.fit.layout.impl.DefaultBox;
import org.fit.layout.model.Box;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFBox extends DefaultBox implements RDFResource
{
    protected URI uri;
    protected int documentOrder;

    public RDFBox(URI uri)
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
        setId(Integer.parseInt(uri.getLocalName()));
    }

    public int getDocumentOrder()
    {
        return documentOrder;
    }

    public void setDocumentOrder(int documentOrder)
    {
        this.documentOrder = documentOrder;
    }

    @Override
    public String toString()
    {
        String ret = getId() + " ";
        if (getType() == Box.Type.TEXT_CONTENT)
        {
            ret += getText();
        }
        else if (getType() == Box.Type.REPLACED_CONTENT)
        {
            if (getContentObject() != null)
                ret += getContentObject().toString();
            else
                ret += "(null object)";
        }
        else
        {
            ret += "<" + getTagName();
            if (getAttribute("id") != null)
                ret += " id=" + getAttribute("id");
            if (getAttribute("class") != null)
                ret += " class=" + getAttribute("class");
            ret += ">";
        }
        return ret;
    }
    
}
