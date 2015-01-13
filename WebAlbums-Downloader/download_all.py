#!/usr/bin/env python2

import tools
import timeit

tools.login("kevin", "")
def compute():
    tools.get_all_themes()
    #tools.get_a_theme(1, "Travel")

    tools.print_error_report()

time = timeit.Timer(compute).timeit(1)
print time
