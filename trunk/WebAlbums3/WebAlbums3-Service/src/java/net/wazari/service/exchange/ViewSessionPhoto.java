/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionPhoto extends ViewSession {

    interface ViewSessionPhotoEdit extends ViewSessionPhoto {

        boolean getSuppr();

        String getDesc();

        boolean getRepresent();

        String getUser();

        Integer[] getNewTag();

        Integer[] getTags();
    }

    interface ViewSessionPhotoDisplay extends ViewSessionPhoto {

        interface ViewSessionPhotoDisplayMassEdit extends ViewSessionPhotoDisplay {

            enum Turn {

                RIGHT, LEFT, TAG, UNTAG, MVTAG, RIEN
            }

            Turn getTurn();

            boolean getChk(Integer id);

            Integer getAddTag();

            Integer getRmTag();
        }

        ViewSessionPhotoDisplayMassEdit getMassEdit();

        boolean wantsDetails();

        Integer getPage();
    }

    interface ViewSessionPhotoSpecial extends ViewSessionPhoto {

        String getWidth();
    }

    Integer getTagPhoto();

    Mode getMode();

    Integer getCount();

    Integer getAlbum();

    Integer getAlbmCount();
}
