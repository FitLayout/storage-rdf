package org.fit.layout.storage;
import java.awt.Color;
import java.util.AbstractMap;
import java.util.Map;

import org.fit.layout.model.Border;
import org.fit.layout.storage.ontology.BOX;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModelLoader.java
 *
 * Created on 17. 1. 2016, 17:19:32 by burgetr
 */

/**
 * Model loader base.
 * @author burgetr
 */
public abstract class ModelLoader
{
    private static Logger log = LoggerFactory.getLogger(ModelLoader.class);
    
    protected Border createBorder(Model model, URI uri)
    {
        Border ret = new Border();
        
        for (Statement st : model.filter(uri, null, null))
        {
            final URI pred = st.getPredicate();
            final Value value = st.getObject();
            
            if (BOX.borderColor.equals(pred)) 
            {
                ret.setColor(hex2Rgb(value.stringValue()));
            }
            else if (BOX.borderWidth.equals(pred))
            {
                if (value instanceof Literal)
                    ret.setWidth(((Literal) value).intValue());
            }
            else if (BOX.borderStyle.equals(pred))
            {
                String style = value.stringValue();
                try {
                    ret.setStyle(Border.Style.valueOf(style));
                } catch (IllegalArgumentException r) {
                    log.error("Invalid style value: {}", style);
                }
            }
        }
        
        return ret;
    }
    
    protected Map.Entry<String, String> createAttribute(Model model, URI uri)
    {
        String name = null;
        String avalue = null;
        for (Statement st : model.filter(uri, null, null))
        {
            final URI pred = st.getPredicate();
            final Value value = st.getObject();
            if (RDFS.LABEL.equals(pred))
            {
                if (value instanceof Literal)
                    name = ((Literal) value).stringValue();
            }
            else if (RDF.VALUE.equals(pred))
            {
                if (value instanceof Literal)
                    avalue = ((Literal) value).stringValue();
            }
        }
        if (name != null && avalue != null)
            return new AbstractMap.SimpleEntry<String, String>(name, avalue);
        else
            return null;
    }
    
    protected Color hex2Rgb(String colorStr) 
    {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    } 

}
