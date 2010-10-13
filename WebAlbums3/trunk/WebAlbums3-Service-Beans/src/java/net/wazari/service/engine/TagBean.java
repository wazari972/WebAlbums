package net.wazari.service.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.EJB;

import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.TagFacadeLocal;
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
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

@Stateless
public class TagBean implements TagLocal {
    private static final Logger log = LoggerFactory.getLogger(TagBean.class.getName());
    private static final long serialVersionUID = 1L;

    @EJB private TagFacadeLocal tagDAO ;
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
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagDISPLAY", log) ;
        XmlBuilder output = new XmlBuilder(null) ;
        Integer[] tags = vSession.getTagAsked();
        Integer page = vSession.getPage();

        boolean wantChildren = vSession.getWantTagChildren() ;
        if (tags != null) {
            Set<Tag> tagSet = new HashSet<Tag>(tags.length) ;
            for (int tagId : Arrays.asList(tags)) {
                try {
                    Tag enrTag = tagDAO.find(tagId) ;
                    tagSet.add(enrTag) ;
                    if (wantChildren) {
                        tagSet.addAll(tagDAO.getChildren(enrTag));
                    }
                } catch (Exception e) {
                    log.warn("Tag {} cannot be looked up", tagId);
                }
            }

            XmlBuilder thisPage = new XmlBuilder(null);
            thisPage.add("name", "Tags");
            for (Tag enrCurrentTag : tagSet) {
                thisPage.add("tagAsked", enrCurrentTag.getId());
            }

            XmlBuilder title = new XmlBuilder("title");
            title.add(webService.displayListLB(Mode.TAG_USED, vSession, new ArrayList(tagSet),
                    Box.NONE));
            output.add(title);
            PhotoRequest rq = new PhotoRequest(TypeRequest.TAG, tagSet) ;
            Special special = vSession.getSpecial();
            if (Special.FULLSCREEN == special) {
                sysTools.fullscreenMultiple(vSession, rq, "Tags", null, page);
                stopWatch.stop("Service.treatTagDISPLAY.FULLSCREEN") ;
                return null;
            } else {
                output.add(photoLocal.displayPhoto(rq, (ViewSessionPhotoDisplay)vSession, thisPage, submit));
            }
        }
        stopWatch.stop() ;
        return output.validate();
    }

    private static class PairTagXmlBuilder {
        Tag tag ;
        XmlBuilder xml ;

        public PairTagXmlBuilder(Tag tag, XmlBuilder xml) {
            this.tag = tag;
            this.xml = xml;
        }

        public Tag getTag() {
            return tag;
        }

        public XmlBuilder getXml() {
            return xml;
        }

    }
    private static final int SIZE_SCALE = 200 ;
    private static final int SIZE_MIN = 100 ;
    @Override
    public XmlBuilder treatTagCloud(ViewSessionTag vSession){
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagCloud", log) ;
        XmlBuilder cloud = new XmlBuilder("cloud");

        long max = 0;
        Map<Tag,Long> map = tagDAO.queryIDNameCount(vSession);
        for (long current : map.values()) if (current > max) max = current ;

        Tag enrCurrentTag = null ;
        Stack<PairTagXmlBuilder> enrSonStack = new Stack<PairTagXmlBuilder>() ;
        while (!map.isEmpty()) {
            //if we've got not --parent-- Tag to treat
            if (enrCurrentTag == null) {
                //take the first of the map
                enrCurrentTag = map.keySet().iterator().next() ;
            }
            log.info("Current Tag: {}", enrCurrentTag) ;
            if (enrCurrentTag.getParent() != null) {
                //switch to the parent
                enrCurrentTag = enrCurrentTag.getParent() ;
                log.info("Switch to parent Tag: {}", enrCurrentTag) ;
                continue ;
            } 

            enrSonStack.push(new PairTagXmlBuilder(enrCurrentTag, cloud)) ;
            while (!enrSonStack.isEmpty()) {
                log.info("The stack has {} elements", enrSonStack.size());
                PairTagXmlBuilder pair = enrSonStack.pop() ;

                Long nbElts = map.get(pair.tag);
                if (nbElts == null) {
                    nbElts = 0L ;
                }
                else {
                    Object removed = map.remove(pair.tag) ;
                    log.info("Removing entry {}:{} ", pair.tag.getNom(), removed) ;
                    
                }
                log.info("Tag '{}' has {} pictures", pair.tag.getNom(), nbElts) ;
                int size = (int) (SIZE_MIN + ((double) nbElts / max) * SIZE_SCALE);
                XmlBuilder xmlParent = new XmlBuilder("tag")
                    .addAttribut("size", size)
                    .addAttribut("nb", nbElts)
                    .addAttribut("id", pair.tag.getId()) 
                    .addAttribut("name", pair.tag.getNom()) ;
                pair.xml.add(xmlParent);

                log.info("Tag {} has {} children",pair.tag.getNom(),pair.tag.getSonList().size()) ;
                if (!pair.tag.getSonList().isEmpty()) {
                    XmlBuilder xmlSon = new XmlBuilder("children") ;
                    xmlParent.add(xmlSon) ;
                    for (Tag enrSon : pair.tag.getSonList()) {
                        log.info("Push {} in the stack", enrSon.getNom()) ;
                        enrSonStack.push(new PairTagXmlBuilder(enrSon, xmlSon)) ;
                    }
                }
            }
            enrCurrentTag = null ;
        }
        stopWatch.stop() ;
        return cloud.validate();
    }

    @Override
    public XmlBuilder treatTagPersonsPlaces(ViewSessionTag vSession) {
        Special special = vSession.getSpecial();
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagPersonsPlaces."+special, log) ;

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
            log.warn(e.getClass().toString(), "{}:", new Object[]{e.getClass().getSimpleName(), e}) ;

            xmlSpec.cancel();
            xmlSpec.addException(e);
        }
        stopWatch.stop() ;
        return xmlSpec.validate();
    }
}
