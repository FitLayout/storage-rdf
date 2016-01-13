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

import org.fit.layout.api.PageSet;
import org.fit.layout.api.ScriptObject;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.RDFStorage;
import org.fit.layout.storage.model.RDFPage;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

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

    
    public ScriptApi()
    {
        
    }
    
    @Override
    public String getName()
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
        } 
        catch (RepositoryException e)
        {
            werr.println("Couldn't connect: " + e.getMessage());
        }
    }
    
    public void createPageSet(String name)
    {
        try
        {
            bdi.createPageSet(name);
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
    
    public RDFPage insertPage(Page page)
    {
        if (page != null) 
        {
            try
            {
                return bdi.insertPageBoxModel(page);
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
    
    public void saveAreaTree(AreaTree atree, LogicalAreaTree ltree, String pageUri)
    {
        if (atree != null) 
        {
            try
            {
                bdi.insertAreaTree(atree, ltree, new URIImpl(pageUri+"#something"));  //TODO the #suffix is required by bdi implementation
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearDB()
    {
        bdi.clearRDFDatabase();
    }
    
    public void execQueryFromResource(String res)
    {
        try {
            Scanner scan = new Scanner(ClassLoader.getSystemResourceAsStream(res));
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
        } catch (RDFParseException | RepositoryException | IOException e) {
            werr.println("Couldn't import Turtle data: " + e.getMessage());
        }
    }
    
    public void importTurtleFromResource(String res)
    {
        Scanner scan = new Scanner(ClassLoader.getSystemResourceAsStream(res));
        String turtle = scan.useDelimiter("\\Z").next();
        scan.close();
        try {
            bdi.importTurtle(turtle);
        } catch (RDFParseException | RepositoryException | IOException e) {
            werr.println("Couldn't import Turtle data: " + e.getMessage());
        }
    }
    
}
