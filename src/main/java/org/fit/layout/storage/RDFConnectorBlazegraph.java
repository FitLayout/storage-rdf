/**
 * RDFConnectorBlazegraph.java
 *
 * Created on 15. 2. 2016, 13:02:13 by burgetr
 */
package org.fit.layout.storage;

import org.openrdf.repository.RepositoryException;

//import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

/**
 * 
 * @author burgetr
 */
public class RDFConnectorBlazegraph extends RDFConnector
{

    public RDFConnectorBlazegraph(String endpoint) throws RepositoryException
    {
        super(endpoint);
    }

    @Override
    protected void initRepository() throws RepositoryException
    {
        //TODO not supported yet
        super.initRepository();
        /*RemoteRepositoryManager mgr = new RemoteRepositoryManager(endpointUrl, true);
        repo = mgr.getRepositoryForNamespace("user").getBigdataSailRemoteRepository();
        try
        {
            mgr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    
}
