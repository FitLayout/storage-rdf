package org.fit.layout.storage.ontology;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

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
	public static final URI containsPage;

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#hasName}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#hasName">hasName</a>
	 */
	public static final URI hasName;

	/**
	 * {@code http://fitlayout.github.io/ontology/fitlayout.owl#PageSet}.
	 * <p>
	 * A set of pages processed together.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/fitlayout.owl#PageSet">PageSet</a>
	 */
	public static final URI PageSet;

	static {
		ValueFactory factory = ValueFactoryImpl.getInstance();

		containsPage = factory.createURI(LAYOUT.NAMESPACE, "containsPage");
		hasName = factory.createURI(LAYOUT.NAMESPACE, "hasName");
		PageSet = factory.createURI(LAYOUT.NAMESPACE, "PageSet");
	}

	private LAYOUT() {
		//static access only
	}

}
