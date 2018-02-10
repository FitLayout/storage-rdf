package org.fit.layout.tagViewer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.impl.DefaultTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Tag;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * 
 * Plugin shows all tags and their areas defined on the web page
 * @author milicka
 *
 */
public class TagViewerPlugin implements BrowserPlugin 
{
	private Browser browser;
	private JPanel pnl_mainPanel;
	private JPanel pnl_control;
	private JButton btn_loadAllTags;
	private JScrollPane scrl_tagTable;
	private JTable tagTable;
	private DefaultTableModel tagModelTable;
	private JLabel lbl_found;
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public boolean init(Browser browser) {
		
		this.browser = browser;
		
		browser.addStructurePanel("Tag viewer", getPnl_mainPanel());
		
		return true;
	}

	private JPanel getPnl_mainPanel()
    {
        if (pnl_mainPanel == null)
        {
            pnl_mainPanel = new JPanel();
            
            GridBagLayout gbl_pathsPanel = new GridBagLayout();
            gbl_pathsPanel.columnWidths = new int[] { 0, 0 };
            gbl_pathsPanel.rowHeights = new int[] { 0, 0, 0 };
            gbl_pathsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
            gbl_pathsPanel.rowWeights = new double[] { 0.1, 1.0,
                    Double.MIN_VALUE };
            pnl_mainPanel.setLayout(gbl_pathsPanel);
            
            GridBagConstraints gbc_control = new GridBagConstraints();
            gbc_control.fill = GridBagConstraints.HORIZONTAL;
            gbc_control.gridx = 0;
            gbc_control.gridy = 0;
            pnl_mainPanel.add(getPnl_control(), gbc_control);
            
            GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
            gbc_extractionScroll.fill = GridBagConstraints.BOTH;
            gbc_extractionScroll.gridx = 0;
            gbc_extractionScroll.gridy = 1;
            pnl_mainPanel.add(getScrl_tagTable(), gbc_extractionScroll);
        }
        
        return pnl_mainPanel;
    }
	
	
	//control panel ====================
    private JPanel getPnl_control() 
    {
    	if(pnl_control==null) 
    	{
    		pnl_control = new JPanel();
    		
			GridBagLayout gbl_pathsPanel = new GridBagLayout();
			gbl_pathsPanel.columnWidths = new int[] { 0, 0 };
			gbl_pathsPanel.rowHeights = new int[] { 0, 0, 0 };
			gbl_pathsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_pathsPanel.rowWeights = new double[] { 0.1, 1.0,
			         Double.MIN_VALUE };
			pnl_control.setLayout(gbl_pathsPanel);
			 
			 
			GridBagConstraints gbc_control = new GridBagConstraints();
			gbc_control.fill = GridBagConstraints.HORIZONTAL;
			gbc_control.gridx = 0;
			gbc_control.gridy = 0;
			pnl_control.add(getBtn_loadAllTags(), gbc_control);
			 
			GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
			gbc_extractionScroll.fill = GridBagConstraints.BOTH;
			gbc_extractionScroll.gridx = 0;
			gbc_extractionScroll.gridy = 1;
			pnl_control.add(getLbl_found(), gbc_extractionScroll);
    	}
    	
    	return pnl_control;
    }

	private JButton getBtn_loadAllTags() 
	{
		if (btn_loadAllTags == null) 
		{
			btn_loadAllTags = new JButton("Load all tags");
			btn_loadAllTags.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
					clearTagList();
					fillTagListFromAreaTree( browser.getAreaTree() );
					getLbl_found().setText("Found:"+tagModelTable.getRowCount());
					
				}
			});
		}
		return btn_loadAllTags;
	}

	
	
	//anotation list ========================
	private JScrollPane getScrl_tagTable()
    {
        if (scrl_tagTable == null)
        {
            scrl_tagTable = new JScrollPane();
            scrl_tagTable.setViewportView(getTbl_classificationTags());
        }
        return scrl_tagTable;
    }

    private JTable getTbl_classificationTags()
    {
        if (tagTable == null)
        {
        	tagModelTable = new DefaultTableModel(); 
        	tagModelTable.addColumn("TagType");
        	tagModelTable.addColumn("Tag"); 
        	tagModelTable.addColumn("Content");
        	
            tagTable = new JTable(tagModelTable);
        }
        return tagTable;
    }
    
    
    
    //private operations ============================    
    private void fillTagListFromAreaTree(AreaTree areaTree) 
    {
    	Area root = areaTree.getRoot();
    	addAreaTagsToList(root.getTags(), root.getText());
    	fillTagListFromParentArea(root);
    }
    
    private void fillTagListFromParentArea(Area parentArea) 
    {
    	for(Area area : parentArea.getChildren()) 
    	{
    		addAreaTagsToList(area.getTags(), area.getText());
    		fillTagListFromParentArea(area);
    	}
    }
    
    private void addAreaTagsToList(Map<Tag,Float> tags, String text) 
    {
    	Set<Entry<Tag, Float>> entrySet = tags.entrySet();
		
		for(Entry<Tag, Float> et: entrySet) 
		{
			addTagToList((DefaultTag)et.getKey(), text );
		}
    }
    
    /**
     * Adds tag to list
     * @param tag
     */
    private void addTagToList(DefaultTag tag, String text) 
    {
    	tagModelTable.addRow(new Object[] {tag.getType(), tag.getValue(), text } );
    }
    
    /**
     * Removes all rows of tag list
     */
    private void clearTagList() 
    {
    	for(int i = tagModelTable.getRowCount(); i>0; i--) 
    	{
    		tagModelTable.removeRow(i-1);
    	}
    }
    
	private JLabel getLbl_found() 
	{
		if (lbl_found == null) 
		{
			lbl_found = new JLabel("");
			lbl_found.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lbl_found;
	}
}
