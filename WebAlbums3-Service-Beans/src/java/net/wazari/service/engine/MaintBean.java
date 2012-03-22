package net.wazari.service.engine;

import java.util.Arrays;
import javax.ejb.EJB;

import javax.ejb.Stateless;

import net.wazari.dao.MaintFacadeLocal;
import net.wazari.service.AlbumLocal;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionMaint.MaintAction;
import net.wazari.service.MaintLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.XmlMaint;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MaintBean implements MaintLocal {

    private static final Logger log = LoggerFactory.getLogger(MaintBean.class.getCanonicalName());

    private static String getPath(Configuration conf) {
        return conf.getBackupPath();
    }
    @EJB
    MaintFacadeLocal maintDAO;
    @EJB
    private TagLocal tagService;
    @EJB
    private AlbumLocal albumService;

    public XmlMaint treatMAINT(ViewSessionMaint vSession) throws WebAlbumsServiceException {
        MaintAction action = vSession.getMaintAction();

        XmlMaint output = new XmlMaint();
        if (MaintAction.EXPORT_XML == action) {
            maintDAO.treatExportXML(getPath(vSession.getConfiguration()));
        } else if (MaintAction.IMPORT_XML == action) {
            maintDAO.treatImportXML(vSession.getConfiguration().wantsProtectDB(), getPath(vSession.getConfiguration()));
        } else if (MaintAction.TRUNCATE_DB == action) {
            maintDAO.treatTruncateDB(vSession.getConfiguration().wantsProtectDB());
        } else if (MaintAction.PRINT_STATS == action) {
            maintDAO.treatDumpStats();
        } else if (MaintAction.BENCHMARK == action) {
            benchmark(vSession);

        } else if (MaintAction.UPDATE_DAO == action) {
            maintDAO.treatUpdate();
        } else {
            for (MaintAction act : Arrays.asList(MaintAction.values())) {
                output.actions.add(act.toString());
            }
        }
        return output;
    }
    private static final int REPETITION = 100;

    private void benchmark(ViewSession vSession) throws WebAlbumsServiceException {
        if (true)return ;
        for (int i = 0; i < REPETITION; i++) {
            StopWatch stopWatch = new Slf4JStopWatch("Benchmark.all", log);
            benchmarkTags(vSession);
            benchmarkAlbums(vSession);
            stopWatch.stop();
        }
    }

    private void benchmarkTags(ViewSession vSession) {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Tag", log);
        benchmarkTagCloud(vSession);
        benchmarkTagPlaces(vSession);
        stopWatch.stop();
    }

    private void benchmarkTagCloud(ViewSession vSession) {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Tag.Cloud", log);
        tagService.treatTagCloud((ViewSessionTag) vSession);
        stopWatch.stop();
    }

    private void benchmarkTagPlaces(ViewSession vSession) {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Tag.Places", log);
        tagService.treatTagPlaces((ViewSessionTag) vSession);
        tagService.treatTagPersons((ViewSessionTag) vSession);
        stopWatch.stop();
    }

    private void benchmarkAlbums(ViewSession vSession) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Album", log);
        benchmarkAlbumsDisplay(vSession);
        benchmarkAlbumsTop(vSession);
        benchmarkAlbumsSelect(vSession);
        benchmarkAlbumsYears(vSession);
        stopWatch.stop();
    }

    private void benchmarkAlbumsDisplay(ViewSession vSession) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Album.Display", log);
        albumService.treatAlbmDISPLAY((ViewSessionAlbumDisplay) vSession, null);
        stopWatch.stop();
    }

    private void benchmarkAlbumsTop(ViewSession vSession) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Album.Top", log);
        albumService.treatTOP((ViewSessionAlbum) vSession);
        stopWatch.stop();
    }

    private void benchmarkAlbumsSelect(ViewSession vSession) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Album.Select", log);
        albumService.treatSELECT((ViewSessionAlbum) vSession);
        stopWatch.stop();
    }

    private void benchmarkAlbumsYears(ViewSession vSession) throws WebAlbumsServiceException {
        StopWatch stopWatch = new Slf4JStopWatch("Benchmark.Album.years", log);
        albumService.treatYEARS((ViewSessionAlbum) vSession);
        stopWatch.stop();
    }
}
