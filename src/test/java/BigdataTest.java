 
 
import org.fit.layout.storage.RDFStorage;
import org.junit.Test; 
import org.eclipse.rdf4j.repository.RepositoryException;
 
/**  
 *  
 */ 
public class BigdataTest { 
 
	
	public BigdataTest() {
		
	}
	
	
	/**
	 * general valid launch
	 * @throws RepositoryException 
	 */
    @Test
    public void correctRun() { 
    	
			try {
				RDFStorage bdi = new RDFStorage("http://localhost:8080/bigdata/sparql");		
				
				System.out.println("no problem");
			} catch (RepositoryException e) {
				System.out.println("problem");
			}
			System.out.println("toto je jasne");
    } 
  
    
    /**
	 * general valid launch
	 * @throws RepositoryException 
	 */
    //@Test
    public void correctRun2() { 
    	
			try {
                RDFStorage bdi = new RDFStorage("http://localhost:8080/bigdata/sparql");        
				
				System.out.println("no problem");
			} catch (RepositoryException e) {
				System.out.println("problem");
			}
			System.out.println("toto je jasne");
    } 
    
} 