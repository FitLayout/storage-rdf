package org.fit.layout.storage;

import java.awt.Color;

import org.fit.layout.model.Border;
import org.fit.layout.model.Border.Side;
import org.fit.layout.model.Box;
import org.fit.layout.model.Box.Type;
import org.fit.layout.model.Page;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.RESOURCE;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * Implements an RDF graph construction from a page box model. 
 * 
 * @author milicka
 * @author burgetr 
 */
public class BoxModelBuilder 
{
	private Graph graph;
	private Page page;
	private String baseUrl;
	private ValueFactoryImpl vf;
	private URI pageNode;
	
	private int next_order; //order counter

	public BoxModelBuilder(Page page, URI uri) 
	{
	    this.page = page;
		baseUrl = page.getSourceURL().toString();
		pageNode = uri;
		initializeGraph();
		Box root = page.getRoot();
		insertBox(root);
		insertChildBoxes(root);
	}
	
	/**
	 * Initializes the graph model
	 * 
	 * @return launch node for the element linking
	 */
	private URI initializeGraph() 
	{
		graph = new LinkedHashModel(); // it holds whole model
		vf = ValueFactoryImpl.getInstance();
		next_order = 0;
		
		// inicialization with launch node
		graph.add(pageNode, RDF.TYPE, BOX.Page);
		graph.add(pageNode,	BOX.launchDatetime,	vf.createLiteral(new java.util.Date()));
		graph.add(pageNode, BOX.sourceUrl, vf.createLiteral(baseUrl));
		if (page.getTitle() != null)
		    graph.add(pageNode, BOX.hasTitle, vf.createLiteral(page.getTitle()));

		return this.pageNode;
	}

	/**
	 * Recursively inserts the child boxes of a root box into the grapgh.
	 * @param root
	 */
	private void insertChildBoxes(Box root) 
	{
		// insert the boxes themselves
		for (int i = 0; i < root.getChildCount(); i++)
			insertBox(root.getChildBox(i));
		// recursively insert children
		for (int i = 0; i < root.getChildCount(); i++)
			insertChildBoxes(root.getChildBox(i));
	}

	/**
	 * Appends a single box into graph model.
	 * @param box
	 */
	private void insertBox(Box box) 
	{
		// add BOX individual into graph
		final URI individual = RESOURCE.createBoxURI(pageNode, box);
		graph.add(individual, RDF.TYPE, BOX.Box);
		graph.add(individual, BOX.documentOrder, vf.createLiteral(next_order++));

		// pin to page node
		graph.add(individual, BOX.belongsTo, pageNode);
		
		//parent
		if (box.getParentBox() != null)
		    graph.add(individual, BOX.isChildOf, RESOURCE.createBoxURI(pageNode, box.getParentBox()));

		//tag properties
		if (box.getTagName() != null)
		    graph.add(individual, BOX.htmlTagName, vf.createLiteral(box.getTagName()));
		
		// store position and size of element
		Rectangular content = box.getContentBounds();
		graph.add(individual, BOX.height, vf.createLiteral(content.getHeight()));
		graph.add(individual, BOX.width, vf.createLiteral(content.getWidth()));
		graph.add(individual, BOX.positionX, vf.createLiteral(content.getX1()));
		graph.add(individual, BOX.positionY, vf.createLiteral(content.getY1()));
        Rectangular visual = box.getVisualBounds();
        graph.add(individual, BOX.visualHeight, vf.createLiteral(visual.getHeight()));
        graph.add(individual, BOX.visualWidth, vf.createLiteral(visual.getWidth()));
        graph.add(individual, BOX.visualX, vf.createLiteral(visual.getX1()));
        graph.add(individual, BOX.visualY, vf.createLiteral(visual.getY1()));

		if (box.getBackgroundColor() != null)
		{
            graph.add(individual, BOX.backgroundColor, vf.createLiteral(colorString(box.getBackgroundColor())));
		}

		// add text content into element
		if (box.getType() == Type.TEXT_CONTENT) 
		{
			graph.add(individual, BOX.hasText, vf.createLiteral(box.getText()));
		}
		// font attributes
		graph.add(individual, BOX.fontFamily, vf.createLiteral(box.getFontFamily()));
		graph.add(individual, BOX.fontSize, vf.createLiteral(box.getFontSize()));
		graph.add(individual, BOX.fontWeight, vf.createLiteral(box.getFontWeight()));
		graph.add(individual, BOX.fontStyle, vf.createLiteral(box.getFontStyle()));
        graph.add(individual, BOX.underline, vf.createLiteral(box.getUnderline()));
        graph.add(individual, BOX.lineThrough, vf.createLiteral(box.getLineThrough()));
        graph.add(individual, BOX.color, vf.createLiteral(colorString(box.getColor())));
        
        if (box.getBorderStyle(Side.TOP) != null && box.hasTopBorder())
        {
            URI btop = insertBorder(box.getBorderStyle(Side.TOP), individual, "top");
            graph.add(individual, BOX.hasTopBorder, btop);
        }
        if (box.getBorderStyle(Side.RIGHT) != null && box.hasRightBorder())
        {
            URI bright = insertBorder(box.getBorderStyle(Side.RIGHT), individual, "right");
            graph.add(individual, BOX.hasRightBorder, bright);
        }
        if (box.getBorderStyle(Side.BOTTOM) != null && box.hasBottomBorder())
        {
            URI bbottom = insertBorder(box.getBorderStyle(Side.BOTTOM), individual, "bottom");
            graph.add(individual, BOX.hasBottomBorder, bbottom);
        }
        if (box.getBorderStyle(Side.LEFT) != null && box.hasLeftBorder())
        {
            URI bleft = insertBorder(box.getBorderStyle(Side.LEFT), individual, "left");
            graph.add(individual, BOX.hasLeftBorder, bleft);
        }

	}
	
	private URI insertBorder(Border border, URI boxUri, String side)
	{
	    URI uri = RESOURCE.createBorderURI(boxUri, side);
	    graph.add(uri, RDF.TYPE, BOX.Border);
	    graph.add(uri, BOX.borderWidth, vf.createLiteral(border.getWidth()));
	    graph.add(uri, BOX.borderStyle, vf.createLiteral(border.getStyle().toString()));
        graph.add(uri, BOX.borderColor, vf.createLiteral(colorString(border.getColor())));
	    return uri;
	}

	public Graph getGraph() 
	{
		return graph;
	}

	public URI getLaunchNode()
	{
		return pageNode;
	} 

	private String colorString(Color color)
	{
	    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
	
}
