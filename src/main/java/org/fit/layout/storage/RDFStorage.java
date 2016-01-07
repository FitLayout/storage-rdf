package org.fit.layout.storage;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.LAYOUT;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
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

public class RDFStorage 
{
    private static final String PREFIXES =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX box: <" + BOX.NAMESPACE + "> " +        
            "PREFIX segm: <" + SEGM.NAMESPACE + "> ";        
    
	private RDFConnector bddb;
	private boolean lbs = false;
	private String url = "http://localhost:8080/bigdata/sparql";

	public RDFStorage() throws RepositoryException 
	{
		bddb = new RDFConnector(this.url, this.lbs);
	}

	public RDFStorage(String url, boolean lbs) throws RepositoryException
	{
		this.url = url;
		this.lbs = lbs;
		bddb = new RDFConnector(this.url, this.lbs);
	}

	public RepositoryConnection getConnection() {
		return this.bddb.getConnection();
	}
	

	
    public List<String> getPageSets() 
    {
        List<String> output = new ArrayList<String>();
        
        try {
            RepositoryResult<Statement> result = this.bddb.getConnection()
                    .getStatements(null, RDF.TYPE, LAYOUT.PageSet, true);

            // do something with the results
            while (result.hasNext()) {
                Statement bindingSet = result.next();

                String url = bindingSet.getSubject().stringValue();

                if (!output.contains(url)) {
                    output.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
        
	
	
	//box tree functions ===========================================================	
	
	/**
	 * it returns a list of distinct source urls in database
	 * 
	 */
	public List<String> getDistinctUrlPages() {

		List<String> output = new ArrayList<String>();

		try {
			RepositoryResult<Statement> result = this.bddb.getConnection()
					.getStatements(null, BOX.sourceUrl, null, true);

			while (result.hasNext()) {
				Statement bindingSet = result.next();

				String url = bindingSet.getObject().stringValue();

				if (!output.contains(url)) {
					output.add(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;

	}

	/**
	 * gets all pages URI
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllPageIds() 
	{
		
		List<String> output = new ArrayList<String>();

		try {
			RepositoryResult<Statement> result = this.bddb.getConnection()
					.getStatements(null, RDF.TYPE, BOX.Page, true);

			// do something with the results
			while (result.hasNext()) {
				Statement bindingSet = result.next();

				String url = bindingSet.getSubject().stringValue();

				if (!output.contains(url)) {
					output.add(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}
		
	/**
	 * method gives a list of pageIDs for the specific url
	 * 
	 * @param url
	 *            it defines url of processed site
	 * @return list of specific launches
	 */
	public List<String> getPageIdsForUrl(String url) {
		
		List<String> output = new ArrayList<String>();

		try {
			// request for all launches of the specific url
			URIImpl sourceUrlPredicate = new URIImpl(BOX.sourceUrl.toString());
			ValueFactoryImpl vf = ValueFactoryImpl.getInstance(); 
																	
			RepositoryResult<Statement> result = bddb.getConnection()
					.getStatements(null, sourceUrlPredicate, vf.createLiteral(url), true); 

			// stores all launches into list of string
			while (result.hasNext()) 
			{
				Statement row = result.next();
				String page = row.getSubject().toString();

				System.out.println("output "+page);
				
				if (!output.contains(page))
					output.add(page);
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}
	
	/**
	 * stores page model into bigdata database
	 * 
	 * @param page
	 */
	public void insertPageBoxModel(Page page) {
		
		// creates graph representation of RDF triples
		BoxModelBuilder pgb = new BoxModelBuilder(page);

		// stores graph of triples into DB
		insertGraph(pgb.getGraph());
	}

	/**
	 * it removes page
	 * 
	 * @param pageId
	 */
	public void removePage(String pageId) {

		removePageModel(pageId);
		removePageInfo(pageId);
	}

	/**
	 * it builds Model variable (specific type of Graph) for the information
	 * 
	 * 
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public Model getBoxModelForTimestamp(String timestamp) throws Exception {
		
		String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " + "?s ?b ?a . "
				+ "?a box:launchDatetime \"" + timestamp + "\". "
				+ "?a rdf:type box:Page  }";

		GraphQuery pgq = bddb.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query);
		GraphQueryResult gqr = pgq.evaluate();

		return createModel(gqr);
	}
	
	/*
	 * gets page box model from the unique page ID
	 * 
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getBoxModelForPageId(String pageId) throws Exception {
		
		String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type app:Box . " 
				+ "?s box:belongsTo <"+pageId+">}";

		GraphQuery pgq = bddb.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query);
		GraphQueryResult gqr = pgq.evaluate();

		return createModel(gqr);
	}
	
	/**
	 * loads page info - sourceUrl, launchDateTime
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public Model getPageInfo(String pageId) throws Exception {
		
		URIImpl page = new URIImpl(pageId);
		
		RepositoryResult<Statement> result = null;
		try {
			result = this.bddb.getConnection().getStatements(page, null, null, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return createModel(result);
	}
	
	
	
	
	//AREA tree functions ===========================================================
	
	
	/**
	 * it returns area model
	 * @param areaTreeId
	 * @return
	 * @throws Exception
	 */
	public Model getAreaModelForAreaTreeId(String areaTreeId) throws Exception {
		
		String query = PREFIXES
				+ "CONSTRUCT { ?s ?p ?o } " + "WHERE { ?s ?p ?o . "
				+ "?s rdf:type segm:Area . "
				+ "?s segm:belongsTo <" + areaTreeId + "> }";

		GraphQuery pgq = bddb.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, query);
		GraphQueryResult gqr = pgq.evaluate();

		return createModel(gqr);
	}
	
	/**
	 * gets all area models for specific url
	 * @param pageId
	 * @throws Exception 
	 */
	public List<String> getAreaTreeIdsForPageId(String pageId) throws Exception {
		
		List<String> output = new ArrayList<String>();
		URI page = new URIImpl(pageId);

		try {
			RepositoryResult<Statement> result = this.bddb.getConnection()
					.getStatements(null, SEGM.sourcePage, page, true);

			while (result.hasNext()) {
				Statement bindingSet = result.next();
				String url = bindingSet.getObject().stringValue();

				if (!output.contains(url))
					output.add(url);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	/**
	 * inserts area tree to specific pageId
	 * @param atree
	 * @param pageId
	 */
	public void insertAreaTree(AreaTree atree, LogicalAreaTree ltree, URIImpl pageId) {
		
		try {
		    String actualUrl = pageId.toString();
		    if (actualUrl.lastIndexOf("#") != -1)
		        actualUrl = actualUrl.substring(0, actualUrl.lastIndexOf("#"));
			
			AreaModelBuilder buildingModel = new AreaModelBuilder(atree, ltree, pageId, actualUrl);
			insertGraph(buildingModel.getGraph());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	//others =========================================================================

	
	
	/**
	 * gets all statements for the specific subject
	 * (gets all triples for specific node)
	 * 
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryResult<Statement> getSubjectStatements(Resource subject)
			throws RepositoryException {
		RepositoryResult<Statement> stm = bddb.getConnection().getStatements(subject, null, null, true);
		
		return stm;
	}
	
	/**
	 * gets model with all attributes
	 * 
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public Model getSubjectModel(Resource subject) throws Exception {
		
		RepositoryResult<Statement> gqr = this.bddb.getConnection().getStatements(subject, null, null, true);
		Model m = createModel(gqr);
		return m;
	}
	

	/**
	 * it executes SPARQL query on the databse
	 * 
	 * @param str
	 * @return
	 * @throws QueryEvaluationException
	 */
	public TupleQueryResult executeQuery(String query)
			throws QueryEvaluationException {

		try {
			org.openrdf.query.TupleQuery tq = bddb.getConnection()
					.prepareTupleQuery(QueryLanguage.SPARQL, query);
			return tq.evaluate();

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void clearRDFDatabase() {
		
		try {
			Update upd = bddb.getConnection().prepareUpdate(QueryLanguage.SPARQL, "DELETE WHERE { ?s ?p ?o }");
			upd.execute();
			
		} catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
			e.printStackTrace();
		}
		
	}

	public void execSparql(String query) {
	    
        try {
            Update upd = bddb.getConnection().prepareUpdate(QueryLanguage.SPARQL, query);
            upd.execute();
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }
        
	}
	
    public void importTurtle(String query) {
        
        try {
            bddb.getConnection().add(new StringReader(query), null, RDFFormat.TURTLE);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RDFParseException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }

	
	//PRIVATE =========================================
	
	/**
	 * removes page model with its area trees
	 * 
	 * @param pageId
	 */
	private void removePageModel(String pageId) {

		//remove page model
		try {
			Model m;
			m = getBoxModelForPageId(pageId);
			bddb.getConnection().remove(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//load all area trees
		List<String> areaTreeModels = null;
		try {
			areaTreeModels = getAreaTreeIdsForPageId(pageId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//removes all area trees
		for(String areaTreeId: areaTreeModels) {
			try {
				Model mat = getAreaModelForAreaTreeId(areaTreeId);
				bddb.getConnection().remove(mat);	
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * it removes page info
	 * 
	 * @param pageId
	 */
	private void removePageInfo(String pageId) {
		
		try {
			Model m = getPageInfo(pageId);
			bddb.getConnection().remove(m);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * converstion from GraphQueryResult info Model
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
     * converstion from GraphQueryResult info Model
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
	 * it appends graph into database
	 * 
	 * @param graph
	 */
	private void insertGraph(Graph graph) {
		bddb.addGraph(graph);
	}

}
