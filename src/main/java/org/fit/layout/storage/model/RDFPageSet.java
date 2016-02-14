/**
 * RDFPageSet.java
 *
 * Created on 2. 2. 2016, 0:02:20 by burgetr
 */
package org.fit.layout.storage.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fit.layout.impl.AbstractPageSet;
import org.fit.layout.model.Page;
import org.fit.layout.storage.RDFStorage;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A page set stored in a RDF repository.
 * 
 * @author burgetr
 */
public class RDFPageSet extends AbstractPageSet
{
    private static Logger log = LoggerFactory.getLogger(RDFPageSet.class);
    
    private RDFStorage storage;
    private URI uri;

    public RDFPageSet(String name, URI uri, RDFStorage storage)
    {
        super(name);
        this.uri = uri;
        this.storage = storage;
    }

    @Override
    public int size()
    {
        try
        {
            return storage.getPagesForPageSet(uri).size();
        } catch (RepositoryException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void addPage(Page page)
    {
        try
        {
            if (page instanceof RDFPage)
                storage.addPageToPageSet(((RDFPage) page).getUri(), getName());
            else
                log.error("addPage: The saved instance of the page is required.");
        } 
        catch (RepositoryException e)
        {
            log.error("Error: " + e.getMessage());
        }
    }

    @Override
    public Iterator<Page> iterator()
    {
        try
        {
            return new PageIterator(storage, storage.getPagesForPageSet(uri));
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String toString()
    {
        return getName();
    }

    public URI[] getAreaTreeURIs()
    {
        try
        {
            ArrayList<URI> list = new ArrayList<URI>();
            TupleQueryResult data = storage.getAvailableTrees(getName());
            while (data.hasNext())
            {
                BindingSet tuple = data.next();
                if (tuple.getBinding("tree").getValue() instanceof URI)
                {
                    list.add((URI) tuple.getBinding("tree").getValue());
                }
            }
            URI[] ret = new URI[list.size()];
            return list.toArray(ret);
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public class PageIterator implements Iterator<Page>
    {
        private RDFStorage storage;
        private List<URI> pageUris;
        private int currentIndex;
        
        public PageIterator(RDFStorage storage, List<URI> pageUris)
        {
            this.storage = storage;
            this.pageUris = pageUris;
            currentIndex = 0;
        }

        @Override
        public boolean hasNext()
        {
            return (currentIndex < pageUris.size());
        }

        @Override
        public Page next()
        {
            if (currentIndex < pageUris.size())
            {
                try
                {
                    return storage.loadPage(pageUris.get(currentIndex++));
                } catch (RepositoryException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else
                return null;
        }

        @Override
        public void remove()
        {
        }
        
    }
    
}
