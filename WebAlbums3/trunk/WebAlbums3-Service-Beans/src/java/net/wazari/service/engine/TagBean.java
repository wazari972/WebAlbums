package net.wazari.service.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.EJB;

import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.entity.Tag;

import net.wazari.service.PhotoLocal;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.PhotoLocal.TypeRequest;
import net.wazari.service.TagLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.common.util.XmlBuilder;
import net.wazari.util.system.SystemTools;

@Stateless
public class TagBean implements TagLocal {

    private static final long serialVersionUID = 1L;

    @EJB private TagFacadeLocal tagDAO ;
    @EJB private TagThemeFacadeLocal tagThemeDAO ;
    @EJB private PhotoLocal photoLocal ;
    @EJB private WebPageLocal webService ;
    @EJB private SystemTools sysTools ;

    @Override
    public XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException {
        return photoLocal.treatPhotoSUBMIT(vSession, correct) ;
    }

    @Override
    public XmlBuilder treatTagEDIT(ViewSessionTag vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        Integer[] tags = vSession.getTagAsked();
        Integer page = vSession.getPage();
        
        XmlBuilder output = photoLocal.treatPhotoEDIT((ViewSessionPhotoEdit) vSession, submit);
        XmlBuilder return_to = new XmlBuilder("return_to");
        return_to.add("name", "Tags");
        return_to.add("page", page);
        for (int i = 0; i < tags.length; i++) {
            return_to.add("tagAsked", tags[i]);
        }
        output.add(return_to);
        return output.validate();
    }

    @Override
    public XmlBuilder treatTagDISPLAY(ViewSessionTag vSession, XmlBuilder submit) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder(null) ;
        Integer[] tags = vSession.getTagAsked();
        Integer page = vSession.getPage();

        XmlBuilder thisPage = new XmlBuilder(null);
        thisPage.add("name", "Tags");
        for (int i = 0; tags != null && i < tags.length; i++) {
            thisPage.add("tagAsked", tags[i]);
        }

        if (tags != null) {
            List<Tag> listTag = new ArrayList<Tag>(tags.length) ;
            for (int tagId : Arrays.asList(tags)) {
                try {
                    listTag.add(tagDAO.find(tagId)) ;
                } catch (Exception e) {

                }
            }
            List<Integer> listTagId = Arrays.asList(tags) ;
            XmlBuilder title = new XmlBuilder("title");
            title.add(webService.displayListLB(Mode.TAG_USED, vSession, listTag,
                    Box.NONE));
            output.add(title);
            PhotoRequest rq = new PhotoRequest(TypeRequest.TAG, listTagId) ;
            Special special = vSession.getSpecial();
            if (Special.FULLSCREEN == special) {
                sysTools.fullscreenMultiple(vSession, rq, "Tags", null, page);
                return null;
            } else {
                output.add(photoLocal.displayPhoto(rq, (ViewSessionPhotoDisplay)vSession, thisPage, submit));
            }
        }

        return output.validate();
    }

    @Override
    public XmlBuilder treatTagCloud(ViewSessionTag vSession){
        XmlBuilder cloud = new XmlBuilder("cloud");

        int sizeScale = 200;
        int sizeMin = 100;
        long max = 0;
        Map<Tag,Long> map = tagDAO.queryIDNameCount(vSession);
        for (long current : map.values()) if (current > max) max = current ;

        for (Tag enrTag : map.keySet()) {
            long current = map.get(enrTag);
            int size = (int) (sizeMin + ((double) current / max) * sizeScale);
            cloud.add(new XmlBuilder("tag", enrTag.getNom())
                    .addAttribut("size", size)
                    .addAttribut("nb", current)
                    .addAttribut("id", enrTag.getId()));
        }

        return cloud.validate();
    }

    @Override
    public XmlBuilder treatTagPersonsPlaces(ViewSessionTag vSession) {
        Special special = vSession.getSpecial();
        
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
                XmlBuilder tag = new XmlBuilder("tag", enrTag.getNom())
                        .addAttribut("id", enrTag.getId());
                List<TagTheme> lstTT = enrTag.getTagThemeList() ;
                Random rand = new Random();
                //pick up a RANDOM valid picture visible from this theme
                while (!lstTT.isEmpty()) {
                    int i = rand.nextInt(lstTT.size());
                    TagTheme enrTT =  lstTT.get(i);
                    if (enrTT.getPhoto() != null &&
                            (vSession.isRootSession() || vSession.getTheme().getId().equals(enrTT.getTheme().getId()))) {
                        tag.addAttribut("picture", enrTT.getPhoto());
                        break;
                    } else {
                        lstTT.remove(i);
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
            log.warn(e.getClass().toString(), "{0}:", new Object[]{e.getClass().getSimpleName(), e}) ;

            xmlSpec.cancel();
            xmlSpec.addException(e);
        }
        return xmlSpec.validate();
    }
    private static final Logger log = LoggerFactory.getLogger(TagBean.class.getName());
}
