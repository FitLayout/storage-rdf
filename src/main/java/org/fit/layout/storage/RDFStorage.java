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
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.LAYOUT;
import org.fit.layout.storage.ontology.RESOURCE;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.IsolationLevels;
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


/**
 * This class implements all the high level operations on a RDF repository.
 * 
 * @author milicka
 * @author burgetr
 */
public class RDFStorage 
{
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
		db = new RDFConnectorSesame(url);
	}

	/**
	 * Obtains a connection to the current repository.
	 * @return the repository connection.
	 */
	public RepositoryConnection getConnection() 
	{
		return db.getConnection();
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
	
    public PageSet getPageSet(String name) throws RepositoryException
    {
        return getPageSet(RESOURCE.createPageSetURI(name));
    }
    
	public PageSet getPageSet(URI uri) throws RepositoryException
	{
        RepositoryResult<Statement> result = getConnection().getStatements(uri, null, null, false);
        PageSet ret = new PageSet(null);
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
        if (ret.getName() == null)
            return null; //not found
        else
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
        return ret;
    }
	
	/**
	 * Obtains the tabular data about the available segmented pages in the repository.
	 * @param pageSetUri the selected page set or {@code null} for all the available pages
	 * @return
	 * @throws RepositoryException
	 */
	public TupleQueryResult getAvailableTrees(URI pageSetUri) throws RepositoryException
	{
	    String contClause = "";
	    if (pageSetUri != null)
	        contClause = " . <" + pageSetUri.toString() + "> layout:containsPage ?page";
	    final String query = PREFIXES
	            + " SELECT ?page ?tree ?date ?url ?title " 
                + "WHERE {"
                +     "?tree segm:sourcePage ?page . " 
                +     "?page box:launchDatetime ?date . "
                +     "?page box:hasTitle ?title . "
                +     "?page box:sourceUrl ?url" + contClause
                + "}";
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
	 * @param pageId
	 * @throws RepositoryException 
	 */
	public void removePage(URI pageId) throws RepositoryException 
	{
		removePageModel(pageId);
		removePageInfo(pageId);
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
				+ "?s box:belongsTo <" + pageId.toString() + ">}";
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
				+ "?s segm:belongsTo <" + areaTreeUri.stringValue() + "> }";
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
                + "?s segm:belongsTo <" + areaTreeUri.stringValue() + "> }";
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
        return ret;
	}
	
	/**
	 * Adds an area tree to a specific pageId
	 * @param atree
	 * @param pageId
	 * @return 
	 * @throws RepositoryException 
	 */
	public RDFAreaTree insertAreaTree(AreaTree atree, LogicalAreaTree ltree, URI pageId) throws RepositoryException
	{
        long seq = getNextSequenceValue("areatree");
        URI pageUri = RESOURCE.createAreaTreeURI(seq);
        AreaModelBuilder pgb = new AreaModelBuilder(atree, ltree, pageId, pageUri);
        insertGraph(pgb.getGraph());
        if (atree instanceof RDFAreaTree)
            return (RDFAreaTree) atree;
        else
            return new RDFAreaTree(atree, pageUri);
	}

	/**
	 * Removes the area tree from the repository. 
	 * @param areaTreeUri the URI of the area tree
	 * @throws RepositoryException
	 */
	public void removeAreaTree(URI areaTreeUri) throws RepositoryException
	{
        Model mat = getAreaModelForAreaTree(areaTreeUri);
        getConnection().remove(mat);    
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
		} catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
			e.printStackTrace();
		}
	}

	public void execSparqlUpdate(String query) throws RepositoryException, MalformedQueryException, UpdateExecutionException 
	{
        Update upd = getConnection().prepareUpdate(QueryLanguage.SPARQL, query);
        upd.execute();
	}
	
    public void importTurtle(String query) throws RDFParseException, RepositoryException, IOException 
    {
        getConnection().add(new StringReader(query), null, RDFFormat.TURTLE);
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
            if (val instanceof Literal)
                return ((Literal) val).longValue();
            else
                return 0;
        }
        else
        {
            result.close();
            return 0;
        }
    }
    
    public long getNextSequenceValue(String name) throws RepositoryException
    {
        getConnection().begin(IsolationLevels.SERIALIZABLE);
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
        return val;
    }
    
    
	
	//PRIVATE =========================================
	
	/**
	 * Removes the page model together with its area trees.
	 * @param pageId
	 * @throws RepositoryException 
	 */
	private void removePageModel(URI pageUri) throws RepositoryException 
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
	}

}
