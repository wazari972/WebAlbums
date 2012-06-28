#!/usr/bin/env python2

import tools
    
tools.ABS_DATA_PATH = "../"
    
tools.login("kevin", "")
exit()
tools.get_a_theme(5, "Martinique")
theme = None
tools.get_a_theme(4, "Vayrac")
theme = None
tools.get_a_theme(11, "Mariage")

tools.print_error_report()