from gi.repository import Gtk, GdkPixbuf, GObject, Gdk

GObject.threads_init()

from photowall import DEFAULTS, PARAMS, do_main
import photowall
import threading
import time
import os

def long_substr(data):
  substr = ''
  if len(data) > 1 and len(data[0]) > 0:
    for i in range(len(data[0])):
      for j in range(len(data[0])-i+1):
        if j > len(substr) and all(data[0][i:i+j] in x for x in data):
          substr = data[0][i:i+j]
  return substr
  
  
class UpdateCallback:
  def __init__(self, builder, handler):
    self.builder = builder
    self.handler = handler
    
    self.paused = False
    self.stopped = False
    
    self.log = []
    
  def newExec(self):
    self.handler.updateImage()
    img = self.builder.get_object('imgPreview')
    img.set_visible(True)
  
  def newImage(self, row=0, col=0, filename=""):
    msg = "#%d#%d %s" % (row, col, filename)
    
    self.builder.get_object('lblInfo').set_text(msg)
    self.builder.get_object('lblInfo1').set_text(msg)
    self.log.append((row, col, filename))
    
  def updLine(self, row, name):
    self.handler.updateImage(name, major=False)
      
  def newFinal(self, name):
    self.handler.updateImage(major=False)
    self.handler.updateImage(name, major=True, alone=True)
    
  def finished(self, name):
    self.handler.updateImage(major=False)
    self.builder.get_object('lblInfo').set_text("Finished: %s" % name)
    self.builder.get_object('lblInfo1').set_text("Finished: %s" % name)
    
    img = self.builder.get_object('imgPreview')
    img.set_visible(False)

  def stopRequested(self):
    return self.stopped
    
  def checkPause(self):
    while self.paused:
      time.sleep(1)
      if self.stopped:
        break
    
class Handler:
  def __init__(self, builder):
    self.builder = builder
    
    self.saver_cb = None
    self.running = None
    
    self.wasRunning = False
    self.is_fullscreen = False
    
    self.thr = None
    
    self.init()
    
    self.onWebAlbmFS()
    self.onPolaroid()
    self.onRandom()
    
  def init(self):
    self.builder.get_object("winFullscreen").modify_bg(Gtk.StateType.NORMAL, Gdk.color_parse('black'))
    for imgName in ('imgPreview', 'imgPreview2', 'imgPreviewFull', 'imgPreview2Full'):
      self.builder.get_object(imgName).modify_bg(Gtk.StateType.NORMAL, Gdk.color_parse('black'))
      self.builder.get_object('imgPreview').set_valign(Gtk.Align.FILL)
      
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
    
    self.builder.get_object('ckCaption').set_active(not DEFAULTS["WANT_NO_CAPTION"])
    
    # False if we want to add pictures randomly
    self.builder.get_object('ckRandom').set_active(DEFAULTS["PUT_RANDOM"])
    
    self.builder.get_object('ckResize').set_active(DEFAULTS["DO_RESIZE"])
    
    ### VFS options ###
    
    self.builder.get_object('ckMini').set_active(not DEFAULTS["NO_SWITCH_TO_MINI"])
    
    ### Directory options ###
    
    # False if we pick directory images sequentially, false if we take them randomly
    
    ## Random wall options ##
    self.builder.get_object('txtSleep').set_value(DEFAULTS["SLEEP_TIME"])
    
    self.updateImage()
    
    self.is_jnetFS = False
    self.checkJnetFS()
    
    self.onWebAlbmFS()
    self.onPolaroid()
    self.onRandom()
  
  def onExternalButton(self, *args):
    os.system("eog %s &" % self.builder.get_object('btSelectTarget').get_label())
  
  def onSelectTarget(self, *args):
    saver = self.builder.get_object('fileSaverDialog')
    
    def selectTarget_cb(target_filename):
        if target_filename is not None:
            self.builder.get_object("btSelectTarget").set_label(target_filename)
            
        self.checkJnetFS()
        
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
  
  def checkJnetFS(self):
    path = self.builder.get_object('btSelectTarget').get_label()
    self.onJnetFS(photowall.path_is_jnetfs(path))
  
  def onJnetFS(self, is_jnet):
    self.builder.get_object('ckWebAlbumFS').set_active(is_jnet)
    self.is_jnetFS = is_jnet
    
  def onDeleteWindow(self, *args):
    self.doQuit()
    Gtk.main_quit(*args)
      
  def onDestroy(self, *args):
    self.doQuit()
    Gtk.main_quit(*args)
  
  def doQuit(self):
    if photowall.updateCB is not None:
      photowall.updateCB.stopped = True
      
    print("bye")
  
  def onBtFullscreen(self, *args):
    fullscreen = self.builder.get_object('winFullscreen')
    
    self.is_fullscreen = not self.is_fullscreen
    
    fullscreen.set_visible(self.is_fullscreen)
    if self.is_fullscreen:
      fullscreen.fullscreen()
    else:
      fullscreen.unfullscreen()
    
  def onFullscreenDeleteEvent(self, *args):
    fullscreen = self.builder.get_object('winFullscreen')
    fullscreen.set_visible(False)
    self.is_fullscreen = False
    return Gtk.false
  
  def onWebAlbmFS(self, *args):
    use_fs = self.builder.get_object('ckWebAlbumFS').get_active()
    do_pol = self.builder.get_object('ckPolaroid').get_active()
    
    self.builder.get_object('ckCaption').set_sensitive(do_pol and use_fs)
    self.builder.get_object('ckMini').set_sensitive(use_fs)
    self.builder.get_object('ckPickRandom').set_sensitive(not use_fs)
    print("use", use_fs)
    print("is", self.is_jnetFS)
    color = None if self.is_jnetFS == use_fs else Gdk.color_parse("red")
    self.builder.get_object('ckWebAlbumFS').modify_bg(Gtk.StateType.NORMAL, color)
    
    
  def onPolaroid(self, *args):
    do_pol = self.builder.get_object('ckPolaroid').get_active()
    use_fs = self.builder.get_object('ckWebAlbumFS').get_active()
    
    self.builder.get_object('lblMinCrop').set_sensitive(do_pol)
    self.builder.get_object('txtMinCrop').set_sensitive(do_pol)
    
    self.builder.get_object('ckCaption').set_sensitive(do_pol and use_fs)
    
    self.builder.get_object('ckWrap').set_sensitive(not do_pol)
    self.builder.get_object('ckRandom').set_sensitive(do_pol)
  
  def onRandom(self, *args):
    do_rand = self.builder.get_object('ckRandom').get_active()
    
    self.builder.get_object('ckWrap').set_sensitive(not do_rand)
    self.builder.get_object('ckLoop').set_sensitive(not do_rand)
    self.builder.get_object('ckRemove').set_sensitive(do_rand)
    
    self.builder.get_object('lblMinCrop').set_sensitive(not do_rand)
    self.builder.get_object('txtMinCrop').set_sensitive(not do_rand)
  
  def onResetButton(self, *args):        
    self.onStopButton(args)
    self.init()
  
  def onStopButton(self, *args):
    go = self.builder.get_object("btGo")
    go.set_label("Start")
    go.set_image(self.builder.get_object('imgPlay'))
    self.builder.get_object("btGo1").set_image(self.builder.get_object('imgPlay1'))
    self.running = None
    
    if photowall.updateCB is not None:
      photowall.updateCB.stopped = True
      
  def onStartButton(self, *args):
    if self.running is None:
      correct = self.doStart()
    elif self.running:
      correct = self.doPause()
    else:
      correct = self.doContinue()
    
    for num in ('', '1'):
      bt = self.builder.get_object("btGo"+num)
      if correct:
        if self.running is None or not self.running:
          if num != '1': bt.set_label("Pause")
          bt.set_image(self.builder.get_object('imgPause'+num))
        else:
          if num != '1': bt.set_label("Continue")
          bt.set_image(self.builder.get_object('imgPlay'+num))
          
    # switch and get rid of None
    self.running = True if not self.running else False 
    
      
  def onInfoButton(self, *args):
    self.wasRunning = False
    if self.running:
      self.onStartButton()
      self.wasRunning = True
    
    inf = self.builder.get_object('infoGrid')
    inf.set_visible(True)
    inf.set_title("Grid information")
    
    if photowall.updateCB and photowall.updateCB.log:
      # get the length of the longest common substring
      substr_len = len(long_substr([s[2] for s in photowall.updateCB.log]))
      # decrease the prefix to the last /
      substr_len = photowall.updateCB.log[0][2][:substr_len].rindex("/") + 1
      txt = ["(%d, %d) %s" % (r, c, f[substr_len:]) for r, c, f in photowall.updateCB.log]
      
      inf.set_markup("\n".join(txt))
    
  def onInfoGridDeleteEvent(self, *args):
    self.onInfoGridClose(args)
    return Gtk.false
  
  def onInfoGridClose(self, *args):
    inf = self.builder.get_object('infoGrid')
    inf.set_visible(False)
    if self.wasRunning:
      self.onStartButton()
  
  def doFinished(self):
    loop = self.builder.get_object('ckLoop')
    if loop.get_active():
      time.sleep(1)
      
      self.doStart()
    else:
      self.onStopButton(None)
      
  def doContinue(self):
    photowall.updateCB.paused = False
    
    return True
  
  def doStop(self):
    photowall.updateCB.stopped = True
  
  def updateImage(self, filename=None, major=None, alone=False):
    if filename is None:
      def do_clear(imgName):
        i = self.builder.get_object(imgName)
        i.clear()
        
        if self.is_fullscreen:
          i = self.builder.get_object(imgName+"Full")
          i.clear()
          
      if major is None or major:
        do_clear("imgPreview2")
      if major is None or not major:
        do_clear("imgPreview")
      
      return
    
    pixbuf = GdkPixbuf.Pixbuf.new_from_file(filename)
      
    img_width = pixbuf.get_width()
    img_height = pixbuf.get_height()
    
    ratio = (img_width+0.0)/img_height
    
    def set_image(name, full=False):
      i = self.builder.get_object(name+("Full" if full else ""))
      
      # use NB_LINE to compute the right size
      # major is max NB_LINE,
      # minor is max 1 line
      box_preview = builder.get_object("boxPreview"+("Full" if full else ""))
      box_width = box_preview.get_allocation().width
      box_height = box_preview.get_allocation().height
      
      if alone:
        allowed_height = box_height
      elif major:
        allowed_height = box_height * (PARAMS["LINES"]-1.0)/PARAMS["LINES"]
      else:
        allowed_height = box_height * 1.0/PARAMS["LINES"]
      
      if img_height*(box_width+0.0)/img_width <= allowed_height:
        width = box_width
        height = img_height*(box_width+0.0)/img_width
      else:
        width = img_width*(allowed_height+0.0)/img_height
        height = allowed_height
        
      
      pixbuf_scaled = pixbuf.scale_simple(width, height, GdkPixbuf.InterpType.BILINEAR)
      i.set_from_pixbuf(pixbuf_scaled)
      i.set_visible(True)
    
    imgName = "imgPreview%s" % ("2" if major else "")
    
    set_image(imgName)
    if self.is_fullscreen:
      set_image(imgName, full=True)
    
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
        
    PARAMS["WANT_NO_CAPTION"] = not self.builder.get_object('ckCaption').get_active()
        
    # False if we want to add pictures randomly
    PARAMS["PUT_RANDOM"] = self.builder.get_object('ckRandom').get_active()
        
    PARAMS["DO_RESIZE"] = self.builder.get_object('ckResize').get_active()
        
    ### VFS options ###
        
    PARAMS["NO_SWITCH_TO_MINI"] = not self.builder.get_object('ckMini').get_active()
        
    ### Directory options ###
    
    # False if we pick directory images sequentially, false if we take them randomly
    PARAMS["PICK_RANDOM"] = self.builder.get_object('ckPickRandom').get_sensitive()
    
    ## Random wall options ##
    PARAMS["SLEEP_TIME"] = self.builder.get_object('txtSleep').get_value()
    
    
    if photowall.updateCB is not None:
      photowall.updateCB.stopped = True
    
    if self.builder.get_object('ckRemove').get_active():
      try:
        os.unlink(PARAMS["TARGET"])
      except:
        pass
    
    
    photowall.updateCB = UpdateCallback(self.builder, self)  
      
    def run_main():
      do_main()
      self.doFinished()
      
    self.thr = threading.Thread(target=run_main)
    self.thr.start()
    
    return True
      
  def doPause(self):
    print("do pause")
    photowall.updateCB.paused = True
    
    return True
    
builder = Gtk.Builder()

builder.add_from_file("Gui.glade")
builder.get_object("main").show_all()
builder.connect_signals(Handler(builder))

Gtk.main()
