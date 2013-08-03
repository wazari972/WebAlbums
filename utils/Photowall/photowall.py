#!/usr/bin/env python2

import os
import tempfile
import pipes
import subprocess
import time
import random
import shutil

try:
  from wand.image import Image
  from wand.display import display
except ImportError as e:
  # cd /usr/lib/
  # ln -s libMagickWand-6.Q16.so libMagickWand.so
  print "Couldn't import Wand package."
  print "Please refer to #http://dahlia.kr/wand/ to install it."
  import traceback; traceback.print_exc()
  raise e

try:
  import magic
  mime = magic.Magic()
except ImportError:
  mime = None
  #https://github.com/ahupp/python-magic

try:
  from docopt import docopt
except ImportError:
  print "Couldn't import Docopt package."
  print "Please refer to#https://github.com/docopt/docopt to install it."
  print "/!\\ Option parsing not possible, defaulting to hardcoded values/!\\"

def to_bool(val):
  if val is None:
    return false
  return val == 1
  
def to_int(val):
  return int(val)
  
def to_str(val):
  return val

def to_path(val):
  return val

OPT_TO_KEY = {
 '--do-wrap'        : ("DO_WRAP", to_bool),
 '--line-height': ("LINE_HEIGHT", to_int),
 '--nb-lines'        : ('LINES', to_int),
 '--no-caption'        : ("WANT_NO_CAPTION", to_bool),
'--force-no-vfs': ("FORCE_VFS", to_bool),
 '--force-vfs'        : ("FORCE_NO_VFS", to_bool),
 '--pick-random': ("PICK_RANDOM", to_bool),
 '--put-random'        : ("PUT_RANDOM", to_bool),
 '--resize'        : ("DO_RESIZE", to_bool),
 '--sleep'        : ('SLEEP_TIME', to_int),
 '--width'        : ('WIDTH', to_int),
'--no-switch-to-mini': ("NO_SWITCH_TO_MINI", to_bool),
 '<path>'        : ('PATH', to_path),
 '<target>'        : ('TARGET', to_path),
 '--polaroid'        : ("DO_POLAROID", to_bool),
 '--format'        : ("IMG_FORMAT_SUFFIX", to_str),
 '--crop-size'        : ("CROP_SIZE", to_int),
 '~~use-vfs'        : ("USE_VFS", to_bool),
 '--help'        : ("HELP", to_bool)
}

KEY_TO_OPT = dict([(key, (opt, ttype)) for opt, (key, ttype) in OPT_TO_KEY.items()])

PARAMS = {
"PATH" : "/home/kevin/WebAlbums/WebAlbums3/WebAlbums3-FS/Grenoble/Random",
"TARGET" : "/tmp/final.png",
#define the size of the picture
"WIDTH" : 1366,

#define how many lines do we want
"LINES": 3,

"LINE_HEIGHT": 200,

#minimum width of cropped image. Below that, we black it out
#only for POLAROID
"CROP_SIZE": 100,

"IMG_FORMAT_SUFFIX": ".png",

# False if PATH is a normal directory, True if it is WebAlbums-FS
"USE_VFS": True,
"FORCE_VFS": False,
"FORCE_NO_VFS": False,

# True if end-of-line photos are wrapped to the next line
"DO_WRAP": False,
# True if we want a black background and white frame, plus details
"DO_POLAROID": False,

"WANT_NO_CAPTION": False,

# False if we want to add pictures randomly
"PUT_RANDOM": False,

"DO_RESIZE": False,

### VFS options ###

"NO_SWITCH_TO_MINI": False,

### Directory options ###

# False if we pick directory images sequentially, false if we take them randomly
"PICK_RANDOM": False, #not implemented yet

## Random wall options ##
"SLEEP_TIME": 0,

"HELP": False
}

DEFAULTS = dict([(key, value) for key, value in PARAMS.items()])
DEFAULTS_docstr = dict([(KEY_TO_OPT[key][0], value) for key, value in PARAMS.items()])

usage = """Photo Wall for WebAlbums 3.

Usage: 
  photowall.py <path> <target> [options]

Arguments:
  <path>        The path where photos are picked up from. [default: %(<path>)s]
  <target>      The path where the target photo is written. Except in POLAROID+RANDOM mode, the image will be blanked out first. [default: %(<target>)s]

Options:
  --polaroid              Use polaroid-like images for the wall
  --width <width>         Set final image width. [default: %(--width)d]
  --nb-lines <nb>         Number on lines of the target image. [default: %(--nb-lines)d]
  --resize                Resize images before putting in the wall. [default: %(--resize)s]
  --line-height <height>  Set the height of a single image. [default: %(--line-height)d]
  --do-wrap               If not POLAROID, finish images on the next line. [default: %(--do-wrap)s]
  --help                  Display this message

Polaroid mode options:
  --crop-size <crop>      Minimum size to allow cropping an image. [default: %(--crop-size)s]
  --no-caption            Disable caption. [default: %(--no-caption)s] 
  --put-random            Put images randomly instead of linearily. [default: %(--put-random)s]
  --sleep <time>          If --put-random, time (in seconds) to go asleep before adding a new image. [default: %(--sleep)d]

Filesystem options:
  --force-vfs             Treat <path> as a VFS filesystem. [default: %(--force-vfs)s]
  --force-no-vfs          Treat <path> as a normal filesystem. [default: %(--force-no-vfs)s]
  --no-switch-to-mini     If VFS, don't switch from the normal image to the miniature. [default: %(--no-switch-to-mini)s]
  --pick-random           If not VFS, pick images randomly in the <path> folder. [default: %(--pick-random)s]
  """ % DEFAULTS_docstr


class UpdateCallback:
  def newExec(self):
    pass
  
  def newImage(self, row=0, col=0, filename=""):
    print "%d.%d > %s" % (row, col, filename)
    
  def updLine(self, row, tmpLine):
    print "--- %d ---" % row
      
  def newFinal(self, name):
    pass
  
  def finished(self, name):
    print "=========="

  def stopRequested(self):
    return False
  
  def checkPause(self):
    pass

updateCB = UpdateCallback()

if __name__ == "__main__":
    arguments = docopt(usage, version="3.5-dev")

    if arguments["--help"]:
        print usage
        exit()

    param_args = dict([(OPT_TO_KEY[opt][0], OPT_TO_KEY[opt][1](value)) for opt, value in arguments.items()])

    PARAMS = dict(PARAMS, **param_args)

###########################################

###########################################

previous = None
def get_next_file_vfs():
  global previous
  if previous is not None:
    try:
      os.unlink(previous)
    except:
      pass
    
  files = os.listdir(PARAMS["PATH"])
  for filename in files:
    if not "By Years" in filename:
      previous = PARAMS["PATH"]+filename
      if "gpx" in previous:
        return get_next_file()
      to_return = previous
      try:
        to_return = os.readlink(to_return)
      except OSError:
        pass

      if not PARAMS["NO_SWITCH_TO_MINI"]:
        to_return = to_return.replace("/images/", "/miniatures/") + ".png"
      return to_return

def get_file_details(filename):
  try:
    link = filename
    try:
      link = os.readlink(filename)
    except OSError:
      pass
    link = pipes.quote(link)
    names = link[link.index("/miniatures/" if not PARAMS["NO_SWITCH_TO_MINI"] else "/images"):].split("/")[2:]
    theme, year, album, fname = names
    
    return "%s (%s)" % (album, theme)
  except Exception as e:
    print e
    return get_file_details_dir(filename)

###########################################

class GetFileDir:
  def __init__(self, randomize):
    self.idx = 0
    self.files = os.listdir(PARAMS["PATH"])
    
    if len(self.files) == 0:
      raise EnvironmentError("No file available")
    
    if randomize:
      random.shuffle(self.files)
  
  def get_next_file(self):
    to_return = self.files[self.idx]
    
    self.idx += 1 
    self.idx %= len(self.files) 
    
    return PARAMS["PATH"]+to_return
  
def get_file_details_dir(filename):
  return filename[filename.rindex("/")+1:]

###########################################
###########################################


def do_append(first, second, underneath=False):
  sign = "-" if underneath else "+"
  background = "-background black" if PARAMS["DO_POLAROID"] else ""
  command = "convert -gravity center %s %sappend %s %s %s" % (background, sign, first, second, first)
  ret = subprocess.call(command, shell=True)
  
  if ret != 0:
    raise Exception("Command failed: ", command)

def do_polaroid (image, filename=None, background="black", suffix=None):
  if suffix is None:
    suffix = PARAMS["IMG_FORMAT_SUFFIX"]
  tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
  tmp.close()
  image.save(filename=tmp.name)
  
  if not(PARAMS["WANT_NO_CAPTION"]) and filename:
    details = get_file_details(filename)
    caption = """-caption "%s" """ % details.replace("'", "\\'")
  else:
    caption = ""
    
  command = "convert -bordercolor snow -background %(bg)s -gravity center %(caption)s +polaroid %(name)s %(name)s" % {"bg" : background, "name":tmp.name, "caption":caption}
    
  ret = subprocess.call(command, shell=True)
  if ret != 0:
    raise Exception("Command failed: "+ command)
  
  img = Image(filename=tmp.name).clone()
  
  os.unlink(tmp.name)
  
  img.resize(width=image.width, height=image.height)

  return img

def do_blank_image(height, width, filename, color="black"):
  command = "convert -size %dx%d xc:%s %s" % (width, height, color, filename)

  ret = subprocess.call(command, shell=True)

  if ret != 0:
    raise Exception("Command failed: "+ command)

def do_polaroid_and_random_composite(target_filename, target, image, filename):
  PERCENT_IN = 100
  
  image = do_polaroid(image, filename, background="transparent", suffix=".png")

  tmp = tempfile.NamedTemporaryFile(delete=False, suffix=PARAMS["IMG_FORMAT_SUFFIX"])
  image.save(filename=tmp.name)

  height = random.randint(0, target.height - image.height) - target.height/2
  width = random.randint(0, target.width - image.width) - target.width/2

  geometry = ("+" if height >= 0 else "") + str(height) + ("+" if width >= 0 else "") + str(width)

  command = "composite -geometry %s  -compose Over -gravity center %s %s %s" % (geometry, tmp.name, target_filename, target_filename)
  ret = os.system(command)
  os.unlink(tmp.name)
  
  if ret != 0:
    raise object("failed")

def photowall(name):
  output_final = None

  previous_filename = None
  #for all the rows, 
  for row in xrange(PARAMS["LINES"]):    
    output_row = None
    row_width = 0
    #concatenate until the image width is reached
    img_count = 0
    while row_width < PARAMS["WIDTH"]:
      # get a new file, or the end of the previous one, if it was split
      filename = get_next_file() if previous_filename is None else previous_filename
      mimetype = None
      previous_filename = None
      
      # get a real image
      if mime is not None:
        mimetype = mime.from_file(filename)
        if "symbolic link" in mimetype:
          filename = os.readlink(filename)
          mimetype = mime.from_file(filename)
        
        if not "image" in mimetype:
          continue
      else:
        try:
          filename = os.readlink(filename)
        except OSError:
          pass
      
      updateCB.newImage(row, img_count, filename)
      img_count += 1
      # resize the image
      image = Image(filename=filename)
      with image.clone() as clone:
        factor = float(PARAMS["LINE_HEIGHT"])/clone.height
        clone.resize(height=PARAMS["LINE_HEIGHT"], width=int(clone.width*factor))
        #if the new image makes an overflow
        if row_width + clone.width  > PARAMS["WIDTH"]:
          #compute how many pixels will overflow
          overflow = row_width + clone.width - PARAMS["WIDTH"]
          will_fit = clone.width - overflow
          
          if PARAMS["DO_POLAROID"] and will_fit < PARAMS["CROP_SIZE"]:
            row_width = PARAMS["WIDTH"]
            continue
          
          if PARAMS["DO_WRAP"]:
            with clone.clone() as next_img:
              next_img.crop(will_fit+1, 0, width=overflow, height=PARAMS["LINE_HEIGHT"])
              tmp = tempfile.NamedTemporaryFile(delete=False, suffix=PARAMS["IMG_FORMAT_SUFFIX"])
              tmp.close()
              next_img.save(filename=tmp.name)
              previous_filename = tmp.name
          clone.crop(0, 0, width=will_fit, height=PARAMS["LINE_HEIGHT"])
        
        if PARAMS["DO_POLAROID"]:
          clone = do_polaroid(clone, filename)
        
        tmp = tempfile.NamedTemporaryFile(delete=False, suffix=PARAMS["IMG_FORMAT_SUFFIX"])
        tmp.close()
        clone.save(filename=tmp.name)
        
        row_width += clone.width
        if output_row is not None:
          do_append(output_row.name, tmp.name)
          os.unlink(tmp.name)
        else:
          output_row = tmp
        
        updateCB.updLine(row, output_row.name)
        updateCB.checkPause()
        
        if updateCB.stopRequested():
          break
    else:
      if output_final is not None:
        do_append(output_final.name, output_row.name, underneath=True)
        os.unlink(output_row.name)
      else:
        output_final = output_row
      updateCB.newFinal(output_final.name)
  
  if output_final is not None:
    shutil.move(output_final.name, name)
    updateCB.finished(name)
  else:
    updateCB.finished(None)
    
  return name 
    
def random_wall(real_target_filename):
  name = real_target_filename
  filename = name[name.rindex("/"):]
  name = filename[:filename.index(".")]
  ext = filename[filename.index("."):]
  target_filename = tempfile.gettempdir()+"/"+name+".2"+ext
  
  try:
    #remove any existing tmp file
    os.unlink(target_filename)
  except:
    pass
  
  try:
    #if source already exist, build up on it
    os.system("cp %s %s" % (target_filename, real_target_filename))
  except:
    pass
  
  print "Target file is %s" % real_target_filename 
  target = None
  if mime is not None:
    try:
      mimetype = mime.from_file(target_filename)
      if "symbolic link" in mimetype:
        filename = os.readlink(target_filename)
        mimetype = mime.from_file(target_filename)
        
      if "image" in mimetype:
        target = Image(filename=target_filename)
      
    except IOError:
      pass

  if target is None:
    height = PARAMS["LINES"] * PARAMS["LINE_HEIGHT"]
    do_blank_image(height, PARAMS["WIDTH"], target_filename)
    target = Image(filename=target_filename)
  
  cnt = 0
  while True:
    updateCB.checkPause()
    if updateCB.stopRequested():
      break
      
    filename = get_next_file()
    print filename
    
    img = Image(filename=filename)
    with img.clone() as clone:
      if PARAMS["DO_RESIZE"]:
        factor = float(PARAMS["LINE_HEIGHT"])/clone.height
        clone.resize(width=int(clone.width*factor), height=int(clone.height*factor))
                     
      do_polaroid_and_random_composite(target_filename, target, clone, filename)
      updateCB.checkPause()
      if updateCB.stopRequested():
        break
      updateCB.newImage(row=cnt, filename=filename)
      updateCB.newFinal(target_filename)
      os.system("cp %s %s" % (target_filename, real_target_filename))
      cnt += 1
      
    updateCB.checkPause()
    if updateCB.stopRequested():
      break  
    time.sleep(PARAMS["SLEEP_TIME"])
    updateCB.checkPause()
    if updateCB.stopRequested():
      break
      
get_next_file = None

def path_is_jnetfs(path):
  #check if PATH is VFS or not
  df_output_lines = os.popen("df -Ph '%s'" % path).read().splitlines()
  return "JnetFS" in df_output_lines[1]

def fix_args():
  global get_next_file
  
  if PARAMS["PATH"][-1] != "/":
    PARAMS["PATH"] += "/"  
  
  if PARAMS["FORCE_NO_VFS"]:
    PARAMS["USE_VFS"]
  elif PARAMS["FORCE_NO_VFS"]:
    PARAMS["USE_VFS"]
  else:
    PARAMS["USE_VFS"] = path_is_jnetfs(PARAMS["PATH"]) 

  if not PARAMS["USE_VFS"]:    
    get_next_file = GetFileDir(PARAMS["PICK_RANDOM"]).get_next_file
  else:
    get_next_file = get_next_file_vfs

def do_main():
  fix_args()
  
  updateCB.newExec()
  target = PARAMS["TARGET"]
  if not(PARAMS["PUT_RANDOM"]):
    photowall(target)
  else:
    random_wall(target)

if __name__== "__main__":
    do_main()