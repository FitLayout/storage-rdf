package org.fit.layout.storage;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.layout.api.PageSet;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.model.RDFAreaTree;
import org.fit.layout.storage.model.RDFPage;
import org.fit.layout.storage.model.RDFPageSet;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.LAYOUT;
import org.fit.layout.storage.ontology.RESOURCE;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements all the high level operations on a RDF repository.
 * 
 * @author milicka
 * @author burgetr
 */
public class RDFStorage 
{
    private static Logger log = LoggerFactory.getLogger(RDFStorage.class);
    
    private static final String PREFIXES =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX box: <" + BOX.NAMESPACE + "> " +        
            "PREFIX segm: <" + SEGM.NAMESPACE + "> " +
            "PREFIX layout: <" + LAYOUT.NAMESPACE + ">";
    
	private RDFConnector db;

	/**
	 * Creates a new RDFStorage for a given SPARQL endpoint.
	 * @param url the SPARQL endpoint URL
	 * @throws RepositoryException
	 */
	public RDFStorage(String url) throws RepositoryException
	{
	    if (url.startsWith("sesame:"))
	        db = new RDFConnectorSesame(url.substring(7));
	    else if (url.startsWith("blazegraph:"))
	        db = new RDFConnectorBlazegraph(url.substring(11));
	    else
	    {
	        log.warn("RDFStorage: no provider specified, using generic SPARQL endpoint: {}", url);
	        db = new RDFConnector(url);
	    }
	}

	/**
	 * Obtains a connection to the current repository.
	 * @return the repository connection.
	 * @throws RepositoryException 
	 */
	public RepositoryConnection getConnection() throws RepositoryException 
	{
		return db.getConnection();
	}
	
	public void closeConnection() throws RepositoryException
	{
	    db.closeConnection();
	}
	
	//==============================================================================
	
	public void createPageSet(String name) throws RepositoryException
	{
        Graph graph = new LinkedHashModel(); // it holds whole model
        ValueFactory vf = ValueFactoryImpl.getInstance();
        URI uri = RESOURCE.createPageSetURI(name);
        graph.add(uri, RDF.TYPE, LAYOUT.PageSet);
        graph.add(uri, LAYOUT.hasName, vf.createLiteral(name));
        graph.add(uri, LAYOUT.createdOn, vf.createLiteral(new java.util.Date()));
	    insertGraph(graph);
	}
	
	public void deletePageSet(String name) throws RepositoryException
	{
	    URI uri = RESOURCE.createPageSetURI(name);
	    getConnection().remove(uri, null, null);
	    closeConnection();
	}
	
    public PageSet getPageSet(String name) throws RepositoryException
    {
        return getPageSet(RESOURCE.createPageSetURI(name));
    }
    
	public PageSet getPageSet(URI uri) throws RepositoryException
	{
        RepositoryResult<Statement> result = getConnection().getStatements(uri, null, null, false);
        RDFPageSet ret = new RDFPageSet(null, uri, this);
        while (result.hasNext()) 
        {
            Statement st = result.next();
            if (LAYOUT.hasName.equals(st.getPredicate()))
                ret.setName(st.getObject().stringValue());
            else if (LAYOUT.createdOn.equals(st.getPredicate()))
            {
                Value val = st.getObject();
                if (val instanceof Literal)
                {
                    Date date = ((Literal) val).calendarValue().toGregorianCalendar().getTime();
                    ret.setDateCreated(date);
                }
            }
        }
        result.close();
        closeConnection();
        if (ret.getName() == null)
            return null; //not found
        else
            return ret;
	}
	
	public List<URI> getPagesForPageSet(URI pageSetUri) throws RepositoryException
	{
	    List<URI> ret = new ArrayList<URI>();
        RepositoryResult<Statement> result = getConnection().getStatements(pageSetUri, LAYOUT.containsPage, null, false);
        while (result.hasNext()) 
        {
            Value obj = result.next().getObject();
            if (obj instanceof URI)
                ret.add((URI) obj);
        }
        closeConnection();
        return ret;
	}
	
    /**
     * Reads all the existing page sets.
     * @return a list of page sets
     * @throws RepositoryException 
     */
	public List<PageSet> getPageSets() throws RepositoryException 
    {
        List<PageSet> ret = new ArrayList<PageSet>();
        RepositoryResult<Statement> result = getConnection().getStatements(null, RDF.TYPE, LAYOUT.PageSet, false);
        while (result.hasNext()) 
        {
            Statement st = result.next();
            if (st.getSubject() instanceof URI)
            {
                PageSet newset = getPageSet((URI) st.getSubject());
                if (newset != null)
                    ret.add(newset);
            }
            
        }
        result.close();
        closeConnection();
        return ret;
    }
	
	public void addPageToPageSet(URI pageUri, String psetName) throws RepositoryException
	{
	    URI psetUri = RESOURCE.createPageSetURI(psetName);
	    getConnection().add(psetUri, LAYOUT.containsPage, pageUri);
	    closeConnection();
	}
	
	/**
	 * Finds all the pages that do not belong to any page set.
	 * @return a set of page URIs
	 * @throws RepositoryException 
	 */
	public Set<URI> getOrphanedPages() throws RepositoryException
	{
        final String query = PREFIXES
                + "SELECT ?pg "
                + "WHERE {"
                + "  ?pg rdf:type box:Page "
                + "  OPTIONAL { ?set layout:containsPage ?pg } "
                + "  FILTER ( !BOUND(?set) ) "
                + "}";
        
        System.out.println("QUERY: " + query);
        TupleQueryResult data = executeSafeTupleQuery(query);
        Set<URI> ret = new HashSet<URI>();
        try
        {
            while (data.hasNext())
            {
                BindingSet binding = data.next();
                Binding b = binding.getBinding("pg");
                ret.add((URI) b.getValue());
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
	    return ret;
	}
	
	/**
	 * Removes all the pages that do not belong to any page set. Also removes the corresponding area trees.
	 * @throws RepositoryException
	 */
	public void removeOrphanedPages() throws RepositoryException
	{
	    Set<URI> pages = getOrphanedPages();
	    for (URI page : pages)
	        removePage(page);
	}
	
	/**
	 * Obtains the tabular data about the available segmented pages in the repository.
	 * @param psetName the selected page set or {@code null} for all the available pages
	 * @return
	 * @throws RepositoryException
	 */
	public TupleQueryResult getAvailableTrees(String psetName) throws RepositoryException
	{
	    String contClause = "";
	    if (psetName != null)
	    {
	        URI pageSetUri = RESOURCE.createPageSetURI(psetName);
	        contClause = " . <" + pageSetUri.toString() + "> layout:containsPage ?page";
	    }
	    final String query = PREFIXES
	            + " SELECT ?page ?tree ?date ?url ?title " 
                + "WHERE {"
                +     "?tree segm:sourcePage ?page . " 
                +     "?page box:launchDatetime ?date . "
                +     "?page box:hasTitle ?title . "
                +     "?page box:sourceUrl ?url" + contClause
                + "} ORDER BY ?date ?page ?tree";
	    System.out.println("QUERY: " + query);
	    return executeSafeTupleQuery(query);
	}
	
	//box tree functions ===========================================================	
	
	/**
	 * Returns a list of distinct source urls in database.
	 * @throws RepositoryException 
	 */
	public Set<String> getDistinctPageUrls() throws RepositoryException 
	{
		Set<String> output = new HashSet<String>();

		RepositoryResult<Statement> result = getConnection().getStatements(null, BOX.sourceUrl, null, true);
		while (result.hasNext()) 
		{
			Statement bindingSet = result.next();
			String url = bindingSet.getObject().stringValue();
			output.add(url);
		}
		result.close();
		closeConnection();
		
		return output;
	}

	/**
	 * Obtains the URIs of all the stored Page objects.
	 * @return
	 * @throws RepositoryException 
	 */
	public Set<URI> getAllPageIds() throws RepositoryException 
	{
		RepositoryResult<Statement> result = getConnection().getStatements(null, RDF.TYPE, BOX.Page, true);
		Set<URI> ret = getSubjectsFromResult(result);
		result.close();
		closeConnection();
		return ret;
	}
		
	/**
	 * Obtains the pageIDs for a specific url
	 * @param url the processed page url
	 * @return list of launch URIs
	 * @throws RepositoryException 
	 */
	public Set<URI> getPageIdsForUrl(String url) throws RepositoryException 
	{
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance(); 
		RepositoryResult<Statement> result = getConnection().getStatements(null, BOX.sourceUrl, vf.createLiteral(url), true); 
		Set<URI> ret = getSubjectsFromResult(result);
		result.close();
		closeConnection();
		return ret;
	}
	
	/**
	 * Inserts a new page model to the database.
	 * @param page the Page to be stored.
	 * @return the RDFPage implementation of the stored page
	 * @throws RepositoryException 
	 */
	public RDFPage insertPageBoxModel(Page page) throws RepositoryException 
	{
	    long seq = getNextSequenceValue("page");
        URI pageUri = RESOURCE.createPageURI(seq);
		BoxModelBuilder pgb = new BoxModelBuilder(page, pageUri);
		insertGraph(pgb.getGraph());
		if (page instanceof RDFPage)
		    return (RDFPage) page;
		else
            return new RDFPage(page, pageUri);
	}

    /**
     * Inserts a new page model to the database.
     * @param page the Page to be updated.
     * @return the RDFPage implementation of the stored page
     * @throws RepositoryException 
     */
    public RDFPage updatePageBoxModel(RDFPage page) throws RepositoryException 
    {
        removePage(page.getUri());
        BoxModelBuilder pgb = new BoxModelBuilder(page, page.getUri());
        insertGraph(pgb.getGraph());
        return page;
    }

	/**
	 * Removes a page from the storage. 
	 * @param pageUri the page URI
	 * @throws RepositoryException 
	 */
	public void removePage(URI pageUri) throws RepositoryException 
	{
	    removeAreaTreesForPage(pageUri);
		removePageModel(pageUri);
		removePageInfo(pageUri);
	}

	/**
	 * Loads a page from the repository.
	 * @param pageId The page URI
	 * @return the corresponding Page or {@code null} when the page is not available in the repository.
	 * @throws RepositoryException
	 */
	public RDFPage loadPage(URI pageId) throws RepositoryException
	{
        BoxModelLoader loader = new BoxModelLoader(this, pageId);
        return loader.getPage();
	}
	
	/**
	 * Gets page box model from the unique page ID.
	 * @param pageId
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getBoxModelForPage(URI pageId) throws RepositoryException
	{
		final String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type box:Box . "
				+ "?s box:documentOrder ?ord . "
				+ "?s box:belongsTo <" + pageId.toString() + ">}"
				+ " ORDER BY ?ord";
        return executeSafeQuery(query);
	}
	
    /**
     * Gets page box model from the unique page ID.
     * @param pageId
     * @return
     * @throws RepositoryException 
     */
    public Model getBorderModelForPage(URI pageId) throws RepositoryException
    {
        final String query = PREFIXES
                + "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o . "
                + "?b rdf:type box:Box . " 
                + "?b box:belongsTo <" + pageId.toString() + "> . "
                + "{?b box:hasTopBorder ?s} UNION {?b box:hasRightBorder ?s} UNION {?b box:hasBottomBorder ?s} UNION {?b box:hasLeftBorder ?s}}";
        return executeSafeQuery(query);
    }
    
	/**
	 * Loads the page info - sourceUrl, launchDateTime, ...
	 * @param pageId
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getPageInfo(URI pageUri) throws RepositoryException 
	{
		RepositoryResult<Statement> result = null;
		result = getConnection().getStatements(pageUri, null, null, true);
		Model ret = createModel(result);
		result.close();
		closeConnection();
		return ret;
	}

	
	//AREA tree functions ===========================================================
	
    /**
     * Loads an area tree from the repository.
     * @param areaTreeId The area tree URI
     * @return a create model loader. Use its {@code getAreaTree} and {@code getLogicalAreaTree}
     * methods for obtaining the trees.
     * @throws RepositoryException
     */
    public AreaModelLoader loadAreaTrees(URI areaTreeId, RDFPage srcPage) throws RepositoryException
    {
        return new AreaModelLoader(this, areaTreeId, srcPage);
    }
    
    /**
     * Obtains the the URI of the source page given an AreaTree URI
     * @param areaTreeUri the URI of the area tree
     * @return the source Page URI or {@code null} when not specified
     * @throws RepositoryException 
     */
    public URI getSourcePageForAreaTree(URI areaTreeUri) throws RepositoryException 
    {
        URI ret = null;
        RepositoryResult<Statement> result = getConnection().getStatements(areaTreeUri, SEGM.sourcePage, null, true); 
        while (result.hasNext())
        {
            Value val = result.next().getObject();
            if (val instanceof URI)
            {
                ret = (URI) val;
                break;
            }
        }
        result.close();
        closeConnection();
        return ret;
    }
    
	/**
	 * Obtains the model of visual areas for the given area tree.
	 * @param areaTreeUri
	 * @return A Model containing the triplets for all the visual areas contained in the given area tree.
	 * @throws RepositoryException 
	 */
	public Model getAreaModelForAreaTree(URI areaTreeUri) throws RepositoryException
	{
		final String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type segm:Area . "
                + "?s box:documentOrder ?ord . "
				+ "?s segm:belongsTo <" + areaTreeUri.stringValue() + "> }"
				+ " ORDER BY ?ord";
        return executeSafeQuery(query);
	}
	
    /**
     * Obtains the model of logical areas for the given area tree.
     * @param areaTreeUri
     * @return A Model containing the triplets for all the visual areas contained in the given area tree.
     * @throws RepositoryException 
     */
    public Model getLogicalAreaModelForAreaTree(URI areaTreeUri) throws RepositoryException
    {
        final String query = PREFIXES
                + "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
                + "?s rdf:type segm:LogicalArea . "
                + "?s box:documentOrder ?ord . "
                + "?s segm:belongsTo <" + areaTreeUri.stringValue() + "> }"
                + " ORDER BY ?ord";
        return executeSafeQuery(query);
    }
    
    /**
     * Gets page border information for the given area tree.
     * @param pageId
     * @return
     * @throws RepositoryException 
     */
    public Model getBorderModelForAreaTree(URI areaTreeUri) throws RepositoryException
    {
        final String query = PREFIXES
                + "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o . "
                + "?b rdf:type segm:Area . " 
                + "?b segm:belongsTo <" + areaTreeUri.toString() + "> . "
                + "{?b box:hasTopBorder ?s} UNION {?b box:hasRightBorder ?s} UNION {?b box:hasBottomBorder ?s} UNION {?b box:hasLeftBorder ?s}}";
        return executeSafeQuery(query);
    }
    
    /**
     * Obtains the model of visual areas for the given area tree.
     * @param areaTreeUri
     * @return A Model containing the triplets for all tags of the visual areas contained in the given area tree.
     * @throws RepositoryException 
     */
	public Model getTagModelForAreaTree(URI areaTreeUri) throws RepositoryException
	{
        final String query = PREFIXES
                + "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
                + "{?a rdf:type segm:Area} UNION {?a rdf:type segm:LogicalArea} . "
                + "?a segm:hasTag ?s . "
                + "?a segm:belongsTo <" + areaTreeUri.stringValue() + "> }";
        return executeSafeQuery(query);
	}
	
    /**
     * Obtains the model of visual areas for the given area tree.
     * @param areaTreeUri
     * @return A Model containing the triplets for all tags of the visual areas contained in the given area tree.
     * @throws RepositoryException 
     */
    public Model getTagSupportModelForAreaTree(URI areaTreeUri) throws RepositoryException
    {
        final String query = PREFIXES
                + "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
                + "?a rdf:type segm:Area . "
                + "?a segm:tagSupport ?s . "
                + "?a segm:belongsTo <" + areaTreeUri.stringValue() + "> }";
        return executeSafeQuery(query);
    }
    
	/**
	 * Obtains all the area trees for a page URI.
	 * @param pageUri the URI of a Page
	 * @return A set of AreaTree URIs that come from the specified page.
	 * @throws RepositoryException 
	 */
	public Set<URI> getAreaTreeIdsForPageId(URI pageUri) throws RepositoryException 
	{
		RepositoryResult<Statement> result = getConnection().getStatements(null, SEGM.sourcePage, pageUri, true);
        Set<URI> ret = getSubjectsFromResult(result);
        result.close();
        closeConnection();
        return ret;
	}
	
    /**
     * Adds an area tree to the repository. The tree is assigned to a given page that must be
     * already stored in the repository. The URI of the tree is generated automatically.
     * @param targetUri the URI of the new area tree
     * @param atree the area tree to be stored
     * @param ltree the logical area tree to be stored or {@code null} when there is no logical area tree.
     * @param pageUri the URI of the source page model (rendered page)
     * @return the RDFAreaTree version of the given area tree
     * @throws RepositoryException 
     */
	public RDFAreaTree insertAreaTree(AreaTree atree, LogicalAreaTree ltree, URI pageUri) throws RepositoryException
	{
        long seq = getNextSequenceValue("areatree");
        URI targetUri = RESOURCE.createAreaTreeURI(seq);
        return insertAreaTree(targetUri, atree, ltree, pageUri);
	}

    /**
     * Adds an area tree to the repository. The tree is assigned to a given page that must be
     * already stored in the repository.
     * @param targetUri the URI of the new area tree
     * @param atree the area tree to be stored
     * @param ltree the logical area tree to be stored or {@code null} when there is no logical area tree.
     * @param pageUri the URI of the source page model (rendered page)
     * @return the RDFAreaTree version of the given area tree
     * @throws RepositoryException 
     */
    public RDFAreaTree insertAreaTree(URI targetUri, AreaTree atree, LogicalAreaTree ltree, URI pageUri) throws RepositoryException
    {
        AreaModelBuilder pgb = new AreaModelBuilder(atree, ltree, pageUri, targetUri);
        insertGraph(pgb.getGraph());
        if (atree instanceof RDFAreaTree)
            return (RDFAreaTree) atree;
        else
            return new RDFAreaTree(atree, targetUri);
    }
    
	/**
	 * Removes the area tree from the repository. 
	 * @param areaTreeUri the URI of the area tree
	 * @throws RepositoryException
	 */
	public void removeAreaTree(URI areaTreeUri) throws RepositoryException
	{
        Model mat = getAreaModelForAreaTree(areaTreeUri);
        mat.addAll(getLogicalAreaModelForAreaTree(areaTreeUri));
        mat.addAll(getBorderModelForAreaTree(areaTreeUri));
        //mat.addAll(getTagModelForAreaTree(areaTreeUri)); //tags probably should not be deleted
        mat.addAll(getTagSupportModelForAreaTree(areaTreeUri));
        RepositoryResult<Statement> result = getConnection().getStatements(areaTreeUri, null, null, false);
        while (result.hasNext())
            mat.add(result.next());
        getConnection().remove(mat);
        closeConnection();
	}
	
	//others =========================================================================
	
	/**
	 * Obtains all statements for the specific subject.
	 * (gets all triples for specific node)
	 * 
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryResult<Statement> getSubjectStatements(Resource subject) throws RepositoryException 
	{
		return getConnection().getStatements(subject, null, null, true);
	}
	
	/**
	 * Obtains a model for the specific subject.
	 * @param subject
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getSubjectModel(Resource subject) throws RepositoryException 
	{
	    RepositoryResult<Statement> result = getSubjectStatements(subject); 
		Model ret = createModel(result);
		result.close();
		closeConnection();
		return ret;
	}

	/**
	 * Executes a SPARQL query on the databse
	 * @param query the SPARQL query
	 * @return
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 */
	public TupleQueryResult executeQuery(String query) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		org.openrdf.query.TupleQuery tq = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
		return tq.evaluate();
	}
	
	
	public void clearRDFDatabase() 
	{
		try {
			Update upd = getConnection().prepareUpdate(QueryLanguage.SPARQL, "DELETE WHERE { ?s ?p ?o }");
			upd.execute();
			closeConnection();
		} catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
			e.printStackTrace();
		}
	}

	public void execSparqlUpdate(String query) throws RepositoryException, MalformedQueryException, UpdateExecutionException 
	{
        Update upd = getConnection().prepareUpdate(QueryLanguage.SPARQL, query);
        upd.execute();
        closeConnection();
	}
	
    public void importTurtle(String query) throws RDFParseException, RepositoryException, IOException 
    {
        getConnection().add(new StringReader(query), null, RDFFormat.TURTLE);
        closeConnection();
    }

    //sequences ==================================================================
    
    /**
     * Obtains the last assigned value of a sequence with the given name.
     * @param name the sequence name
     * @return the last assigned value or 0 when the sequence does not exist.
     * @throws RepositoryException 
     */
    public long getLastSequenceValue(String name) throws RepositoryException
    {
        URI sequence = RESOURCE.createSequenceURI(name);
        RepositoryResult<Statement> result = getConnection().getStatements(sequence, RDF.VALUE, null, false);
        if (result.hasNext())
        {
            Value val = result.next().getObject();
            result.close();
            closeConnection();
            if (val instanceof Literal)
                return ((Literal) val).longValue();
            else
                return 0;
        }
        else
        {
            result.close();
            closeConnection();
            return 0;
        }
    }
    
    public long getNextSequenceValue(String name) throws RepositoryException
    {
        getConnection().begin(); //TODO should be IsolationLevels.SERIALIZABLE but not supported by Sesame 2.7
        URI sequence = RESOURCE.createSequenceURI(name);
        RepositoryResult<Statement> result = getConnection().getStatements(sequence, RDF.VALUE, null, false); 
        long val = 0;
        if (result.hasNext())
        {
            Statement statement = result.next();
            Value vval = statement.getObject();
            if (vval instanceof Literal)
                val = ((Literal) vval).longValue();
            getConnection().remove(statement);
        }
        result.close();
        val++;
        ValueFactory vf = ValueFactoryImpl.getInstance();
        getConnection().add(sequence, RDF.VALUE, vf.createLiteral(val));
        getConnection().commit();
        closeConnection();
        return val;
    }
    
    
	
	//PRIVATE =========================================
	
	/**
	 * Removes the page contents.
	 * @param pageId
	 * @throws RepositoryException 
	 */
	private void removePageModel(URI pageUri) throws RepositoryException 
	{
        Model mat = getBoxModelForPage(pageUri);
        mat.addAll(getBorderModelForPage(pageUri));
        getConnection().remove(mat);
        closeConnection();
	}

	/**
	 * Removes all the area trees that belong to the given page
	 * @param pageUri
	 * @throws RepositoryException
	 */
	private void removeAreaTreesForPage(URI pageUri) throws RepositoryException
	{
        //load all area trees
        Set<URI> areaTreeModels = getAreaTreeIdsForPageId(pageUri);
        //removes all area trees
        for(URI areaTreeId : areaTreeModels)
            removeAreaTree(areaTreeId);
	}
	
	/**
	 * Removes the page record.
	 * @param pageId
	 * @throws RepositoryException 
	 */
	private void removePageInfo(URI pageUri) throws RepositoryException 
	{
		Model m = getPageInfo(pageUri);
		getConnection().remove(m);
		closeConnection();
	}
	
	/**
	 * Executes a SPARQL query where the query syntax is safe (should not fail)
	 * @param query
	 * @return
	 * @throws RepositoryException
	 */
	private Model executeSafeQuery(String query) throws RepositoryException
	{
        try
        {
            GraphQuery pgq = getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query);
            GraphQueryResult gqr = pgq.evaluate();
            Model ret = createModel(gqr);
            gqr.close();
            closeConnection();
            return ret;
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        return new LinkedHashModel(); //this should not happen
	}
	
    /**
     * Executes an internal (safe) tuple query
     * @param query
     * @return a TupleQueryResult object representing the result
     * @throws RepositoryException
     */
	private TupleQueryResult executeSafeTupleQuery(String query) throws RepositoryException
    {
        try
        {
            TupleQuery pgq = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult gqr = pgq.evaluate();
            return gqr;
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        return null; //this should not happen
    }
    
	/**
	 * Creates a Model from the RepositoryResult
	 * @param result
	 * @return
	 * @throws RepositoryException 
	 */
	private Model createModel(RepositoryResult<Statement> result) throws RepositoryException 
	{
		Model model = new LinkedHashModel();
		while (result.hasNext())
			model.add(result.next());
		return model;
	}

    /**
     * Creates a model from a GraphQueryResult
     * @param result
     * @return
     * @throws QueryEvaluationException 
     */
    private Model createModel(GraphQueryResult result) throws QueryEvaluationException 
    {
        Model model = new LinkedHashModel();
        while (result.hasNext())
            model.add(result.next());
        return model;
    }

    /**
     * Create a set of subjects in a repository result.
     * @param result
     * @return
     * @throws RepositoryException
     */
    public Set<URI> getSubjectsFromResult(RepositoryResult<Statement> result) throws RepositoryException 
    {
        Set<URI> output = new HashSet<URI>();
        while (result.hasNext()) 
        {
            Resource uri = result.next().getSubject();
            if (uri instanceof URI)
                output.add((URI) uri);
        }
        return output;
    }
    
	/**
	 * Inserts a new graph to the database.
	 * @param graph
	 * @throws RepositoryException 
	 */
    private void insertGraph(Graph graph) throws RepositoryException
	{
        getConnection().begin();
		getConnection().add(graph);
		getConnection().commit();
		closeConnection();
	}

}
