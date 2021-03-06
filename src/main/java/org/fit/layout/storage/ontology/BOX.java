package org.fit.layout.storage.ontology;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * Namespace BOX.
 * Prefix: {@code <http://fitlayout.github.io/ontology/render.owl#>}
 */
public class BOX {

	/** {@code http://fitlayout.github.io/ontology/render.owl#} **/
	public static final String NAMESPACE = "http://fitlayout.github.io/ontology/render.owl#";

	/** {@code box} **/
	public static final String PREFIX = "box";

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#backgroundColor}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#backgroundColor">backgroundColor</a>
	 */
	public static final IRI backgroundColor;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#backgroundImagePosition}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#backgroundImagePosition">backgroundImagePosition</a>
	 */
	public static final IRI backgroundImagePosition;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#backgroundImageUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#backgroundImageUrl">backgroundImageUrl</a>
	 */
	public static final IRI backgroundImageUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#belongsTo}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#belongsTo">belongsTo</a>
	 */
	public static final IRI belongsTo;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Border}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Border">Border</a>
	 */
	public static final IRI Border;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderColor}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderColor">borderColor</a>
	 */
	public static final IRI borderColor;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderStyle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderStyle">borderStyle</a>
	 */
	public static final IRI borderStyle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderWidth}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderWidth">borderWidth</a>
	 */
	public static final IRI borderWidth;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Box}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Box">Box</a>
	 */
	public static final IRI Box;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#color}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#color">color</a>
	 */
	public static final IRI color;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContainerBox}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContainerBox">ContainerBox</a>
	 */
	public static final IRI ContainerBox;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#containsImage}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#containsImage">containsImage</a>
	 */
	public static final IRI containsImage;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#containsObject}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#containsObject">containsObject</a>
	 */
	public static final IRI containsObject;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContentBox}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContentBox">ContentBox</a>
	 */
	public static final IRI ContentBox;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContentObject}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContentObject">ContentObject</a>
	 */
	public static final IRI ContentObject;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#documentOrder}.
	 * <p>
	 * The order of a rectangle within its page
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#documentOrder">documentOrder</a>
	 */
	public static final IRI documentOrder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontFamily}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontFamily">fontFamily</a>
	 */
	public static final IRI fontFamily;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontSize}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontSize">fontSize</a>
	 */
	public static final IRI fontSize;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontStyle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontStyle">fontStyle</a>
	 */
	public static final IRI fontStyle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontVariant}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontVariant">fontVariant</a>
	 */
	public static final IRI fontVariant;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontWeight}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontWeight">fontWeight</a>
	 */
	public static final IRI fontWeight;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasAttribute}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasAttribute">hasAttribute</a>
	 */
	public static final IRI hasAttribute;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasBottomBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasBottomBorder">hasBottomBorder</a>
	 */
	public static final IRI hasBottomBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasLeftBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasLeftBorder">hasLeftBorder</a>
	 */
	public static final IRI hasLeftBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasRightBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasRightBorder">hasRightBorder</a>
	 */
	public static final IRI hasRightBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasText}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasText">hasText</a>
	 */
	public static final IRI hasText;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasTitle}.
	 * <p>
	 * Page title
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasTitle">hasTitle</a>
	 */
	public static final IRI hasTitle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasTopBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasTopBorder">hasTopBorder</a>
	 */
	public static final IRI hasTopBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#height}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#height">height</a>
	 */
	public static final IRI height;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#htmlTagName}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#htmlTagName">htmlTagName</a>
	 */
	public static final IRI htmlTagName;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Image}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Image">Image</a>
	 */
	public static final IRI Image;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#imageUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#imageUrl">imageUrl</a>
	 */
	public static final IRI imageUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#isChildOf}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#isChildOf">isChildOf</a>
	 */
	public static final IRI isChildOf;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Launch}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Launch">Launch</a>
	 */
	public static final IRI Launch;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#launchDatetime}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#launchDatetime">launchDatetime</a>
	 */
	public static final IRI launchDatetime;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#lineThrough}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#lineThrough">lineThrough</a>
	 */
	public static final IRI lineThrough;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#objectInformation}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#objectInformation">objectInformation</a>
	 */
	public static final IRI objectInformation;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Page}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Page">Page</a>
	 */
	public static final IRI Page;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#positionX}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#positionX">positionX</a>
	 */
	public static final IRI positionX;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#positionY}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#positionY">positionY</a>
	 */
	public static final IRI positionY;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Rectangle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Rectangle">Rectangle</a>
	 */
	public static final IRI Rectangle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#sourceUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#sourceUrl">sourceUrl</a>
	 */
	public static final IRI sourceUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#underline}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#underline">underline</a>
	 */
	public static final IRI underline;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualHeight}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualHeight">visualHeight</a>
	 */
	public static final IRI visualHeight;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualWidth}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualWidth">visualWidth</a>
	 */
	public static final IRI visualWidth;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualX}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualX">visualX</a>
	 */
	public static final IRI visualX;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualY}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualY">visualY</a>
	 */
	public static final IRI visualY;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#width}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#width">width</a>
	 */
	public static final IRI width;

	static {
		ValueFactory factory = SimpleValueFactory.getInstance();

		backgroundColor = factory.createIRI(BOX.NAMESPACE, "backgroundColor");
		backgroundImagePosition = factory.createIRI(BOX.NAMESPACE, "backgroundImagePosition");
		backgroundImageUrl = factory.createIRI(BOX.NAMESPACE, "backgroundImageUrl");
		belongsTo = factory.createIRI(BOX.NAMESPACE, "belongsTo");
		Border = factory.createIRI(BOX.NAMESPACE, "Border");
		borderColor = factory.createIRI(BOX.NAMESPACE, "borderColor");
		borderStyle = factory.createIRI(BOX.NAMESPACE, "borderStyle");
		borderWidth = factory.createIRI(BOX.NAMESPACE, "borderWidth");
		Box = factory.createIRI(BOX.NAMESPACE, "Box");
		color = factory.createIRI(BOX.NAMESPACE, "color");
		ContainerBox = factory.createIRI(BOX.NAMESPACE, "ContainerBox");
		containsImage = factory.createIRI(BOX.NAMESPACE, "containsImage");
		containsObject = factory.createIRI(BOX.NAMESPACE, "containsObject");
		ContentBox = factory.createIRI(BOX.NAMESPACE, "ContentBox");
		ContentObject = factory.createIRI(BOX.NAMESPACE, "ContentObject");
		documentOrder = factory.createIRI(BOX.NAMESPACE, "documentOrder");
		fontFamily = factory.createIRI(BOX.NAMESPACE, "fontFamily");
		fontSize = factory.createIRI(BOX.NAMESPACE, "fontSize");
		fontStyle = factory.createIRI(BOX.NAMESPACE, "fontStyle");
		fontVariant = factory.createIRI(BOX.NAMESPACE, "fontVariant");
		fontWeight = factory.createIRI(BOX.NAMESPACE, "fontWeight");
		hasAttribute = factory.createIRI(BOX.NAMESPACE, "hasAttribute");
		hasBottomBorder = factory.createIRI(BOX.NAMESPACE, "hasBottomBorder");
		hasLeftBorder = factory.createIRI(BOX.NAMESPACE, "hasLeftBorder");
		hasRightBorder = factory.createIRI(BOX.NAMESPACE, "hasRightBorder");
		hasText = factory.createIRI(BOX.NAMESPACE, "hasText");
		hasTitle = factory.createIRI(BOX.NAMESPACE, "hasTitle");
		hasTopBorder = factory.createIRI(BOX.NAMESPACE, "hasTopBorder");
		height = factory.createIRI(BOX.NAMESPACE, "height");
		htmlTagName = factory.createIRI(BOX.NAMESPACE, "htmlTagName");
		Image = factory.createIRI(BOX.NAMESPACE, "Image");
		imageUrl = factory.createIRI(BOX.NAMESPACE, "imageUrl");
		isChildOf = factory.createIRI(BOX.NAMESPACE, "isChildOf");
		Launch = factory.createIRI(BOX.NAMESPACE, "Launch");
		launchDatetime = factory.createIRI(BOX.NAMESPACE, "launchDatetime");
		lineThrough = factory.createIRI(BOX.NAMESPACE, "lineThrough");
		objectInformation = factory.createIRI(BOX.NAMESPACE, "objectInformation");
		Page = factory.createIRI(BOX.NAMESPACE, "Page");
		positionX = factory.createIRI(BOX.NAMESPACE, "positionX");
		positionY = factory.createIRI(BOX.NAMESPACE, "positionY");
		Rectangle = factory.createIRI(BOX.NAMESPACE, "Rectangle");
		sourceUrl = factory.createIRI(BOX.NAMESPACE, "sourceUrl");
		underline = factory.createIRI(BOX.NAMESPACE, "underline");
		visualHeight = factory.createIRI(BOX.NAMESPACE, "visualHeight");
		visualWidth = factory.createIRI(BOX.NAMESPACE, "visualWidth");
		visualX = factory.createIRI(BOX.NAMESPACE, "visualX");
		visualY = factory.createIRI(BOX.NAMESPACE, "visualY");
		width = factory.createIRI(BOX.NAMESPACE, "width");
	}

	private BOX() {
		//static access only
	}

}
