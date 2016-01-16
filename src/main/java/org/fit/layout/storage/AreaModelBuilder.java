package org.fit.layout.storage;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalArea;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Rectangular;
import org.fit.layout.model.Tag;
import org.fit.layout.storage.ontology.BOX;
import org.fit.layout.storage.ontology.RESOURCE;
import org.fit.layout.storage.ontology.SEGM;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * Implements an RDF graph construction from an area tree. 
 * 
 * @author milicka
 * @author burgetr 
 */
public class AreaModelBuilder
{
	private Graph graph = null;
	private ValueFactoryImpl vf;
	private URI areaTreeNode;
	private int logAreaCnt;

	public AreaModelBuilder(AreaTree areaTree, LogicalAreaTree logicalTree, URI pageNode, URI uri)
	{
		graph = new LinkedHashModel();
		vf = ValueFactoryImpl.getInstance();
		areaTreeNode = uri;
		createAreaTreeModel(pageNode, areaTree, logicalTree);
	}

	public Graph getGraph()
	{
		return graph;
	}
	
	//=========================================================================
	
	private void createAreaTreeModel(URI pageNode, AreaTree areaTree, LogicalAreaTree logicalTree) 
	{
		graph.add(areaTreeNode, RDF.TYPE, SEGM.AreaTree);
		graph.add(areaTreeNode, SEGM.sourcePage, pageNode);
		
		addArea(areaTree.getRoot());
		insertAllAreas(areaTree.getRoot().getChildAreas());
		
		if (logicalTree != null)
		{
    		URI lroot = addLogicalArea(logicalTree.getRoot(), null);
    		insertAllLogicalAreas(logicalTree.getRoot().getChildAreas(), lroot);
		}
	}

	/**
	 * Adds a list of areas to the model 
	 * @param areas
	 */
	private void insertAllAreas(List<Area> areas) 
	{
		for(Area area : areas) 
		{
			addArea(area);
			insertAllAreas(area.getChildAreas());
		}
	}

    /**
     * Adds a list of logical areas to the model 
     * @param areas
     * @param parent
     */
    private void insertAllLogicalAreas(List<LogicalArea> areas, URI parent) 
    {
        for (LogicalArea area : areas) 
        {
            URI p = addLogicalArea(area, parent);
            insertAllLogicalAreas(area.getChildAreas(), p);
        }
    }

	/**
	 * Adds a single area and all its properties to the model.
	 * @param area
	 */
	private void addArea(Area area) 
	{
		final URI individual = getAreaUri(area);
		graph.add(individual, RDF.TYPE, SEGM.Area);
        graph.add(individual, SEGM.belongsTo, this.areaTreeNode);

        if (area.getParentArea() != null)
            graph.add(individual, SEGM.isChildOf, getAreaUri(area.getParentArea()));
        
		// appends geometry
		Rectangular rec = area.getBounds();
		graph.add(individual, BOX.height, vf.createLiteral(rec.getHeight()));
		graph.add(individual, BOX.width, vf.createLiteral(rec.getWidth()));
		graph.add(individual, BOX.positionX, vf.createLiteral(rec.getX1()));
		graph.add(individual, BOX.positionY, vf.createLiteral(rec.getY1()));

		// appends tags
		if (area.getTags().size() > 0) 
		{
			Map<Tag, Float> tags = area.getTags();
			for (Tag t : tags.keySet()) 
			{
				Float support = tags.get(t);
				if (support != null && support > 0.0f)
				{
				    final URI tagUri = getTagUri(t);
				    graph.add(individual, SEGM.hasTag, tagUri);
				    final URI supUri = getTagSupportUri(area, t);
				    graph.add(individual, SEGM.tagSupport, supUri);
				    graph.add(supUri, SEGM.support, vf.createLiteral(support));
				    graph.add(supUri, SEGM.hasTag, tagUri);
				}
			}
		}
		
        if (area.getBackgroundColor() != null)
        {
            graph.add(individual, BOX.backgroundColor, vf.createLiteral(colorString(area.getBackgroundColor())));
        }

        // font attributes
        graph.add(individual, BOX.fontSize, vf.createLiteral(area.getFontSize()));
        graph.add(individual, BOX.fontWeight, vf.createLiteral(area.getFontWeight()));
        graph.add(individual, BOX.fontStyle, vf.createLiteral(area.getFontStyle()));
        graph.add(individual, BOX.underline, vf.createLiteral(area.getUnderline()));
        graph.add(individual, BOX.lineThrough, vf.createLiteral(area.getLineThrough()));
	}

    private URI addLogicalArea(LogicalArea area, URI parent) 
    {
        final URI individual = getLogicalAreaUri(logAreaCnt++);
        graph.add(individual, RDF.TYPE, SEGM.LogicalArea);
        graph.add(individual, SEGM.belongsTo, areaTreeNode);
        graph.add(individual, SEGM.hasText, vf.createLiteral(area.getText()));
        if (parent != null)
            graph.add(individual, SEGM.isSubordinateTo, parent);
        if (area.getMainTag() != null)
            graph.add(individual, SEGM.hasTag, getTagUri(area.getMainTag()));
        return individual;
    }
    
	public void addTag(Tag tag) 
	{
	    URI tagNode = getTagUri(tag);
	    graph.add(tagNode, RDF.TYPE, SEGM.Tag);
	    graph.add(tagNode, SEGM.hasType, vf.createLiteral(tag.getType()));
        graph.add(tagNode, SEGM.hasName, vf.createLiteral(tag.getValue()));
	}
	
	public URI getAreaUri(Area area) 
	{
	    return vf.createURI(areaTreeNode.toString() + "#a" + area.getId());
	}
	
    public URI getLogicalAreaUri(int cnt) 
    {
        return vf.createURI(areaTreeNode.toString() + "#l" + cnt);
    }
    
	public URI getTagSupportUri(Area area, Tag tag) 
	{
        return vf.createURI(areaTreeNode.toString() + "#" + area.getId()
                + "-" + getTagDesc(tag));
	}
	
    public URI getTagUri(Tag tag) 
    {
        return vf.createURI(RESOURCE.NAMESPACE, "tag-" + getTagDesc(tag));
    }
    
    private String getTagDesc(Tag tag) 
    {
        return tag.getType().replaceAll("\\.", "-") + "--" + tag.getValue();
    }
    
    private String colorString(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
