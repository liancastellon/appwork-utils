/**
 * Copyright (c) 2009 - 2015 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 *
 * This file is part of org.appwork.swing.exttable
 *
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.swing.exttable;

import javax.swing.table.TableColumn;

import org.appwork.utils.logging.Log;

/**
 * @author thomas
 *
 */
public class CustomOriginalTableColumn extends TableColumn {
    private ExtColumn<?> extColumn;

    /**
     * @param ext
     * @param i
     */
    public CustomOriginalTableColumn(ExtColumn<?> ext, int i) {
        super(i);
        extColumn = ext;

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#getPreferredWidth()
     */
    @Override
    public int getPreferredWidth() {

        if (extColumn.getForcedWidth() > 0) {
            if ("Column: Name".equals(this.toString())) {
                System.out.println("Get PrefWidth forced" + extColumn.getForcedWidth());
            }
            return extColumn.getForcedWidth();
        }
        if ("Column: Name".equals(this.toString())) {
            System.out.println("Get PrefWidth super" + super.getPreferredWidth());
        }
        return super.getPreferredWidth();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#getMaxWidth()
     */
    @Override
    public int getMaxWidth() {
        // if (getModelIndex() == 1) {
        // System.out.println("extColumn.getForcedWidth() > 0 " + (extColumn.getForcedWidth() > 0) + "- " + extColumn.getForcedWidth());
        // }
        if (extColumn.getForcedWidth() > 0) {
            if ("Column: Name".equals(this.toString())) {
                System.out.println("Get getMaxWidth forced" + extColumn.getForcedWidth());
            }
            return extColumn.getForcedWidth();
        }
        if ("Column: Name".equals(this.toString())) {
            System.out.println("Get getMaxWidth super" + super.getMaxWidth());
        }
        return super.getMaxWidth();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#getMinWidth()
     */
    @Override
    public int getMinWidth() {
        // if (getModelIndex() == 1) {
        // System.out.println("extColumn.getForcedWidth() > 0 " + (extColumn.getForcedWidth() > 0) + "- " + extColumn.getForcedWidth());
        // }
        if (extColumn.getForcedWidth() > 0) {
            if ("Column: Name".equals(this.toString())) {
                System.out.println("Get getMinWidth forced" + extColumn.getForcedWidth());
            }
            return extColumn.getForcedWidth();
        }
        if ("Column: Name".equals(this.toString())) {
            System.out.println("Get getMinWidth super" + super.getMinWidth());
        }
        return super.getMinWidth();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#setPreferredWidth(int)
     */
    @Override
    public void setPreferredWidth(int preferredWidth) {
        // int oldMax = getMaxWidth();
        // int oldMin = getMinWidth();
        // try {
        // setMaxWidth(Integer.MAX_VALUE);
        // setMinWidth(0);
        // if (getModelIndex() == 8) {
        // int now = getPreferredWidth();
        // System.out.println("pref " + getModelIndex() + " " + now + "->" + width);
        // }
        super.setPreferredWidth(width);
        // if (getModelIndex() == 8) {
        // System.out.println("pref-->" + getPreferredWidth());
        // }
        // } finally {
        // setMaxWidth(oldMax);
        // setMinWidth(oldMin);
        // }

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#getWidth()
     */
    @Override
    public int getWidth() {

        if ("Column: Name".equals(this.toString())) {
            System.out.println("Get Width " + super.getWidth());
        }

        return super.getWidth();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Column: " + extColumn.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableColumn#setMinWidth(int)
     */
    @Override
    public void setMinWidth(int minWidth) {
        // if ("Modul".equals(extColumn.getName())) {
        // System.out.println("set max");
        // }
        super.setMinWidth(minWidth);
    }

    public void setWidth(int width) {
        // int oldMax = getMaxWidth();
        // int oldMin = getMinWidth();
        // try {
        // setMaxWidth(Integer.MAX_VALUE);
        // setMinWidth(0);

        int now = getWidth();

        if ("Column: Name".equals(this.toString()) && width == 10) {
            System.out.println("w " + this + " " + now + "->" + width);
        }
        if ("Column: Name".equals(this.toString()) && width == 386) {
            System.out.println("w " + this + " " + now + "->" + width);
        }
        super.setWidth(width);
        if (width != getWidth()) {

            Log.L.severe("Bad Column Implementation: " + extColumn.getModel().getClass().getName() + "/" + extColumn.getName() + " Min: " + super.getMinWidth() + " Max: " + super.getMaxWidth());
        }
        // if (getModelIndex() == 8) {
        // System.out.println("w-->" + getWidth());
        // }
        // } finally {
        // setMaxWidth(oldMax);
        // setMinWidth(oldMin);
        // }

    }
}