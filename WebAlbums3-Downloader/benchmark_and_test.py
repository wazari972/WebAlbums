#!/usr/bin/env python2
from lxml import etree
import tools
import timeit

DO_SAVE = False

def treat(xml, url, name):
    path = tools.get_current_target_folder()+url+name+".xml"
    xml.find("time").text = "nothing"
    if DO_SAVE:
        with open(path, "w") as f:
            f.write(etree.tostring(xml))
    else:
        with open(path, "r") as f:
            xml_ref = etree.fromstringlist(f.readlines())
        
        print etree.tostring(xml_ref) == etree.tostring(xml)
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
    
print timeit.Timer(compute).timeit(1)