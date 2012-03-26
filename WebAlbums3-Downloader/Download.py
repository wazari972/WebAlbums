#!/usr/bin/env python2

from httplib2 import Http
from urllib import urlencode
import os
from lxml import etree
import timeit
from StringIO import StringIO

STATIC_PATH = "/home/kevin/WebAlbums/WebAlbums3-Servlet/web/static"
ROOT_PATH = "http://127.0.0.1:8080/WebAlbums3/"
TARGET_PATH = "./static"

h = Http("")
headers = None

def post_a_page(url, data):
    response, content = h.request(ROOT_PATH+url, 'POST', body=urlencode(data), headers=headers)
    return content

errors = []
count = 0
theme = None
def get_a_page(url, name="", save=True, parse_and_transform=True):
    global count
    count += 1
    #url += name
    url = url.replace(" ", "%20")
    try:
        response, content = h.request(ROOT_PATH+url, 'GET', headers=headers)
        if response["status"] == 500:
            raise "HTTP Error 500 "+url
        if response["status"] == 404:
            raise "HTTP Error 404 "+url
        folder = TARGET_PATH +'/'+ (theme+"/" if theme is not None else "")
        try:
            os.mkdir(folder)
        except OSError as e:
            if e.errno != 17:
                raise e
        print "#%d %s: %s %s" % (count, theme, repr(url), repr(name))
        content_to_save = content
        content_to_return =  content        
        
        if parse_and_transform:
            content_to_return = etree.fromstring(content)
            #print "#%d %s %s: %s %s" % (count, theme, xml.find("time").text, repr(url), repr(name))
            content_to_save = etree.tostring(displayXslt(content_to_return), pretty_print=False, method="html")

        if save:
            path = folder+url+name
            with open(path, "w") as f:
                f.write(content_to_save)
        
        return content_to_return
    except Exception as e:
        print "Request: ", repr(url)
        print "Status: ", response["status"]
        print "Response: ", response
        print e
        print ""
        errors.append((e, repr(url), response, content))
        return

def print_error_report():
    print "======================"
    for (e, url, response, content) in errors:
        print "Error %s" % e
        print "For url: %s" % url
        print "Response: %s" % response
        print "------------"
        
def login(user, paswd):
    global headers
    data = dict(userName=user, userPass=paswd)
    headers = {'Content-type': 'application/x-www-form-urlencoded'}
    response, content = h.request("%sUsers?action=LOGIN" % ROOT_PATH, "POST", body=urlencode(data), headers=headers)
    headers = {'Cookie': response['set-cookie']}
    data = dict(themeId=9)
    
def get_choix(themeId, name=""):
    global theme
    theme = name
    return get_a_page("Choix__%s__" % themeId, name)

def get_an_albumSet(page=0):
    if page == 0:
        return get_a_page("Albums")
    else:
        return get_a_page("Albums__p%s" % page)

def get_a_photoSet(albmId, page=0, name=""):
    return get_a_page("Photos__%s_p%s__" % (albmId, page), name)
    
def get_a_tag_page(tagId, page=0, name=""):
    if page == 0:
        return get_a_page("Tag__%s__" % (tagId), name)
    else:
        return get_a_page("Tag__%s_p%d__" % (tagId, page), name)
def get_a_carnet(carnedId, name=""):
    return get_a_page("Carnet__%s__" % carnedId, name)

def get_a_carnetSet(page=0):
    if page == 0:
        return get_a_page("Carnets")
    else:
        return get_a_page("Carnets__p%s" % (page))
def get_all_carnet_from_carnetSet(page=0):
    carnetSet = get_a_carnetSet(page)
    for carnet in carnetSet.find("carnets").find("display").findall("carnet"):
        get_a_carnet(carnet.get("id"), carnet.find("name").text)
    return carnetSet
    
def get_all_carnets():
    first = get_all_carnet_from_carnetSet()
    
    page = first.find("carnets").find("display").find("page")
    if page.get("last") is not None:
        nb_pages = int(page.get("last"))
    elif page.find("next") is not None:
        nb_pages = int(page.find("next[last()]").text)
    else:
        nb_pages = 0
    for cur_page in range(1, nb_pages+1):
        current = get_all_carnet_from_carnetSet(cur_page)
    
def get_all_photos_of_photoSet(albumId, name=""):
    first = get_a_photoSet(albumId, name=name)
    if first is None:
        print "Couldn't fetch photos from %s/%s" % (repr(name), albumId)
        return
    page = first.find("photos").find("display").find("photoList").find("page")
    if page.get("last") is not None:
        nb_pages = int(page.get("last"))
    elif page.find("next") is not None:
        nb_pages = int(page.find("next[last()]").text)
    else:
        nb_pages = 0
    for cur_page in range(1, nb_pages+1):
        current = get_a_photoSet(albumId, cur_page, name)
        
def get_albums_of_albumSet(albumSet):
    for album in albumSet.find("albums").find("display").find("albumList").findall("album"):
        get_all_photos_of_photoSet(album.get("id"), album.find("title").text)

    
def get_a_tagSet(tagId, name=""):
    first = get_a_tag_page(tagId, name=name)
    if first is None:
        print "Couldn't fetch tag page %s/%s" % (repr(name), tagId)
        return
    page = first.find("tags").find("display").find("photoList").find("page")
    if page.get("last") is not None:
        nb_pages = int(page.get("last"))
    elif page.find("next") is not None:
        nb_pages = int(page.find("next[last()]").text)
    else:
        nb_pages = 0
    for cur_page in range(1, nb_pages+1):
        current = get_a_tag_page(tagId, cur_page, name=name)
  
def get_all_albums():
    first = get_an_albumSet()
    if first is None:
        print "Couldn't fetch albums page"
        return
    page = first.find("albums").find("display").find("albumList").find("page")
    if page.get("last") is not None:
        nb_pages = int(page.get("last"))
    elif page.find("next") is not None:
        nb_pages = int(page.find("next[last()]").text)
    else:
        nb_pages = 0
    get_albums_of_albumSet(first)
    for cur_page in range(1, nb_pages+1):
        current = get_an_albumSet(cur_page)
        get_albums_of_albumSet(current)

        
def get_all_tags(choix):
    tagList = choix.find("choix").find("tagList")
    for tag in tagList.findall("who")+tagList.findall("what")+tagList.findall("where"):
        #tag.find("name").text
        get_a_tagSet(tag.get("id"), tag.find("name").text)
        get_a_tagSet(tag.get("id")+"x", tag.find("name").text)

def get_background(themeId, name):
    get_a_page("background__%s__%s.jpg" % (themeId, name), parse_and_transform=False)
        
def get_a_theme(themeId, name):
    choix = get_choix(themeId, name)
    
    if choix is None:
        print "Couldn't fetch Choix for %s/%s" % (repr(name), themeId)
        return
    get_static()
    get_background(themeId, name)
    get_all_albums()
    get_all_carnets()
    get_all_tags(choix)
    
def get_static():
    target = TARGET_PATH+("/"+theme if theme is not None else "")
    print "Copy static to '%s'" % target
    os.system("cp -r '%s' '%s'" % (STATIC_PATH, target))
    
def get_all_themes():
    get_static()
    index = get_a_page("index.html")
    if index is None:
        print "Couldn't fetch the index ..."
        return
        
    for xmlTheme in index.find("themes").find("themeList").findall("theme"):
        global theme
        theme = None
        get_a_theme(xmlTheme.get("id"),  xmlTheme.get("name"))
        
    print_error_report()

class PrefixResolver(etree.Resolver):        
    def resolve(self, url, pubid, context):
        stylesheet = get_a_page("static/%s" % url, parse_and_transform=False, save=False)
        return self.resolve_string(stylesheet, context)
    
displayXslt = None
def get_XSLT():
    global displayXslt
    if displayXslt is None:
        print "Get stylesheets"
        parser = etree.XMLParser()
        parser.resolvers.add(PrefixResolver())
        display = get_a_page("static/Display.xsl", parse_and_transform=False, save=False)
        displayXml = etree.parse(StringIO(display), parser)
        
        displayXslt = etree.XSLT(displayXml)
    return displayXslt
        
    
login("kevin", "")
get_XSLT()

print timeit.Timer(get_all_themes).timeit(1)
