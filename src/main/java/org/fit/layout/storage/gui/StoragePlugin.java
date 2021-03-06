/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.fit.layout.api.PageSet;
import org.fit.layout.api.PageStorage;
import org.fit.layout.api.ScriptObject;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.gui.GUIUpdateListener;
import org.fit.layout.gui.GUIUpdateSource;
import org.fit.layout.gui.TreeListener;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.AreaModelLoader;
import org.fit.layout.storage.RDFStorage;
import org.fit.layout.storage.model.RDFAreaTree;
import org.fit.layout.storage.model.RDFPage;
import org.fit.layout.storage.ontology.RESOURCE;
import org.fit.layout.storage.service.RDFStorageService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryException;

import javax.swing.JList;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;



/**
 * 
 * @author imilicka
 * @author burgetr
 */
public class StoragePlugin implements BrowserPlugin, GUIUpdateSource, TreeListener
{
    private Browser browser;
    private List<GUIUpdateListener> updateListeners;
    private RDFStorage bdi = null;
    private boolean connected = false;
    private int loadedPageIndex = -1;
    private Vector<String> listColumns;
    private Vector<Vector<String>> listData;
    private Vector<IRI> listPageIRIs;
    private Vector<IRI> listTreeIRIs;
    
    private JPanel pnl_main;
    private JPanel tbr_connection;
    private JLabel lblStatus;
    private JPanel tbr_storageSelection;
    private JPanel tbr_control;
    private JButton btn_saveAsNew;
    private JButton btn_removeModel;
    private JButton btn_load;
    private JButton btn_updateModel;
    private JPanel tbr_pageset;
    private JLabel lblPageSets;
    private JList<PageSet> pageSetList;
    private JButton btnNew;
    private JScrollPane pageSourceScroll;
    private JButton btnDelete;
    private JButton btnConnect;
    private JTable pageTable;
    private JScrollPane tableScroll;
    
    
	//=============================
    
    /**
     * @wbp.parser.entryPoint
     */
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolPanel("RDF Storage", getPnl_main());
        this.browser.addTreeListener(this);
        updateListeners = new ArrayList<GUIUpdateListener>();
        
        initStorageServices();
        
        listColumns = new Vector<String>();
        listColumns.add("PageID");
        listColumns.add("Date");
        listColumns.add("Title");
        listColumns.add("URL");
        listColumns.add("TreeID");
        listData = new Vector<Vector<String>>();
        listPageIRIs = new Vector<IRI>();
        listTreeIRIs = new Vector<IRI>();
        updatePageTable();
        updatePageSets();
        updateGUIState();
        
        return true;
    }
    
    private void initStorageServices()
    {
        Map<String, PageStorage> services = ServiceManager.findPageStorages();
        for (PageStorage storage : services.values())
        {
            if (storage instanceof RDFStorageService)
                ((RDFStorageService) storage).setPlugin(this);
        }
        Map<String, ScriptObject> scripts = ServiceManager.findScriptObjects();
        for (ScriptObject script : scripts.values())
        {
            if (script instanceof ScriptApi)
                ((ScriptApi) script).setPlugin(this);
        }
    }
    
    @Override
    public void pageRendered(Page page)
    {
        updateGUIState();
    }

    @Override
    public void areaTreeUpdated(AreaTree tree)
    {
        updateGUIState();
    }

    @Override
    public void logicalAreaTreeUpdated(LogicalAreaTree tree)
    {
        updateGUIState();
    }

    @Override
    public void registerGUIUpdateListener(GUIUpdateListener listener)
    {
        updateListeners.add(listener);
    }
    
    public void setStorage(RDFStorage storage)
    {
        try {
            bdi = storage;
            bdi.getLastSequenceValue("box"); //just for checking the connection
            connected = true;
        }
        catch (Exception e) {
            connected = false;
            e.printStackTrace();
        }
        updateStorageState();
    }
    
    public void updateStorageState()
    {
        fillPageTable();
        updatePageSets();
        updateGUIState();
    }
    
    public void closeStorage()
    {
        clearPageTable();
        updatePageSets();
        updateGUIState();
    }
    
    private void connect(String DBConnectionUrl)
    {
        try {
            bdi = new RDFStorage(DBConnectionUrl);
            bdi.getLastSequenceValue("box"); //just for checking the connection
            connected = true;
        }
        catch (Exception e) {
            connected = false;
            e.printStackTrace();
            JOptionPane.showMessageDialog(getPnl_main(),
                    "Couldn't connect the repository: "+e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        updateGUIState();
    }
    
    public void disconnect()
    {
        if (connected)
        {
            try
            {
                bdi.closeConnection();
            } catch (RepositoryException e)
            {
                e.printStackTrace();
            }
        }
        connected = false;
        updateGUIState();
    }
    
    public boolean isConnected()
    {
        return connected;
    }

    public Vector<Vector<String>> getPageListData()
    {
        return listData;
    }
    
    public void updateGUIState()
    {
        if (connected)
        {
            getBtnConnect().setText("Disconnect");
            getLblStatus().setText("Connected");

            boolean psetAvail = (getPageSetList().getSelectedIndex() != -1);
            boolean loaded = (browser.getPage() != null && browser.getPage() instanceof RDFPage
                    && browser.getAreaTree() != null && browser.getAreaTree() instanceof RDFAreaTree);
            boolean pageSelected = (getSelectedPageURI() != null);
            
            getBtn_load().setEnabled(pageSelected);
            getBtn_saveAsNew().setEnabled(psetAvail);
            getBtn_removeModel().setEnabled(pageSelected);
            getBtn_updateModel().setEnabled(psetAvail && loaded);
            getBtnNew().setEnabled(true);
            getBtnDelete().setEnabled(psetAvail);
        }
        else
        {
            getBtnConnect().setText("Connect...");
            getLblStatus().setText("Not connected");
            
            getBtn_load().setEnabled(false);
            getBtn_saveAsNew().setEnabled(false);
            getBtn_removeModel().setEnabled(false);
            getBtn_updateModel().setEnabled(false);
            getBtnNew().setEnabled(false);
            getBtnDelete().setEnabled(false);
        }
        for (GUIUpdateListener listener : updateListeners)
            listener.updateGUI();
    }
    
    private void updatePageTable()
    {
        DefaultTableModel model = new DefaultTableModel(listData, listColumns) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) 
            {
                return false;
            };
        };
        pageTable.setModel(model);
        if (listData.size() > 0)
            pageTable.getSelectionModel().setSelectionInterval(0, 0);
    }
    
    private void clearPageTable()
    {
        listData.removeAllElements();
        listPageIRIs.removeAllElements();
        listTreeIRIs.removeAllElements();
        updatePageTable();
    }
    
    private void fillPageTable()
    {
        listData.removeAllElements();
        listPageIRIs.removeAllElements();
        listTreeIRIs.removeAllElements();
        PageSet pset = getSelectedPageSet();
        if (pset != null)
        {
            try
            {
                String prevPageUri = "";
                TupleQueryResult data = bdi.getAvailableTrees(pset.getName());
                while (data.hasNext())
                {
                    BindingSet tuple = data.next();
                    if (tuple.getBinding("tree").getValue() instanceof IRI
                            && tuple.getBinding("page").getValue() instanceof IRI)
                    {
                        listPageIRIs.add((IRI) tuple.getBinding("page").getValue());
                        listTreeIRIs.add((IRI) tuple.getBinding("tree").getValue());
                        
                        Vector<String> row = new Vector<String>(listColumns.size());
                        String pageUri = tuple.getBinding("page").getValue().stringValue();
                        if (pageUri.equals(prevPageUri)) //do not repeat the same page URIs in the table
                        {
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                        }
                        else
                        {
                            row.add(formatURI(pageUri));
                            row.add(tuple.getBinding("date").getValue().stringValue());
                            row.add(tuple.getBinding("title").getValue().stringValue());
                            row.add(tuple.getBinding("url").getValue().stringValue());
                        }
                        row.add(formatURI(tuple.getBinding("tree").getValue().stringValue()));
                        listData.add(row);
                        prevPageUri = pageUri;
                    }
                }
                
            } catch (RepositoryException e) {
                e.printStackTrace();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }
        }
        updatePageTable();
    }
    
    private String formatURI(String src)
    {
        if (src.startsWith(RESOURCE.NAMESPACE))
            return src.substring(RESOURCE.NAMESPACE.length());
        else
            return src;
    }
    
    public int getLoadedPageIndex()
    {
        return loadedPageIndex;
    }
    
    public void setSelectedPageIndex(int index)
    {
        getPageTable().setRowSelectionInterval(index, index);
    }
    
    private IRI getSelectedPageURI()
    {
        int sel = getPageTable().getSelectedRow();
        if (sel != -1)
            return listPageIRIs.elementAt(sel);
        else
            return null;
    }

    public void loadSelectedPage()
    {
        RDFPage page = null;
        AreaTree atree = null;
        LogicalAreaTree ltree = null;
        try {
            IRI pageId = getSelectedPageURI();
            if (pageId != null)
            {
                //load the box model
                page = bdi.loadPage(pageId);
                //load the trees
                if (page != null)
                {
                    IRI atreeUri = getSelectedTreeURI();
                    AreaModelLoader loader = bdi.loadAreaTrees(atreeUri, page);
                    atree = loader.getAreaTree();
                    ltree = loader.getLogicalAreaTree();
                    loadedPageIndex = getPageTable().getSelectedRow(); 
                }
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty())
                msg = "Exception occured: " + e.toString();
            JOptionPane.showMessageDialog(getPnl_main(),
                    msg,
                    "Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (page != null)
            browser.setPage(page);
        if (atree != null)
            browser.setAreaTree(atree);
        if (ltree != null)
            browser.setLogicalTree(ltree);
        browser.refreshView();
        updateGUIState();
    }
    
    public boolean isSaveAvailable()
    {
        Page page = browser.getPage();
        AreaTree atree = browser.getAreaTree();
        return (connected && page != null && atree != null);
    }
    
    public void saveCurrentPage()
    {
        Page page = browser.getPage();
        AreaTree atree = browser.getAreaTree();
        LogicalAreaTree ltree = browser.getLogicalTree();
        if (page != null && atree != null) 
        {
            RDFPage rdfpage = null;
            if (page instanceof RDFPage)
                rdfpage = (RDFPage) page; //page already saved
            else
            { //page not yet saved
                try
                {
                    rdfpage = bdi.insertPageBoxModel(page);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
            
            if (rdfpage != null)
            {
                try
                {
                    PageSet currentPset = getSelectedPageSet();
                    if (currentPset != null)
                        bdi.addPageToPageSet(rdfpage.getIri(), currentPset.getName());
                    bdi.insertAreaTree(atree, ltree, rdfpage.getIri());
                } catch (RepositoryException e1) {
                    e1.printStackTrace();
                }
            }
            
            fillPageTable();
        }
        else 
        {
            JOptionPane.showMessageDialog(getPnl_main(),
                    "No area tree found. The page segmentation should be performed first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateAvailable()
    {
        Page page = browser.getPage();
        AreaTree atree = browser.getAreaTree();
        return (connected && page != null && page instanceof RDFPage && atree != null && atree instanceof RDFAreaTree);
    }
    
    public void updateCurrentPage()
    {
        Page page = browser.getPage();
        AreaTree atree = browser.getAreaTree();
        LogicalAreaTree ltree = browser.getLogicalTree();
        if (page != null && atree != null) 
        {
            if (page instanceof RDFPage && atree instanceof RDFAreaTree)
            {
                RDFPage rdfpage = (RDFPage) page;
                try
                {
                    IRI atreeUri = ((RDFAreaTree) atree).getIri();
                    bdi.removeAreaTree(atreeUri);
                    bdi.insertAreaTree(atreeUri, atree, ltree, rdfpage.getIri());
                } catch (RepositoryException e1) {
                    e1.printStackTrace();
                }
                fillPageTable();
            }
            else
            {
                JOptionPane.showMessageDialog(getPnl_main(),
                        "The page or area tree have not been stored. Cannot update.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else 
        {
            JOptionPane.showMessageDialog(getPnl_main(),
                    "No area tree found. The page segmentation should be performed first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        fillPageTable();
    }
    
    private IRI getSelectedTreeURI()
    {
        int sel = getPageTable().getSelectedRow();
        if (sel != -1)
            return listTreeIRIs.elementAt(sel);
        else
            return null;
    }
    
    public PageSet getSelectedPageSet()
    {
        return getPageSetList().getSelectedValue();
    }
    
    private void updatePageSets()
    {
        PageSet current = getSelectedPageSet();
        DefaultListModel<PageSet> model = (DefaultListModel<PageSet>) getPageSetList().getModel();
        model.removeAllElements();
        if (connected)
        {
            try
            {
                List<PageSet> sets = bdi.getPageSets();
                for (PageSet pset : sets)
                    model.addElement(pset);
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        if (current == null)
            getPageSetList().setSelectedIndex(0);
        else
            getPageSetList().setSelectedValue(current, true);
    }
    
    private JPanel getPnl_main() 
    {
    	
    	 if (pnl_main == null) {
    		 
             pnl_main = new JPanel();
             GridBagLayout gbl_main = new GridBagLayout();
             gbl_main.columnWeights = new double[] { 0.0, 0.0, 0.0 };
             gbl_main.rowWeights = new double[] { 0.0, 0.0 };
             pnl_main.setLayout(gbl_main);
             GridBagConstraints gbc_connection = new GridBagConstraints();
             gbc_connection.fill = GridBagConstraints.BOTH;
             gbc_connection.anchor = GridBagConstraints.EAST;
             gbc_connection.insets = new Insets(0, 0, 5, 5);
             gbc_connection.gridx = 0;
             gbc_connection.gridy = 0;
             pnl_main.add(getPnl_connection(), gbc_connection);
             GridBagConstraints gbc_storageSelection = new GridBagConstraints();
             gbc_storageSelection.gridheight = 2;
             gbc_storageSelection.weightx = 1.0;
             gbc_storageSelection.fill = GridBagConstraints.BOTH;
             gbc_storageSelection.insets = new Insets(0, 0, 5, 0);
             gbc_storageSelection.gridx = 1;
             gbc_storageSelection.gridy = 0;
             pnl_main.add(getPnl_storageSelection(), gbc_storageSelection);
             GridBagConstraints gbc_control = new GridBagConstraints();
             gbc_control.gridheight = 2;
             gbc_control.anchor = GridBagConstraints.EAST;
             gbc_control.fill = GridBagConstraints.BOTH;
             gbc_control.insets = new Insets(0, 0, 5, 0);
             gbc_control.gridx = 2;
             gbc_control.gridy = 0;
             pnl_main.add(getPnl_control(), gbc_control);
             GridBagConstraints gbc_tbr_pageset = new GridBagConstraints();
             gbc_tbr_pageset.weighty = 1.0;
             gbc_tbr_pageset.insets = new Insets(0, 0, 0, 5);
             gbc_tbr_pageset.fill = GridBagConstraints.BOTH;
             gbc_tbr_pageset.gridx = 0;
             gbc_tbr_pageset.gridy = 1;
             pnl_main.add(getTbr_pageset(), gbc_tbr_pageset);
         }
         return pnl_main;
    }
    
    
    //Connection panel=================================================================
    
    private JPanel getPnl_connection() 
    {
		if (tbr_connection == null) {
			tbr_connection = new JPanel();
			FlowLayout flowLayout = (FlowLayout) tbr_connection.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			tbr_connection.add(getBtnConnect());
			tbr_connection.add(getLblStatus());
		}
		return tbr_connection;
	}
	
	private JLabel getLblStatus() 
	{
		if (lblStatus == null) {
			lblStatus = new JLabel("Not connected");
		}
		return lblStatus;
	}
	
	
	//Selection panel =============================
	
	private JPanel getPnl_storageSelection() 
	{
		if (tbr_storageSelection == null) {
			tbr_storageSelection = new JPanel();
			GridBagLayout gbl_tbr_storageSelection = new GridBagLayout();
			gbl_tbr_storageSelection.columnWeights = new double[]{0.0};
			gbl_tbr_storageSelection.rowWeights = new double[]{0.0};
			tbr_storageSelection.setLayout(gbl_tbr_storageSelection);
			GridBagConstraints gbc_tableScroll = new GridBagConstraints();
			gbc_tableScroll.weighty = 1.0;
			gbc_tableScroll.weightx = 1.0;
			gbc_tableScroll.fill = GridBagConstraints.BOTH;
			gbc_tableScroll.anchor = GridBagConstraints.NORTHWEST;
			gbc_tableScroll.gridx = 0;
			gbc_tableScroll.gridy = 0;
			tbr_storageSelection.add(getTableScroll(), gbc_tableScroll);
			
		}
		return tbr_storageSelection;
	}
	
	
	//Control panel =============================
	
	private JPanel getPnl_control() 
	{
		if (tbr_control == null) {
			tbr_control = new JPanel();
			GridBagLayout gbl_tbr_control = new GridBagLayout();
			gbl_tbr_control.columnWeights = new double[]{1.0};
			gbl_tbr_control.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
			tbr_control.setLayout(gbl_tbr_control);
			GridBagConstraints gbc_btn_saveBoxTreeModel = new GridBagConstraints();
			gbc_btn_saveBoxTreeModel.insets = new Insets(5, 0, 5, 0);
			gbc_btn_saveBoxTreeModel.gridx = 0;
			gbc_btn_saveBoxTreeModel.gridy = 4;
			tbr_control.add(getBtn_saveAsNew(), gbc_btn_saveBoxTreeModel);
			GridBagConstraints gbc_btn_saveAreaTreeModel = new GridBagConstraints();
			gbc_btn_saveAreaTreeModel.insets = new Insets(0, 0, 5, 0);
			gbc_btn_saveAreaTreeModel.gridx = 0;
			gbc_btn_saveAreaTreeModel.gridy = 1;
			tbr_control.add(getBtn_updateModel(), gbc_btn_saveAreaTreeModel);
			GridBagConstraints gbc_btn_removePage = new GridBagConstraints();
			gbc_btn_removePage.insets = new Insets(0, 0, 5, 0);
			gbc_btn_removePage.gridx = 0;
			gbc_btn_removePage.gridy = 2;
			tbr_control.add(getBtn_removeModel(), gbc_btn_removePage);
			GridBagConstraints gbc_btn_clearDB = new GridBagConstraints();
			gbc_btn_clearDB.insets = new Insets(0, 0, 5, 0);
			gbc_btn_clearDB.gridx = 0;
			gbc_btn_clearDB.gridy = 0;
			tbr_control.add(getBtn_load(), gbc_btn_clearDB);
		}
		return tbr_control;
	}
	
	private JButton getBtn_saveAsNew() 
	{
		if (btn_saveAsNew == null) {
			btn_saveAsNew = new JButton("Insert new");
			btn_saveAsNew.setEnabled(false);
			btn_saveAsNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
                    saveCurrentPage();
				}
			});
		}
		return btn_saveAsNew;
	}
	
	private JButton getBtn_removeModel() 
	{
		if (btn_removeModel == null) {
			btn_removeModel = new JButton("Delete");
			btn_removeModel.setEnabled(false);
			btn_removeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
				    int response = JOptionPane.showConfirmDialog(getPnl_main(), "Do you want to remove the selected tree?", "Confirm",
				            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				    if (response == JOptionPane.YES_OPTION)
				    {
    					try {
    					    IRI pageId = getSelectedPageURI();
    						IRI areaTreeId = getSelectedTreeURI();
    						bdi.removeAreaTree(areaTreeId);
    						Set<IRI> remainingTrees = bdi.getAreaTreeIdsForPageId(pageId);
    						if (remainingTrees.isEmpty())
    						{
    						    bdi.removePage(pageId);  //no trees remaining, remove the page
    						}
    						fillPageTable();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
				    }
				}
			});
		}
		return btn_removeModel;
	}
	
	private JButton getBtn_load() 
	{
		if (btn_load == null) {
			btn_load = new JButton("Load");
			btn_load.setEnabled(false);
			btn_load.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) 
				{
				    loadSelectedPage();
                }
			});
		}
		return btn_load;
	}

	
	/**
	 * stores actual 
	 * @return
	 */
	private JButton getBtn_updateModel() {
		if (btn_updateModel == null) {
			btn_updateModel = new JButton("Update");
			btn_updateModel.setEnabled(false);
			btn_updateModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    updateCurrentPage();
				}
			});
			
			
		}
		return btn_updateModel;
	}
    private JPanel getTbr_pageset() {
        if (tbr_pageset == null) {
        	tbr_pageset = new JPanel();
        	GridBagLayout gbl_tbr_pageset = new GridBagLayout();
        	gbl_tbr_pageset.columnWidths = new int[] {0, 0, 0, 0};
        	gbl_tbr_pageset.rowHeights = new int[] {0, 0, 0};
        	gbl_tbr_pageset.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};
        	gbl_tbr_pageset.rowWeights = new double[]{0.0, 1.0, 0.0};
        	tbr_pageset.setLayout(gbl_tbr_pageset);
        	GridBagConstraints gbc_lblPageSets = new GridBagConstraints();
        	gbc_lblPageSets.gridwidth = 4;
        	gbc_lblPageSets.insets = new Insets(0, 0, 5, 0);
        	gbc_lblPageSets.fill = GridBagConstraints.HORIZONTAL;
        	gbc_lblPageSets.gridx = 0;
        	gbc_lblPageSets.gridy = 0;
        	tbr_pageset.add(getLblPageSets(), gbc_lblPageSets);
        	GridBagConstraints gbc_pageSourceScroll = new GridBagConstraints();
        	gbc_pageSourceScroll.gridwidth = 4;
        	gbc_pageSourceScroll.fill = GridBagConstraints.BOTH;
        	gbc_pageSourceScroll.insets = new Insets(0, 0, 5, 0);
        	gbc_pageSourceScroll.gridx = 0;
        	gbc_pageSourceScroll.gridy = 1;
        	tbr_pageset.add(getPageSourceScroll(), gbc_pageSourceScroll);
        	GridBagConstraints gbc_btnNew = new GridBagConstraints();
        	gbc_btnNew.insets = new Insets(0, 0, 5, 5);
        	gbc_btnNew.gridx = 1;
        	gbc_btnNew.gridy = 2;
        	tbr_pageset.add(getBtnNew(), gbc_btnNew);
        	GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        	gbc_btnDelete.insets = new Insets(0, 0, 5, 5);
        	gbc_btnDelete.gridx = 3;
        	gbc_btnDelete.gridy = 2;
        	tbr_pageset.add(getBtnDelete(), gbc_btnDelete);
        }
        return tbr_pageset;
    }
    private JLabel getLblPageSets() {
        if (lblPageSets == null) {
        	lblPageSets = new JLabel("Page Sets");
        }
        return lblPageSets;
    }
    private JList<PageSet> getPageSetList() {
        if (pageSetList == null) {
        	pageSetList = new JList<PageSet>();
        	pageSetList.addListSelectionListener(new ListSelectionListener() {
        	    public void valueChanged(ListSelectionEvent arg0) {
        	        updateGUIState();
        	        fillPageTable();
        	    }
        	});
        	pageSetList.setModel(new DefaultListModel<PageSet>());
        }
        return pageSetList;
    }
    private JButton getBtnNew() {
        if (btnNew == null) {
        	btnNew = new JButton("New...");
        	btnNew.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) 
        	    {
        	        String name = JOptionPane.showInputDialog(btnNew,
        	                "New page set name",
        	                "New page set",
        	                JOptionPane.QUESTION_MESSAGE);
        	        if (name != null && !name.trim().isEmpty())
        	        {
        	            try
                        {
                            bdi.createPageSet(name);
                        } catch (RepositoryException e1) {
                            e1.printStackTrace();
                        }
        	            updatePageSets();
        	        }
        	    }
        	});
        }
        return btnNew;
    }
    private JScrollPane getPageSourceScroll() {
        if (pageSourceScroll == null) {
        	pageSourceScroll = new JScrollPane();
        	pageSourceScroll.setViewportView(getPageSetList());
        }
        return pageSourceScroll;
    }
    private JButton getBtnDelete() {
        if (btnDelete == null) {
        	btnDelete = new JButton("Delete");
        	btnDelete.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent arg0) 
        	    {
        	        PageSet pset = getSelectedPageSet();
        	        if (pset != null)
        	        {
        	            String message = "Do you want to remove the selected page set?";
        	            JCheckBox cbox = new JCheckBox("Also delete orphaned pages", true);
        	            if (listPageIRIs.size() == 0)
        	            {
        	                cbox.setEnabled(false);
        	                cbox.setSelected(false);
        	            }
        	            Object[] params = { message, cbox };
                        int response = JOptionPane.showConfirmDialog(btnDelete, params, "Confirm",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (response == JOptionPane.YES_OPTION)
                        {
            	            try
                            {
                                bdi.deletePageSet(pset.getName());
                                if (cbox.isSelected())
                                    bdi.removeOrphanedPages();
                            } catch (RepositoryException e) {
                                e.printStackTrace();
                            }
            	            updatePageSets();
                        }
        	        }
        	    }
        	});
        }
        return btnDelete;
    }
    private JButton getBtnConnect() {
        if (btnConnect == null) {
        	btnConnect = new JButton("Connect...");
        	btnConnect.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        if (!connected)
        	        {
            	        String urlstring = ConnectDialog.show("sesame:http://localhost:8080/rdf4j-server/repositories/user");
            	        if (urlstring != null)
            	        {
                            connect(urlstring);
                            fillPageTable();
                            updatePageSets();
                            updateGUIState();
            	        }
        	        }
        	        else
        	        {
        	            disconnect();
        	            clearPageTable();
        	            updatePageSets();
                        updateGUIState();
        	        }
        	    }
        	});
        }
        return btnConnect;
    }
    private JTable getPageTable() {
        if (pageTable == null) {
        	pageTable = new JTable();
        	pageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	pageTable.addMouseListener(new MouseAdapter() {
        	    public void mousePressed(MouseEvent me) {
        	        JTable table =(JTable) me.getSource();
        	        Point p = me.getPoint();
        	        int row = table.rowAtPoint(p);
        	        if (me.getClickCount() == 2 && row != -1) {
        	            loadSelectedPage();
        	        }
        	    }
        	});
        }
        return pageTable;
    }
    private JScrollPane getTableScroll() {
        if (tableScroll == null) {
        	tableScroll = new JScrollPane();
        	tableScroll.setViewportView(getPageTable());
        }
        return tableScroll;
    }
}
