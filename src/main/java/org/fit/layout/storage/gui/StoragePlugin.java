/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.RDFStorage;
import org.fit.layout.storage.model.RDFPage;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import javax.swing.JList;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;



/**
 * 
 * @author imilicka
 * @author burgetr
 */
public class StoragePlugin implements BrowserPlugin
{
    private Browser browser;
    private RDFStorage bdi = null;
    private boolean connected = false;
    private Vector<String> listColumns;
    private Vector<Vector<String>> listData;
    private Vector<URI> listURIs;
    
    private JPanel pnl_main;
    private JPanel tbr_connection;
    private JLabel lblStatus;
    private JPanel tbr_storageSelection;
    private JPanel tbr_control;
    private JButton btn_saveBoxTreeModel;
    private JButton btn_removePage;
    private JButton btn_clearDB;
    private JButton btn_saveAreaTreeModel;
    private JPanel tbr_pageset;
    private JLabel lblPageSets;
    private JList pageSetList;
    private JButton btnNew;
    private JButton btnEdit;
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
        this.browser.addToolPanel("RDF Storage", getPnl_main()  );
        
        listColumns = new Vector<String>();
        listColumns.add("PageID");
        listColumns.add("Date");
        listColumns.add("Title");
        listColumns.add("URL");
        listColumns.add("TreeID");
        listData = new Vector<Vector<String>>();
        listURIs = new Vector<URI>();
        updatePageTable();
        
        return true;
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
                bdi.getConnection().close();
            } catch (RepositoryException e)
            {
                e.printStackTrace();
            }
        }
        connected = false;
        updateGUIState();
    }
    
    private void updateGUIState()
    {
        if (connected)
        {
            getBtnConnect().setText("Disconnect");
            getLblStatus().setText("Connected");
            
            getBtn_saveAsNew().setEnabled(true);
            getBtn_removePage().setEnabled(true);
            getBtn_clearDB().setEnabled(true);
            getBtn_saveAreaTreeModel().setEnabled(true);
        }
        else
        {
            getBtnConnect().setText("Connect...");
            getLblStatus().setText("Not connected");
        }
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
        listURIs.removeAllElements();
        updatePageTable();
    }
    
    private void fillPageTable()
    {
        listData.removeAllElements();
        listURIs.removeAllElements();
        try
        {
            TupleQueryResult data = bdi.getAvailableTrees(null);
            while (data.hasNext())
            {
                BindingSet tuple = data.next();
                if (tuple.getBinding("tree").getValue() instanceof URI)
                {
                    listURIs.add((URI) tuple.getBinding("tree").getValue());
                    
                    Vector<String> row = new Vector<String>(listColumns.size());
                    row.add(tuple.getBinding("page").getValue().stringValue());
                    row.add(tuple.getBinding("date").getValue().stringValue());
                    row.add(""); //TODO title
                    row.add(tuple.getBinding("url").getValue().stringValue());
                    row.add(tuple.getBinding("tree").getValue().stringValue());
                    listData.add(row);
                }
            }
            
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        updatePageTable();
    }
    
    private URI getSelectedURI()
    {
        int sel = getPageTable().getSelectedRow();
        if (sel != -1)
            return listURIs.elementAt(sel);
        else
            return null;
    }
    
    private JPanel getPnl_main() 
    {
    	
    	 if (pnl_main == null) {
    		 
             pnl_main = new JPanel();
             GridBagLayout gbl_main = new GridBagLayout();
             gbl_main.columnWeights = new double[] { 1.0, 0.0, 0.0 };
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
			gbl_tbr_control.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
			tbr_control.setLayout(gbl_tbr_control);
			GridBagConstraints gbc_btn_saveBoxTreeModel = new GridBagConstraints();
			gbc_btn_saveBoxTreeModel.anchor = GridBagConstraints.NORTHWEST;
			gbc_btn_saveBoxTreeModel.insets = new Insets(0, 0, 5, 0);
			gbc_btn_saveBoxTreeModel.gridx = 0;
			gbc_btn_saveBoxTreeModel.gridy = 0;
			tbr_control.add(getBtn_saveAsNew(), gbc_btn_saveBoxTreeModel);
			GridBagConstraints gbc_btn_saveAreaTreeModel = new GridBagConstraints();
			gbc_btn_saveAreaTreeModel.anchor = GridBagConstraints.NORTHWEST;
			gbc_btn_saveAreaTreeModel.insets = new Insets(0, 0, 5, 0);
			gbc_btn_saveAreaTreeModel.gridx = 0;
			gbc_btn_saveAreaTreeModel.gridy = 1;
			tbr_control.add(getBtn_saveAreaTreeModel(), gbc_btn_saveAreaTreeModel);
			GridBagConstraints gbc_btn_removePage = new GridBagConstraints();
			gbc_btn_removePage.anchor = GridBagConstraints.NORTHWEST;
			gbc_btn_removePage.insets = new Insets(0, 0, 5, 0);
			gbc_btn_removePage.gridx = 0;
			gbc_btn_removePage.gridy = 2;
			tbr_control.add(getBtn_removePage(), gbc_btn_removePage);
			GridBagConstraints gbc_btn_clearDB = new GridBagConstraints();
			gbc_btn_clearDB.insets = new Insets(0, 0, 5, 0);
			gbc_btn_clearDB.anchor = GridBagConstraints.NORTH;
			gbc_btn_clearDB.gridx = 0;
			gbc_btn_clearDB.gridy = 3;
			tbr_control.add(getBtn_clearDB(), gbc_btn_clearDB);
		}
		return tbr_control;
	}
	
	private JButton getBtn_saveAsNew() 
	{
		if (btn_saveBoxTreeModel == null) {
			btn_saveBoxTreeModel = new JButton("Add new");
			btn_saveBoxTreeModel.setEnabled(false);
			btn_saveBoxTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
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
                                bdi.insertAreaTree(atree, ltree, rdfpage.getUri());
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
			});
		}
		return btn_saveBoxTreeModel;
	}
	
	private JButton getBtn_removePage() 
	{
		if (btn_removePage == null) {
			btn_removePage = new JButton("Remove Model");
			btn_removePage.setEnabled(false);
			btn_removePage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
					try {
						/*URI pageId = (URI) cbx_pages.getSelectedItem();
						bdi.removePage(pageId);*/
						
						fillPageTable();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return btn_removePage;
	}
	
	private JButton getBtn_clearDB() 
	{
		if (btn_clearDB == null) {
			btn_clearDB = new JButton("Clear DB");
			btn_clearDB.setEnabled(false);
			btn_clearDB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bdi.clearRDFDatabase();
                    fillPageTable();
				}
			});
		}
		return btn_clearDB;
	}

	
	/**
	 * stores actual 
	 * @return
	 */
	private JButton getBtn_saveAreaTreeModel() {
		if (btn_saveAreaTreeModel == null) {
			btn_saveAreaTreeModel = new JButton("Save Area Tree to DB");
			btn_saveAreaTreeModel.setEnabled(false);
			btn_saveAreaTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/*Page page = browser.getPage();
					AreaTree atree = browser.getAreaTree();
					LogicalAreaTree ltree = browser.getLogicalTree();
					
					if(atree!=null && page!=null) {
						try
                        {
                            bdi.insertAreaTree(atree, ltree, new URIImpl(cbx_pages.getSelectedItem().toString()));
                        } catch (RepositoryException e1) {
                            e1.printStackTrace();
                        }
					}*/
					
                    fillPageTable();
				}
			});
			
			
		}
		return btn_saveAreaTreeModel;
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
        	gbc_lblPageSets.weightx = 1.0;
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
        	GridBagConstraints gbc_btnEdit = new GridBagConstraints();
        	gbc_btnEdit.insets = new Insets(0, 0, 5, 5);
        	gbc_btnEdit.gridx = 2;
        	gbc_btnEdit.gridy = 2;
        	tbr_pageset.add(getBtnEdit(), gbc_btnEdit);
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
    private JList getPageSetList() {
        if (pageSetList == null) {
        	pageSetList = new JList();
        }
        return pageSetList;
    }
    private JButton getBtnNew() {
        if (btnNew == null) {
        	btnNew = new JButton("New...");
        	btnNew.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	    }
        	});
        }
        return btnNew;
    }
    private JButton getBtnEdit() {
        if (btnEdit == null) {
        	btnEdit = new JButton("Edit...");
        }
        return btnEdit;
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
            	        String urlstring = ConnectDialog.show("http://localhost:8080/openrdf-sesame/repositories/user");
            	        if (urlstring != null)
            	        {
                            connect(urlstring);
                            fillPageTable();
            	        }
        	        }
        	        else
        	        {
        	            disconnect();
        	            clearPageTable();
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
