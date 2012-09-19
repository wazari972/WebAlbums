/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.awt.Desktop;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;
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
    private static GF glassfish = new GF();
    
    public static void main(String args[]) throws Throwable {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Gui gui = new Gui();
                    gui.setVisible(true);
                } catch (Throwable ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
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
    
    //mi.setIcon(new ImageIcon(getClass().getResource("/testswing/icone.gif")));
    public Gui() throws Throwable {
        this.setTitle("WebAlbums3.5-dev GUI Bootloader");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar mb = new JMenuBar();
        this.setJMenuBar(mb);

        mWebAlbums = new JMenu();
        mWebAlbums.setText("WebAlbums");
        mb.add(mWebAlbums);

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
        mb.add(mWebAlbumsFS);
                
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
        mb.add(mConfig);
        
        miCfgRootpath = new JMenuItem();
        miCfgRootpath.setText("WebAlbums root path"); 
        mConfig.add(miCfgRootpath);
        miCfgRootpath.addActionListener(new PathActionListener(new StringPointer() {

            public void setString(String str) {
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
        miCfgLoad.addActionListener(new LoadCfgActionListener());
        
        mConfig.addSeparator();
        
        miCfgSave = new JMenuItem();
        miCfgSave.setText("Save configuration");
        mConfig.add(miCfgSave);
        miCfgSave.addActionListener(new SaveCfgActionListener());
        
        glassfishStopped();
    }

    private void glassfishStarting() {
        miCfgRootpath.setEnabled(false);
        miCfgLoad.setEnabled(false);
        miStart.setEnabled(false);
        miQuit.setText("Force quit");
        miStart.setText("Starting ...");
        gfState = GlassfishState.STARTING;
    }
    
    private void glassfishRunning() {
        gfState = GlassfishState.RUNNING;
        miQuit.setText("Shutdown && quit");
        miShutdown.setEnabled(true);
        miLaunch.setEnabled(true);
        mWebAlbumsFS.setEnabled(true);
        miStart.setText("Running");
        miQuit.setText("Shutdown");
    }
    
    private void glassfishStopped() {
        gfState = GlassfishState.STOPPED;
         
        miStart.setEnabled(true);
        miLaunch.setEnabled(false);
        miShutdown.setEnabled(false);
        miQuit.setText("Quit");
        miQuit.setEnabled(true);
        miMount.setEnabled(true);
        miOpen.setEnabled(false);
        miUmount.setEnabled(false);
        miCfgLoad.setEnabled(true); 
        miCfgSave.setEnabled(true);
        miCfgLibFSPath.setEnabled(true);
        miCfgFSPath.setEnabled(true);
        miCfgRootpath.setEnabled(true); 
    }
    
    private void glassfishFailed() {
        gfState = GlassfishState.FAILED;
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
    
    private class SaveCfgActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                GF.cfg.save(GF.DEFAULT_CONFIG_PATH);
            } catch (JAXBException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
    }
    
    private class LoadCfgActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                GF.cfg = GF.Config.load(GF.DEFAULT_CONFIG_PATH);
            } catch (JAXBException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                fc.setCurrentDirectory(new File(ptr.getString()).getParentFile());
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