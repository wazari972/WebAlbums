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
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagCloud.XmlTagCloudEntry;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;
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
    public XmlTagDisplay treatTagDISPLAY(ViewSessionTag vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagDISPLAY", log) ;
        XmlTagDisplay output = new XmlTagDisplay() ;
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

            XmlFrom thisPage = new XmlFrom();
            thisPage.name = "Tags" ;
            ArrayList<Integer> tagsAsked = new ArrayList<Integer>(tagSet.size()) ;
            for (Tag enrCurrentTag : tagSet) {
                tagsAsked.add(enrCurrentTag.getId());
            }
            thisPage.tagAsked = (Integer[]) tagsAsked.toArray() ;
            
            output.title = webService.displayListLB(Mode.TAG_USED, vSession, new ArrayList(tagSet),
                    Box.NONE);
            
            PhotoRequest rq = new PhotoRequest(TypeRequest.TAG, tagSet) ;
            Special special = vSession.getSpecial();
            if (Special.FULLSCREEN == special) {
                sysTools.fullscreenMultiple(vSession, rq, "Tags", null, page);
                stopWatch.stop("Service.treatTagDISPLAY.FULLSCREEN") ;
                return null;
            } else {
                output.photoList = photoLocal.displayPhoto(rq, (ViewSessionPhotoDisplay)vSession, submit, thisPage);
            }
        }
        stopWatch.stop() ;
        return output ;
    }

    private static class PairTagXmlBuilder {
        Tag tag ;
        XmlTagCloudEntry xml ;

        public PairTagXmlBuilder(Tag tag, XmlTagCloudEntry xml) {
            this.tag = tag;
            this.xml = xml;
        }

        public Tag getTag() {
            return tag;
        }

        public XmlTagCloudEntry getXml() {
            return xml;
        }

    }
    private static final int SIZE_SCALE = 200 ;
    private static final int SIZE_MIN = 100 ;
    @Override
    public XmlTagCloud treatTagCloud(ViewSessionTag vSession){
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagCloud", log) ;
        XmlTagCloud output = new XmlTagCloud();

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
            XmlTagCloudEntry parent = new XmlTagCloudEntry();
            output.parentTag = parent ;
            
            enrSonStack.push(new PairTagXmlBuilder(enrCurrentTag, parent)) ;
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
                XmlTagCloudEntry xmlParent = new XmlTagCloudEntry() ;
                xmlParent.size = size ;
                xmlParent.nb = nbElts ;
                xmlParent.id = pair.tag.getId() ;
                xmlParent.name = pair.tag.getNom() ;
                pair.xml.sonList.add(xmlParent);

                log.info("Tag {} has {} children",pair.tag.getNom(),pair.tag.getSonList().size()) ;
                if (!pair.tag.getSonList().isEmpty()) {
                    XmlTagCloudEntry xmlSon = new XmlTagCloudEntry() ;
                    xmlParent.sonList.add(xmlSon) ;
                    for (Tag enrSon : pair.tag.getSonList()) {
                        log.info("Push {} in the stack", enrSon.getNom()) ;
                        enrSonStack.push(new PairTagXmlBuilder(enrSon, xmlSon)) ;
                    }
                }
            }
            enrCurrentTag = null ;
        }
        stopWatch.stop() ;
        return output ;
    }

    @Override
    public XmlTagPersonsPlaces treatTagPersonsPlaces(ViewSessionTag vSession) {
        Special special = vSession.getSpecial();
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagPersonsPlaces."+special, log) ;

        XmlTagPersonsPlaces output = new XmlTagPersonsPlaces();

        int type;
        if (Special.PERSONS == special) {
            type = 1;
        } else {
            type = 3;
        }

        try {
            List<Tag> lstT = tagDAO.queryAllowedTagByType(vSession, type);
            for(Tag enrTag : lstT) {
                XmlTag tag = new XmlTag() ;
                tag.name = enrTag.getNom() ;
                tag.id = enrTag.getId() ;
                List<TagTheme> lstTT = enrTag.getTagThemeList() ;
                Random rand = new Random();
                //pick up a RANDOM valid picture visible from this theme
                while (!lstTT.isEmpty()) {
                    int i = rand.nextInt(lstTT.size());
                    TagTheme enrTT =  lstTT.get(i);
                    if (enrTT.getPhoto() != null &&
                            (vSession.isRootSession() || vSession.getTheme().getId().equals(enrTT.getTheme().getId()))) {
                        tag.picture = enrTT.getPhoto() ;
                        break;
                    } else {
                        lstTT.remove(i);
                    }
                }
                if (Special.RSS == special) {
                    Geolocalisation enrGeo = enrTag.getGeolocalisation();
                    if (enrGeo != null) {
                        tag.lat = enrGeo.getLat();
                        tag.longit = enrGeo.getLongitude();
                    }
                }
                output.tags.add(tag) ;
            }
        } catch (Exception e) {
            log.warn(e.getClass().toString(), "{}: {}", new Object[]{e.getClass().getSimpleName(), e}) ;

            output.exception = e.getMessage() ;
        }
        stopWatch.stop() ;
        return output ;
    }
}
