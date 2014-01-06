#!/usr/bin/env python2

import tools
    
tools.login("kevin", "")

tools.get_choix(1, "Root", want_static=False, want_background=False)

tools.get_a_carnet(19, "Panoramiques", full=True)

tools.print_error_report()
