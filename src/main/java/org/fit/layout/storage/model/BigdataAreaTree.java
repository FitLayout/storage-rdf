package org.fit.layout.storage.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.fit.layout.impl.DefaultArea;
import org.fit.layout.impl.DefaultBox;
import org.fit.layout.impl.DefaultPage;
import org.fit.layout.impl.GenericTreeNode;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

public class BigdataAreaTree  implements AreaTree {

	
	private DefaultArea root;
	private int height;
	private int width;
	
	
	/**
	 * creates model from the given RDF model statements
	 * @param pageStatements page statements
	 * @param urlString original url
	 * @throws MalformedURLException
	 */
	public BigdataAreaTree(Model pageStatements, String urlString)
			throws MalformedURLException {

		super();

		List<DefaultArea> areas = getAllPageAreas(pageStatements);
		createDocumentTree(areas);
	}

	

	/**
	 * conversion all RDF statements into a list of Boxes
	 * 
	 * @param pageStatements
	 * @return list of Boxes that must be converted into a tree
	 */
	private List<DefaultArea> getAllPageAreas(Model pageStatements) {
		List<DefaultArea> allElements = new ArrayList<>();

		try {

			for (Statement s : pageStatements.filter(null, RDF.TYPE, null)) {
				Model attributes = pageStatements.filter(s.getSubject(), null,
						null);

				allElements.add(new BigdataArea(attributes));
			}
		} catch (Exception ex) {
		}

		return allElements;
	}

	// ////////////////////////////////////////////////////////////
	// PAGE building
	// ////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param model
	 * @return
	 */
	private void createDocumentTree(List<DefaultArea> elements) {

		// sorting the individuals
		List<DefaultArea> list = sortBySize(elements);

		DefaultArea root = list.get(0);

		this.setHeight(root.getHeight());
		this.setWidth(root.getWidth());

		this.setRoot(root);
		list.remove(list.get(0));

		// process inner elements in the document tree
		appendTo(root, getInnerIndividuals(root, list));
	}

	/**
	 * it sorts page's elements from higher to lower volume
	 * 
	 * @param elements
	 * @return list of individuals
	 */
	public static List<DefaultArea> sortBySize(List<DefaultArea> elements) {

		// list sorting
		Collections.sort(elements, new Comparator<DefaultArea>() {
			public int compare(DefaultArea one, DefaultArea two) {
				int width1 = one.getWidth();
				int height1 = one.getHeight();
				int width2 = two.getWidth();
				int height2 = two.getHeight();

				return (width2 * height2) - (width1 * height1);
			}
		});

		List<DefaultArea> dlb = new ArrayList<>();

		for (DefaultArea db : elements) {

			dlb.add(db);
		}

		return dlb;
		// return elements;
	}

	/**
	 * it appends individual into tree
	 * 
	 * @param appendInd
	 * @param sourceList
	 */
	private void appendTo(DefaultArea appendInd, List<DefaultArea> sourceList) {

		if (sourceList == null || sourceList.size() == 0)
			return;

		// append actual node into tree
		DefaultArea actual = (DefaultArea) sourceList.get(0);
		appendInd.add(actual);
		sourceList.remove(sourceList.get(0));

		if (sourceList.size() == 0)
			return;

		List<DefaultArea> inner = getInnerIndividuals(actual, sourceList);
		List<DefaultArea> outer = getOuterIndividuals(actual, sourceList);

		if (inner.size() > 0)
			appendTo(actual, inner);

		if (outer.size() > 0)
			appendTo(appendInd, outer);
	}

	/**
	 * it returns innner individuals from individualBuffer that are inside the
	 * individual
	 * 
	 * @return
	 */
	private List<DefaultArea> getInnerIndividuals(DefaultArea individual,
			List<DefaultArea> sourceList) {
		List<DefaultArea> listNested = new ArrayList<DefaultArea>();


		for (DefaultArea ind : sourceList) {
			if (isNested(individual, ind)) {
				listNested.add(ind);
			}
		}

		return listNested;
	}

	/**
	 * it returns individuals from individualBuffer that are not inner
	 * 
	 * @param individual
	 * @return
	 */
	private List<DefaultArea> getOuterIndividuals(DefaultArea individual,
			List<DefaultArea> sourceList) {
		List<DefaultArea> listNested = new ArrayList<DefaultArea>();

		for (DefaultArea ind : sourceList) {
			if (!isNested(individual, ind)) {
				listNested.add(ind);
			}
		}

		return listNested;
	}

	/**
	 * detects if in2 is nested in in1
	 * 
	 * @param in1
	 * @param in2
	 * @return
	 */
	private Boolean isNested(DefaultArea in1, DefaultArea in2) {

		int width1 = in1.getWidth();
		int height1 = in1.getHeight();
		int posYAbs1 = in1.getY1();
		int posXAbs1 = in1.getX1();

		int width2 = in2.getWidth();
		int height2 = in2.getHeight();
		int posYAbs2 = in2.getY1();
		int posXAbs2 = in2.getX1();

		if (posXAbs1 <= posXAbs2 && (posXAbs1 + width1) >= (posXAbs2 + width2)
				&& posYAbs1 <= posYAbs2
				&& (posYAbs1 + height1) >= (posYAbs2 + height2))
			return true;
		else
			return false;
	}
	
	@Override
	public Area getAreaAt(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Area getAreaByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultArea getRoot() {
		// TODO Auto-generated method stub
		return root;
	}
	
	public void setRoot(DefaultArea area) {
		root = area;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}



    @Override
    public void updateTopologies()
    {
        // TODO Auto-generated method stub
        System.err.println("Update topologies not implemented in BigdataAreaTree");
    }
	
}
