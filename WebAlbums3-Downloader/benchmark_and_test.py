#!/usr/bin/env python2
import lxml
import tools
import timeit

def treat(xml, url, name):
    print "--->", url, name
    return False

def fail(e, url, name, response, content):
    import pdb;pdb.set_trace()
    pass

tools.callback = treat
tools.fail = fail
tools.TARGET_PATH = "./test-ref"

def compute():
    tools.login("kevin", "", save_index=False)
    tools.get_a_theme(5, "Martinique", want_static=False, want_background=False)
    
timeit.Timer(compute).timeit(1)