/**
 * RDFPageSet.java
 *
 * Created on 2. 2. 2016, 0:02:20 by burgetr
 */
package org.fit.layout.storage.model;

import java.util.Iterator;

import org.fit.layout.impl.AbstractPageSet;
import org.fit.layout.model.Page;
import org.fit.layout.storage.RDFStorage;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * A page set stored in a RDF repository.
 * 
 * @author burgetr
 */
public class RDFPageSet extends AbstractPageSet
{
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
    }

    @Override
    public Iterator<Page> iterator()
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }

}
