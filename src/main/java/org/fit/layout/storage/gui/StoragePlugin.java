/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.storage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.storage.AreaModelLoader;
import org.fit.layout.storage.RDFStorage;
import org.fit.layout.storage.model.RDFPage;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

import javax.swing.JList;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;



/**
 * 
 * @author imilicka
 * @author burgetr
 */
public class StoragePlugin implements BrowserPlugin
{
    private Browser browser;
    RDFStorage bdi = null;	
    
    private JPanel pnl_main;
    private JPanel tbr_connection;
    private JLabel lbl_status;
    private JPanel tbr_storageSelection;
    private JLabel lbl_urls;
    private JComboBox<URI> cbx_pages;
    private JButton btn_loadBoxModel;
    private JPanel tbr_control;
    private JButton btn_saveBoxTreeModel;
    private JButton btn_removePage;
    private JButton btn_clearDB;
    private JButton btn_saveAreaTreeModel;
    private JComboBox<URI> cbx_areaTrees;
    private JButton btn_loadAreaTreeModel;
    private JPanel tbr_pageset;
    private JLabel lblPageSets;
    private JList pageSetList;
    private JButton btnNew;
    private JButton btnEdit;
    private JScrollPane pageSourceScroll;
    private JButton btnDelete;
    private JButton btnConnect;
    
    
	//=============================
    
    /**
     * @wbp.parser.entryPoint
     */
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolPanel("RDF Storage", getPnl_main()  );
        return true;
    }
    
    private void connect(String DBConnectionUrl)
    {
        cbx_pages.removeAllItems();
        
        try {
            bdi = new RDFStorage(DBConnectionUrl);
            
            getBtn_loadBoxModel().setEnabled(true);
            getBtn_loadAreaTreeModel().setEnabled(true);
            
            getBtn_saveBoxTreeModel().setEnabled(true);
            getBtn_removePage().setEnabled(true);
            getBtn_clearDB().setEnabled(true);
            getBtn_saveAreaTreeModel().setEnabled(true);
        }
        catch (Exception e) {

            /*JOptionPane.showMessageDialog((Component) browser,
                    "There is a problem with DB connection: "+e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);*/
            e.printStackTrace();
        }
    }
    
    /**
     * it loads distinct URLs into ulrsComboBox
     */
    private void loadAllPages() 
    {
        try {
            Set<URI> listURL = bdi.getAllPageIds();
            for(URI url : listURL)
                cbx_pages.addItem(url);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPageSets()
    {
    }
    
    private JPanel getPnl_main() 
    {
    	
    	 if (pnl_main == null) {
    		 
             pnl_main = new JPanel();
             GridBagLayout gbl_main = new GridBagLayout();
             gbl_main.columnWeights = new double[] { 1.0, 0.0 };
             gbl_main.rowWeights = new double[] { 0.0, 0.0 };
             pnl_main.setLayout(gbl_main);
             GridBagConstraints gbc_connection = new GridBagConstraints();
             gbc_connection.fill = GridBagConstraints.BOTH;
             gbc_connection.weightx = 1.0;
             gbc_connection.anchor = GridBagConstraints.EAST;
             gbc_connection.insets = new Insets(0, 0, 5, 5);
             gbc_connection.gridx = 0;
             gbc_connection.gridy = 0;
             pnl_main.add(getPnl_connection(), gbc_connection);
             GridBagConstraints gbc_storageSelection = new GridBagConstraints();
             gbc_storageSelection.weightx = 1.0;
             gbc_storageSelection.fill = GridBagConstraints.BOTH;
             gbc_storageSelection.insets = new Insets(0, 0, 5, 0);
             gbc_storageSelection.gridx = 1;
             gbc_storageSelection.gridy = 0;
             pnl_main.add(getPnl_storageSelection(), gbc_storageSelection);
             GridBagConstraints gbc_control = new GridBagConstraints();
             gbc_control.weightx = 1.0;
             gbc_control.anchor = GridBagConstraints.EAST;
             gbc_control.fill = GridBagConstraints.BOTH;
             gbc_control.insets = new Insets(0, 0, 5, 0);
             gbc_control.gridx = 1;
             gbc_control.gridy = 1;
             pnl_main.add(getPnl_control(), gbc_control);
             GridBagConstraints gbc_tbr_pageset = new GridBagConstraints();
             gbc_tbr_pageset.weighty = 1.0;
             gbc_tbr_pageset.weightx = 1.0;
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
			tbr_connection.add(getLbl_RdfDb());
		}
		return tbr_connection;
	}
	
	private JLabel getLbl_RdfDb() 
	{
		if (lbl_status == null) {
			lbl_status = new JLabel("Server");
		}
		return lbl_status;
	}
	
	
	//Selection panel =============================
	
	private JPanel getPnl_storageSelection() 
	{
		if (tbr_storageSelection == null) {
			tbr_storageSelection = new JPanel();
			tbr_storageSelection.add(getLbl_pages());
			tbr_storageSelection.add(getCbx_pages());
			tbr_storageSelection.add(getBtn_loadBoxModel());
			tbr_storageSelection.add(getCbx_areaTrees());
			tbr_storageSelection.add(getBtn_loadAreaTreeModel());
			
		}
		return tbr_storageSelection;
	}
	
	private JLabel getLbl_pages() 
	{
		if (lbl_urls == null) {
			lbl_urls = new JLabel("URLs");
		}
		return lbl_urls;
	}
	
	private JComboBox<URI> getCbx_pages() 
	{
		if (cbx_pages == null) {
			cbx_pages = new JComboBox<URI>();
			cbx_pages.setMaximumRowCount(8);
			cbx_pages.setPreferredSize(new Dimension(300,25));
			cbx_pages.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					cbx_areaTrees.removeAllItems();
			    	
			    	if( cbx_pages.getItemCount()>0 ) 
			    	{
			    		cbx_areaTrees.setEnabled(true);
			    		
			    		try {
							Set<URI> areaTrees = bdi.getAreaTreeIdsForPageId((URI) cbx_pages.getSelectedItem());
							for(URI area : areaTrees) {
								cbx_areaTrees.addItem(area);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						} 
			    	}
			    	else {
			    		cbx_areaTrees.setEnabled(false);
			    	}
				}
			});
		}
		return cbx_pages;
	}
	
	private JButton getBtn_loadBoxModel() 
	{
		if (btn_loadBoxModel == null) {
			btn_loadBoxModel = new JButton("Load Box Model");
			btn_loadBoxModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
			        try {
			        	URI pageId = (URI) cbx_pages.getSelectedItem();
			        	Page page = bdi.loadPage(pageId);
			        	browser.setPage(page);
					} catch (Exception e1) {
						
						JOptionPane.showMessageDialog((Component)browser,
							    "Cannot load defined launch!",
							    "Loading Error",
							    JOptionPane.ERROR_MESSAGE);
					}
					
				}
			});
			btn_loadBoxModel.setEnabled(false);
		}
		return btn_loadBoxModel;
	}
	
	
	//Control panel =============================
	
	private JPanel getPnl_control() 
	{
		if (tbr_control == null) {
			tbr_control = new JPanel();
			tbr_control.add(getBtn_saveBoxTreeModel());
			tbr_control.add(getBtn_saveAreaTreeModel());
			tbr_control.add(getBtn_removePage());
			tbr_control.add(getBtn_clearDB());
		}
		return tbr_control;
	}
	
	private JButton getBtn_saveBoxTreeModel() 
	{
		if (btn_saveBoxTreeModel == null) {
			btn_saveBoxTreeModel = new JButton("Save Box Tree to DB");
			btn_saveBoxTreeModel.setEnabled(false);
			btn_saveBoxTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					Page page = browser.getPage();
					
					if(page!=null) {
						try
                        {
                            bdi.insertPageBoxModel(page);
                        } catch (RepositoryException e) {
                            e.printStackTrace();
                        }
						
						loadAllPages();
					}
					else {
						/*
						JOptionPane.showMessageDialog(mainWindow,
							    "There is not loaded web page! You have to load some before saving it into RDF DB!",
							    "Info",
							    JOptionPane.ERROR_MESSAGE);
							    */
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
						URI pageId = (URI) cbx_pages.getSelectedItem();
						bdi.removePage(pageId);
						
						loadAllPages();
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
					loadAllPages();
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
					Page page = browser.getPage();
					AreaTree atree = browser.getAreaTree();
					LogicalAreaTree ltree = browser.getLogicalTree();
					
					if(atree!=null && page!=null) {
						try
                        {
                            bdi.insertAreaTree(atree, ltree, new URIImpl(cbx_pages.getSelectedItem().toString()));
                        } catch (RepositoryException e1) {
                            e1.printStackTrace();
                        }
					}
					
					loadAllPages();
				}
			});
			
			
		}
		return btn_saveAreaTreeModel;
	}
	
	private JComboBox<URI> getCbx_areaTrees() {
		if (cbx_areaTrees == null) {
			cbx_areaTrees = new JComboBox<URI>();
			cbx_areaTrees.setPreferredSize(new Dimension(300,25));
			
		}
		return cbx_areaTrees;
	}
	
	private JButton getBtn_loadAreaTreeModel() {
		if (btn_loadAreaTreeModel == null) {
			btn_loadAreaTreeModel = new JButton("Load AreaTree");
			btn_loadAreaTreeModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					if(cbx_areaTrees.getItemCount()>0) {
						
						try {
						    Page curPage = browser.getPage();
						    if (curPage instanceof RDFPage)
						    {
    						    URI atreeUri = (URI) cbx_areaTrees.getSelectedItem();
    						    AreaModelLoader loader = bdi.loadAreaTrees(atreeUri, (RDFPage) browser.getPage());
    						    AreaTree atree = loader.getAreaTree();
    						    if (atree != null)
    						        browser.setAreaTree(atree);
    						    LogicalAreaTree ltree = loader.getLogicalAreaTree();
    						    if (ltree != null)
    						        browser.setLogicalTree(ltree);
    						    browser.refreshView();
						    }
						    else
						        System.err.println("No RDF page loaded");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			});
		}
		return btn_loadAreaTreeModel;
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
        	        String urlstring = ConnectDialog.show("http://localhost:8080/openrdf-sesame/repositories/user");
        	        if (urlstring != null)
        	        {
                        connect(urlstring);
                        loadAllPages();
        	        }
        	    }
        	});
        }
        return btnConnect;
    }
}
