package org.fit.layout.storage.ontology;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

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
	public static final URI backgroundColor;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#backgroundImagePosition}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#backgroundImagePosition">backgroundImagePosition</a>
	 */
	public static final URI backgroundImagePosition;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#backgroundImageUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#backgroundImageUrl">backgroundImageUrl</a>
	 */
	public static final URI backgroundImageUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#belongsTo}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#belongsTo">belongsTo</a>
	 */
	public static final URI belongsTo;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Border}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Border">Border</a>
	 */
	public static final URI Border;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderColor}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderColor">borderColor</a>
	 */
	public static final URI borderColor;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderStyle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderStyle">borderStyle</a>
	 */
	public static final URI borderStyle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#borderWidth}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#borderWidth">borderWidth</a>
	 */
	public static final URI borderWidth;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Box}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Box">Box</a>
	 */
	public static final URI Box;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#color}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#color">color</a>
	 */
	public static final URI color;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContainerBox}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContainerBox">ContainerBox</a>
	 */
	public static final URI ContainerBox;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#containsImage}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#containsImage">containsImage</a>
	 */
	public static final URI containsImage;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#containsObject}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#containsObject">containsObject</a>
	 */
	public static final URI containsObject;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContentBox}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContentBox">ContentBox</a>
	 */
	public static final URI ContentBox;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#ContentObject}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#ContentObject">ContentObject</a>
	 */
	public static final URI ContentObject;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#documentOrder}.
	 * <p>
	 * The order of a rectangle within its page
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#documentOrder">documentOrder</a>
	 */
	public static final URI documentOrder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontFamily}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontFamily">fontFamily</a>
	 */
	public static final URI fontFamily;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontSize}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontSize">fontSize</a>
	 */
	public static final URI fontSize;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontStyle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontStyle">fontStyle</a>
	 */
	public static final URI fontStyle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontVariant}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontVariant">fontVariant</a>
	 */
	public static final URI fontVariant;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#fontWeight}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#fontWeight">fontWeight</a>
	 */
	public static final URI fontWeight;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasAttribute}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasAttribute">hasAttribute</a>
	 */
	public static final URI hasAttribute;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasBottomBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasBottomBorder">hasBottomBorder</a>
	 */
	public static final URI hasBottomBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasLeftBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasLeftBorder">hasLeftBorder</a>
	 */
	public static final URI hasLeftBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasRightBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasRightBorder">hasRightBorder</a>
	 */
	public static final URI hasRightBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasText}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasText">hasText</a>
	 */
	public static final URI hasText;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasTitle}.
	 * <p>
	 * Page title
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasTitle">hasTitle</a>
	 */
	public static final URI hasTitle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#hasTopBorder}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#hasTopBorder">hasTopBorder</a>
	 */
	public static final URI hasTopBorder;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#height}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#height">height</a>
	 */
	public static final URI height;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#htmlTagName}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#htmlTagName">htmlTagName</a>
	 */
	public static final URI htmlTagName;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Image}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Image">Image</a>
	 */
	public static final URI Image;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#imageUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#imageUrl">imageUrl</a>
	 */
	public static final URI imageUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#isChildOf}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#isChildOf">isChildOf</a>
	 */
	public static final URI isChildOf;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Launch}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Launch">Launch</a>
	 */
	public static final URI Launch;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#launchDatetime}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#launchDatetime">launchDatetime</a>
	 */
	public static final URI launchDatetime;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#lineThrough}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#lineThrough">lineThrough</a>
	 */
	public static final URI lineThrough;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#objectInformation}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#objectInformation">objectInformation</a>
	 */
	public static final URI objectInformation;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Page}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Page">Page</a>
	 */
	public static final URI Page;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#positionX}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#positionX">positionX</a>
	 */
	public static final URI positionX;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#positionY}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#positionY">positionY</a>
	 */
	public static final URI positionY;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#Rectangle}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#Rectangle">Rectangle</a>
	 */
	public static final URI Rectangle;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#sourceUrl}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#sourceUrl">sourceUrl</a>
	 */
	public static final URI sourceUrl;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#underline}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#underline">underline</a>
	 */
	public static final URI underline;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualHeight}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualHeight">visualHeight</a>
	 */
	public static final URI visualHeight;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualWidth}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualWidth">visualWidth</a>
	 */
	public static final URI visualWidth;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualX}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualX">visualX</a>
	 */
	public static final URI visualX;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#visualY}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#visualY">visualY</a>
	 */
	public static final URI visualY;

	/**
	 * {@code http://fitlayout.github.io/ontology/render.owl#width}.
	 *
	 * @see <a href="http://fitlayout.github.io/ontology/render.owl#width">width</a>
	 */
	public static final URI width;

	static {
		ValueFactory factory = ValueFactoryImpl.getInstance();

		backgroundColor = factory.createURI(BOX.NAMESPACE, "backgroundColor");
		backgroundImagePosition = factory.createURI(BOX.NAMESPACE, "backgroundImagePosition");
		backgroundImageUrl = factory.createURI(BOX.NAMESPACE, "backgroundImageUrl");
		belongsTo = factory.createURI(BOX.NAMESPACE, "belongsTo");
		Border = factory.createURI(BOX.NAMESPACE, "Border");
		borderColor = factory.createURI(BOX.NAMESPACE, "borderColor");
		borderStyle = factory.createURI(BOX.NAMESPACE, "borderStyle");
		borderWidth = factory.createURI(BOX.NAMESPACE, "borderWidth");
		Box = factory.createURI(BOX.NAMESPACE, "Box");
		color = factory.createURI(BOX.NAMESPACE, "color");
		ContainerBox = factory.createURI(BOX.NAMESPACE, "ContainerBox");
		containsImage = factory.createURI(BOX.NAMESPACE, "containsImage");
		containsObject = factory.createURI(BOX.NAMESPACE, "containsObject");
		ContentBox = factory.createURI(BOX.NAMESPACE, "ContentBox");
		ContentObject = factory.createURI(BOX.NAMESPACE, "ContentObject");
		documentOrder = factory.createURI(BOX.NAMESPACE, "documentOrder");
		fontFamily = factory.createURI(BOX.NAMESPACE, "fontFamily");
		fontSize = factory.createURI(BOX.NAMESPACE, "fontSize");
		fontStyle = factory.createURI(BOX.NAMESPACE, "fontStyle");
		fontVariant = factory.createURI(BOX.NAMESPACE, "fontVariant");
		fontWeight = factory.createURI(BOX.NAMESPACE, "fontWeight");
		hasAttribute = factory.createURI(BOX.NAMESPACE, "hasAttribute");
		hasBottomBorder = factory.createURI(BOX.NAMESPACE, "hasBottomBorder");
		hasLeftBorder = factory.createURI(BOX.NAMESPACE, "hasLeftBorder");
		hasRightBorder = factory.createURI(BOX.NAMESPACE, "hasRightBorder");
		hasText = factory.createURI(BOX.NAMESPACE, "hasText");
		hasTitle = factory.createURI(BOX.NAMESPACE, "hasTitle");
		hasTopBorder = factory.createURI(BOX.NAMESPACE, "hasTopBorder");
		height = factory.createURI(BOX.NAMESPACE, "height");
		htmlTagName = factory.createURI(BOX.NAMESPACE, "htmlTagName");
		Image = factory.createURI(BOX.NAMESPACE, "Image");
		imageUrl = factory.createURI(BOX.NAMESPACE, "imageUrl");
		isChildOf = factory.createURI(BOX.NAMESPACE, "isChildOf");
		Launch = factory.createURI(BOX.NAMESPACE, "Launch");
		launchDatetime = factory.createURI(BOX.NAMESPACE, "launchDatetime");
		lineThrough = factory.createURI(BOX.NAMESPACE, "lineThrough");
		objectInformation = factory.createURI(BOX.NAMESPACE, "objectInformation");
		Page = factory.createURI(BOX.NAMESPACE, "Page");
		positionX = factory.createURI(BOX.NAMESPACE, "positionX");
		positionY = factory.createURI(BOX.NAMESPACE, "positionY");
		Rectangle = factory.createURI(BOX.NAMESPACE, "Rectangle");
		sourceUrl = factory.createURI(BOX.NAMESPACE, "sourceUrl");
		underline = factory.createURI(BOX.NAMESPACE, "underline");
		visualHeight = factory.createURI(BOX.NAMESPACE, "visualHeight");
		visualWidth = factory.createURI(BOX.NAMESPACE, "visualWidth");
		visualX = factory.createURI(BOX.NAMESPACE, "visualX");
		visualY = factory.createURI(BOX.NAMESPACE, "visualY");
		width = factory.createURI(BOX.NAMESPACE, "width");
	}

	private BOX() {
		//static access only
	}

}
