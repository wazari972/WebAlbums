#!/usr/bin/env python2

import tools

tools.login("kevin", "",  get_xslt=False, parse_and_transform=False, do_static=False)
tools.get_choix(1, "Root", want_static=False, want_background=False, parse_and_transform=False)
tools.get_a_page("Database?action=TRUNK", save=False, parse_and_transform=False)
page = tools.get_a_page("Database?action=IMPORT", save=False, parse_and_transform=False)
correct = "Export OK" in page

print "Correct ? %s" % correct
exit(0 if correct else 1)


