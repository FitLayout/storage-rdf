package org.fit.layout.storage.ontology;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Namespace SEGM.
 * Prefix: {@code <http://fitlayout.github.io/ontology/segmentation.owl#>}
 */
public class SEGM {

	/** {@code http://fitlayout.github.io/ontology/segmentation.owl#} **/
	public static final String NAMESPACE = "http://fitlayout.github.io/ontology/segmentation.owl#";

	/** {@code segm} **/
	public static final String PREFIX = "segm";

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#Area}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#Area">Area</a>
	 */
	public static final URI Area;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#AreaTree}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#AreaTree">AreaTree</a>
	 */
	public static final URI AreaTree;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#belongsTo}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#belongsTo">belongsTo</a>
	 */
	public static final URI belongsTo;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#contains}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#contains">contains</a>
	 */
	public static final URI contains;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#hasName}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#hasName">hasName</a>
	 */
	public static final URI hasName;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#hasTag}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#hasTag">hasTag</a>
	 */
	public static final URI hasTag;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#hasText}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#hasText">hasText</a>
	 */
	public static final URI hasText;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#hasType}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#hasType">hasType</a>
	 */
	public static final URI hasType;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#isChildOf}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#isChildOf">isChildOf</a>
	 */
	public static final URI isChildOf;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#isSubordinateTo}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#isSubordinateTo">isSubordinateTo</a>
	 */
	public static final URI isSubordinateTo;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#LogicalArea}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#LogicalArea">LogicalArea</a>
	 */
	public static final URI LogicalArea;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#sourcePage}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#sourcePage">sourcePage</a>
	 */
	public static final URI sourcePage;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#support}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#support">support</a>
	 */
	public static final URI support;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#Tag}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#Tag">Tag</a>
	 */
	public static final URI Tag;

	/**
	 * {@code http://fitlayout.github.io/ontology/segmentation.owl#tagSupport}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/segmentation.owl#tagSupport">tagSupport</a>
	 */
	public static final URI tagSupport;

	static {
		ValueFactory factory = ValueFactoryImpl.getInstance();

		Area = factory.createURI(SEGM.NAMESPACE, "Area");
		AreaTree = factory.createURI(SEGM.NAMESPACE, "AreaTree");
		belongsTo = factory.createURI(SEGM.NAMESPACE, "belongsTo");
		contains = factory.createURI(SEGM.NAMESPACE, "contains");
		hasName = factory.createURI(SEGM.NAMESPACE, "hasName");
		hasTag = factory.createURI(SEGM.NAMESPACE, "hasTag");
		hasText = factory.createURI(SEGM.NAMESPACE, "hasText");
		hasType = factory.createURI(SEGM.NAMESPACE, "hasType");
		isChildOf = factory.createURI(SEGM.NAMESPACE, "isChildOf");
		isSubordinateTo = factory.createURI(SEGM.NAMESPACE, "isSubordinateTo");
		LogicalArea = factory.createURI(SEGM.NAMESPACE, "LogicalArea");
		sourcePage = factory.createURI(SEGM.NAMESPACE, "sourcePage");
		support = factory.createURI(SEGM.NAMESPACE, "support");
		Tag = factory.createURI(SEGM.NAMESPACE, "Tag");
		tagSupport = factory.createURI(SEGM.NAMESPACE, "tagSupport");
	}

	private SEGM() {
		//static access only
	}

}
