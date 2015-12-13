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

from gi.repository import GObject, Gtk, Gdk, Eog, PeasGtk, Gio, GLib # , Pango
from os.path import join, basename
from urllib.parse import urlparse
from lxml import etree

import re
import datetime
import time
import sys

tools = None

class WebAlbumsPlugin(GObject.Object, Eog.WindowActivatable):
    # the main EoG window
    window = GObject.property(type=Eog.Window)
    Debug = False

    Make = 'WebAlbumsEOG'
    Model = 'v0.1b'

    def __init__(self):
        GObject.Object.__init__(self)
        self.ready = False
        
    def do_activate(self):
        '''Activate the plugin - adds my dialog to the Eog Sidebar'''
        return 
        global tools
        if not tools:
            sys.path.append("/home/kevin/WebAlbums/WebAlbums-Downloader/")
            import tools

            if not tools.server_on():
                print("Server is off, cannot load webalbums pluging.")
                return
            
            def do_login(a, b, c):
                tools.login("kevin", "", get_xslt=False, parse_and_transform=False, save_index=False)
                self.ready = True
            Gio.io_scheduler_push_job(do_login, None, GLib.PRIORITY_DEFAULT, None)
            
            
        # the sidebar is where the dialog window is added
        self.sidebar = self.window.get_sidebar()
        # need to track file changes in the EoG thumbview
        self.thumbview = self.window.get_thumb_view()

        # the EogImage of the main window
        self.curImage = None
        # the EogImage selected in the thumbview
        self.thumbImage = None

        # build my dialog
        builder = Gtk.Builder()
        builder.add_from_file(join(self.plugin_info.get_data_dir(), "eog-webalbums.glade"))
        pluginDialog = builder.get_object('eogWebAlbumsPanel')
        
        self.isChangedDialog = builder.get_object('isChangedDialog')
        self.errorMessageDialog = builder.get_object('errorMessageDialog')
        self.errorMsg = builder.get_object('errorMsg')

        # my widgets
        self.albumValueLabel = builder.get_object("albumValueLabel")
        self.dateValueLabel = builder.get_object("dateValueLabel")
        self.descriptionValueLabel = builder.get_object("descriptionValueLabel")
        self.tagsValueLabel = builder.get_object("tagsValueLabel")
        self.starsValueLabel = builder.get_object("starsValueLabel")
        self.visibilityValueLabel = builder.get_object("visibilityValueLabel")

        self.cb_ids = {}
        self.cb_ids['selection-changed'] = {}
        self.cb_ids['selection-changed'][self.thumbview] = \
            self.thumbview.connect('selection-changed', \
                                       self.selection_changed_cb, self)
        # set the buttons disabled initially
        #self.commitButton.set_state(Gtk.StateType.INSENSITIVE)
        #self.revertButton.set_state(Gtk.StateType.INSENSITIVE)
        

        # these lists are for convenience later
        #self.combos =  [self.newTitle, self.newDate, self.newCaption, self.newKeyword]
        #self.entries = [self.newTitleEntry, self.newDateEntry, self.newCaptionEntry, 
        #            self.newKeywordEntry]

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
        """
        self.cb_ids = {}
        self.cb_ids['key-press-event'] = {}
        self.cb_ids['key-press-event'][self.window] = self.window.connect('key-press-event', self.key_press_event_cb)
        # block this callback initially - it is only enabled when I want focus
        self.window.handler_block(self.cb_ids['key-press-event'][self.window])

        for S in 'focus-in-event', 'focus-out-event':
            if S not in self.cb_ids:
                self.cb_ids[S]={}
            for W in self.entries:
                self.cb_ids[S][W] = W.connect(S, self.focus_event_cb, self.window, self.cb_ids['key-press-event'][self.window])

        self.cb_ids['focus-out-event'][self.newDateEntry] = self.newDateEntry.connect('focus-out-event', self.test_date_cb, self.newDate, self)

        for S in 'changed', :
            if S not in self.cb_ids:
                self.cb_ids[S]={}
            for W in self.combos:
                self.cb_ids[S][W] = W.connect(S, self.combo_changed_cb, self)

        self.cb_ids['clicked'] = {}
        self.cb_ids['clicked'][self.commitButton] = self.commitButton.connect('clicked', self.commit_clicked_cb, self)
        self.cb_ids['clicked'][self.revertButton] = self.revertButton.connect('clicked', self.revert_clicked_cb, self)

        self.cb_ids['selection-changed'] = {}
        self.cb_ids['selection-changed'][self.thumbview] = self.thumbview.connect('selection-changed', 
                                                                                  self.selection_changed_cb, self)

        self.cb_ids['toggled'] = {}
        #self.cb_ids['toggled'][self.forceDefaults] = self.forceDefaults.connect('toggled', self.forceToggled, self)

        # finally, add my dialog to the sidebar


        if self.thumbview.get_first_selected_image() != None:
            self.changedImage = self.thumbview.get_first_selected_image()
            self.loadMeta(urlparse(self.changedImage.get_uri_for_display()).path)
        """
        Eog.Sidebar.add_page(self.sidebar, "WebAlbums Information", pluginDialog)
        Eog.Sidebar.set_page(self.sidebar, pluginDialog)
        
    def do_deactivate(self):
        '''remove all the callbacks stored in dict self.cb_ids '''

        #for S in self.cb_ids:
        #    for W, id in self.cb_ids[S].items():
        #        W.disconnect(id)

        pass
    
    @staticmethod
    def forceToggled(checkB, self):
        ''' toggle button changed '''
        return True
    
    @staticmethod
    def test_date_cb(widget, event, entry, win):
        ''' test the validity of the date string (against vDates format) '''
        
        #entry.set_active(0)
        #entry.grab_focus()
        return True

    @staticmethod
    def focus_event_cb(widget, event, win, id):
        '''
        Process the change of focus for the comboboxes.  If the combobox has
        focus, unblock the key-press-event callback on the main EogWindow so
        that the combobox can get the key-press and not eog_window_key_press.

        '''
        """
        if widget.has_focus():
            win.handler_unblock(id)
        else:
            win.handler_block(id)
        """
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
        plugin.get_focus().do_key_press_event(plugin.get_focus(), event)
        plugin.emit_stop_by_name('key-press-event')

        return True
    
    @staticmethod
    def	selection_changed_cb(thumb, self):
        try:
            self.thumbImage = self.thumbview.get_first_selected_image()
        except:
            print("Cannot access self.thumbview ...")
            return
        if self.thumbImage:
            self.update_image()
        
        return False

    @staticmethod
    def combo_changed_cb(plugin, self):
        '''
        One of the comboboxes has changed.
        Enable the revert and commit buttons and save changedImage
        '''

        # return True -- default callback isn't needed
        return True

    def show_changes(self):
        ''' return a string detailing the changes a commit would make '''

        return "rien"

    @staticmethod
    def commit_clicked_cb(plugin, self):
        '''Commit the changes to the file'''

        return True

    @staticmethod
    def revert_clicked_cb(plugin, self):
        '''
        Revert the three comboboxes to the values in the file itself and set
        the buttons back to insensitive
        '''

        return True

    def update_image(self):
        PREFIX = "file:///other/Web/data/images/"
        uri = self.thumbImage.get_uri_for_display()
        if not uri.startswith(PREFIX):
            return
        
        uri = uri.replace(PREFIX, "")

        theme, _, path = uri.partition("/")
        Gio.io_scheduler_push_job(lambda a, b, c:self.get_path(path), None, GLib.PRIORITY_DEFAULT, None)
        
    def get_path(self, path):
        while not self.ready: time.sleep(0.1)
        print("GET {}".format(path))

        XML = tools.get_a_page("Photos?special=ABOUT&path={}".format(path), save=False, parse_and_transform=False).decode('ISO-8859-1')
        
        XML = XML.replace('encoding="ISO-8859-1"', "")
        
        if "This request requires HTTP authentication." in XML:
            print("Authentication problem ...")
            return
        
        try:
            self.tree = etree.fromstring(XML)
        except Exception as e:
            print(e)
            print(XML)
            return
        
        albumName = self.tree.xpath("/webAlbums/photos/about/details/albumName")[0].text
        albumDate = self.tree.xpath("/webAlbums/photos/about/details/albumDate")[0].text
        description = self.tree.xpath("/webAlbums/photos/about/details/description")
        tagList = self.tree.xpath("/webAlbums/photos/about/details/tagList/*")
        stars = int(self.tree.xpath("/webAlbums/photos/about/details/@stars")[0])
        user = self.tree.xpath("/webAlbums/photos/about/details/user")[0]
        
        self.albumValueLabel.set_text(albumName)
        self.dateValueLabel.set_text(albumDate)
        self.descriptionValueLabel.set_text("(no description)" if not description else description[0].text)
        self.tagsValueLabel.set_text(", ".join([tag.find("name").text for tag in tagList]))
        self.starsValueLabel.set_text(stars*"★" + (5-stars)*"☆")
        
        user = user.text if user.get("outside") != 'true' else "[{}]".format(user.text)
        self.visibilityValueLabel.set_text(user)
