/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.jtray.*;
import javax.xml.bind.JAXBException;
import net.wazari.bootstrap.AppServer.AppServerException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */

public class Gui extends JFrame {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GF.class.getName());

    private static final boolean WANT_SYSTRAY = true;
    
    private static final AppServer server = new Tomee();

    public static void startServer() throws AppServerException, IOException {
        long timeStart = System.currentTimeMillis();
        log.warn("Starting WebAlbums GF bootstrap");
        
        try {
            log.info(Util.cfg.print());
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File earfile = new File(Util.cfg.webAlbumsEAR);
        log.warn("Using EAR: {}", earfile);
        if (!earfile.exists()) {
            log.warn("The earFile {} doesn't exist ...", earfile.getAbsolutePath());
            return;
        }

        try {
            new ServerSocket(Util.cfg.port).close();
        } catch (BindException e) {
            log.error("Port {} already in use", new Object[]{Util.cfg.port});
            return;
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!Util.cfg.root_path.endsWith("/")) {
            Util.cfg.root_path += "/" ;
        }
        log.info("Setting root path: {}", Util.cfg.root_path);
        System.setProperty("root.path", Util.cfg.root_path);
        
        log.info("Setting java library path: {}", Util.cfg.libJnetFs);
        Util.addToJavaLibraryPath(new File(Util.cfg.libJnetFs));
        
       
        server.start(Util.cfg.port);
        
        server.createJDBC_add_Resources(Util.cfg.sunResourcesXML);
        
        
        for (Util.Config.User usr : Util.cfg.user) {
            server.createUsers(usr);
        }
    
        log.info("Deploying EAR: {}", Util.cfg.webAlbumsEAR);
        server.deploy(earfile);
        
        long loadingTime = System.currentTimeMillis();
        float time = ((float) (loadingTime - timeStart) / 1000);
        
        log.info("Ready to server at http://localhost:{}/WebAlbums3.5-dev after {}s", new Object[]{Integer.toString(Util.cfg.port), time});
    }
    
    public static void waitForPortStop() throws IOException {
        try (ServerSocket servSocker = new ServerSocket(Util.cfg.port+1)) {
            servSocker.accept().close();
        }
    }
    
    public static void main(final String args[]) throws Throwable {
        boolean no_gui = false;
        for (String arg : args) {
            if ("--no-gui".equals(arg) || "-nx".equals(arg)) {
                no_gui = true;
            }
        }
        
        if (no_gui) {
            startServer();
            waitForPortStop();
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Gui(args);
                } catch (Throwable ex) {
                    log.error("New GUI error:", ex);
                }
            }
        });
    }
    
    public interface ToAddTo {
        void add(JMenuItem item) ;
    }
    
    enum GlassfishState {STOPPED, STARTING, RUNNING, FAILED}
    
    GlassfishState gfState ;
    
    final JMenu mWebAlbums;
    final JMenuItem miState;
    final JMenuItem miStart;
    final JMenuItem miQuit;
    final JMenuItem miShutdown;
    final JMenuItem miLaunch;
    
    final JMenu mWebAlbumsFS;
    final JMenuItem miMount;
    final JMenuItem miUmount;
    final JMenuItem miOpen;
    
    final JMenu mConfig;
    final JMenu mConfigPath;
    final JMenuItem miCfgPathRoot;
    final JMenuItem miCfgPathFS;
    final JMenuItem miCfgPathLibFS;
    final JMenuItem miOpenRoot;
    final JMenuItem miCfgLoad;
    final JMenuItem miCfgSave;
    final JMenuItem miCfgVerify;
    
    JTrayIcon icon = null;
    
    public String config_path = Util.DEFAULT_CONFIG_PATH;
    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = Gui.class.getResource(path);
        
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
    
    public Gui(String args[]) throws Throwable {
        boolean do_start = false;
        for (String arg : args) {
            if ("--start".equals(arg) || "-s".equals(arg)) {
                do_start = true;
            }
        }
        
        String title = "WebAlbums3.5-dev GUI Bootloader";
        boolean systrayed = false;
        
        ToAddTo toAddTo;
        if (WANT_SYSTRAY && SystemTray.isSupported()) {
            systrayed = true;
            final JPopupMenu jpop = new JPopupMenu();

            JTrayIcon.initSystemTray();

            Image img = createImage("/images/jonquille-busy.png", "WebAlbums");
            icon = new JTrayIcon(img, null, jpop);
            
            toAddTo = new ToAddTo() {
                @Override
                public void add(JMenuItem item) {
                    if (item != null) {
                        jpop.add(item);
                    } else {
                        jpop.addSeparator();
                    }
                }
            };
            
            
        } else {
            this.setTitle(title);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            final JMenuBar mb = new JMenuBar();   
            this.setJMenuBar(mb);
            
            toAddTo = new ToAddTo() {
                @Override
                public void add(JMenuItem item) {
                    if (item != null) {
                        mb.add(item);
                    }
                }
            };
        }
        
        ToAddTo toAddToWebAlbums;
        if (!systrayed) {
            mWebAlbums = new JMenu();
            mWebAlbums.setText("WebAlbums");
            toAddTo.add(mWebAlbums);
            
            toAddToWebAlbums = new ToAddTo() {
                @Override
                public void add(JMenuItem item) {
                    if (item != null) {
                        mWebAlbums.add(item);
                    }
                }
            };
            
        } else {
            mWebAlbums = null;
            toAddToWebAlbums = toAddTo;
        }
        
        miState = new JMenuItem();
        toAddToWebAlbums.add(miState);
        
        toAddToWebAlbums.add(null);
        toAddToWebAlbums.add(null);
        
        miStart = new JMenuItem();
        toAddToWebAlbums.add(miStart);
        miStart.addActionListener(new StartActionListener());
        miStart.setText("Start");
        
        toAddToWebAlbums.add(null);
        
        miLaunch = new JMenuItem();
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            toAddToWebAlbums.add(miLaunch);
            miLaunch.addActionListener(new LaunchActionListener());
            miLaunch.setText("Launch");

            toAddToWebAlbums.add(null);
        }
        
        miShutdown = new JMenuItem();
        toAddToWebAlbums.add(miShutdown);
        miShutdown.addActionListener(new QuitActionListener(false));
        miShutdown.setText("Shutdown");
        
        miQuit = new JMenuItem();
        toAddToWebAlbums.add(miQuit);
        miQuit.addActionListener(new QuitActionListener(true));
        
        toAddTo.add(null);
        toAddTo.add(null);
        
        mWebAlbumsFS = new JMenu();
        mWebAlbumsFS.setText("Filesystem");
        toAddTo.add(mWebAlbumsFS);
                
        miMount = new JMenuItem();
        miMount.setText("Mount"); 
        mWebAlbumsFS.add(miMount);
        miMount.addActionListener(new MountActionListener(true));
        
        mWebAlbumsFS.addSeparator();
        
        miOpen = new JMenuItem();
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            miOpen.setText("Open"); 
            mWebAlbumsFS.add(miOpen);
            miOpen.addActionListener(new OpenActionListener(new StringGetPointer() {

                @Override
                public String getString() {
                    return Util.cfg.webAlbumsFS;
                }
            }));

            mWebAlbumsFS.addSeparator();
        }
        
        miUmount = new JMenuItem();
        miUmount.setText("Unmount"); mWebAlbumsFS.add(miUmount);
        miUmount.addActionListener(new MountActionListener(false));
        
        mConfig = new JMenu();
        mConfig.setText("Configuration");
        toAddTo.add(mConfig);
        
        mConfigPath = new JMenu();
        mConfigPath.setText("Paths");
        mConfig.add(mConfigPath);
        
        miCfgPathRoot = new JMenuItem();
        miCfgPathRoot.setText("WebAlbums root"); 
        mConfigPath.add(miCfgPathRoot);
        miCfgPathRoot.addActionListener(new PathActionListener(new StringPointer() {

            @Override
            public void setString(String str) {
                Util.cfg.root_path = str;
            }

            @Override
            public String getString() {
                return Util.cfg.root_path;
            }
        }, true, null));
        
        miCfgPathFS = new JMenuItem();
        miCfgPathFS.setText("Filesystem mount");
        mConfigPath.add(miCfgPathFS);
        miCfgPathFS.addActionListener(new PathActionListener(new StringPointer() {

            @Override
            public void setString(String str) {
                Util.cfg.webAlbumsFS = str;
            }

            @Override
            public String getString() {
                return Util.cfg.webAlbumsFS;
            }
        }, true, null));
        
        mConfig.addSeparator();
        
        miCfgPathLibFS = new JMenuItem();
        miCfgPathLibFS.setText("libJnetFS.so");
        mConfigPath.add(miCfgPathLibFS);
        miCfgPathLibFS.addActionListener(new PathActionListener(new StringPointer() {

            @Override
            public void setString(String str) {
                Util.cfg.libJnetFs = new File(str).getParent();
            }

            @Override
            public String getString() {
                return Util.cfg.libJnetFs;
            }
        }, false, "libJnetFS.so"));
        
        miOpenRoot = new JMenuItem();
        if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            mConfigPath.addSeparator();
            
            miOpenRoot.setText("Open image folder"); 
            mConfigPath.add(miOpenRoot);
            miOpenRoot.addActionListener(new OpenActionListener(new StringGetPointer() {

                @Override
                public String getString() {
                    return Util.cfg.root_path + File.separator + "data" + File.separator + "images";
                }
            }));

        }
        
        mConfig.addSeparator();
        
        miCfgLoad = new JMenuItem();
        miCfgLoad.setText("Load"); 
        mConfig.add(miCfgLoad);
        miCfgLoad.addActionListener(new PathActionListener(new StringPointer() {
            
            @Override
            public void setString(String str) {
                config_path = str;
                try {
                    Util.Config.load(config_path);
                } catch (Exception ex) {
                    log.error("Load config error: {}", ex);
                }
            }

            @Override
            public String getString() {
                return config_path;
            }
        }, false, ".xml"));
        
        mConfig.addSeparator();
        
        miCfgSave = new JMenuItem();
        miCfgSave.setText("Save");
        mConfig.add(miCfgSave);
        miCfgSave.addActionListener(new PathActionListener(new StringPointer() {

            @Override
            public void setString(String str) {
                config_path = str;
                try {
                    Util.cfg.save(config_path);
                } catch (Exception ex) {
                    log.error("Save config error: {}", ex);
                }
            }

            @Override
            public String getString() {
                return config_path;
            }
        }, false, ".xml"));
        
        mConfig.addSeparator();
        
        miCfgVerify = new JMenuItem();
        miCfgVerify.setText("Verify");
        mConfig.add(miCfgVerify);
        miCfgVerify.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Map<String, Boolean> checkpoints = new HashMap<>();
                
                checkpoints.put("Root path is directory", new File(Util.cfg.root_path).isDirectory());
                checkpoints.put("FS path is directory", new File(Util.cfg.webAlbumsFS).isDirectory());
                checkpoints.put("EAR file exists", new File(Util.cfg.webAlbumsEAR).isFile());
                checkpoints.put("FS library file exists", new File(Util.cfg.libJnetFs+File.separator+"libJnetFS.so").isFile());
                checkpoints.put("Database configuration file exists", new File(Util.cfg.sunResourcesXML).isFile());
                
                //Util.cfg.port is free
                
                StringBuilder res = new StringBuilder();
                for (String key : checkpoints.keySet()) {
                    res.append(key).append(" : ").append(checkpoints.get(key)).append("\n");
                }
                
                JOptionPane.showMessageDialog(Gui.this, res.toString());
            }
        });
                
        glassfishStopped();
        
        if (!SystemTray.isSupported()) {
            this.setVisible(true);
        }
        
        if (do_start) {
            miStart.doClick();
        }
        
    }

    private void serverStarting() {
        miCfgPathRoot.setEnabled(false);
        miCfgLoad.setEnabled(false);
        miStart.setEnabled(false);
        miQuit.setText("Force quit");
        miState.setText("Starting");
        miCfgPathLibFS.setEnabled(false);
        miCfgVerify.setEnabled(false);
//        icon.setIcon(createImage("/images/jonquille-busy.png", "WebAlbums"));
        gfState = GlassfishState.STARTING;
    }
    
    private void serverRunning() {
        gfState = GlassfishState.RUNNING;
        miQuit.setText("Shutdown & Quit");
        miShutdown.setEnabled(true);
        miLaunch.setEnabled(true);
        mWebAlbumsFS.setEnabled(true);
        miMount.setEnabled(true);
        miState.setText("Running");
        if (icon != null) {
            icon.setIcon(createImage("/images/jonquille-ready.png", "WebAlbums"));
        }
    }
    
    private void glassfishStopped() {
        gfState = GlassfishState.STOPPED;
        
        miState.setText("Stopped");
        miCfgVerify.setEnabled(true);
        miStart.setEnabled(true);
        miLaunch.setEnabled(false);
        miShutdown.setEnabled(false);
        miQuit.setText("Quit");
        miQuit.setEnabled(true);
        
        mWebAlbumsFS.setEnabled(false);
        miMount.setEnabled(false);
        miOpen.setEnabled(false);
        miUmount.setEnabled(false);
        
        miCfgLoad.setEnabled(true); 
        miCfgSave.setEnabled(true);
        miCfgPathLibFS.setEnabled(true);
        miCfgPathFS.setEnabled(true);
        miCfgPathRoot.setEnabled(true); 
        
        if (icon != null) {
            icon.setIcon(createImage("/images/jonquille-stop.png", "WebAlbums"));
        }
    }
    
    private void serverFailed() {
        try {
            server.terminate();
        } catch (AppServerException ex) {
            log.error("Error while terminating the server: {}", ex);
        }
        glassfishStopped();
        miState.setText("Crashed");
        
        gfState = GlassfishState.FAILED;
        if (icon != null) {
            icon.setIcon(createImage("/images/jonquille-crash.png", "WebAlbums"));
        }
    }
    
    private class StartActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverStarting();
                        startServer();
                        serverRunning();
                    } catch (Throwable ex) {
                        log.error("Start glassfish error: {}", ex);
                        serverFailed();
                        
                    }
                }
            }).start();
       }
    }
    
    private class QuitActionListener implements ActionListener {
        private final boolean quit;
        
        public QuitActionListener(boolean quit) {
            this.quit = quit;
        }
        
        @Override
        public void actionPerformed(ActionEvent event) {
            if (gfState == GlassfishState.RUNNING) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            server.terminate();
                            glassfishStopped();
                            if (QuitActionListener.this.quit) {
                                log.error("That's all, folks!");
                                System.exit(0);
                            }
                        } catch (AppServerException ex) {
                            log.error("Error while stopping the server: {}", ex);
                            serverFailed();
                        }
                    }
                }).start();
            } else if (this.quit) {
                log.error("That's all, folks!");
                System.exit(0);
            }
       }
    }
    
    private interface StringGetPointer {
        String getString();
    }
    
    private interface StringPointer extends StringGetPointer {
        void setString(String str);
    }
    
    private class PathActionListener implements ActionListener {
        private final StringPointer ptr;
        private final boolean dirOnly;
        private final String ext;
        PathActionListener(StringPointer ptr, boolean dirOnly, String ext) {
            this.ptr = ptr;
            this.dirOnly = dirOnly;
            this.ext = ext;
        }
        @Override
        public void actionPerformed(ActionEvent event) {
            final JFileChooser fc = new JFileChooser();
            
            if (this.dirOnly) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            fc.setAcceptAllFileFilterUsed(false);
            
            if(ptr.getString() != null)  {
                fc.setCurrentDirectory(new File(ptr.getString()));
            }
            
            fc.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (dirOnly) {
                        return file.isDirectory();
                    } else if (!file.isDirectory() && ext != null) {
                        return file.getName().endsWith(ext);
                    } else {
                        return true;
                    }
                }

                @Override
                public String getDescription() {
                    if (dirOnly) {
                        return "Directories" ;
                    } else if (ext != null) {
                        return "'"+ext+"' files";
                    } else {
                        return "Any file";
                    }
                }
            });
            int returnVal = fc.showOpenDialog(Gui.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                
                ptr.setString(file.getAbsolutePath());
            }
       }
    }
    
    private class LaunchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (gfState == GlassfishState.RUNNING) {
                try {
                    new BrowserLauncher().openURLinBrowser("http://localhost:"+Util.cfg.port+"/WebAlbums3.5-dev");
                } catch (BrowserLaunchingInitializingException | UnsupportedOperatingSystemException ex) {
                    log.error("Launch website error: {}", ex);
                }
            }
       }
    }
    
    private class OpenActionListener implements ActionListener {
        private final StringGetPointer ptr;
        public OpenActionListener(StringGetPointer ptr) {
            this.ptr = ptr;
        }
    
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                Desktop.getDesktop().open(new File(ptr.getString()));    
            } catch (Exception ex) {
                try {
                    Runtime.getRuntime().exec("nautilus "+ptr.getString());
                } catch (Exception e) {
                    log.error("Open FS error: ", ex);
                    log.error("Open FS-2 error: ", ex);
                }
            }
       }
    }
    
    private class MountActionListener implements ActionListener {        
        private final boolean do_mount;
        MountActionListener(boolean do_mount) {
            this.do_mount = do_mount;
        }
        private boolean mounted = false;
        
        private synchronized boolean setMounted(Boolean mounted) {
            boolean wasMounted = this.mounted;
            if (mounted != null) {
                this.mounted = mounted;
                Gui.this.miMount.setEnabled(!mounted);
                
                Gui.this.miUmount.setEnabled(mounted);
                Gui.this.miOpen.setEnabled(mounted);
                Gui.this.miCfgPathFS.setEnabled(mounted);
            }
            return wasMounted;
        }
        
        public boolean isMounted() {
            return setMounted(null);
        }
        
        @Override
        public void actionPerformed(ActionEvent event) {
            doTrigger(do_mount, Util.cfg.webAlbumsFS);
        }
        
        public void doTrigger(final boolean mount, String mountPoint) {
            try {
                mountPoint = mountPoint == null ? "" : mountPoint;
                URL url = new URL("http://localhost:"+Util.cfg.port+"/WebAlbums3-FS/Launch?"+(mount ? "" : "umount=true")+"path="+mountPoint);
                final URLConnection conn = url.openConnection();

                new Thread (new Runnable() {

                    @SuppressWarnings("empty-statement")
                    @Override
                    public void run() {
                        setMounted(mount);
                        try {
                            // open the stream and put it into BufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((br.readLine()) != null) ;
                        } catch (IOException ex) {}
                    }
                }).start();
                
            } catch (IOException ex) {
                log.error("Mount FS error: {}", ex);
            }
       }
    }
    
}