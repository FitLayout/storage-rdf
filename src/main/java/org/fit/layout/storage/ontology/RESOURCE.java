/**
 * RESOURCE.java
 *
 * Created on 9. 1. 2016, 10:33:35 by burgetr
 */
package org.fit.layout.storage.ontology;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.unbescape.uri.UriEscape;

/**
 * FitLayout resource URI generation.
 * 
 * @author burgetr
 */
public class RESOURCE
{
    public static final String NAMESPACE = "http://fitlayout.github.io/resource/";
    public static final String PREFIX = "flr";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();
    
    /**
     * Creates a page set URI from its name.
     * @param name the name of the page set
     * @return the created URI
     */
    public static URI createPageSetURI(String name)
    {
        String res = name.replace(' ', '_');
        res = UriEscape.escapeUriPathSegment(res);
        return factory.createURI(NAMESPACE, res);
    }
    
    public static URI createPageURI(long seq)
    {
        return factory.createURI(NAMESPACE, "page" + seq);
    }
    
    /**
     * Creates a sequence URI from its name.
     * @param name the name of the sequence (alphabetical characters only)
     * @return the created URI
     */
    public static URI createSequenceURI(String name)
    {
        return factory.createURI(NAMESPACE, name);
    }
    
    
    private RESOURCE()
    {
        //static access only
    }

    
}