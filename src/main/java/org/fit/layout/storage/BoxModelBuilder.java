package org.fit.layout.storage;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.fit.layout.model.Box;
import org.fit.layout.model.Box.Type;
import org.fit.layout.model.Page;
import org.fit.layout.model.Rectangular;
import org.fit.layout.storage.ontology.BOX;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * Class creates RDF graph model from the given Page model
 * 
 * @author milicka
 * 
 */
public class BoxModelBuilder {

	private Graph graph;
	private String baseUrl;
	private ValueFactoryImpl vf;
	private String launchID;
	private URI pageNode;

	public BoxModelBuilder(Page page) {

		baseUrl = page.getSourceURL().toString();
		
		initializeGraph();

		Box bdb = page.getRoot();
		this.insertBox(bdb);

		insertAllBoxes(bdb);
	}
	
	/*
	 * Initializes graph model
	 * 
	 * @param url defines page url for the identification
	 * 
	 * @return launch node for the element linking
	 */
	private URI initializeGraph() {

		graph = new LinkedHashModel(); // it holds whole model
		vf = ValueFactoryImpl.getInstance();
		
		String dateTime = getDateTime();
		launchID = getLaunchIdFromDatetime(dateTime);
		
		// inicialization with launch node
		pageNode = vf.createURI(baseUrl + "#" + this.launchID);
		graph.add(pageNode, RDF.TYPE, BOX.Page);
		graph.add(pageNode,	BOX.launchDatetime,	vf.createLiteral(dateTime));
		graph.add(pageNode, BOX.sourceUrl, vf.createLiteral(baseUrl));

		return this.pageNode;
	}

	/**
	 * It generates unique identifier for database storing
	 * 
	 * @return
	 */
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}

	/**
	 * it creates unique identification for the current launch
	 * 
	 * @param dateTime
	 * @return
	 */
	private String getLaunchIdFromDatetime(String dateTime) {
		dateTime = dateTime.replace(":", "");
		dateTime = dateTime.replace("-", "");
		dateTime = dateTime.replace(" ", "");

		return dateTime;
	}

	/**
	 * it goes through page model and appends information in triples into graph
	 * model
	 * 
	 * @param root
	 */
	private void insertAllBoxes(Box parent) {

		// in case of element with children
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);
			insertBox(sub1);
		}

		// if there are some children
		for (int i = 0; i < parent.getChildCount(); i++) {
			Box sub1 = parent.getChildBox(i);
			insertAllBoxes(sub1);
		}
	}

	/**
	 * it appends particular box into graph model
	 * 
	 * @param box
	 */
	private void insertBox(Box box) {

		// add BOX individual into graph
		final URI individual = getBoxUri(box);
		graph.add(individual, RDF.TYPE, BOX.Box);

		// pin to launch node
		graph.add(individual, BOX.belongsTo, pageNode);
		
		//parent
		if (box.getParentBox() != null)
		    graph.add(individual, BOX.isChildOf, getBoxUri(box.getParentBox()));

		// store position and size of element
		// Rectangular rec = box.getContentBounds();
		Rectangular rec = box.getBounds();
		graph.add(individual, BOX.height, vf.createLiteral(rec.getHeight()));
		graph.add(individual, BOX.width, vf.createLiteral(rec.getWidth()));
		graph.add(individual, BOX.positionX, vf.createLiteral(rec.getX1()));
		graph.add(individual, BOX.positionY, vf.createLiteral(rec.getY1()));

		if (box.getBackgroundColor() != null)
		{
    		final String bgcol = String.format("#%02x%02x%02x", 
    		        box.getBackgroundColor().getRed(),
    		        box.getBackgroundColor().getGreen(),
    		        box.getBackgroundColor().getBlue());
            graph.add(individual, BOX.backgroundColor, vf.createLiteral(bgcol));
		}

		// add text content into element
		if (box.getType() == Type.TEXT_CONTENT) {
			graph.add(individual, BOX.hasText, vf.createLiteral(box.getText()));
		}
		// font attributes
		graph.add(individual, BOX.fontFamily, vf.createLiteral(box.getFontFamily()));
		graph.add(individual, BOX.fontSize, vf.createLiteral(box.getFontSize()));
		graph.add(individual, BOX.fontWeight, vf.createLiteral(box.getFontWeight()));
		graph.add(individual, BOX.fontStyle, vf.createLiteral(box.getFontStyle()));
        graph.add(individual, BOX.underline, vf.createLiteral(box.getUnderline()));
        graph.add(individual, BOX.lineThrough, vf.createLiteral(box.getLineThrough()));
		
        final String col = String.format("#%02x%02x%02x", 
                box.getColor().getRed(),
                box.getColor().getGreen(),
                box.getColor().getBlue());
        graph.add(individual, BOX.color, vf.createLiteral(col));

	}

	public Graph getGraph() {
		return graph;
	}

	public URI getLaunchNode() {
		return pageNode;
	} 

	public URI getBoxUri(Box box) {
	    return vf.createURI(baseUrl + "#" + launchID + "-" + box.getId());
	}
	
}
