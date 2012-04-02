#!/usr/bin/env python2
from lxml import etree
import tools
import timeit
        

REPEAT = 10
        
def treat(xml, url, name):
    if xml.find("*/exception") is not None:
        raise Exception (xml.find("*/exception").text)
    return False

def fail(e, url, name, response, content):
    import pdb;pdb.set_trace()
    pass

tools.callback = treat
tools.fail = fail

def compute(action, mode):
    tools.get_a_page("Benchmark?action=%s&mode=%s" % (action, mode))
    
    
tools.login("kevin", "", save_index=False, get_xslt=False, do_static=False)
tools.get_choix(5, "Martinique", want_static=False, want_background=False)

with open("benchmark.log", "a+") as f:
    f.write("------------- x%d\n" % REPEAT)

action = "TAGS"
for mode in ("TAG_USED", "TAG_NUSED", "TAG_ALL", "TAG_NEVER", "TAG_NEVER_EVER", "TAG_GEO"):
    time = timeit.Timer((lambda:compute(action, mode))).timeit(REPEAT)/REPEAT

    with open("benchmark.log", "a+") as f:
        string = "Action: %s, Mode %s, %s" % (action, mode, time)
        print string
        f.write(string+"\n")
        