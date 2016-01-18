/**
 * RDFPage.java
 *
 * Created on 13. 1. 2016, 22:33:22 by burgetr
 */
package org.fit.layout.storage.model;

import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.fit.layout.impl.DefaultPage;
import org.fit.layout.model.Page;
import org.openrdf.model.URI;

/**
 * 
 * @author burgetr
 */
public class RDFPage extends DefaultPage implements RDFResource
{
    protected URI uri;
    protected Date createdOn;
    protected Map<URI, RDFBox> boxUris;
    

    public RDFPage(URL url)
    {
        super(url);
    }

    public RDFPage(URL url, URI uri, Date createdOn)
    {
        super(url);
        this.uri = uri;
        this.createdOn = createdOn;
    }
    
    public RDFPage(Page src, URI uri)
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

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    public Map<URI, RDFBox> getBoxUris()
    {
        return boxUris;
    }

    public void setBoxUris(Map<URI, RDFBox> boxUris)
    {
        this.boxUris = boxUris;
    }

    public RDFBox findBoxByUri(URI uri)
    {
        return boxUris.get(uri);
    }
}
