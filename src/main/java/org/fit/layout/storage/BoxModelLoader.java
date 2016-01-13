/**
 * BoxModelLoader.java
 *
 * Created on 13. 1. 2016, 23:49:14 by burgetr
 */
package org.fit.layout.storage;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.layout.storage.model.RDFPage;
import org.openrdf.model.Model;

/**
 * This class implements creating a RDFPage from the RDF models.
 * @author burgetr
 */
public class BoxModelLoader
{
    private Model pageModel;
    private Model boxTreeModel;
    private RDFPage page;
    
    public BoxModelLoader(Model pageModel, Model boxTreeModel)
    {
        this.pageModel = pageModel;
        this.boxTreeModel = boxTreeModel;
    }
    
    public RDFPage getPage()
    {
        if (page == null)
            page = constructPage(pageModel, boxTreeModel);
        return page;
    }
    
    private RDFPage constructPage(Model pageModel, Model boxTreeModel)
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
        //TODO
        
        return page;
    }
}
