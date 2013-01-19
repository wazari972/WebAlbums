package net.wazari.service.engine;

import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.service.PhotoLocal;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.PhotoLocal.TypeRequest;
import net.wazari.service.TagLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.service.exchange.xml.tag.*;
import net.wazari.service.exchange.xml.tag.XmlTagCloud.XmlTagCloudEntry;
import net.wazari.util.system.SystemTools;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class TagBean implements TagLocal {

    private static final Logger log = LoggerFactory.getLogger(TagBean.class.getName());
    private static final long serialVersionUID = 1L;
    @EJB
    private TagFacadeLocal tagDAO;
    @EJB
    private PhotoLocal photoLocal;
    @EJB
    private WebPageLocal webService;
    @EJB
    private SystemTools sysTools;

    @Override
    public XmlTagDisplay treatTagDISPLAY(ViewSessionTag vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagDISPLAY", log);
        XmlTagDisplay output = new XmlTagDisplay();
        Integer[] tags = vSession.getTagAsked();
        
        boolean wantChildren = vSession.getWantTagChildren();
        if (tags != null) {
            Set<Tag> tagSet = new HashSet<Tag>(tags.length);
            for (int tagId : Arrays.asList(tags)) {
                try {
                    Tag enrTag = tagDAO.find(tagId);
                    if (enrTag == null) {
                        continue;
                    }
                    tagSet.add(enrTag);
                    if (wantChildren) {
                        tagSet.addAll(tagDAO.getChildren(enrTag));
                    }
                } catch (Exception e) {
                    log.warn("Tag {} cannot be looked up", tagId);
                }
            }
            
            XmlFrom thisPage = new XmlFrom();
            thisPage.name = "Tags";
            List<Integer> tagsAsked = new ArrayList<Integer>(tagSet.size());
            StringBuilder tagsId = new StringBuilder(tagSet.size() * 4);
            for (Tag enrCurrentTag : tagSet) {
                tagsAsked.add(enrCurrentTag.getId());
                tagsId.append(enrCurrentTag.getId()).append("-");
            }
            thisPage.tagAsked = tagsAsked;

            output.title = new XmlTagTitle();
            output.title.tagList = webService.displayListLB(Mode.TAG_USED, vSession, new ArrayList(tagSet), Box.NONE);

            PhotoRequest rq = new PhotoRequest(TypeRequest.TAG, tagSet);
            
            output.photoList = photoLocal.displayPhoto(rq, (ViewSessionPhotoDisplay) vSession, submit, thisPage);
        }
        stopWatch.stop();
        return output;
    }

    @Override
    public XmlTagAbout treatABOUT(ViewSessionTag vSession) {
        Integer tagId = vSession.getId() ;
        Tag enrTag = tagDAO.find(tagId) ;
        if (enrTag == null) return null ;
        XmlTagAbout about = new XmlTagAbout() ;
        about.tag = new XmlTag();
        about.tag.name = enrTag.getNom();
        about.tag.id = enrTag.getId();
        
        Photo enrTagThemePhoto = tagDAO.getTagThemePhoto(vSession, enrTag);
        if (enrTagThemePhoto != null) {
            about.tag.picture = new XmlPhotoId(enrTagThemePhoto.getId());
            about.tag.picture.path = enrTagThemePhoto.getPath(true);
        }

        return about ;
    }

    private static class PairTagXmlBuilder {

        Tag tag;
        XmlTagCloudEntry xml;
        List<XmlTagCloudEntry> parentList;
        
        public PairTagXmlBuilder(Tag tag, XmlTagCloudEntry xml, 
                                 List<XmlTagCloudEntry> parentList) {
            this.tag = tag;
            this.xml = xml;
            if (xml.children == null) {
                xml.children = new LinkedList<XmlTagCloudEntry>() ;
            }
            this.parentList = parentList;
        }
    }
    private static final int SIZE_SCALE = 200;
    private static final int SIZE_MIN = 100;

    @Override
    public XmlTagCloud treatTagCloud(ViewSessionTag vSession) {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagCloud", log);
        XmlTagCloud output = new XmlTagCloud();

        //what's the max number of pict per tag ?
        long max = 0;
        Map<Tag, Long> map = tagDAO.queryIDNameCount(vSession);
        for (long current : map.values()) {
            if (current > max) {
                max = current;
            }
        }

        Tag enrCurrentTag = null;
        Stack<PairTagXmlBuilder> enrSonStack = new Stack<PairTagXmlBuilder>();
        while (!map.isEmpty()) {
            //if we've got no --parent-- tag to treat
            if (enrCurrentTag == null) {
                //take the first of the map
                enrCurrentTag = map.keySet().iterator().next();
            }
            while (enrCurrentTag.getParent() != null) {
                //switch to the oldest parent
                enrCurrentTag = enrCurrentTag.getParent();
            }
            XmlTagCloudEntry currentXml = new XmlTagCloudEntry();
            enrSonStack.push(new PairTagXmlBuilder(enrCurrentTag, currentXml, 
                                                   output.parentList));
            while (!enrSonStack.isEmpty()) {
                PairTagXmlBuilder pair = enrSonStack.pop();

                Long nbElts = map.get(pair.tag);
                if (nbElts == null || nbElts == 0) {
                    // the tag is not in the map, so it has 0 elements in the
                    // current theme.
                    nbElts = 0L;
                    // get rid of it if it has no son tags.
                    if (pair.tag.getSonList().isEmpty()) {
                        continue ;
                    }
                } else {
                    // it's in the map, so remove it, we'll process it right now
                    map.remove(pair.tag);
                }
                
                int size = (int)(SIZE_MIN + ((double) nbElts/max) * SIZE_SCALE);
                
                pair.parentList.add(pair.xml);
                pair.xml.size = size;
                pair.xml.nb = nbElts;
                pair.xml.id = pair.tag.getId();
                pair.xml.name = pair.tag.getNom();
                
                switch(pair.tag.getTagType()) {
                    case 1: pair.xml.type = "who"; break;
                    case 2: pair.xml.type = "what"; break;
                    case 3: pair.xml.type = "where"; break;
                }
                
                if (pair.tag.getGeolocalisation() != null) {
                    Geolocalisation geo = pair.tag.getGeolocalisation();
                    pair.xml.setGeo(geo.getLatitude(), geo.getLongitude());
                }
                for (Tag enrSon : pair.tag.getSonList()) {
                    XmlTagCloudEntry xmlSon = new XmlTagCloudEntry();
                    enrSonStack.push(new PairTagXmlBuilder(enrSon, xmlSon, pair.xml.children));
                }
            }
            enrCurrentTag = null;
        }
        
        for (XmlTagCloudEntry tag : output.parentList) {
            updateParentTagCloud(tag);
        }
        
        stopWatch.stop();
        return output;
    }
    
    private long updateParentTagCloud(XmlTagCloudEntry tag) {
        long sonCount = tag.nb;
        
        if (tag.children == null) {
            return sonCount;
        }
        
        List<XmlTagCloudEntry> toRemove = new LinkedList<XmlTagCloudEntry>();
        for (XmlTagCloudEntry son : tag.children) {
            long count = updateParentTagCloud(son);
            
            sonCount += count;
            
            if (count == 0) {
                toRemove.add(son);
            }
        }
        
        for (XmlTagCloudEntry rm : toRemove) {
            tag.children.remove(rm);
        }
        
        tag.nb = sonCount;
        
        return sonCount;
    }

    @Override
    public XmlTagPersonsPlaces treatTagPersons(ViewSessionTag vSession) {
        return treatTagPersonsPlaces(vSession, Special.PERSONS);
    }
    
    @Override
    public XmlTagPersonsPlaces treatTagPlaces(ViewSessionTag vSession) {
        return treatTagPersonsPlaces(vSession, Special.PLACES);
    }
    
    
    private XmlTagPersonsPlaces treatTagPersonsPlaces(ViewSessionTag vSession, Special special) {
        StopWatch stopWatch = new Slf4JStopWatch("Service.treatTagPersonsPlaces." + special, log);

        XmlTagPersonsPlaces output = new XmlTagPersonsPlaces();

        int type;
        if (Special.PERSONS == special) {
            type = 1;
        } else {
            type = 3;
        }

        List<Tag> lstT = tagDAO.queryAllowedTagByType(vSession, type);
        for (Tag enrTag : lstT) {
            XmlTag tag = new XmlTag();
            tag.name = enrTag.getNom();
            tag.id = enrTag.getId();
            
            if (enrTag.getGeolocalisation() != null) {
                Geolocalisation geo = enrTag.getGeolocalisation();
                tag.setGeo(geo.getLatitude(), geo.getLongitude());
            }
            
            Photo enrTagThemePhoto = tagDAO.getTagThemePhoto(vSession, enrTag);
            if (enrTagThemePhoto != null) {
                tag.picture = new XmlPhotoId(enrTagThemePhoto.getId());
                tag.picture.path = enrTagThemePhoto.getPath(true);
            }
            
            output.tagList.add(tag);
        }
        stopWatch.stop();
        return output;
    }
}
