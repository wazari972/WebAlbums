#!/usr/bin/env python2
import sys
import tools
    
tools.ABS_DATA_PATH = "../"
    
tools.login("kevin", "")

default_themes = {5 : "Martinique", 4: "Vayrac", 11: "Mariage"}

if len(sys.argv) == 3:
    themes = {int(sys.argv[1]): sys.argv[2]}
else:
    themes = default_themes

for idx, name in themes.items():
    print "Download {}/{}".format(idx, name)
    tools.get_a_theme(idx, name)

tools.print_error_report()
