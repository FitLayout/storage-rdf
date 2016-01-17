/**
 * BoxModelLoader.java
 *
 * Created on 13. 1. 2016, 23:49:14 by burgetr
 */
package org.fit.layout.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fit.layout.model.Border;
import org.fit.layout.model.Box;
import org.fit.layout.model.Border.Side;
import org.fit.layout.model.Box.Type;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.model.RDFBox;
import org.fit.layout.storage.model.RDFPage;
import org.fit.layout.storage.ontology.BOX;
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
 * This class implements creating a RDFPage from the RDF models.
 * @author burgetr
 */
public class BoxModelLoader extends ModelLoader
{
    private static Logger log = LoggerFactory.getLogger(BoxModelLoader.class);
    
    private RDFStorage storage;
    private URI pageUri;
    private Model borderModel;
    private RDFPage page;
    

    public BoxModelLoader(RDFStorage storage, URI pageUri)
    {
        this.storage = storage;
        this.pageUri = pageUri;
    }

    public RDFPage getPage() throws RepositoryException
    {
        if (page == null)
            page = constructPage();
        return page;
    }
    
    private RDFPage constructPage() throws RepositoryException
    {
        Model pageModel = storage.getPageInfo(pageUri);
        if (pageModel.size() > 0)
        {
            //create the page
            PageInfo info = new PageInfo(pageModel);
            URL srcURL;
            try {
                srcURL = new URL(info.getUrl());
            } catch (MalformedURLException e) {
                try {
                    srcURL = new URL("http://no/url");
                } catch (MalformedURLException e1) {
                    srcURL = null;
                }
            }
            RDFPage page = new RDFPage(srcURL, info.getId(), info.getDate());
            
            //create the box tree
            Model boxTreeModel = storage.getBoxModelForPage(pageUri);
            RDFBox root = constructBoxTree(boxTreeModel); 
            page.setRoot(root);
            page.setWidth(root.getWidth());
            page.setHeight(root.getHeight());
            
            return page;
        }
        else
            return null;
    }
    
    private RDFBox constructBoxTree(Model model) throws RepositoryException
    {
        Map<URI, RDFBox> boxes = new HashMap<URI, RDFBox>();
        //find all boxes
        for (Resource res : model.subjects())
        {
            if (res instanceof URI)
            {
                RDFBox box = createBoxFromModel(model, (URI) res);
                boxes.put((URI) res, box);
            }
        }
        List<RDFBox> rootBoxes = new ArrayList<RDFBox>(boxes.values());
        //construct the tree
        for (Statement st : model.filter(null, BOX.isChildOf, null))
        {
            if (st.getSubject() instanceof URI && st.getObject() instanceof URI)
            {
                RDFBox parent = boxes.get(st.getObject());
                RDFBox child = boxes.get(st.getSubject());
                if (parent != null && child != null)
                {
                    parent.add(child);
                    rootBoxes.remove(child);
                }
            }
        }
        if (rootBoxes.size() == 1)
            return rootBoxes.get(0);
        else
        {
            log.error("Strange number of root boxes: {}", rootBoxes.toString());
            return null; //strange number of root nodes
        }
    }
    
    private RDFBox createBoxFromModel(Model model, URI uri) throws RepositoryException
    {
        RDFBox box = new RDFBox(uri);
        box.setTagName("unknown");
        box.setType(Box.Type.ELEMENT);
        box.setDisplayType(Box.DisplayType.BLOCK);
        int x = 0, y = 0, width = 0, height = 0;
        int vx = 0, vy = 0, vwidth = 0, vheight = 0;
        
        for (Statement st : model.filter(uri, null, null))
        {
            final URI pred = st.getPredicate();
            final Value value = st.getObject();
            
            if (BOX.backgroundColor.equals(pred)) 
            {
                String bgColor = value.stringValue();
                //bgColor = bgColor.substring(1,bgColor.length());
                box.setBackgroundColor( hex2Rgb( bgColor ) );
            }
            else if (BOX.backgroundImagePosition.equals(pred)) 
            {
            }
            else if (BOX.backgroundImageUrl.equals(pred)) 
            {
            }
            else if (BOX.color.equals(pred)) 
            {
                box.setColor(hex2Rgb(value.stringValue()));
            }
            else if (BOX.underline.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setUnderline(((Literal) value).floatValue());
            }
            else if (BOX.lineThrough.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setLineThrough(((Literal) value).floatValue());
            }
            else if (BOX.fontFamily.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setFontFamily(value.stringValue());
            }
            else if (BOX.fontSize.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setFontSize(((Literal) value).floatValue());
            }
            else if (BOX.fontStyle.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setFontStyle(((Literal) value).floatValue());
            }
            else if (BOX.fontWeight.equals(pred)) 
            {
                if (value instanceof Literal)
                    box.setFontWeight(((Literal) value).floatValue());
            }
            else if (BOX.hasBottomBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    box.setBorderStyle(Side.BOTTOM, border);
                }
            }
            else if (BOX.hasLeftBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    box.setBorderStyle(Side.LEFT, border);
                }
            }
            else if (BOX.hasRightBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    box.setBorderStyle(Side.RIGHT, border);
                }
            }
            else if (BOX.hasTopBorder.equals(pred)) 
            {
                if (value instanceof URI)
                {
                    Border border = createBorder(getBorderModel(), (URI) value);
                    box.setBorderStyle(Side.TOP, border);
                }
            }
            else if (BOX.hasText.equals(pred)) 
            {
                box.setType(Type.TEXT_CONTENT);
                box.setText(value.stringValue());
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
            else if (BOX.visualHeight.equals(pred)) 
            {
                if (value instanceof Literal)
                    vheight = ((Literal) value).intValue();
            }
            else if (BOX.visualWidth.equals(pred)) 
            {
                if (value instanceof Literal)
                    vwidth = ((Literal) value).intValue();
            }
            else if (BOX.visualX.equals(pred)) 
            {
                if (value instanceof Literal)
                    vx = ((Literal) value).intValue();
            }   
            else if (BOX.visualY.equals(pred)) 
            {
                if (value instanceof Literal)
                    vy = ((Literal) value).intValue();
            }
            else if (BOX.htmlTagName.equals(pred)) 
            {
                box.setTagName(value.stringValue());
            }
        }
        box.setBounds(new Rectangular(x, y, x + width - 1, y + height - 1));
        box.setContentBounds(new Rectangular(x, y, x + width - 1, y + height - 1));
        box.setVisualBounds(new Rectangular(vx, vy, vx + vwidth - 1, vy + vheight - 1));
        
        return box;
    }

    private Model getBorderModel() throws RepositoryException
    {
        if (borderModel == null)
            borderModel = storage.getBorderModelForPage(pageUri);
        return borderModel;
    }
    
}
