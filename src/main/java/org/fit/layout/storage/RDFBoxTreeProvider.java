/**
 * CSSBoxTreeProvider.java
 *
 * Created on 27. 1. 2015, 15:14:55 by burgetr
 */
package org.fit.layout.storage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fit.layout.api.Parameter;
import org.fit.layout.impl.BaseBoxTreeProvider;
import org.fit.layout.impl.ParameterString;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.RESOURCE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * A box tree provider that obtains the stored pages from a RDF repository. 
 * 
 * @author milicka
 * @author burgetr
 */
public class RDFBoxTreeProvider extends BaseBoxTreeProvider
{
    private URL urlDb;
    private IRI pageId;
    
    public RDFBoxTreeProvider() throws MalformedURLException
    {
		urlDb = new URL("http://localhost:8080/rdf4j-server/repositories/user");
		pageId = RESOURCE.createPageURI(1);
    }
    
    public RDFBoxTreeProvider(URL urlDb, IRI pageId)
    {
        this.urlDb = urlDb;
        this.pageId = pageId;
    }

    @Override
    public String getId()
    {
        return "FitLayout.RDFSource";
    }
   
    @Override
    public String getName()
    {
        return "RDF page source";
    }
    
    @Override
    public String getDescription()
    {
        return "Uses the a RDF repository for obtaining the box tree.";
    }
    
    @Override
    public List<Parameter> defineParams()
    {
        List<Parameter> ret = new ArrayList<>();
        ret.add(new ParameterString("urlDb"));
        ret.add(new ParameterString("pageId"));
        return ret;
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

    public IRI getPageId()
    {
        return pageId;
    }

    public void setPageId(IRI pageId)
    {
        this.pageId = pageId;
    }

    public void setPageId(String pageId)
    {
        ValueFactory vf = SimpleValueFactory.getInstance();
        this.pageId = vf.createIRI(pageId);
    }
    
    @Override
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
