/**
 * RDFStorageService.java
 *
 * Created on 4. 2. 2016, 22:09:30 by burgetr
 */
package org.fit.layout.storage.service;

import org.fit.layout.api.PageSetStorage;
import org.fit.layout.api.PageStorage;
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
    
    public void setPlugin(StoragePlugin plugin)
    {
        this.plugin = plugin;
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void saveCurrentPage()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean updateAvailable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateCurrentPage()
    {
        // TODO Auto-generated method stub
        
    }

}
