/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.api.page;

/**
 *
 * @author hoat4
 */
public interface WindowContext {
/**
     * Opens a new tab and retuns it. 
     * @return a newly opened tab
     */
    TabContext openTab();
    /**
     * Returns the currently active tab where the focus is in this window.
     * @return the current tab
     */
    TabContext currentTab();
    /**
     * Returns a tab which doesn't disturbs the user if it went to an other URL, for example 
     * a tab with chrome://newtab URL. 
     * @return 
     */
    default TabContext blankTab() {
        TabContext result = currentTab();
        if(result.getRealURL().toExternalForm().equals("chrome://newtab"))
            return result;
        return openTab();
    }

}
