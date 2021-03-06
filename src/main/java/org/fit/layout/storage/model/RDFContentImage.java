/**
 * RDFContentImage.java
 *
 * Created on 15. 11. 2016, 13:52:18 by burgetr
 */
package org.fit.layout.storage.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.rdf4j.model.IRI;
import org.fit.layout.model.ContentImage;

/**
 * ContentImage implementation for the RDF model.
 * 
 * @author burgetr
 */
public class RDFContentImage extends RDFContentObject implements ContentImage
{
    private URL url;

    public RDFContentImage(IRI iri)
    {
        super(iri);
    }

    @Override
    public URL getUrl()
    {
        return url;
    }

    public void setUrl(URL url)
    {
        this.url = url;
    }
    
    public void setUrl(String url) throws MalformedURLException
    {
        this.url = new URL(url);
    }
    
    @Override
    public String toString()
    {
        if (url != null)
            return "img: " + url.toString();
        else
            return "img: no url";
    }
    
}
