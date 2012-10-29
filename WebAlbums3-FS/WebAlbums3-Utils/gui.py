#!/usr/bin/python
# -*- coding: iso-8859-1 -*-
import pygtk
pygtk.require("2.0")
import gtk
import gtk.glade

class monprogramme:
    def __init__(self):
        self.widgets = gtk.glade.XML('Gui.glade',"window1")
        events = { 'on_button1_clicked': self.monclic,
                   'delete': self.delete               }
        self.widgets.signal_autoconnect(events)

    def delete(self, source=None, event=None):
	gtk.main_quit()

    def monclic(self, source=None, event=None):
        self.widgets.get_widget('label1').set_text('Vous avez cliqué !')
        return True
       
if __name__ == '__main__':
    app = monprogramme()
    gtk.main()
