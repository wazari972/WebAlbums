from gi.repository import Gtk, GdkPixbuf, GObject

GObject.threads_init()

from photowall import DEFAULTS, PARAMS, do_main
import photowall
import threading

def updateImage(filename, imgName):
  i = builder.get_object(imgName)
  if filename is None:
    i.clear()
    i.set_visible(False)
    return
  
  pixbuf = GdkPixbuf.Pixbuf.new_from_file(filename)
    
  img_width = pixbuf.get_width()
  img_height = pixbuf.get_height()
  
  ratio = (img_width+0.0)/img_height
  
  box_width = i.get_allocation().width
  box_height = i.get_allocation().height
  
  if img_height*(box_width+0.0)/img_width <= box_height:
    width = box_width
    height = img_height*(box_width+0.0)/img_width
  else:
    width = img_width*(box_height+0.0)/img_height
    height = box_height
 
  
  pixbuf = pixbuf.scale_simple(width, height, GdkPixbuf.InterpType.BILINEAR)
  i.set_from_pixbuf(pixbuf)
  
  i.set_visible(True)
  
  
class UpdateCallback:
  def __init__(self, builder):
    self.builder = builder
    
  def newExec(self):
    updateImage(None, 'imgPreview')
    updateImage(None, 'imgPreview2')
  
  def newImage(self, row, col, filename):
    i = self.builder.get_object('lblInfo')
    i.set_text("#%d#%d %s" % (row, col, filename))
    
  def updLine(self, row, name):
    updateImage(name, 'imgPreview')
      
  def newFinal(self, name):
    updateImage(None, 'imgPreview')
    updateImage(name, 'imgPreview2')
    
  def finished(self, name):
    updateImage(None, 'imgPreview')
    i = self.builder.get_object('lblInfo')
    i.set_text("Finished: %s" % name)

class Handler:
    def __init__(self, builder):
        self.builder = builder
        
        self.init()
        
        self.onWebAlbmFS()
        self.onPolaroid()
        
        self.saver_cb = None
        self.running = False
        
    def init(self):
        self.builder.get_object('fileSource').set_filename(DEFAULTS["PATH"])
        self.builder.get_object('btSelectTarget').set_label(DEFAULTS["TARGET"])
        #define the size of the picture
        self.builder.get_object('txtWidth').set_value(DEFAULTS["WIDTH"])
        
        #define how many lines do we want
        self.builder.get_object('txtLines').set_value(DEFAULTS["LINES"])
        
        self.builder.get_object('txtLineHeight').set_value(DEFAULTS["LINE_HEIGHT"])
        
        # minimum width of cropped image. Below that, we black it out
        # only for POLAROID
        self.builder.get_object('txtMinCrop').set_value(DEFAULTS["CROP_SIZE"])
        
        # False if PATH is a normal directory, True if it is WebAlbums-FS
        self.builder.get_object('ckWebAlbumFS').set_active(DEFAULTS["USE_VFS"])
        
        # True if end-of-line photos are wrapped to the next line
        self.builder.get_object('ckWrap').set_active(DEFAULTS["DO_WRAP"])
        # True if we want a black background and white frame, plus details
        self.builder.get_object('ckPolaroid').set_active(DEFAULTS["DO_POLAROID"])
        
        self.builder.get_object('ckCaption').set_active(DEFAULTS["WANT_NO_CAPTION"])
        
        # False if we want to add pictures randomly
        self.builder.get_object('ckRandom').set_active(DEFAULTS["PUT_RANDOM"])
        
        self.builder.get_object('ckResize').set_active(DEFAULTS["DO_RESIZE"])
        
        ### VFS options ###
        
        self.builder.get_object('ckMini').set_active(not DEFAULTS["NO_SWITCH_TO_MINI"])
        
        ### Directory options ###
        
        # False if we pick directory images sequentially, false if we take them randomly
        
        ## Random wall options ##
        self.builder.get_object('txtSleep').set_value(DEFAULTS["SLEEP_TIME"])
        
        updateImage(None, 'imgPreview')
        updateImage(None, 'imgPreview2')
    
    def onSelectTarget(self, *args):
        saver = self.builder.get_object('fileSaverDialog')
        
        def selectTarget_cb(target_filename):
            if target_filename is not None:
                self.builder.get_object("btSelectTarget").set_label(target_filename)
            
        self.saver_cb = selectTarget_cb
        
        saver.set_visible(True)
    
    def onSaverCancel(self, *args):
        saver = self.builder.get_object('fileSaverDialog')
        saver.set_visible(False)
        
        if callable(self.saver_cb):
            self.saver_cb(None)
        
    def onSaverOk(self, *args):
        saver = self.builder.get_object('fileSaverDialog')
        saver.set_visible(False)
        if callable(self.saver_cb):
            self.saver_cb(saver.get_filename())
        
    def onDeleteWindow(self, *args):
        print "bye"
        Gtk.main_quit(*args)
        
    def onDestroy(self, *args):
        print "bye"
        Gtk.main_quit(*args)
    
    def onWebAlbmFS(self, *args):
        use_fs = self.builder.get_object('ckWebAlbumFS').get_active()
        do_pol = self.builder.get_object('ckPolaroid').get_active()
        
        self.builder.get_object('ckCaption').set_sensitive(do_pol and use_fs)
        self.builder.get_object('ckMini').set_sensitive(use_fs)
    
    def onPolaroid(self, *args):
        do_pol = self.builder.get_object('ckPolaroid').get_active()
        use_fs = self.builder.get_object('ckWebAlbumFS').get_active()
        
        self.builder.get_object('lblMinCrop').set_sensitive(do_pol)
        self.builder.get_object('txtMinCrop').set_sensitive(do_pol)
        
        self.builder.get_object('ckCaption').set_sensitive(do_pol and use_fs)
        
        self.builder.get_object('ckWrap').set_sensitive(not do_pol)
        self.builder.get_object('ckRandom').set_sensitive(do_pol)
    
    def onResetButton(self, *args):        
        self.onStopButton(args)
        self.init()
    
    def onStopButton(self, *args):
        bt = self.builder.get_object('btGo')
        bt.set_active(False)
        bt.set_label("Start")
        bt.set_image(self.builder.get_object('imgPlay'))
        self.running = False
    
    def onStartButton(self, *args):
        bt = self.builder.get_object('btGo')
        
        correct = self.doStart() if not self.running else self.doPause()
        
        if correct:
            if not self.running:
                bt.set_label("Pause")
                bt.set_image(self.builder.get_object('imgPause'))
            else:
                bt.set_label("Continue")
                bt.set_image(self.builder.get_object('imgPlay'))
            self.running = not self.running
            
        bt.set_active(self.running)
    
    def doFinished(self):
        self.onStopButton(None)
    
    def doStop(self):
        pass
        
    def doStart(self):
        PARAMS["PATH"] = self.builder.get_object('fileSource').get_filename()
        PARAMS["TARGET"] =  self.builder.get_object('btSelectTarget').get_label()
        #define the size of the picture
        PARAMS["WIDTH"] = int(self.builder.get_object('txtWidth').get_value())
            
        #define how many lines do we want
        PARAMS["LINES"] = int(self.builder.get_object('txtLines').get_value())
            
        PARAMS["LINE_HEIGHT"] = int(self.builder.get_object('txtLineHeight').get_value())
            
        # minimum width of cropped image. Below that, we black it out
        # only for POLAROID
        PARAMS["CROP_SIZE"] = int(self.builder.get_object('txtMinCrop').get_value())
            
        PARAMS["IMG_FORMAT_SUFFIX"] = ".png"
            
        # False if PATH is a normal directory, True if it is WebAlbums-FS
        PARAMS["USE_VFS"] = self.builder.get_object('ckWebAlbumFS').get_active()
        PARAMS["FORCE_VFS"] = False
        PARAMS["FORCE_NO_VFS"] = False
            
        # True if end-of-line photos are wrapped to the next line
        PARAMS["DO_WRAP"] = self.builder.get_object('ckWrap').get_active()
        # True if we want a black background and white frame, plus details
        PARAMS["DO_POLAROID"] = self.builder.get_object('ckPolaroid').get_active()
            
        PARAMS["WANT_NO_CAPTION"] = self.builder.get_object('ckCaption').get_active()
            
        # False if we want to add pictures randomly
        PARAMS["PUT_RANDOM"] = self.builder.get_object('ckRandom').get_active()
            
        PARAMS["DO_RESIZE"] = self.builder.get_object('ckResize').get_active()
            
        ### VFS options ###
            
        PARAMS["NO_SWITCH_TO_MINI"] = not self.builder.get_object('ckMini').get_active()
            
        ### Directory options ###
            
        # False if we pick directory images sequentially, false if we take them randomly
        PARAMS["PICK_RANDOM"] = False #not implemented yet
            
        ## Random wall options ##
        PARAMS["SLEEP_TIME"] = self.builder.get_object('txtSleep').get_value()
        
        photowall.updateCB = UpdateCallback(self.builder)
        
        
        def run_main():
          do_main()
          self.doFinished()
          
        thr = threading.Thread(target=run_main).start()
        
        return True
        
    def doPause(self):
        return True
    
builder = Gtk.Builder()

builder.add_from_file("Gui.glade")
builder.get_object("main").show_all()
builder.connect_signals(Handler(builder))

Gtk.main()