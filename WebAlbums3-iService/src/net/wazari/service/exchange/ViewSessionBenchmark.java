/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionBenchmark extends ViewSession {
    enum BenchAction {TAGS} ;
    
    BenchAction getBenchAction();
    
    Mode getMode() ;
}