/**
 * RDFConnectorSesame.java
 *
 * Created on 9. 1. 2016, 13:17:59 by burgetr
 */
package org.fit.layout.storage;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A RDF connector optimized for the Sesame remote server.
 * @author burgetr
 */
public class RDFConnectorSesame extends RDFConnector
{

    public RDFConnectorSesame(String endpoint) throws RepositoryException
    {
        super(endpoint);
    }

    @Override
    protected void createConnection() throws RepositoryException
    {
        repo = new HTTPRepository("http://localhost:8080/openrdf-sesame", "user");
        repo.initialize();
        connection = repo.getConnection();
    }

}
