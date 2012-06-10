#!/usr/bin/env python2

import tools
    
tools.login("kevin", "")

tools.get_a_theme(4, "Vayrac")
theme = None
tools.get_a_theme(5, "Martinique")

tools.print_error_report()