package org.fit.layout.storage;

import org.fit.layout.storage.ontology.BOX;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;


/**
 * Class represents a specific launch info
 * 
 * @author milicka
 *
 */
public class PageInfo {

	private String date;
	private URI id;
	private String url;
	
	
	public PageInfo(GraphQueryResult statements) throws QueryEvaluationException {
		
		
		while(statements.hasNext()) {
			
		    Statement st = statements.next();
			
			if (st.getSubject() instanceof URI)
			{
    			id = (URI) st.getSubject();
    			
    			if (st.getPredicate().equals(BOX.sourceUrl)) {
    			    url = st.getObject().stringValue();
    			} else if (st.getPredicate().equals(BOX.launchDatetime)) {
					date = st.getObject().stringValue();
    			}
			}
		}
		
	}
	
	public URI getId() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getUrl() {
		return url;
	}
	
}
