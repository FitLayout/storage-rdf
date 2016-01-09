package org.fit.layout.storage;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.LAYOUT;
import org.fit.layout.storage.ontology.RESOURCE;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.IsolationLevels;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
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
            "PREFIX segm: <" + SEGM.NAMESPACE + "> ";        
    
	private RDFConnector db;

	/**
	 * Creates a new RDFStorage for a given SPARQL endpoint.
	 * @param url the SPARQL endpoint URL
	 * @throws RepositoryException
	 */
	public RDFStorage(String url) throws RepositoryException
	{
		db = new RDFConnector(url);
	}

	/**
	 * Obtains a connection to the current repository.
	 * @return the repository connection.
	 */
	public RepositoryConnection getConnection() 
	{
		return db.getConnection();
	}
	
    /**
     * Reads all the existing page sets.
     * @return a list of page sets
     */
	public List<String> getPageSets() 
    {
        List<String> output = new ArrayList<String>();
        try {
            RepositoryResult<Statement> result = getConnection().getStatements(null, RDF.TYPE, LAYOUT.PageSet, true);
            while (result.hasNext()) 
            {
                Statement bindingSet = result.next();
                String url = bindingSet.getSubject().stringValue();
                if (!output.contains(url))
                    output.add(url);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return output;
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
		return getSubjectsFromResult(result);
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
        return getSubjectsFromResult(result);
	}
	
	/**
	 * Stores a page model.
	 * @param page the Page to be stored.
	 */
	public void insertPageBoxModel(Page page) 
	{
		BoxModelBuilder pgb = new BoxModelBuilder(page);
		insertGraph(pgb.getGraph());
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
	 * Builds a Model for the given timestamp.
	 * @param timestamp
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getBoxModelForTimestamp(String timestamp) throws RepositoryException
	{
		final String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a box:launchDatetime \"" + timestamp + "\". "
				+ "?a rdf:type box:Page  }";
		return executeSafeQuery(query);
	}
	
	/**
	 * Gets page box model from the unique page ID.
	 * @param pageId
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getBoxModelForPageId(String pageId) throws RepositoryException
	{
		final String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " 
				+ "?s box:belongsTo <"+pageId+">}";
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
		return createModel(result);
	}

	
	//AREA tree functions ===========================================================
	
	/**
	 * it returns area model
	 * @param areaTreeId
	 * @return
	 * @throws RepositoryException 
	 */
	public Model getAreaModelForAreaTreeId(URI areaTreeId) throws RepositoryException
	{
		final String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type segm:Area . "
				+ "?s segm:belongsTo <" + areaTreeId.stringValue() + "> }";
        return executeSafeQuery(query);
	}
	
	/**
	 * gets all area models for specific url
	 * @param pageId
	 * @throws RepositoryException 
	 */
	public Set<URI> getAreaTreeIdsForPageId(URI pageUri) throws RepositoryException 
	{
		RepositoryResult<Statement> result = getConnection().getStatements(null, SEGM.sourcePage, pageUri, true);
        return getSubjectsFromResult(result);
	}
	
	/**
	 * Adds an area tree to a specific pageId
	 * @param atree
	 * @param pageId
	 */
	public void insertAreaTree(AreaTree atree, LogicalAreaTree ltree, URI pageId)
	{
	    String actualUrl = pageId.toString();
	    if (actualUrl.lastIndexOf("#") != -1) //TODO what's this?
	        actualUrl = actualUrl.substring(0, actualUrl.lastIndexOf("#"));
		
		AreaModelBuilder buildingModel = new AreaModelBuilder(atree, ltree, pageId, actualUrl);
		insertGraph(buildingModel.getGraph());
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
		return createModel(getSubjectStatements(subject));
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
            try {
                return Long.parseLong(result.next().getObject().stringValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        else
            return 0;
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
            try {
                val = Long.parseLong(statement.getObject().stringValue());
            } catch (NumberFormatException e) {
                val = 0;
            }
            getConnection().remove(statement);
        }
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
		{
			Model mat = getAreaModelForAreaTreeId(areaTreeId);
			getConnection().remove(mat);	
		}
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
            return createModel(gqr);
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        return new LinkedHashModel(); //this should not happen
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
	 */
    private void insertGraph(Graph graph)
	{
		db.addGraph(graph);
	}

}
