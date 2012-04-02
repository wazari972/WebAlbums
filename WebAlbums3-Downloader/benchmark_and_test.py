#!/usr/bin/env python2
from lxml import etree
import tools
import timeit

DO_SAVE = False
DO_STATIC = False
DO_BENCHMARK_ONLY = True

class Difference(Exception):
    def __init__(self, url, name, ref, cur):
        self.url = url
        self.name = name
        self.ref = ref
        self.cur = cur
        

def treat(xml, url, name):
    if DO_BENCHMARK_ONLY:
        return False
    path = tools.get_current_target_folder()+url+name+".xml"
    xml.find("time").text = "nothing"
    years = xml.find("*/years")
    
    if years is not None:
        years.getparent().remove(years)
        
    if DO_SAVE:
        with open(path, "w") as f:
            f.write(etree.tostring(xml))
    else:
        with open(path, "r") as f:
            xml_ref = etree.fromstringlist(f.readlines())
        
        if etree.tostring(xml_ref) != etree.tostring(xml):
            raise Difference(url, name, xml_ref, xml)
    return False

def fail(e, url, name, response, content):
    if isinstance(e, Difference):
        ref = etree.tostring(e.ref, pretty_print=True).split("\n")
        cur = etree.tostring(e.cur, pretty_print=True).split("\n")
        
        i = 0
        for ref_line in ref:
            if ref_line != cur[i]:
                print "|>| %s | =/= | %s |<|" % (ref_line, cur[i])
            i += 1
        
    
    import pdb;pdb.set_trace()
    pass

tools.callback = treat
tools.fail = fail
tools.TARGET_PATH = "./test-ref"

def compute():
    tools.login("kevin", "", save_index=False, do_static=DO_STATIC)
    tools.get_a_theme(5, "Martinique", want_static=False, want_background=False)

time = timeit.Timer(compute).timeit(1)
with open("benchmark_and_test.log", "a+") as f:
    string = "save? %s -- %d ; static? %s, benchmark_only ? %s" % (DO_SAVE, time, DO_STATIC, DO_BENCHMARK_ONLY)
    print string
    f.write(string+"\n")