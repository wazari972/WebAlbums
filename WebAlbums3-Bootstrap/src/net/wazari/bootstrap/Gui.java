/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;
import javax.swing.jtray.*;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
import org.glassfish.embeddable.GlassFishException;

/**
 *
 * @author kevin
 * 
 * @TODO: 
 *   - VFS: add PATH to URL params
 *   - VFS: ensure that we can change the java.library.path at runtime
 *   - root path: force root path from bootloader
 * 
 *   - test all this :)
 */
public class Gui extends JFrame {
    private static final boolean WANT_SYSTRAY = true;
    
    public static String config_path = GF.DEFAULT_CONFIG_PATH;
    
    private static GF glassfish = new GF();
    
    public static void main(String args[]) throws Throwable {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Gui gui = new Gui();
                } catch (Throwable ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
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
    final JMenuItem miStart;
    final JMenuItem miQuit;
    final JMenuItem miShutdown;
    final JMenuItem miLaunch;
    
    final JMenu mWebAlbumsFS;
    final JMenuItem miMount;
    final JMenuItem miUmount;
    final JMenuItem miOpen;
    
    final JMenu mConfig;
    final JMenuItem miCfgRootpath;
    final JMenuItem miCfgFSPath;
    final JMenuItem miCfgLibFSPath;
    final JMenuItem miCfgLoad;
    final JMenuItem miCfgSave;
    
    JTrayIcon icon = null;
    
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
    //mi.setIcon(new ImageIcon(getClass().getResource("/testswing/icone.gif")));
    public Gui() throws Throwable {
        String title = "WebAlbums3.5-dev GUI Bootloader";

        ToAddTo toAddTo;
        if (WANT_SYSTRAY && SystemTray.isSupported()) {
            final JPopupMenu jpop = new JPopupMenu();

            JTrayIcon.initSystemTray();

            Image img = createImage("/images/favicon-orange.png", "WebAlbums Favicon");
            icon = new JTrayIcon(img, null, jpop);
            
            toAddTo = new ToAddTo() {
                public void add(JMenuItem item) {
                    jpop.add(item);
                }
            };
        } else {
            this.setTitle(title);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            final JMenuBar mb = new JMenuBar();   
            this.setJMenuBar(mb);
            
            toAddTo = new ToAddTo() {
                public void add(JMenuItem item) {
                    mb.add(item);
                }
            };
        }
        
        mWebAlbums = new JMenu();
        mWebAlbums.setText("WebAlbums");
        toAddTo.add(mWebAlbums);

        miStart = new JMenuItem();
        mWebAlbums.add(miStart);
        miStart.addActionListener(new StartActionListener());
        miStart.setText("Start");
        
        mWebAlbums.addSeparator();
        
        miLaunch = new JMenuItem();
        mWebAlbums.add(miLaunch);
        miLaunch.addActionListener(new LaunchActionListener());
        miLaunch.setText("Launch");
        
        mWebAlbums.addSeparator();
        
        miShutdown = new JMenuItem();
        mWebAlbums.add(miShutdown);
        miShutdown.addActionListener(new QuitActionListener(false));
        miShutdown.setText("Shutdown");
        
        miQuit = new JMenuItem();
        mWebAlbums.add(miQuit);
        miQuit.addActionListener(new QuitActionListener(true));
        
        mWebAlbumsFS = new JMenu();
        mWebAlbumsFS.setText("Filesystem");
        toAddTo.add(mWebAlbumsFS);
                
        miMount = new JMenuItem();
        miMount.setText("Mount"); 
        mWebAlbumsFS.add(miMount);
        miMount.addActionListener(new MountActionListener());
        
        mWebAlbumsFS.addSeparator();
        
        miOpen = new JMenuItem();
        miOpen.setText("Open"); 
        mWebAlbumsFS.add(miOpen);
        miOpen.addActionListener(new OpenActionListener());
        
        mWebAlbumsFS.addSeparator();
        
        miUmount = new JMenuItem();
        miUmount.setText("Unmount"); mWebAlbumsFS.add(miUmount);
        miUmount.addActionListener(new MountActionListener());
        
        mConfig = new JMenu();
        mConfig.setText("Configuration");
        toAddTo.add(mConfig);
        
        miCfgRootpath = new JMenuItem();
        miCfgRootpath.setText("WebAlbums root path"); 
        mConfig.add(miCfgRootpath);
        miCfgRootpath.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "Root path was "+GF.cfg.root_path);
                GF.cfg.root_path = str;
            }

            public String getString() {
                return GF.cfg.root_path;
            }
        }, true, null));
        
        miCfgFSPath = new JMenuItem();
        miCfgFSPath.setText("Filesystem mount path");
        mConfig.add(miCfgFSPath);
        miCfgFSPath.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "webAlbumsFS was "+GF.cfg.webAlbumsFS);
                GF.cfg.webAlbumsFS = str;
            }

            public String getString() {
                return GF.cfg.webAlbumsFS;
            }
        }, true, null));
        
        mConfig.addSeparator();
        
        miCfgLibFSPath = new JMenuItem();
        miCfgLibFSPath.setText("Library libjnetfs.so path");
        mConfig.add(miCfgLibFSPath);
        miCfgLibFSPath.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, "libJnetFs was "+GF.cfg.libJnetFs);
                GF.cfg.libJnetFs = str;
            }

            public String getString() {
                return GF.cfg.libJnetFs;
            }
        }, false, "libJnetFS.so"));
        
        mConfig.addSeparator();
        
        miCfgLoad = new JMenuItem();
        miCfgLoad.setText("Load configuration"); 
        mConfig.add(miCfgLoad);
        miCfgLoad.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
                Gui.config_path = str;
                try {
                    GF.Config.load(Gui.config_path);
                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public String getString() {
                return Gui.config_path;
            }
        }, false, ".xml"));
        
        mConfig.addSeparator();
        
        miCfgSave = new JMenuItem();
        miCfgSave.setText("Save configuration");
        mConfig.add(miCfgSave);
        miCfgSave.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
                Gui.config_path = str;
                try {
                    GF.cfg.save(Gui.config_path);
                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public String getString() {
                return Gui.config_path;
            }
        }, false, ".xml"));
        
        glassfishStopped();
        
        if (SystemTray.isSupported()) {
            
        } else {
            this.setVisible(true);
        }
    }

    private void glassfishStarting() {
        miCfgRootpath.setEnabled(false);
        miCfgLoad.setEnabled(false);
        miStart.setEnabled(false);
        miQuit.setText("Force quit");
        miStart.setText("Starting ...");
        icon.setIcon(createImage("/images/favicon-orange.png", "WebAlbums Favicon"));
        gfState = GlassfishState.STARTING;
    }
    
    private void glassfishRunning() {
        gfState = GlassfishState.RUNNING;
        miQuit.setText("Shutdown & quit");
        miShutdown.setEnabled(true);
        miLaunch.setEnabled(true);
        mWebAlbumsFS.setEnabled(true);
        miMount.setEnabled(true);
        miStart.setText("Running");
        if (icon != null) {
            icon.setIcon(createImage("/images/favicon.png", "WebAlbums Favicon"));
        }
    }
    
    private void glassfishStopped() {
        gfState = GlassfishState.STOPPED;
         
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
        miCfgLibFSPath.setEnabled(true);
        miCfgFSPath.setEnabled(true);
        miCfgRootpath.setEnabled(true); 
        
        if (icon != null) {
            icon.setIcon(createImage("/images/favicon-blue.png", "WebAlbums Favicon"));
        }
    }
    
    private void glassfishFailed() {
        gfState = GlassfishState.FAILED;
        if (icon != null) {
            icon.setIcon(createImage("/images/favicon-red.png", "WebAlbums Favicon"));
        }
    }
    
    private class StartActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            new Thread(new Runnable() {
            public void run() {
                try {
                    glassfishStarting();
                    glassfish.start();
                    glassfishRunning();
                } catch (Throwable ex) {
                    glassfishFailed();
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
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
        
        public void actionPerformed(ActionEvent event) {
            if (gfState == GlassfishState.RUNNING) {
                try {
                    Gui.glassfish.terminate();
                    glassfishStopped();
                } catch (GlassFishException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                    glassfishFailed();
                }
            }
            if (this.quit) {
                System.exit(0);
            }
       }
    }
    
    private interface StringPointer {
        void setString(String str);
        String getString();
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
        public void actionPerformed(ActionEvent event) {
            if (gfState == GlassfishState.RUNNING) {
                try {
                    Desktop.getDesktop().browse(new URL("http://localhost:"+GF.cfg.port+"/WebAlbums3.5-dev").toURI());    
                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
       }
    }
    
    private class OpenActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (gfState == GlassfishState.RUNNING) {
                try {
                    Desktop.getDesktop().open(new File("WebAlbums3-FS"));    
                } catch (Exception ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
       }
    }
    
    private class MountActionListener implements ActionListener {        
        private boolean mounted = false;
        
        private synchronized boolean setMounted(Boolean mounted) {
            boolean wasMounted = this.mounted;
            if (mounted != null) {
                this.mounted = mounted;
                Gui.this.miMount.setEnabled(!mounted);
                
                Gui.this.miUmount.setEnabled(mounted);
                Gui.this.miOpen.setEnabled(mounted);
                Gui.this.miCfgFSPath.setEnabled(mounted);
            }
            return wasMounted;
        }
        
        public boolean isMounted() {
            return setMounted(null);
        }
        
        public void actionPerformed(ActionEvent event) {
            doTrigger(!isMounted(), GF.cfg.webAlbumsFS);
            
        }
        
        public void doTrigger(final boolean mount, String mountPoint) {
            try {
                mountPoint = mountPoint == null ? "" : mountPoint;
                URL url = new URL("http://localhost:"+GF.cfg.port+"/WebAlbums3-FS/"+(mount ? "Launch" : "Unlaunch")+"?path="+mountPoint);
                final URLConnection conn = url.openConnection();

                new Thread (new Runnable() {

                    public void run() {
                        if (mount) {
                            setMounted(true);
                        }
                        try {
                            // open the stream and put it into BufferedReader
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((br.readLine()) != null) ;
                        } catch (IOException ex) {
                            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //will be set by mount thread only
                        if (mount) {
                            setMounted(false);
                        }
                    }
                }).start();
                
            } catch (IOException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
    }
}