#!/usr/bin/env python2
import os
import tempfile
import pipes
import subprocess
import time
import random

try:
  from wand.image import Image
  from wand.display import display
except ImportError as e:
  print "Couldn't import Wand package."
  print "Please refer to #http://dahlia.kr/wand/ to install it."
  raise e

try:
  import magic
  mime = magic.Magic()
except ImportError:
  mime = None
  #https://github.com/ahupp/python-magic
  
PATH="/home/kevin/WebAlbums/WebAlbums3/WebAlbums3-FS/Grenoble/Random"
#PATH="/home/kevin/ardeche triees/"


#define the size of the picture
WIDTH=1366
HEIGHT=786

#define how many lines do we want
LINES=3

#minimum width of cropped image. Below that, we black it out
#only for POLAROID
MIN_CROP=100

IMG_FORMAT_SUFFIX=".png"

# False if PATH is a normal directory, True if it is WebAlbums-FS
USE_VFS=True
# True if end-of-line photos are wrapped to the next line
DO_WRAP=False
# True if we want a black background and white frame, plus details
DO_POLAROID=True
# True if want caption
WANT_CAPTION=True

# False if we want to add pictures randomly
DO_WALL=False

DO_RESIZE=False

### VFS options ###
DO_ALL_THEMES=False
MOUNT_PATH="/home/kevin/WebAlbums/WebAlbums3-FS/JnetFS_C/test/"

SWITCH_TO_MINI=True

### Directory options ###

# False if we pick directory images sequentially, false if we take them randomly
PICK_RANDOM=False #not implemented yet


## Random wall options ##
SLEEP_TIME=0

###########################################

if PATH[-1] != "/":
	PATH += "/"

###########################################

previous = None
def get_next_file_vfs():
  global previous
  if previous is not None:
    os.unlink(previous)
    
  files = os.listdir(PATH)
  for filename in files:
    if not "By Years" in filename:
      previous = PATH+filename
      if "gpx" in previous:
	return get_next_file()
      to_return = previous
      try:
        to_return = os.readlink(to_return)
      except OSError:
        pass

      if SWITCH_TO_MINI:
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
    names = link[link.index("/miniatures/" if SWITCH_TO_MINI else "/images"):].split("/")[2:]
    theme, year, album, fname = names
    
    return "%s (%s)" % (album, theme)
  except Exception as e:
    print e
    return get_file_details_dir(filename)

###########################################

files = os.listdir(PATH)
idx = 0
def get_next_file_dir():
  global idx
  to_return = files[idx]
  idx += 1
  if idx >= len(files):
    idx = 0
  
  return PATH+to_return
  
def get_file_details_dir(filename):
  return filename[filename.rindex("/")+1:]
  
###########################################
###########################################

get_next_file = get_next_file_vfs if USE_VFS else get_next_file_dir
#get_file_details = get_file_details_vfs if USE_VFS else get_file_details_dir


###########################################
###########################################


def do_append(first, second, underneath=False):
  sign = "-" if underneath else "+"
  background = "-background black" if DO_POLAROID else ""
  command = "convert -gravity center %s %sappend %s %s %s" % (background, sign, first, second, first)
  ret = subprocess.call(command, shell=True)
  
  if ret != 0:
    raise Exception("Command failed: ", command)

def do_polaroid (image, filename=None, background="black", suffix=None):
  if suffix is None:
    suffix = IMG_FORMAT_SUFFIX
  tmp = tempfile.NamedTemporaryFile(delete=False, suffix=suffix)
  tmp.close()
  image.save(filename=tmp.name)
  
  if WANT_CAPTION and filename:
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
  
  image = do_polaroid(image, filename, background="transparent", suffix=".png")

  tmp = tempfile.NamedTemporaryFile(delete=False, suffix=IMG_FORMAT_SUFFIX)
  image.save(filename=tmp.name)

  height = random.randint(0, target.height) - target.height/2
  width = random.randint(0, target.width) - target.width/2

  geometry = ("+" if height >= 0 else "") + str(height) + ("+" if width >= 0 else "") + str(width)

  
  command = "composite -geometry %s  -compose Over -gravity center %s %s %s" % (geometry, tmp.name, target_filename, target_filename)
  ret = os.system(command)
  os.unlink(tmp.name)
  
  if ret != 0:
    raise object("failed")
  
#compute the size of a line
LINE_HEIGHT=int(HEIGHT/LINES)

def photowall(name):
  output_final = None

  previous_filename = None
  #for all the rows, 
  for row in xrange(LINES):
    print "Row ", row
    output_row = None
    row_width = 0
    #concatenate until the image width is reached
    img_count = 0
    while row_width < WIDTH:
      filename = get_next_file() if previous_filename is None else previous_filename
      previous_filename = None
      
      print img_count,
      if mime is not None:
	mimetype = mime.from_file(filename)
	if "symbolic link" in mimetype:
	  filename = os.readlink(filename)
	  mimetype = mime.from_file(filename)
	
	if not "image" in mimetype:
	  continue
	
	print "%s: %s" % (filename, mimetype)
	
      else:
	try:
	  print os.readlink(filename)
	except OSError:
	  print filename
	  
      img_count += 1
      image = Image(filename=filename)
      with image.clone() as clone:
	factor = float(LINE_HEIGHT)/clone.height
	clone.resize(height=LINE_HEIGHT, width=int(clone.width*factor))
	#if the new image makes an overflow
	if row_width + clone.width  > WIDTH:
	  #compute how many pixels will overflow
	  overflow = row_width + clone.width - WIDTH
	  will_fit = clone.width - overflow
	  
	  if DO_POLAROID and will_fit < MIN_CROP:
	    row_width = WIDTH
	    continue
	  
	  if DO_WRAP:
	    with clone.clone() as next_img:
	      next_img.crop(will_fit+1, 0, width=overflow, height=LINE_HEIGHT)
	      tmp = tempfile.NamedTemporaryFile(delete=False, suffix=IMG_FORMAT_SUFFIX)
	      tmp.close()
	      next_img.save(filename=tmp.name)
	      previous_filename = tmp.name
	  clone.crop(0, 0, width=will_fit, height=LINE_HEIGHT)
	
	if DO_POLAROID:
	  clone = do_polaroid(clone, filename)
	
	tmp = tempfile.NamedTemporaryFile(delete=False, suffix=IMG_FORMAT_SUFFIX)
	tmp.close()
	clone.save(filename=tmp.name)
	
	row_width += clone.width
	if output_row is not None:
	  do_append(output_row.name, tmp.name)
	  os.unlink(tmp.name)
	else:
	  output_row = tmp
	
    if output_final is not None:
      do_append(output_final.name, output_row.name, underneath=True)
      os.unlink(output_row.name)
    else:
      output_final = output_row
  final_name = tempfile.gettempdir()+"/"+name+IMG_FORMAT_SUFFIX
  os.rename(output_final.name, final_name)
  
  return final_name 


def photowall_all_themes():
  global PATH
  for theme in os.listdir(MOUNT_PATH):
    print "--", theme, "--"
    PATH = "%s%s/Random/" % (MOUNT_PATH, theme)
    name = photowall(theme)
    print "==>", name
    ret = subprocess.call("eog %s &" % name, shell=True)
    
def random_wall(name):
  target_filename = tempfile.gettempdir()+"/"+name+"2.png"
  real_target_filename = tempfile.gettempdir()+"/"+name+".png"
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
    do_blank_image(HEIGHT, WIDTH, target_filename)
    target = Image(filename=target_filename)
    
  while True:
    filename = get_next_file()
    print filename
    img = Image(filename=filename)
    with img.clone() as clone:
      if DO_RESIZE:
        factor = float(LINE_HEIGHT)/clone.height
        clone.resize(width=int(clone.width*factor), height=int(clone.height*factor))
                     
      do_polaroid_and_random_composite(target_filename, target, clone, filename)

      os.system("cp %s %s" % (target_filename, real_target_filename))
    print "Tick"
    time.sleep(SLEEP_TIME)
    print "Tack"
    
if __name__=="__main__":
  if DO_WALL:
	  if USE_VFS and DO_ALL_THEMES:
		photowall_all_themes()
	  else:
		name = photowall("final")
		print "==>", name
		os.system("eog %s &" % name)
  else:
    random_wall("final")

