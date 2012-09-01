#!/usr/bin/env python2
import os
import tempfile
from wand.image import Image
from wand.display import display

PATH="/home/kevin/WebAlbums/WebAlbums3-FS/JnetFS_C/test/Voyage/Random/"

previous = None
def get_next_file():
  global previous
  if previous is not None:
    os.unlink(previous)
  files = os.listdir(PATH)
  for filename in files:
    if not "By Years" in filename:
      previous = PATH+filename
      if "gpx" in previous:
	return get_next_file()
      return previous

files = os.listdir(PATH)
idx = 0
def get_next_file_fs():
  global idx
  to_return = files[idx]
  idx += 1
  if idx >= len(files):
    idx = 0
  if "gpx" in to_return:
    return get_next_file()
  return PATH+to_return
  

def do_append(first, second, underneath=False):
  sign = "-" if underneath else "+"
  
  command = "convert %sappend %s %s %s" % (sign, first, second, first)
  
  ret = os.system(command)
  if ret != 0:
    raise object("failed")

#http://dahlia.kr/wand/#why-just-another-binding

#define the size of the picture
WIDTH=1024
HEIGHT=780

#define how many lines do we want
LINES=4

#compute the size of a line
LINE_HEIGHT=int(HEIGHT/LINES)

#output_final = tempfile.NamedTemporaryFile(delete=False)
output_final = None

#for all the rows, 
for row in xrange(LINES):
  print "Row ", row
  output_row = None
  
  row_width = 0
  #concatenate until the image width is reached
  while row_width < WIDTH:
    filename = get_next_file()
    image = Image(filename=filename)
    with image.clone() as clone:
      
      factor = float(LINE_HEIGHT)/clone.height
      clone.resize(height=LINE_HEIGHT, width=int(clone.width*factor))
      row_width += clone.width
      
      if row_width > WIDTH:
	clone.crop(0, 0, height=LINE_HEIGHT, width=(clone.width - (row_width - WIDTH)))
      
      tmp = tempfile.NamedTemporaryFile(delete=False, suffix=".jpg")
      tmp.close()
      clone.save(filename=tmp.name)
      
      if output_row is not None:
	do_append(output_row.name, tmp.name)
	tmp.unlink(tmp.name)
      else:
	output_row = tmp
      
  if output_final is not None:
    do_append(output_final.name, output_row.name, underneath=True)
    output_row.unlink(output_row.name)
  else:
    output_final = output_row
  
print "==>", output_final.name
display(Image(filename=output_final.name))