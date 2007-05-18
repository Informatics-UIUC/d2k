/**
 * 
 */
package ncsa.d2k.modules.core.util;

import java.util.Comparator;

import ncsa.d2k.core.modules.Module;

/**
 * Compares modules by name for sorting.
 * @author jianw
 *
 */
public class ModuleAlphaComparator implements Comparator {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        Module m1 = (Module) o1;
        Module m2 = (Module) o2;

        return m1.getAlias().compareTo(m2.getAlias());
    }
}
