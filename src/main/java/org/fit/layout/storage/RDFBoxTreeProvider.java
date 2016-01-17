/**
 * CSSBoxTreeProvider.java
 *
 * Created on 27. 1. 2015, 15:14:55 by burgetr
 */
package org.fit.layout.storage;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.layout.impl.BaseBoxTreeProvider;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.RESOURCE;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;

/**
 * A box tree provider that obtains the stored pages from a RDF repository. 
 * 
 * @author milicka
 * @author burgetr
 */
public class RDFBoxTreeProvider extends BaseBoxTreeProvider
{
    private URL urlDb;
    private URI pageId;
    
    private final String[] paramNames = { "urlDb", "pageId" };
    private final ValueType[] paramTypes = { ValueType.STRING, ValueType.STRING };

    public RDFBoxTreeProvider() throws MalformedURLException
    {
		urlDb = new URL("http://localhost:8080/openrdf-sesame/repositories/user");
		pageId = RESOURCE.createPageURI(1);
    }

    
    public RDFBoxTreeProvider(URL urlDb, URI pageId)
    {
        this.urlDb = urlDb;
        this.pageId = pageId;
    }


    public String getId()
    {
        return "FitLayout.RDFSource";
    }

   
    public String getName()
    {
        return "RDF page source";
    }

    
    public String getDescription()
    {
        return "Uses the a RDF repository for obtaining the box tree.";
    }

    
    public String[] getParamNames()
    {
        return paramNames;
    }

    
    public ValueType[] getParamTypes()
    {
        return paramTypes;
    }

    public URL getUrlDb()
    {
        return urlDb;
    }

    public void setUrlDb(URL urlDb)
    {
        this.urlDb = urlDb;
    }
    
    public void setUrlDb(String urlDb)
    {
        try {
            this.urlDb = new URL(urlDb);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + urlDb);
        }
    }

    public URI getPageId()
    {
        return pageId;
    }

    public void setPageId(URI pageId)
    {
        this.pageId = pageId;
    }

    public void setPageId(String pageId)
    {
        ValueFactory vf = ValueFactoryImpl.getInstance();
        this.pageId = vf.createURI(pageId);
    }
    
    public Page getPage() 
    {
    	try {
			RDFStorage storage = new RDFStorage(urlDb.toString());
			return storage.loadPage(pageId);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }

}
