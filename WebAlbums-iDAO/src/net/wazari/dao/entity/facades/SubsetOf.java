/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity.facades;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
public class SubsetOf<T> {
    private static final Logger log = LoggerFactory.getLogger(SubsetOf.class.getName());
    
    public static class Bornes {
        public Bornes(int nbElementsPerPage, int currentPage) {
            this.nbElementsPerPage = nbElementsPerPage ;
            this.firstElement = nbElementsPerPage*currentPage;
            this.currentPage = currentPage;
            this.nbElements = null ;
        }
        public Bornes(int nbElements) {
            this.nbElementsPerPage = 0 ;
            this.firstElement = 0;
            this.currentPage = 0;
            this.nbElements = nbElements ;
        }
        //number of element per page
        private Integer nbElementsPerPage ;
        //number of elements in total
        private Integer nbElements ;
        //idx of the first element of the current page
        private Integer firstElement ;
        //idx of the current page
        private Integer currentPage ;
        //idx of the last page
        private Integer lastPage ;

        public Integer getFirstElement() {
            return firstElement;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public Integer getLastPage() {
            return lastPage;
        }

        public Integer getNbElement() {
            return nbElements;
        }

        private void setNbElement(long nbElements) {
            this.nbElements = (int) nbElements ;
            this.lastPage = (int) Math.ceil(nbElements / (double) nbElementsPerPage) ;
        }
    }

    public final Bornes bornes ;
    public final List<T> subset ;
    public int setSize ;

    public SubsetOf(Bornes bornes, List<T> subset, Long setSize) {
        this.bornes = bornes;
        this.subset = subset;
        if (bornes != null) {
            bornes.setNbElement(setSize) ;
        }
    }

    public SubsetOf(List<T> subset) {
        this.bornes = null ;
        this.subset = subset;
        this.setSize = subset.size() ;
    }
}
