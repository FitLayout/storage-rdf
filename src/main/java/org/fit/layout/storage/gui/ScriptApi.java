/**
 * ScriptApi.java
 *
 * Created on 28. 4. 2015, 16:40:17 by burgetr
 */
package org.fit.layout.storage.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Scanner;
import java.util.Set;

import org.fit.layout.api.PageSet;
import org.fit.layout.api.ScriptObject;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.AreaModelLoader;
import org.fit.layout.storage.RDFStorage;
import org.fit.layout.storage.model.RDFPage;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

/**
 * JavaScript application interface for the storage.
 * @author burgetr
 */
public class ScriptApi implements ScriptObject
{
    @SuppressWarnings("unused")
    private BufferedReader rin;
    @SuppressWarnings("unused")
    private PrintWriter wout;
    private PrintWriter werr;
    
    private RDFStorage bdi;
    private StoragePlugin plugin;

    
    public ScriptApi()
    {
        plugin = null;
    }
    
    public void setPlugin(StoragePlugin plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public String getVarName()
    {
        return "storage";
    }
    
    @Override
    public void setIO(Reader in, Writer out, Writer err)
    {
        rin = new BufferedReader(in);
        wout = new PrintWriter(out);
        werr = new PrintWriter(err);
    }
    
    public void connect(String uri)
    {
        try
        {
            bdi = new RDFStorage(uri);
            if (plugin != null)
                plugin.setStorage(bdi);
        } 
        catch (RepositoryException e)
        {
            werr.println("Couldn't connect: " + e.getMessage());
        }
    }

    public boolean isConnected()
    {
        return (bdi != null);
    }
    
    public void close()
    {
        try
        {
            bdi.closeConnection();
            bdi = null;
            if (plugin != null)
                plugin.closeStorage();
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
        }
    }
    
    public void createPageSet(String name)
    {
        try
        {
            bdi.createPageSet(name);
            if (plugin != null)
                plugin.updateStorageState();
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
        }
    }
    
    public void removePageSet(String name)
    {
        try
        {
            bdi.deletePageSet(name);
            if (plugin != null)
                plugin.updateStorageState();
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
        }
    }

    public void removeOrphanedPages()
    {
        try
        {
            bdi.removeOrphanedPages();
            if (plugin != null)
                plugin.updateStorageState();
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
        }
    }

    public PageSet getPageSet(String name)
    {
        try
        {
            return bdi.getPageSet(name);
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    public void addPageToPageSet(Page page, String name)
    {
        try
        {
            if (page instanceof RDFPage)
            {
                bdi.addPageToPageSet(((RDFPage) page).getIri(), name);
                if (plugin != null)
                    plugin.updateStorageState();
            }
            else
                werr.println("Error: The saved instance of the page is required.");
        } 
        catch (RepositoryException e)
        {
            werr.println("Error: " + e.getMessage());
        }
    }
    
    public RDFPage insertPage(Page page)
    {
        if (page != null) 
        {
            try
            {
                RDFPage ret = bdi.insertPageBoxModel(page);
                if (plugin != null)
                    plugin.updateStorageState();
                return ret;
            } catch (RepositoryException e) {
                werr.println("Couldn't save the box tree: " + e.getMessage());
            }
        }
        return null;
    }
    
    public RDFPage updatePage(Page page)
    {
        if (page != null && page instanceof RDFPage) 
        {
            try
            {
                return bdi.updatePageBoxModel((RDFPage) page);
            } catch (RepositoryException e) {
                werr.println("Couldn't save the box tree: " + e.getMessage());
            }
        }
        return null;
    }
    
    public void saveAreaTree(AreaTree atree, LogicalAreaTree ltree, Page sourcePage)
    {
        if (atree != null) 
        {
            try
            {
                if (sourcePage instanceof RDFPage)
                {
                    bdi.insertAreaTree(atree, ltree, ((RDFPage) sourcePage).getIri());
                    if (plugin != null)
                        plugin.updateStorageState();
                }
                else
                    werr.println("Error: The saved instance of the page is required.");
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    public Page loadPage(String pageUri)
    {
        try
        {
            ValueFactory vf = SimpleValueFactory.getInstance();
            IRI uri = vf.createIRI(pageUri);
            return bdi.loadPage(uri);
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public AreaTree loadAreaTree(String treeUri, RDFPage page)
    {
        try
        {
            ValueFactory vf = SimpleValueFactory.getInstance();
            IRI uri = vf.createIRI(treeUri);
            AreaModelLoader loader = bdi.loadAreaTrees(uri, page);
            return loader.getAreaTree();
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String[] getAreaTreeURIs(RDFPage page)
    {
        try
        {
            Set<IRI> uris = bdi.getAreaTreeIdsForPageId(page.getIri());
            String[] ret = new String[uris.size()];
            int i = 0;
            for (IRI uri : uris)
                ret[i++] = uri.toString();
            return ret;
        } catch (RepositoryException e) {
            return new String[0];
        }
    }
    
    public void clearDB()
    {
        bdi.clearRDFDatabase();
        if (plugin != null)
            plugin.updateStorageState();
    }
    
    public void execQueryFromResource(String res)
    {
        try {
            if (!res.startsWith("/"))
                res = "/" + res;
            Scanner scan = new Scanner(ScriptApi.class.getResourceAsStream(res));
            String query = scan.useDelimiter("\\Z").next();
            scan.close();
            bdi.execSparqlUpdate(query);
        } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
            werr.println("Couldn't execute query: " + e.getMessage());
        }
    }
    
    public void importTurtle(String turtle)
    {
        try {
            bdi.importTurtle(turtle);
            if (plugin != null)
                plugin.updateStorageState();
        } catch (RDFParseException | RepositoryException | IOException e) {
            werr.println("Couldn't import Turtle data: " + e.getMessage());
        }
    }
    
    public void importTurtleFromResource(String res)
    {
        if (!res.startsWith("/"))
            res = "/" + res;
        Scanner scan = new Scanner(ScriptApi.class.getResourceAsStream(res));
        String turtle = scan.useDelimiter("\\Z").next();
        scan.close();
        try {
            bdi.importTurtle(turtle);
            if (plugin != null)
                plugin.updateStorageState();
        } catch (RDFParseException | RepositoryException | IOException e) {
            werr.println("Couldn't import Turtle data: " + e.getMessage());
        }
    }
    
}
