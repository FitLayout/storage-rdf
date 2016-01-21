package org.fit.layout.storage;

import java.util.Date;

import org.fit.layout.storage.ontology.BOX;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;


/**
 * Class represents a specific launch info
 * 
 * @author milicka
 *
 */
public class PageInfo {

	private Date date;
	private URI id;
	private String url;
	private String title;
	
	
    public PageInfo(Model model) 
    {
        for (Statement st : model) 
        {
            if (st.getSubject() instanceof URI)
            {
                id = (URI) st.getSubject();
                if (st.getPredicate().equals(BOX.sourceUrl)) {
                    url = st.getObject().stringValue();
                } else if (st.getPredicate().equals(BOX.launchDatetime)) {
                    Value val = st.getObject();
                    if (val instanceof Literal)
                        date = ((Literal) val).calendarValue().toGregorianCalendar().getTime();
                } else if (st.getPredicate().equals(BOX.hasTitle)) {
                    title = st.getObject().stringValue();
                }
            }
        }
        
    }
    
	public URI getId() {
		return id;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
	    return title;
	}
	
}
