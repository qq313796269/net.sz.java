/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sz.framework.way.navmesh.util;

import java.io.Serializable;

/**
 * Can be used for keeping track of whether something has been processed or not.
 * For example usage see TileArray.getAllWithin() which uses a tracker to track
 * whether KPolygons have been processed.
 *
 * @author Keith
 */
public class Tracker implements Serializable {

    int idSystemIdentityHashCode = System.identityHashCode(this);
    long counter = 0;

    public void incrementCounter() {
        counter++;
    }

    public int getID() {
        return idSystemIdentityHashCode;
    }

    public long getCounter() {
        return counter;
    }
}
