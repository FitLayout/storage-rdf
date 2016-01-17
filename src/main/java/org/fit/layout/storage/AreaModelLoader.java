/**
 * AreaModelLoader.java
 *
 * Created on 17. 1. 2016, 13:38:37 by burgetr
 */
package org.fit.layout.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fit.layout.impl.DefaultTag;
import org.fit.layout.model.Border;
import org.fit.layout.model.Page;
import org.fit.layout.model.Rectangular;
import org.fit.layout.model.Border.Side;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.model.RDFArea;
import org.fit.layout.storage.model.RDFAreaTree;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements creating a RDFAreaTree from the RDF models.
 * @author burgetr
 */
public class AreaModelLoader extends ModelLoader
{
    private static Logger log = LoggerFactory.getLogger(AreaModelLoader.class);

    private RDFStorage storage;
    private URI areaTreeUri;
    private Page page;
    
    private Model borderModel;
    private Model tagInfoModel;
    private Model tagSupportModel;
    
    private RDFAreaTree areaTree;
    
    public AreaModelLoader(RDFStorage storage, URI areaTreeUri, Page srcPage)
    {
        this.storage = storage;
        this.areaTreeUri = areaTreeUri;
        this.page = srcPage;
    }
    
    public RDFAreaTree getAreaTree() throws RepositoryException
    {
        if (areaTree == null)
            areaTree = constructAreaTree();
        return areaTree;
    }

    private RDFAreaTree constructAreaTree() throws RepositoryException
    {
        Model model = storage.getAreaModelForAreaTree(areaTreeUri);
        if (model.size() > 0)
        {
            RDFAreaTree atree = new RDFAreaTree(page, areaTreeUri);
            RDFArea root = constructVisualAreaTree(model);
            atree.setRoot(root);
            return atree;
        }
        else
            return null;
    }
    
    private RDFArea constructVisualAreaTree(Model model) throws RepositoryException
    {
        Map<URI, RDFArea> areas = new HashMap<URI, RDFArea>();
        //find all areas
        for (Resource res : model.subjects())
        {
            if (res instanceof URI)
            {
                RDFArea area = createAreaFromModel(model, (URI) res);
                areas.put((URI) res, area);
            }
        }
        List<RDFArea> rootAreas = new ArrayList<RDFArea>(areas.values());
        //construct the tree
        for (Statement st : model.filter(null, SEGM.isChildOf, null))
        {
            if (st.getSubject() instanceof URI && st.getObject() instanceof URI)
            {
                RDFArea parent = areas.get(st.getObject());
                RDFArea child = areas.get(st.getSubject());
                if (parent != null && child != null)
                {
                    parent.add(child);
                    rootAreas.remove(child);
                }
            }
        }
        if (rootAreas.size() == 1)
            return rootAreas.get(0);
        else
        {
            log.error("Strange number of root areas: {}", rootAreas.toString());
            return null; //strange number of root nodes
        }
    }
    
    private RDFArea createAreaFromModel(Model model, URI uri) throws RepositoryException
    {
        RDFArea area = new RDFArea(new Rectangular(), uri);
        int x = 0, y = 0, width = 0, height = 0;
        Map<URI, Float> tagSupport = new HashMap<URI, Float>(); //tagUri->support
        
        for (Statement st : model.filter(uri, null, null))
        {
            final URI pred = st.getPredicate();
            final Value value = st.getObject();
            
            if (BOX.backgroundColor.equals(pred)) 
            {
                String bgColor = value.stringValue();
                area.setBackgroundColor( hex2Rgb( bgColor ) );
            }
            else if (BOX.underline.equals(pred)) 
            {
                if (value instanceof Literal)
                    area.setUnderline(((Literal) value).floatValue());
            }
            else if (BOX.lineThrough.equals(pred)) 
            {
                if (value instanceof Literal)
                    area.setLineThrough(((Literal) value).floatValue());
            }
            else if (BOX.fontSize.equals(pred)) 
            {
                if (value instanceof Literal)
                    area.setFontSize(((Literal) value).floatValue());
            }
            else if (BOX.fontStyle.equals(pred)) 
            {
                if (value instanceof Literal)
                    area.setFontStyle(((Literal) value).floatValue());
            }
            else if (BOX.fontWeight.equals(pred)) 
            {
                if (value instanceof Literal)
                    area.setFontWeight(((Literal) value).floatValue());
            }
            else if (BOX.hasBottomBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    area.setBorderStyle(Side.BOTTOM, border);
                }
            }
            else if (BOX.hasLeftBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    area.setBorderStyle(Side.LEFT, border);
                }
            }
            else if (BOX.hasRightBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    area.setBorderStyle(Side.RIGHT, border);
                }
            }
            else if (BOX.hasTopBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    area.setBorderStyle(Side.TOP, border);
                }
            }
            else if (BOX.height.equals(pred)) 
            {
                if (value instanceof Literal)
                    height = ((Literal) value).intValue();
            }
            else if (BOX.width.equals(pred)) 
            {
                if (value instanceof Literal)
                    width = ((Literal) value).intValue();
            }
            else if (BOX.positionX.equals(pred)) 
            {
                if (value instanceof Literal)
                    x = ((Literal) value).intValue();
            }   
            else if (BOX.positionY.equals(pred)) 
            {
                if (value instanceof Literal)
                    y = ((Literal) value).intValue();
            }
            else if (SEGM.hasTag.equals(pred))
            {
                if (value instanceof URI)
                {
                    if (!tagSupport.containsKey(value))
                    {
                        Tag tag = createTag((URI) value);
                        if (tag != null)
                            area.addTag(tag, 1.0f); //spport is unkwnown (yet)
                    }
                }
            }
            else if (SEGM.tagSupport.equals(pred))
            {
                if (value instanceof URI)
                {
                    URI tsUri = (URI) value;
                    URI tagUri = null;
                    Float support = null;
                    for (Statement sst : getTagSupportModel().filter(tsUri, null, null))
                    {
                        if (SEGM.hasTag.equals(sst.getPredicate()) && sst.getObject() instanceof URI)
                            tagUri = (URI) sst.getObject();
                        else if (SEGM.support.equals(sst.getPredicate()) && sst.getObject() instanceof Literal)
                            support = ((Literal) sst.getObject()).floatValue();
                    }
                    if (tagUri != null && support != null)
                    {
                        Tag tag = createTag((URI) value);
                        if (tag != null)
                            area.addTag(tag, support);
                    }
                }
            }
        }
        area.setBounds(new Rectangular(x, y, x + width - 1, y + height - 1));
        
        return area;
    }
    
    private Tag createTag(URI tagUri) throws RepositoryException
    {
        String name = null;
        String type = null;
        for (Statement st : getTagInfoModel().filter(tagUri, null, null))
        {
            URI pred = st.getPredicate();
            if (SEGM.hasName.equals(pred))
                name = st.getObject().stringValue();
            else if (SEGM.hasType.equals(pred))
                type = st.getObject().stringValue();
        }
        if (name != null && type != null)
            return new DefaultTag(type, name);
        else
            return null;
    }
    
    private Model getBorderModel() throws RepositoryException
    {
        if (borderModel == null)
            borderModel = storage.getBorderModelForAreaTree(areaTreeUri);
        return borderModel;
    }
    
    private Model getTagInfoModel() throws RepositoryException
    {
        if (tagInfoModel == null)
            tagInfoModel = storage.getTagModelForAreaTree(areaTreeUri);
        return tagInfoModel;
    }

    private Model getTagSupportModel() throws RepositoryException
    {
        if (tagSupportModel == null)
            tagSupportModel = storage.getTagModelForAreaTree(areaTreeUri);
        return tagSupportModel;
    }

}
