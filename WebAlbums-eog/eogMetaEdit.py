'''
This file is part of eogMetaEdit.

eogMetaEdit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

eogMetaEdit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with eogMetaEdit.  If not, see <http://www.gnu.org/licenses/>.

Copyright 2012 Wayne Vosberg <wayne.vosberg@mindtunnel.com>
'''

from gi.repository import GObject, Gtk, Gdk, Eog, PeasGtk # , Pango
from os.path import join, basename
from urlparse import urlparse
import pyexiv2
import re
import datetime
import time
from string import strip
import sys
#import pango



class MetaEditPlugin(GObject.Object, Eog.WindowActivatable):

	# the main EoG window
	window = GObject.property(type=Eog.Window)	
	Debug = False
	
	# definition of metadata to manage. TI=title, DT=date/time, CA=caption
	# and KW=keyword
	#
	# on load, existing XXvars and XXrem variables will be added
	# to the combobox pulldown.
	#
	# on save, XXvars variables will all be set to the value in
	# the combobox and all XXrem variables will be removed from
	# the file
	#
	# ****************
	#
	# The following values will be set to be compatible with zenfolio:
	# 
	# Iptc.Application2.Caption = caption
	# Iptc.Application2.Keywords = keywords
	# Iptc.Application2.Headline = title
	# Iptc.Application2.Copyright = copyright (future)
	# Exif.Photo.DateTimeOriginal = date/time taken
	# Exif.Image.Make and Model will be set to Canon/Canon MP990 series Network if not already set
	#
	# ****************
	# 
	# To make the images compatible with Darktable, the following values will also be set:
	# 
	# Iptc.Application2.Headline = Xmp.dc.title = title
	# Xmp.dc.description = Iptc.Application2.Caption = caption
	# Iptc.Application2.Keywords = Xmp.dc.subject = keywords
	# Iptc.Application2.DateCreated = date taken
	# Iptc.Application2.TimeCreated = time taken
	# Xmp.lr.hierarchicalSubject will not be set since I am not supporting the Lightroom namespace yet
	# 
	
	Make = 'eogMetaEdit'
	Model = 'v0.7b'
	
	EXvars = [	'Exif.Image.Make',
				'Exif.Image.Model' ]
	
	TIvars = [	'Iptc.Application2.Headline',
				'Exif.Image.ImageDescription',
				'Xmp.dc.title' ]
	TIrem =  [	]
	
	DTvars = [	'Exif.Photo.DateTimeOriginal',
				'Exif.Image.DateTimeOriginal', 
				'Exif.Photo.DateTimeDigitized',
				'Exif.Image.DateTime' ]
	
	vDates = [	'%Y:%m:%d %H:%M:%S',
				'%Y:%m:%dT%H:%M:%S',
				'%Y-%m-%d %H:%M:%S',
				'%Y-%m-%dT%H:%M:%S' ]
	
	isoDate = [	'Iptc.Application2.DateCreated' ]
	isoTime = [	'Iptc.Application2.TimeCreated' ]
	
	DTrem  = [	'Xmp.exif.DateTimeOriginal',
				'Xmp.dc.date'	]
	
	CAvars = [	'Exif.Photo.UserComment',				
				'Iptc.Application2.Caption',
				'Xmp.dc.description' ]
	CArem  = [	'Xmp.acdsee.caption' ]
	
	KWvars  = [	'Iptc.Application2.Keywords',
				'Xmp.dc.subject' ]
	KWrem = [ 	 ]  
		
		
		
	def __init__(self):
		GObject.Object.__init__(self)
		


	def do_activate(self):
		'''Activate the plugin - adds my dialog to the Eog Sidebar'''
		
		# the sidebar is where the eogMetaEdit dialog is added
		self.sidebar = self.window.get_sidebar()
		# need to track file changes in the EoG thumbview
		self.thumbview = self.window.get_thumb_view()		
		
		# the EogImage of the main window
		self.curImage = None
		# the EogImage selected in the thumbview
		self.thumbImage = None
		# the EogImage whose metadata has been modified
		self.changedImage = None
		# flag for selection_changed_cb to note when we made the change
		self.ignoreChange = False
		
		# build my dialog
		builder = Gtk.Builder()
		builder.add_from_file(join(self.plugin_info.get_data_dir(),\
								"eogMetaEdit.glade"))
		pluginDialog = builder.get_object('eogMetaEdit')
		self.isChangedDialog = builder.get_object('isChangedDialog')
		self.errorMessageDialog = builder.get_object('errorMessageDialog')
		self.errorMsg = builder.get_object('errorMsg')
		
		# my widgets		
		self.fileName = builder.get_object("fileName")
		
		self.newTitle = builder.get_object('newTitle')
		self.newTitleEntry = builder.get_object('newTitleEntry')
		
		self.newDate = builder.get_object("newDate")
		self.newDateEntry = builder.get_object("newDateEntry")
		
		self.newCaption = builder.get_object("newCaption")
		self.newCaptionEntry = builder.get_object("newCaptionEntry")
		
		self.newKeyword = builder.get_object("newKeyword")
		self.newKeywordEntry = builder.get_object("newKeywordEntry")
		
		self.commitButton = builder.get_object('commitButton')
		self.revertButton = builder.get_object('revertButton')
		
		self.forceDefaults = builder.get_object('forceDefaults')
		self.changeDetails = builder.get_object('changeDetails')
		
		# set the buttons disabled initially
		self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
		self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
		self.metaChanged = False
		
		# these lists are for convenience later
		self.combos =  [	self.newTitle, self.newDate, self.newCaption, self.newKeyword ]
		self.entries = [	self.newTitleEntry, self.newDateEntry, self.newCaptionEntry, 
							self.newKeywordEntry ]
		
		# set up my callbacks
		
		# I'm going to create a dict of callback id's:  
		# { 'signal1': { funct1 : id, funct2 : id}, 
		#   'signal2' : { funct1 : id, funct2 : id } 
		#    ... }
		# hopefully making it easier to remove them without forgetting any.
		
		# the key-press-event callback will only be enabled when one of my 
		# comboboxes has focus.  this allows me to block the main window
		# from acting on any shortcut keys (as defined in eog-window.c
		# function eog_window_key_press.
		
		self.cb_ids = {}
		self.cb_ids['key-press-event'] = {}		
		self.cb_ids['key-press-event'][self.window] = \
			self.window.connect('key-press-event', self.key_press_event_cb)
		# block this callback initially - it is only enabled when I want focus
		self.window.handler_block(self.cb_ids['key-press-event'][self.window])					
		
		for S in 'focus-in-event','focus-out-event':
			if not self.cb_ids.has_key(S):
				self.cb_ids[S]={}
			for W in self.entries:
				self.cb_ids[S][W] = W.connect(S, self.focus_event_cb, \
					self.window, self.cb_ids['key-press-event'][self.window])
		
		self.cb_ids['focus-out-event'][self.newDateEntry] = self.newDateEntry.connect(\
			'focus-out-event',self.test_date_cb, self.newDate, self)
		
		for S in 'changed',:
			if not self.cb_ids.has_key(S):
				self.cb_ids[S]={}
			for W in self.combos:
				self.cb_ids[S][W] = W.connect(S, self.combo_changed_cb, self)
		
		self.cb_ids['clicked'] = {}
		self.cb_ids['clicked'][self.commitButton] = \
			self.commitButton.connect('clicked', self.commit_clicked_cb, self)		
		self.cb_ids['clicked'][self.revertButton] = \
			self.revertButton.connect('clicked', self.revert_clicked_cb, self)
		
		self.cb_ids['selection-changed'] = {}
		self.cb_ids['selection-changed'][self.thumbview] = \
			self.thumbview.connect('selection-changed', \
				self.selection_changed_cb, self)
		
		self.cb_ids['toggled'] = {}
		self.cb_ids['toggled'][self.forceDefaults] = \
			self.forceDefaults.connect('toggled', self.forceToggled, self)
			
		# finally, add my dialog to the sidebar
		Eog.Sidebar.add_page(self.sidebar,"Metadata Editor", pluginDialog)
		
		if self.thumbview.get_first_selected_image() != None:
				self.changedImage = self.thumbview.get_first_selected_image()
				self.loadMeta(urlparse(self.changedImage.\
							get_uri_for_display()).path)

	
		
	def do_deactivate(self):
		'''remove all the callbacks stored in dict self.cb_ids '''
		
		for S in self.cb_ids:
			for W, id in self.cb_ids[S].iteritems():
				W.disconnect(id)



	# The callback functions are done statically to avoid causing additional
	# references on the window property causing eog to not quit correctly.



	def date_validate(self, entry):
		''' '''
		nd = entry.get_active_text()
		for t in self.vDates:
			try:
				d = datetime.datetime.strptime( nd, t)				
				# date is OK
				return 2
			except:
				pass
			
		message='Failed to match [%s] with one of:\n\n[%s]\n[%s]\n[%s]\n[%s]'% \
			(nd,self.vDates[0],self.vDates[1],self.vDates[2],self.vDates[3])
			
		self.errorMsg.set_text(message)
		r = self.errorMessageDialog.run()
		self.errorMessageDialog.hide()
		return r
		
		
		
	@staticmethod
	def test_date_cb(widget,event,entry, win):
		''' test the validity of the date string (against vDates format) '''
		
		'''
		nd = entry.get_active_text()
		
		for t in win.vDates:
			try:
				d = datetime.datetime.strptime( nd, t)				
				# date is OK
				if win.Debug:
					print 'matched [%s] with [%s]'%(nd,t)
				return False
			except:
				pass
		
		# failed to match a date string
		message='Failed to match [%s] with one of:\n\n[%s]\n[%s]\n[%s]\n[%s]'% \
			(nd,win.vDates[0],win.vDates[1],win.vDates[2],win.vDates[3])
			
		win.errorMsg.set_text(message)
		r = win.errorMessageDialog.run()
		win.errorMessageDialog.hide()
		'''
		r = win.date_validate(entry)
		
		if r == 2:
			return False
		elif r == 1:
			entry.set_active(0)
			return False
		else:	
			entry.grab_focus()
			return True
	
	

	@staticmethod
	def focus_event_cb(widget, event, win, id):
		'''
		Process the change of focus for the comboboxes.  If the combobox has 
		focus, unblock the key-press-event callback on the main EogWindow so 
		that the combobox can get the key-press and not eog_window_key_press.
		
		'''
			
		if widget.has_focus():
			win.handler_unblock(id)
		else:
			win.handler_block(id)
		
		return False
	
	
	
	@staticmethod
	def key_press_event_cb(plugin, event):
		'''
		Process key events from the comboboxes.  NOTE:  this callback is set 
		for the EogWindow key-press-event.  I expected to be able to set this 
		callback for the comboboxes themselves, but no matter what I tried 
		the signal went to the main window first which caused me to miss any 
		keys acted on by eog_window_key_press in eog-window.c.  Not sure if 
		this is a bug or if I was doing something wrong but I got it working 
		by intercepting the event here and then throwing away the event.  
		
		This callback is only enabled when one of the comboboxes has focus - 
		otherwise the key-press-event goes to eog_window_key_press as normal.
		
		'''
		
		# find out which widget has focus, do the default event and then
		# throw away the event
		plugin.get_focus().do_key_press_event(plugin.get_focus(),event)
		plugin.emit_stop_by_name('key-press-event')

		return True



	@staticmethod
	def combo_changed_cb(plugin,self):
		'''
		One of the comboboxes has changed.  
		Enable the revert and commit buttons and save changedImage
		
		'''
		
		if self.commitButton.get_state() == Gtk.StateType.INSENSITIVE:
			self.commitButton.set_state(Gtk.StateType.NORMAL)
			self.revertButton.set_state(Gtk.StateType.NORMAL)
			self.metaChanged = True
			# make sure changedImage is set
			if self.thumbview.get_first_selected_image() != None:
				self.changedImage = self.thumbview.get_first_selected_image()
			elif self.changedImage == None:
				self.showImages()
				raise ValueError('combo_changed_cb but both '+\
						'changedImage and thumbImage are None!')
			
			if self.Debug:
				print '\ncombo_changed_cb-------\n',self.showImages()
				print 'marking %s (%s)'%(self.changedImage,urlparse(\
						self.changedImage.get_uri_for_display()).path)
				print '----------'
		
		# return True -- default callback isn't needed
		return True
			
	
	
	def show_changes(self):
		''' return a string detailing the changes a commit would make '''
		
		saveTitle = self.newTitle.get_active_text()
		saveDate = self.newDate.get_active_text()
		newisoDate = ''
		for t in self.vDates:
			try:
				# I already test for validity on focus-out-event
				d=datetime.datetime.strptime( saveDate, t)
				newisoDate = str(d.date()) # d.strftime("%Y-%m-%d")
				newisoTime = str(d.time())+'+00:00' # d.strftime("%H:%M:%S")
				break
			except:
				pass
		
		saveCaption = self.newCaption.get_active_text()
		if saveCaption == "" :
			saveCaption = "N/A"
		saveKeyword = self.newKeyword.get_active_text()
		if saveKeyword == "":
			saveKeyword = "N/A"
		
		if saveTitle == "":
			saveTitle = newisoDate
			
		changeString = ''
		
		try:
			if self.EXvars[0] not in self.all_keys:			
				changeString += '\n   add %s to "%s"'%(self.EXvars[0],self.Make)
			elif len(self.metadata[self.EXvars[0]].raw_value) == 0:
				changeString += '\n   set %s to "%s"'%(self.EXvars[0],self.Make)
		except:
			print 'cs1 error: ',sys.exc_info()
			return changeString
		
		try:
			if self.EXvars[1] not in self.all_keys:
				changeString += '\n   add %s to "%s"'%(self.EXvars[1],self.Make+' '+self.Model)
			elif 	len(self.metadata[self.EXvars[1]].raw_value) == 0:
				changeString += '\n   set %s to "%s"'%(self.EXvars[1],self.Make+' '+self.Model)
		except:
			print 'cs2 error: ',sys.exc_info()
			return changeString
	
		
		# title variables
		for k in self.TIvars:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,saveTitle)
			else:
				v=self.metadata[k].raw_value
				if type(v) == str and v != saveTitle:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,saveTitle)
				elif type(v) == list and v[0] != saveTitle:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v[0],saveTitle)
				elif type(v) == dict and v['x-default'] != saveTitle: 
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v['x-default'],saveTitle)
					
			
		# date/time variables
		for k in self.DTvars:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,saveDate)
			else:
				v=self.metadata[k].raw_value
				if type(v) == str and v != saveDate:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,saveDate)
				elif type(v) == list and v[0] != saveDate:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v[0],saveDate)
				elif type(v) == dict and v['x-default'] != saveDate: 
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v['x-default'],saveDate)
						
		for k in self.DTrem:
			if k in self.all_keys:
				changeString += '\nremove %s'%k
		
		for k in self.isoDate:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,d.date())
			else:
				v=self.metadata[k].raw_value
				nd=str(d.date())
				if type(v) == str and v != nd:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,nd)
				elif type(v) == list and v[0] != nd:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v[0],nd)
				elif type(v) == dict and v['x-default'] != nd: 
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v['x-default'],nd)

		for k in self.isoTime:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,d.time())
			else:
				v=self.metadata[k].raw_value
				nt=str(d.time())
				
				if type(v) == str and not v.startswith(nt):
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,nt)
				elif type(v) == list and not v[0].startswith(nt):
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v[0],nt)
				elif type(v) == dict and not v['x-default'].startwith(nt): 
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v['x-default'],nt)
			
		# caption variables	   
		for k in self.CAvars:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,saveCaption)
			else:
				v=""
				if type(self.metadata[k].raw_value) == str:
					v=self.metadata[k].raw_value.strip('\00')
				elif type(self.metadata[k].raw_value) == list:
					v=self.metadata[k].raw_value[0].strip('\00')
				else:
					v=self.metadata[k].raw_value['x-default'].strip('\00')
				if v != saveCaption:
					changeString += '\nupdate %s \n\t from "%s" \n\t  to "%s"'%\
						(k,v,saveCaption)
			
		for k in self.CArem:
			if k in self.all_keys:
				changeString += '\nremove %s'%k	
		
		# keyword variables
		newKW = ' '.join(re.split(',\s+',saveKeyword)).split()
		for k in self.KWvars:
			if k not in self.all_keys:
				changeString += '\n   add %s  = "%s"'%(k,newKW)
			else:
				v=self.metadata[k].raw_value
				if type(v) == str and v != newKW:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,newKW)
				elif type(v) == list and v != newKW:
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v,newKW)
				elif type(v) == dict and v['x-default'] != newKW: 
					changeString += '\nupdate %s \n\tfrom "%s" \n\t  to "%s"'%\
						(k,v['x-default'],newKW)	
			
		for k in self.KWrem:
			if k in self.all_keys:
				changeString += '\nremove %s'%k

		return changeString
	
	
			
	@staticmethod
	def commit_clicked_cb(plugin, self):
		'''Commit the changes to the file'''
		
		if self.Debug:
			print '\ncommit: ',\
				urlparse(self.changedImage.get_uri_for_display()).path
		
		saveTitle = self.newTitle.get_active_text()
		saveDate = self.newDate.get_active_text()
		
		for t in self.vDates:
			try:
				# I already test for validity on focus-out-event
				d=datetime.datetime.strptime( saveDate, t)
				newisoDate = str(d.date()) # d.strftime("%Y-%m-%d")
				newisoTime = str(d.time())+'+00:00' # d.strftime("%H:%M:%S")
				break
			except:
				pass
		
		saveCaption = self.newCaption.get_active_text()
		if saveCaption == "" :
			saveCaption = "N/A"
		saveKeyword = self.newKeyword.get_active_text()
		if saveKeyword == "":
			saveKeyword = "N/A"	
		if saveTitle == "":
			saveTitle = newisoDate
			
		# set the vars in XXvars and remove the ones in XXrem
		
		if self.EXvars[0] not in self.all_keys or \
					len(self.metadata[self.EXvars[0]].raw_value) == 0:
			if self.Debug:
				print 'setting %s to eogMetaEdit'%self.EXvars[0]
				
			self.metadata.__setitem__(self.EXvars[0],self.Make)
			
		if self.EXvars[1] not in self.all_keys or \
					len(self.metadata[self.EXvars[1]].raw_value) == 0:
			if self.Debug:
				print 'setting %s to eogMetaEdit v2.0'%self.EXvars[1]
			self.metadata.__setitem__(self.EXvars[1],self.Make+' '+self.Model)
				
		# title variables
		for k in self.TIvars:
			if self.Debug:
				print 'update[',k,'] to [',saveTitle,']'
			try:
				self.metadata.__setitem__(k,saveTitle)
			except TypeError:
				self.metadata.__setitem__(k,[saveTitle])
		
		# date/time variables
		for k in self.DTvars:
			if self.Debug:
				print "update [",k,"] to [",saveDate,"]"
			self.metadata.__setitem__(k,saveDate)
		for k in self.DTrem:
			if k in self.all_keys:
				if self.Debug:
					print "removing [",k,"]"
				self.metadata.__delitem__(k)
		
		for k in self.isoDate:
			if self.Debug:
				print 'update [',k,'] to [',d.date()
			try:
				self.metadata.__setitem__(k,d.date())
			except TypeError:
				self.metadata.__setitem__(k,[d.date()])
		for k in self.isoTime:
			if self.Debug:
				print 'update [',k,'] to [',d.time()
			try:
				self.metadata.__setitem__(k,d.time())
			except TypeError:
				self.metadata.__setitem__(k,[d.time()])
			
		# caption variables	   
		for k in self.CAvars:
			if self.Debug:
				print "update [",k,"]  to [",saveCaption,"]"
			try:
				self.metadata.__setitem__(k,saveCaption)
			except UnicodeDecodeError:
				self.metadata.__setitem__(k,saveCaption.decode('utf-8'))
			except TypeError:
				self.metadata.__setitem__(k,[saveCaption])
		for k in self.CArem:
			if k in self.all_keys:
				if self.Debug:
					print "removing [",k,"]"
				self.metadata.__delitem__(k)		
		
		# keyword variables
		newKW = ' '.join(re.split(',\s+',saveKeyword)).split()
		for k in self.KWvars:			
			if self.Debug:
				print "update [",k,"] to ",newKW
			self.metadata.__setitem__(k,newKW)
		for k in self.KWrem:
			if k in self.all_keys:
				if self.Debug:
					print "removing [",k,"]"
				self.metadata.__delitem__(k)  
		
		# mark the metadata as unchanged before updating the file in
		# case we trigger a file changed callback
		self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
		self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
		self.metaChanged = False
		self.metadata.write()
		
		# reload the metadata - otherwise we will get nonexistant key
		# errors if we make any more changes without selecting a different
		# file first.
		self.loadMeta(urlparse(self.changedImage.get_uri_for_display()).path)
		
		if self.Debug:
			print 'after commit:'
			self.showImages()

		return True
		


	@staticmethod
	def revert_clicked_cb(plugin, self):
		'''
		Revert the three comboboxes to the values in the file itself and set 
		the buttons back to insensitive
		
		'''
		
		if self.changedImage == None:
			self.showImages()
			raise ValueError('revert clicked but there is no changedImage!')
		else:
			self.loadMeta(urlparse(self.changedImage.\
							get_uri_for_display()).path)
			self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
			self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
			self.metaChanged = False

		return True
		
		
	
	@staticmethod
	def forceToggled(checkB,self):
		''' toggle button changed '''
		
		try:
			self.loadMeta(urlparse(self.thumbImage.get_uri_for_display()).path)
		except:
			#print 'loadMeta failed (%s)'%urlparse(self.thumbImage.get_uri_for_display()).path
			#self.showImages()
			# if you try to toggle the checkbox when an invalid file is selected you
			# should error out here
			for C in self.combos:
				self.clearCombo(C)
				
			self.newTitleEntry.set_text('')
			self.newDateEntry.set_text('')
			self.newCaptionEntry.set_text('')
			self.newKeywordEntry.set_text('')	
			self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
			self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
			self.metaChanged = False

		return True
	
	
			
	@staticmethod
	def	selection_changed_cb(thumb, self):
		'''
		The file selection in the thumb navigator has changed.  
		Load the new metadata and update the comboboxes accordingly.
		
		If this cb is hit when there are currently unsaved metadata changes
		you will be prompted as to whether to 1) cancel the selection change,
		2) throw away the unsaved changes and load the new file or 3) save
		the changes and load the new file.
		
		'''
		
		# constanst for isChangeDialog
		CANCEL=2	# cancel the file change
		NO=1		# discard the changes and load the new file
		YES=0		# save the changes and load the new file
				
		self.curImage = self.window.get_image()
		self.thumbImage = self.thumbview.get_first_selected_image()
		Event = Gtk.get_current_event()
		
		if self.Debug:
			print '\n\nfile changed ----------------------------------------'
			print 'Event: ',Event
			if Event != None:
				print 'Event type: ',Event.type
			self.showImages()
			
		if Event != None and self.thumbImage == None:
			# this happens when you use the toolbar next/previous buttons as 
			# opposed to the arrow keys or clicking an icon in the thumb nav.
			# seem to be able to safely just discard it and then the various
			# new image selections work the same.
			if self.Debug:
				print 'selection event received with no thumbImage.  discard!'
			return False	
		
		# check to see if this callback is from a canceled file change
		if self.ignoreChange: 
			# when cancel is selected in isChangedDialog the current image in
			# the thumb nav is forced back to the modified image.  this causes
			# a file changed callback that we want to ignore so that we don't
			# overwrite the combobox modified data with the old file data. 
			if self.Debug:
				print 'ignoring change'
			
			self.ignoreChange = False							
			return False
			
		elif self.metaChanged:
			# a new file was selected but there are unsaved changes to the 
			# current metadata!
			
			if self.Debug and Event != None:
				print '\n---------------------------------------------------'
				print 'event: %s (%s) state: %s'%(Event.type,\
									Event.get_click_count(),Event.get_state())
				print 'device: %s'%Event.get_device()
				print 'source: %s'%Event.get_source_device()
				print 'button: ',Event.get_button()
				print 'keycode: ',Event.get_keycode()
				print 'keyval: ',Event.get_keyval()
				print 'screen: ',Event.get_screen()
				print 'window stat: ',Event.window_state
			
			if Event != None and Event.type == Gdk.EventType.BUTTON_PRESS:
				# we got here by clicking a thumbnail in the thumb navigator.
				# throw away the release event or we will be left dragging 
				# the thumbnail after the dialog closes (not a critical error, 
				# but annoying and forces you to click an extra time in the 
				# thumb nav to release it).  We don't seem to need to do 
				# anything special if we got here by the arrow keys (Gdk.
				# EventType.KEY_PRESS) or the toolbar Next/Previous (Gdk.
				# EventType.BUTTON_RELEASE)
				self.thumbview.emit('button-release-event', Event)
			
			# display the dialog asking what to do and then hide it when we
			# get the answer
			tBuff = self.changeDetails.get_buffer()
			tBuff.set_text(self.show_changes())
			
			self.result = self.isChangedDialog.run()
			self.isChangedDialog.hide()

			if self.result == CANCEL or self.result < 0: # CANCEL or close
				# stay on the current file.  
				if self.changedImage != None:
					if self.Debug:
						print 'reset thumb %s'%urlparse(\
							self.changedImage.get_uri_for_display()).path
					# ignore the next file changed callback so that we
					# revert to the previous photo without modifying the comboboxes
					self.ignoreChange = True
					self.thumbview.set_current_image(self.changedImage,True)
					return False
				else:
					self.showImages()
					raise AttributeError('Canceled but nothing to revert to!')
				
			elif self.result == YES:
				# save the changes. the newly selected image in the thumb nav
				# will still be loaded and the comboboxes will be updated with
				# the new data.
				self.commitButton.clicked()
			else:
				# don't save.  just continue on with the normal file change
				self.metaChanged = False		

		if self.thumbImage == None:
			if self.changedImage != None:
				if self.Debug:
					print 'setting thumbImage to changedImage'
				self.thumbImage = self.changedImage
		
		if self.thumbImage != None:		
			if self.Debug:
				print 'loading thumb meta:',\
					urlparse(self.thumbImage.get_uri_for_display()).path
			try:
				self.loadMeta(urlparse(self.thumbImage.get_uri_for_display()).path)
			except:
				#print 'loadMeta failed (%s)'%urlparse(self.thumbImage.get_uri_for_display()).path
				#self.showImages()
				# if you select an invalid file you should error out here
				for C in self.combos:
					self.clearCombo(C)
					
				self.newTitleEntry.set_text('')
				self.newDateEntry.set_text('')
				self.newCaptionEntry.set_text('')
				self.newKeywordEntry.set_text('')	
				self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
				self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
				self.metaChanged = False
		else:
			if self.Debug:
				print 'no metadata to load!'
				self.showImages()
			return False

		# return False to let any other callbacks execute as well
		return False



	def clearCombo(self,combo):
		'''clear a combobox'''
		
		combo.set_active(-1)		
		l=range(len(combo.get_model()))
		l.reverse()	   
		for i in l:
			combo.remove(i)
	
	
	
	def loadMeta(self, filePath):
		'''set the comboboxes to the current files data'''
		
		
		self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
		self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
		self.metaChanged = False
		
		self.fileName.set_label(basename(filePath))
		
		self.metadata = pyexiv2.ImageMetadata(filePath)
		
		self.metadata.read()	
		
			
		self.all_keys = self.metadata.exif_keys+self.metadata.iptc_keys+\
			self.metadata.xmp_keys
		
		newTitles = self.loadTitles()	
		newDates = self.loadDates()			
		newCaptions = self.loadCaptions()
		newKeywords = self.loadKeywords()
		if self.forceDefaults.get_active():
			if len(newCaptions) == 0 or newCaptions[0] == '':
				newCaptions.insert(0,'N/A')		
			if len(newKeywords) == 0:
				newKeywords.append('N/A')

		for CB in self.combos:
			self.clearCombo(CB)
		
		# clear the combobox text entry as well
		self.newTitleEntry.set_text('')
		self.newDateEntry.set_text('')
		self.newCaptionEntry.set_text('')
		self.newKeywordEntry.set_text('')		
		
		# and finally set the new values and make them active
	
		# validate the date string in the file.  if it is bad, set it to today.
		try:
			saveDate = newDates[0]
		except:
			saveDate = ''
			
		for t in self.vDates:
			try:
				d=datetime.datetime.strptime( saveDate, t)
				break
			except:
				pass
		try:
			newisoDate = d.strftime("%Y-%m-%d")
			newisoTime = d.strftime("%H:%M:%S")
		except:
			d = datetime.datetime.now()
			newisoDate = str(d.date()) # "YY-MM-DD"
			newisoTime = str(d.strftime('%H:%M:%S'))+'+00:00' # "HH:MM:SS"
			print 'Invalid date: [%s] using current: [%s]'%(saveDate,str(d.strftime('%Y:%m:%d %H:%M:%S')))
			newDates.insert(0,str(d.strftime('%Y:%m:%d %H:%M:%S')))
		
		for t in newDates:
			try:
				self.newDate.append_text(t)
			except:
				self.newDate.append_text(t[0])
				
		self.newDate.set_active(0)

		if self.forceDefaults.get_active():
			if len(newTitles) == 0:
				newTitles.insert(0,newisoDate)
			else:
				if not newTitles[0].startswith(newisoDate):
					newTitles[0] = newisoDate+' - '+newTitles[0]				
			
		for t in newTitles:
			try:
				self.newTitle.append_text(t)
			except TypeError:
				try:
					self.newTitle.append_text(t[0])
				except KeyError:
					self.newTitle.append_text(t['x-default'])
				except:
					print 'title error:',sys.exc_info()
					
		self.newTitle.set_active(0)
		
		
		
		
		for c in newCaptions:
			try:
				self.newCaption.append_text(c)
			except TypeError:
				try:
					self.newCaption.append_text(c[0])
				except KeyError:
					self.newCaption.append_text(c['x-default'])
				except:
					print 'caption error:',sys.exc_info()
					
		self.newCaption.set_active(0)	
		
		for k in newKeywords:  
			try:  
				self.newKeyword.append_text(k)
			except:
				self.newKeyword.append_text(k[0])
				
		self.newKeyword.set_active(0)
		
		# check to see if the commit/revert buttons should be active (any changes needed?)
		
		saveTitle = self.newTitle.get_active_text()
		saveDate = self.newDate.get_active_text()
		for t in self.vDates:
			try:
				d=datetime.datetime.strptime( saveDate, t)
				break
			except:
				pass
		try:
			newisoDate = str(d.date()) # d.strftime("%Y-%m-%d")
			newisoTime = str(d.time())+'+00:00' # d.strftime("%H:%M:%S")
		except:
			d = datetime.datetime.now()
			newisoDate = str(d.date())
			newisoTime = str(dd.strftime('%H:%M:%S'))+'+00:00'
			print 'Invalid date: [%s]'%saveDate	
		
		if self.Debug:
			print 'newisoDate: ',newisoDate
			print 'newisoTime: ',newisoTime
			
		saveCaption = self.newCaption.get_active_text()
		saveKeyword = self.newKeyword.get_active_text()
		
		need_commit = self.checkInitial(self.TIvars,self.TIrem,saveTitle)
		if self.forceDefaults.get_active():
			for k in self.EXvars:
				if k not in self.all_keys:
					need_commit = True
					break
		if not need_commit:
			need_commit = self.checkInitial(self.DTvars,self.DTrem,saveDate)
		if not need_commit:
			need_commit = self.checkInitial(self.CAvars,self.CArem,saveCaption)
		if not need_commit:
			need_commit = self.checkInitial(self.KWvars,self.KWrem,saveKeyword)
		if not need_commit:
			need_commit = self.checkInitial(self.isoDate,[],newisoDate)
		if not need_commit:
			need_commit = self.checkInitial(self.isoTime,[],newisoTime)
			
		if self.Debug:
			print 'need commit: ',need_commit
			
		if need_commit:
			self.commitButton.set_state(Gtk.StateType.NORMAL)
			self.revertButton.set_state(Gtk.StateType.NORMAL)
			self.metaChanged = True
		else:
			self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
			self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
			self.metaChanged = False



	def	checkInitial(self,saveVars,remVars,newValue):
		''' check to see if the file needs a commit '''
	
		if self.forceDefaults.get_active():
			for k in saveVars:
					if k in self.all_keys:
						if type(self.metadata[k].raw_value) == str:
							if self.metadata[k].raw_value != newValue:
								if self.Debug:
									print k,'(s)[',self.metadata[k].raw_value,']!=[',newValue,']'
								return True
						elif type(self.metadata[k].raw_value) == list:
							if ', '.join(self.metadata[k].raw_value) != newValue:
								if self.Debug:
									print k,'(l)[',','.join(self.metadata[k].raw_value),']!=[',newValue,']'
								return True
						else:
							if self.metadata[k].raw_value['x-default'] != newValue:
								if self.Debug:
									print k,'(d)[',self.metadata[k].raw_value['x-default'],']!=[',newValue,']'
								return True							
					else:
						if self.Debug:
							print k,' non-existent'
						return True
			for k in remVars:
				if k in self.all_keys:
					if self.Debug:
						print k,' exists'
					return True
			return False
		else:
			return False


			
	def loadDates(self):
		'''load the Date/Time combobox from the file metadata'''		
		
		myTimes = []
				
		for k in self.DTvars+self.DTrem:
			if k in self.all_keys:
				if self.metadata[k].raw_value not in myTimes:
					myTimes.append(self.metadata[k].raw_value)		
		return myTimes
	
	
	
	def loadTitles(self):
		'''load the Title combobox from the file metadat '''
		
		myTitles=[]
		for k in self.TIvars+self.TIrem:
			if k in self.all_keys:
				if type(self.metadata[k].raw_value) == str:
					v=self.metadata[k].raw_value
				elif type(self.metadata[k].raw_value) == list:
					v=self.metadata[k].raw_value[0]
				else:
					v=self.metadata[k].raw_value['x-default']
					
				if v not in myTitles:
					myTitles.append(v)
		return myTitles
	
	
	
	def loadCaptions(self):
		'''load the Caption combobox from the file metadata'''
		
		myCaptions=[]		
		for k in self.CAvars+self.CArem:
			if k in self.all_keys:
				if type(self.metadata[k].raw_value) == str:
					v=self.metadata[k].raw_value.strip('\00')
				elif type(self.metadata[k].raw_value) == list:
					v=self.metadata[k].raw_value[0].strip('\00')
				else:
					v=self.metadata[k].raw_value['x-default'].strip('\00')
					
				if v not in myCaptions:
					myCaptions.append(v)
			   
		return myCaptions
	
	
	
	def loadKeywords(self):
		'''load the Keyword combobox from the file metadata'''

		myKeywords = []
		
		for k in self.KWvars:
			if k in self.all_keys:
				V=self.metadata[k].raw_value
				if self.Debug:
					print 'k: ',k,' V: ',V
				if type(V) == str:
					myKeywords.append(V)
				else:
					myKeywords.append(', '.join(V))
					for kk in V:
						myKeywords.append(kk)
		if self.Debug:
			print 'myKeywords:',myKeywords
			
		return myKeywords	

	
	
	
	def showImages(self):
		'''debug function: dump the current images paths'''
		
		if self.curImage == None:
			print 'current: None'
		else:
			print 'current: ',urlparse(self.curImage.get_uri_for_display()).path
		try:
			print 'win says: ',urlparse(self.window.get_image().get_uri_for_display()).path
		except:
			print 'none'
		if self.changedImage == None:
			print 'changed: None'
		else:
			print 'changed: ',urlparse(self.changedImage.get_uri_for_display()).path
		if self.thumbImage == None:	
			print 'thumb: None'
		else:
			print 'thumb: ',urlparse(self.thumbImage.get_uri_for_display()).path
		try:
			print 'thumb says: ',urlparse(self.thumbview.get_first_selected_image().get_uri_for_display()).path
		except:
			print 'none'
