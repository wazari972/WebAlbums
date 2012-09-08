from httplib2 import Http
from urllib import urlencode
import os
from lxml import etree
import timeit
from StringIO import StringIO

STATIC_FOLDER = "../WebAlbums3-Servlet/web/static"
WA_URL = "http://127.0.0.1:8080/WebAlbums3.5-beta6/"
TARGET_PATH = "./download"

ABS_DATA_PATH = "/other/Web/"

INDEX = """<html><head><meta http-equiv="refresh" content="0; URL=%s" /></head></html>"""

h = Http("")
headers = None

def get_data_path_for_url():
    if theme is None:
        return "./"
    else:
        return "../"

def get_current_target_folder(with_theme=True):
    folder = TARGET_PATH +'/'+ (theme+"/" if theme is not None and with_theme else "")
    try:
        os.mkdir(folder)
    except OSError as e:
        if e.errno != 17:
            raise e
    return folder

def post_a_page(url, data):
    response, content = h.request(WA_URL+url, 'POST', body=urlencode(data), headers=headers)
    return content

    
def callback(xml, url, name):
    return True
    
errors = []
def fail(e, url, name, response, content):
    errors.append((e, url, name, response, content))
    
def print_error_report():
    print "======================"
    for (e, url, name, response, content) in errors:
        print "Error %s" % e
        print "For url: %s%s" % (url, repr(name))
        print "Response: %s" % response
        print "------------"
    
count = 0
theme = None
def get_a_page(url, name="", save=True, parse_and_transform=True, full=False, make_index=False, use_empty_xsl=False):
    global count
    
    name = name.encode("latin1")
    count += 1
    #url += name
    url = url.replace(" ", "%20")
    try:
        response, content = h.request(WA_URL+url, 'GET', headers=headers)
        if response["status"] == 500:
            raise "HTTP Error 500 "+url
        if response["status"] == 404:
            raise "HTTP Error 404 "+url
        folder = get_current_target_folder()
        
        if make_index:
            path = folder+"index.html"
            with open(path, "w") as f:
                f.write(INDEX % (url+name))
        
        print "#%d %s: %s %s" % (count, theme, repr(url), repr(name))
        
        content_to_save = content
        content_to_return =  content        
        
        if parse_and_transform:
            content_to_return = etree.fromstring(content)
            #print "#%d %s %s: %s %s" % (count, theme, xml.find("time").text, repr(url), repr(name))
            
            if not callback(content_to_return, url, name):
                return content_to_return
            
            if full:
                root_path = ABS_DATA_PATH
            else:
                root_path = get_data_path_for_url()
            
            xslt = displayXslt if not use_empty_xsl else emptyXslt
            
            content_to_full = xslt(content_to_return, RootPath="'%s'" % root_path)
            
            if not full:
                content_to_save = etree.tostring(content_to_full, pretty_print=False, method="html")

        if save:
            if full:
                def do_copy(filename):
                    if not filename.startswith(ABS_DATA_PATH):
                        return filename
                    src = filename
                    target = filename.replace(ABS_DATA_PATH, get_current_target_folder(with_theme=False))
                    link = filename.replace(ABS_DATA_PATH, get_data_path_for_url())
                    #print "%s --> %s (%s)" % (src, target, link)
                    #create dirname(target)
                    target_dir = target[:target.rindex("/")]
                    try:
                        os.makedirs(target_dir)
                    except OSError as e:
                        if e.errno != 17:
                            raise e
                    #copy src to target
                    cmd = "cp '%s' '%s'" % (src, target_dir)
                    print cmd
                    os.system(cmd)
                    return link
                    
                for img in content_to_full.xpath("//*/img[@src]"):
                    img.set("src", do_copy(img.get("src")))
                
                for a in content_to_full.xpath("//*/a[@href]"):
                    a.set("href", do_copy(a.get("href")))
                
                content_to_save = etree.tostring(content_to_full, pretty_print=False, method="html")
                
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
        
        fail(e, url, name, response, content)
        return


        
########################################
#######  COMMON ########################
########################################

def login(user, paswd, save_index=True, do_static=True, get_xslt=True, parse_and_transform=True):
    global headers
    data = dict(userName=user, userPass=paswd)
    headers = {'Content-type': 'application/x-www-form-urlencoded'}
    response, content = h.request("%sUsers?action=LOGIN" % WA_URL, "POST", body=urlencode(data), headers=headers)
    headers = {'Cookie': response['set-cookie']}
    data = dict(themeId=9)
    if get_xslt:
        prepare_XSLTs()
    get_index(save=save_index, do_static=do_static, parse_and_transform=parse_and_transform)
    
def get_choix(themeId, name="", make_index=False, want_static=True, want_background=True, parse_and_transform=True):
    global theme
    theme = name
    choix = get_a_page("Choix__%s__" % themeId, name, make_index=make_index, parse_and_transform=parse_and_transform)
    
    if want_background:
        get_background(themeId, name)
    if want_static:
        get_static()
    return choix

def get_index(full=False, save=False, do_static=False, parse_and_transform=True):
    return get_a_page("index.html" if do_static else "Index", full=full, save=save, parse_and_transform=parse_and_transform)
    
def get_background(themeId, name):
    get_a_page("background__%s__%s.jpg" % (themeId, name), parse_and_transform=False)
    
########################################
#######  STATIC ########################
########################################

def get_static():
    target = TARGET_PATH+("/"+theme if theme is not None else "")
    print "Copy static to '%s'" % target
    os.system("cp -r '%s' '%s'" % (STATIC_FOLDER, target))
    
class PrefixResolver(etree.Resolver):        
    def resolve(self, url, pubid, context):
        stylesheet = get_a_page("static/%s" % url, parse_and_transform=False, save=False)
        if "Include" in url:
            inc = etree.fromstring(stylesheet)
            #overwrite param:RootPath with ...
            [c for c in inc.iterchildren()][0].text = ABS_DATA_PATH
            stylesheet = etree.tostring(inc)
        return self.resolve_string(stylesheet, context)
    
displayXslt = None
emptyXslt = None

def prepare_XSLTs():
    global displayXslt, emptyXslt
    if displayXslt is None:
        displayXslt = get_XSLT("static/Display.xsl")
        emptyXslt = get_XSLT("static/Empty.xsl")
        
def get_XSLT(path):
    print "Get stylesheets"
    parser = etree.XMLParser()
    parser.resolvers.add(PrefixResolver())
    page = get_a_page(path, parse_and_transform=False, save=False)
    xml = etree.parse(StringIO(page), parser)
    xslt = etree.XSLT(xml)
        
    return xslt
    
########################################
#######  ALBUMS ########################
########################################

def get_an_albumSet(page=0):
    if page == 0:
        return get_a_page("Albums")
    else:
        return get_a_page("Albums__p%s" % page)
        
def get_albums_of_albumSet(albumSet):
    for album in albumSet.find("albums").find("display").find("albumList").findall("album"):
        get_all_photos_of_photoSet(album.get("id"), album.find("title").text)

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

########################################
#######  PHOTOS ########################
########################################

def get_a_visioSet(albmId, page=0, name="", full=False):
    return get_a_page("Visio__%s_p%s__" % (albmId, page), name, full=full, use_empty_xsl=True)

def get_a_photoSet(albmId, page=0, name="", full=False):
    return get_a_page("Photos__%s_p%s__" % (albmId, page), name, full=full)
    
photos_already_dl = []
def get_all_photos_of_photoSet(albumId, name="", full=False):
    if albumId in photos_already_dl:
        return
    
    photos_already_dl.append(albumId)
    first = get_a_photoSet(albumId, name=name, full=full)
    if first is None:
        print "Couldn't fetch photos from %s/%s" % (repr(name), albumId)
        return
    
    get_a_visioSet(albumId, name=name, full=full)
        
    page = first.find("photos").find("display").find("photoList").find("page")
    if page.get("last") is not None:
        nb_pages = int(page.get("last"))
    elif page.find("next") is not None:
        nb_pages = int(page.find("next[last()]").text)
    else:
        nb_pages = 0
    for cur_page in range(1, nb_pages+1):
        current = get_a_photoSet(albumId, cur_page, name, full=full)
        get_a_visioSet(albumId, cur_page, name, full=full)
        
        
########################################
#######  TAGS  #########################
########################################
    
def get_a_tag_page(tagId, page=0, name="", full=False, make_index=False, get_related_albums=False):
    if get_related_albums:
        full=False
    if page == 0:
        tag = get_a_page("Tag__%s__" % (tagId), name, full=full, make_index=make_index)
    else:
        tag = get_a_page("Tag__%s_p%d__" % (tagId, page), name, full=full)
        
    if get_related_albums:
        for detail in tag.xpath("//*/details"):
            albumId = detail.get("albumId")
            get_all_photos_of_photoSet(albumId, name=detail.find("albumName").text, full=True)
            
    return tag
    
def get_a_tagSet(tagId, name="", full=False, make_index=False, get_related_albums=False):
    first = get_a_tag_page(tagId, name=name, full=full, make_index=make_index, get_related_albums=get_related_albums)
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
        current = get_a_tag_page(tagId, cur_page, name=name, full=full, get_related_albums=get_related_albums)
        
def get_all_tags(choix):
    tagList = choix.find("choix").find("tagList")
    for tag in tagList.findall("who")+tagList.findall("what")+tagList.findall("where"):
        #tag.find("name").text
        get_a_tagSet(tag.get("id"), tag.find("name").text)
        get_a_tagSet(tag.get("id")+"x", tag.find("name").text)

########################################
#######  CARNETS  ######################
########################################

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
        
########################################
#######  THEMES ########################
########################################

def get_a_theme(themeId, name,  want_static=True, want_background=True):
    choix = get_choix(themeId, name, make_index=True,  want_static=want_static, want_background=want_background)
    
    if choix is None:
        print "Couldn't fetch Choix for %s/%s" % (repr(name), themeId)
        return
    get_static()
    get_all_albums()
    get_all_carnets()
    get_all_tags(choix)
    
def get_all_themes():
    get_static()
    index = get_index()
    if index is None:
        print "Couldn't fetch the index ..."
        return
        
    for xmlTheme in index.find("themes").find("themeList").findall("theme"):
        global theme
        theme = None
        get_a_theme(xmlTheme.get("id"),  xmlTheme.get("name"))

########################################
########################################
########################################
