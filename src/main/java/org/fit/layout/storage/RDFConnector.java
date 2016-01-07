package org.fit.layout.storage;


import org.openrdf.model.Graph;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;


public class RDFConnector {
	String endpointUrl;
	Boolean lbs;
	//BigdataSailRemote
	RepositoryConnection connection;
	BigdataSailRemoteRepository repo;
	

	/******************** getters/setters ************************/
	/**
	 * it returns connection
	 * @return
	 */
	public RepositoryConnection getConnection() 
	{
		return connection;
	}
	
	
	/**
	 * it redefines parameters and creates connection to server 
	 * @param endpoint
	 * @param lbs
	 * @throws RepositoryException
	 */
	public RDFConnector(String endpoint, Boolean lbs) throws RepositoryException 
	{
		this.endpointUrl = endpoint;
		this.lbs = lbs;
		this.createConnection();
	}
	

	/**
	 * it creates db connection
	 * @throws RepositoryException
	 */
	private void createConnection() throws RepositoryException 
	{
		this.repo = new BigdataSailRemoteRepository(this.endpointUrl, this.lbs);
		this.connection = repo.getConnection();
	
		/*	
			try {
				BigdataSail bds = new BigdataSail();
				bds.getConnection().getNamespaces();
			} catch (SailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	*/
	}

	
	/**
	 * it adds single tripple
	 * @param s
	 * @param p
	 * @param o
	 * @throws RepositoryException
	 */
	public void add(Resource s, URI p, Value o) 
	{
		try {
			
			Statement stmt = new StatementImpl(s, p, o);
			this.connection.add(stmt);
			this.connection.commit();
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * it executes sparql and returns tupy
	 * @param queryString
	 * @return
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException 
	 */
	public TupleQueryResult executeQuery(String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		try {
			TupleQuery query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult tqr = query.evaluate();
        	return tqr;
		}
		catch(Exception ex) {
			System.out.println("nastala chyba");
		}
		return null;
	}

	
	/**
	 * it stores graph of statements into bigdata
	 * @param graph
	 */
	public void addGraph(Graph graph) 
	{
		
		try {
			
			this.connection.add(graph);
			this.connection.commit();
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	/**
	 * in BigData 1.4 it is unimplemented function
	 * @param newNamespace
	 */
	public void addNamespace(String newNamespace) {
		
	/*	
		final Properties props = new Properties();
		props.put(BigdataSail.Options.NAMESPACE, newNamespace);
*/
		
		/*
		final RemoteRepositoryManager repo = new RemoteRepositoryManager("http://localhost:8080/bigdata/sparql");
		
		try {
			repo.initialize();
			repo.getAllRepositories();
		} catch (RepositoryConfigException | RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/
		
		/*
		ClientConnectionManager m_cm = DefaultClientConnectionManagerFactory.getInstance().newInstance();;
		final DefaultHttpClient httpClient = new DefaultHttpClient(m_cm);

		httpClient.setRedirectStrategy(new DefaultRedirectStrategy());
		
		final ExecutorService executor = Executors.newCachedThreadPool();

			//final RemoteRepositoryManager m_repo = new RemoteRepositoryManager(endpointUrl, httpClient, executor);
		com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager rrm = new com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager(endpointUrl, httpClient, executor);
		RemoteRepository rr = rrm.getRepositoryForURL(endpointUrl);
		try {
			rrm.getRepositoryDescriptions();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
	
	public void removeNamespace(String namespace) throws RepositoryException {
		repo.getConnection().removeNamespace(namespace);
	}
	
	public RepositoryResult<Namespace> getAllNamespaces() throws RepositoryException {
		
		return repo.getConnection().getNamespaces();
	}
}
