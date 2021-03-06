/**
 * RDFStorageService.java
 *
 * Created on 4. 2. 2016, 22:09:30 by burgetr
 */
package org.fit.layout.storage.service;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.api.PageSet;
import org.fit.layout.api.PageSetStorage;
import org.fit.layout.api.PageStorage;
import org.fit.layout.gui.GUIUpdateListener;
import org.fit.layout.storage.gui.StoragePlugin;

/**
 * A service facade for the StoragePlugin. It allows to call the plugin store/load
 * functions from other parts.
 * 
 * @author burgetr
 */
public class RDFStorageService implements PageStorage, PageSetStorage
{
    private StoragePlugin plugin;
    private List<GUIUpdateListener> updateListeners;
    
    public RDFStorageService()
    {
        updateListeners = new ArrayList<GUIUpdateListener>();
    }
    
    public void setPlugin(StoragePlugin plugin)
    {
        this.plugin = plugin;
        for (GUIUpdateListener listener : updateListeners)
            plugin.registerGUIUpdateListener(listener);
    }

    @Override
    public String getId()
    {
        return "FitLayout.RDF.Storage";
    }

    @Override
    public String getName()
    {
        return "RDF Page Storage";
    }

    @Override
    public String getDescription()
    {
        return "Stores the page data in a RDF repository";
    }

    @Override
    public void registerGUIUpdateListener(GUIUpdateListener listener)
    {
        updateListeners.add(listener);
        if (plugin != null)
            plugin.registerGUIUpdateListener(listener);
    }

    @Override
    public PageSet getCurrentPageSet()
    {
        if (plugin != null && plugin.isConnected())
            return plugin.getSelectedPageSet();
        else
            return null;
    }

    @Override
    public boolean nextPageAvailable()
    {
        if (plugin != null && plugin.isConnected())
        {
            int cur = getCurrentIndex();
            return (cur < getTotalCount() - 1);
        }
        else
            return false;
    }

    @Override
    public void loadNext()
    {
        if (nextPageAvailable())
            loadPageAt(getCurrentIndex() + 1);
    }

    @Override
    public boolean previousPageAvailable()
    {
        if (plugin != null && plugin.isConnected())
            return getCurrentIndex() > 0;
        else
            return false;
    }

    @Override
    public void loadPrevious()
    {
        if (previousPageAvailable())
            loadPageAt(getCurrentIndex() - 1);
    }

    @Override
    public int getTotalCount()
    {
        if (plugin != null && plugin.isConnected())
            return plugin.getPageListData().size();
        else
            return 0;
    }

    @Override
    public int getCurrentIndex()
    {
        if (plugin != null && plugin.isConnected())
            return plugin.getLoadedPageIndex();
        else
            return -1;
    }

    @Override
    public void loadPageAt(int index)
    {
        if (plugin != null && plugin.isConnected())
        {
            plugin.setSelectedPageIndex(index);
            plugin.loadSelectedPage();
        }
    }

    @Override
    public boolean saveAvailable()
    {
        return (plugin != null && plugin.isSaveAvailable());
    }

    @Override
    public void saveCurrentPage()
    {
        if (plugin != null && plugin.isSaveAvailable())
        {
            plugin.saveCurrentPage();
        }        
    }

    @Override
    public boolean updateAvailable()
    {
        return (plugin != null && plugin.isUpdateAvailable());
    }

    @Override
    public void updateCurrentPage()
    {
        if (plugin != null && plugin.isUpdateAvailable())
        {
            plugin.updateCurrentPage();
        }        
    }

}
