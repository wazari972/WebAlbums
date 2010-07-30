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

    interface ViewSessionPhotoSubmit extends ViewSessionPhoto {

        boolean getSuppr();

        String getDesc();

        boolean getRepresent();

        String getDroit() ;

        Integer[] getNewTag();

        Integer[] getTags();

        boolean getThemeBackground();
    }
    interface ViewSessionPhotoEdit extends ViewSessionPhoto {

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

    Integer getId();

    Integer getTagPhoto();

    Mode getMode();

    Integer getCount();

    Integer getAlbum();

    Integer getAlbmCount();
}
