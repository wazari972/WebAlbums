from gi.repository import Gtk

class Handler:
    def onDestroy(self, *args):
        print "bye"
        Gtk.main_quit(*args)

    def onPolaroid(self, button):
        import pdb;pdb.set_trace()
        print "Hello World! :)"
        

builder = Gtk.Builder()
builder.add_from_file("Gui.glade")
builder.connect_signals(Handler())

window = builder.get_object("main")
window.show_all()

Gtk.main()