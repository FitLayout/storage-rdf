/**
 * RESOURCE.java
 *
 * Created on 9. 1. 2016, 10:33:35 by burgetr
 */
package org.fit.layout.storage.ontology;

import org.fit.layout.model.Area;
import org.fit.layout.model.Box;
import org.fit.layout.model.Tag;
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
        return factory.createURI(NAMESPACE, "pset-" + res);
    }
    
    public static URI createPageURI(long seq)
    {
        return factory.createURI(NAMESPACE, "page" + seq);
    }
    
    public static URI createBoxURI(URI pageUri, Box box)
    {
        return factory.createURI(pageUri.toString() + '#' + box.getId());
    }
    
    public static URI createBorderURI(URI boxUri, String side)
    {
        String localName = boxUri.getLocalName() + "B" + side;
        return factory.createURI(boxUri.getNamespace(), localName);
    }
    
    public static URI createAttributeURI(URI boxUri, String name)
    {
        String localName = boxUri.getLocalName() + "-attr-" + name;
        return factory.createURI(boxUri.getNamespace(), localName);
    }
    
    public static URI createAreaTreeURI(long seq)
    {
        return factory.createURI(NAMESPACE, "atree" + seq);
    }
    
    public static URI createAreaURI(URI areaTreeNode, Area area) 
    {
        return factory.createURI(areaTreeNode.toString() + "#a" + area.getId());
    }
    
    public static URI createLogicalAreaURI(URI areaTreeNode, int cnt) 
    {
        return factory.createURI(areaTreeNode.toString() + "#l" + cnt);
    }
    
    public static URI createTagSupportURI(URI areaUri, Tag tag) 
    {
        return factory.createURI(areaUri.toString() + "-" + getTagDesc(tag));
    }
    
    public static URI createTagURI(Tag tag) 
    {
        return factory.createURI(RESOURCE.NAMESPACE, "tag-" + getTagDesc(tag));
    }
    
    private static String getTagDesc(Tag tag) 
    {
        return tag.getType().replaceAll("\\.", "-") + "--" + tag.getValue();
    }
    
    /**
     * Creates a sequence URI from its name.
     * @param name the name of the sequence (alphabetical characters only)
     * @return the created URI
     */
    public static URI createSequenceURI(String name)
    {
        return factory.createURI(NAMESPACE, "seq-" + name);
    }
    
    
    private RESOURCE()
    {
        //static access only
    }

    
}
