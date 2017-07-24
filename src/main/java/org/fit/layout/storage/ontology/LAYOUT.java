package org.fit.layout.storage.ontology;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * FITLayout system types and properties.
 * <p>
 * Namespace LAYOUT.
 * Prefix: {@code <http://fitlayout.github.io/ontology/fitlayout.owl#>}
 */
public class LAYOUT {

	/** {@code http://fitlayout.github.io/ontology/fitlayout.owl#} **/
	public static final String NAMESPACE = "http://fitlayout.github.io/ontology/fitlayout.owl#";

	/** {@code layout} **/
	public static final String PREFIX = "layout";

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#containsPage}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#containsPage">containsPage</a>
	 */
	public static final IRI containsPage;

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#createdOn}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#createdOn">createdOn</a>
	 */
	public static final IRI createdOn;

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#hasName}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#hasName">hasName</a>
	 */
	public static final IRI hasName;

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#PageSet}.
	 * <p>
	 * A set of pages processed together.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#PageSet">PageSet</a>
	 */
	public static final IRI PageSet;

	static {
		ValueFactory factory = SimpleValueFactory.getInstance();

		containsPage = factory.createIRI(LAYOUT.NAMESPACE, "containsPage");
		createdOn = factory.createIRI(LAYOUT.NAMESPACE, "createdOn");
		hasName = factory.createIRI(LAYOUT.NAMESPACE, "hasName");
		PageSet = factory.createIRI(LAYOUT.NAMESPACE, "PageSet");
	}

	private LAYOUT() {
		//static access only
	}

}
