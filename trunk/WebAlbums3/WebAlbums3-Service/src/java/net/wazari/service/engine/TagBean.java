package net.wazari.service.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ejb.Stateless;
import javax.ejb.EJB;

import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;

import net.wazari.service.PhotoLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exception.WebAlbumsServiceException;

import net.wazari.util.XmlBuilder;
import net.wazari.util.system.SystemTools;

@Stateless
public class TagBean implements TagLocal {

    private static final long serialVersionUID = 1L;

    @EJB private PhotoFacadeLocal photoDAO ;
    @EJB private TagFacadeLocal tagDAO ;
    @EJB private TagThemeFacadeLocal tagThemeDAO ;
    @EJB private PhotoLocal photoLocal ;
    @EJB private WebPageLocal webService ;
    @EJB private SystemTools sysTools ;

    public XmlBuilder treatTAGS(ViewSessionTag vSession) throws WebAlbumsServiceException {
        String tagList = "";
        XmlBuilder output = new XmlBuilder("tags");
        Special special = vSession.getSpecial();
        if (Special.CLOUD == special) {
            XmlBuilder cloud = new XmlBuilder("cloud");

            int sizeScale = 200;
            int sizeMin = 100;
            long max = tagDAO.getMaxTagPerPhoto(vSession);
            Map<Tag,Long> map = tagDAO.queryIDNameCount(vSession);

            for (Tag enrTag : map.keySet()) {
                long current = map.get(enrTag);
                int size = (int) (sizeMin + ((double) current / max) * sizeScale);
                cloud.add(new XmlBuilder("tag")
                        .addAttribut("size", size)
                        .addAttribut("nb", current)
                        .addAttribut("id", enrTag.getId()));
            }
            cloud.validate();
           
            output.add(cloud);
            return output.validate();
        }

        if (Special.PERSONS == special || Special.PLACES == special || Special.RSS == special) {
            XmlBuilder xmlSpec = new XmlBuilder(special.toString().toLowerCase());

            int type;
            if (Special.PERSONS == special) {
                type = 1;
            } else {
                type = 3;
            }

            try {

                List<Tag> lstT = tagDAO.queryAllowedTagByType(vSession, type);
                for(Tag enrTag : lstT) {
                    XmlBuilder tag = new XmlBuilder("tag", enrTag.getNom()).addAttribut("id", enrTag.getId());
                    List<TagTheme> lstTh = tagThemeDAO.queryByTag(vSession, enrTag.getId());
                    Random rand = new Random();
                    while (!lstTh.isEmpty()) {
                        int i = rand.nextInt(lstTh.size());
                        TagTheme enrTh =  lstTh.get(i);
                        if (enrTh.getPhoto() != null) {
                            tag.addAttribut("picture", enrTh.getPhoto());
                            break;
                        } else {
                            lstTh.remove(i);
                        }
                    }
                    if (Special.RSS == special) {
                        Geolocalisation enrGeo = enrTag.getGeolocalisation();
                        if (enrGeo != null) {
                            tag.add("lat", enrGeo.getLat());
                            tag.add("long", enrGeo.getLongitude());
                        }
                    }
                    xmlSpec.add(tag);
                }
                xmlSpec.validate();
            } catch (Exception e) {
                e.printStackTrace();

                xmlSpec.cancel();
                xmlSpec.addException(e);
            }
            output.add(xmlSpec);
            return output.validate();
        }

        Integer[] tags = vSession.getTagAsked();
        Action action = vSession.getAction();
        Integer page = vSession.getPage();
        XmlBuilder submit = null;
        Boolean correct = true;

        if (Action.SUBMIT == action && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            submit = photoLocal.treatPhotoSUBMIT((ViewSessionPhoto)vSession, correct);
        }

        if ((Action.EDIT == action || !correct) && vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            output = photoLocal.treatPhotoEDIT(vSession, submit);
            XmlBuilder return_to = new XmlBuilder("return_to");
            return_to.add("name", "Tags");
            return_to.add("page", page);
            for (int i = 0; i < tags.length; i++) {
                return_to.add("tagAsked", tags[i]);
            }
            output.add(return_to);
            return output.validate();
        }

        XmlBuilder thisPage = new XmlBuilder(null);
        thisPage.add("name", "Tags");
        for (int i = 0; tags != null && i < tags.length; i++) {
            thisPage.add("tagAsked", tags[i]);
        }

            if (tags != null) {
                List<Integer> listTagId = Arrays.asList(tags) ;
                XmlBuilder title = new XmlBuilder("title");
                title.add(webService.displayListLB(Mode.TAG_USED, vSession, listTagId,
                        Box.NONE));
                output.add(title);

                List<Photo> lstPhoto = photoDAO.loadByTags(vSession, listTagId);

                if (Special.FULLSCREEN == special) {
                    sysTools.fullscreen(vSession, lstPhoto, "Tags", null, page);
                    return null;
                } else {
                    output.add(photoLocal.displayPhoto(lstPhoto, (ViewSessionPhoto)vSession, thisPage, Integer.parseInt(tagList), submit));
                }
            }
       
        return output.validate();
    }
}
