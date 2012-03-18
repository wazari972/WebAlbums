from httplib2 import Http
from urllib import urlencode
import os

ROOT_PATH = "http://127.0.0.1:8080/WebAlbums3/"
TARGET_PATH = "./static"

h = Http("")
headers = None

def post_a_page(url, data):
    response, content = h.request(ROOT_PATH+url, 'POST', body=urlencode(data), headers=headers)
    return content

def get_a_page(url):
    response, content = h.request(ROOT_PATH+url, 'GET', headers=headers)
    f = open("%s/%s" % (TARGET_PATH, url), "w")
    f.write(content)
    f.close()
    return content

def login(user, paswd, theme):
    global headers
    data = dict(userName=user, userPass=paswd)
    headers = {'Content-type': 'application/x-www-form-urlencoded'}
    response, content = h.request("%sUsers?action=LOGIN" % ROOT_PATH, "POST", body=urlencode(data), headers=headers)
    headers = {'Cookie': response['set-cookie']}
    data = dict(themeId=9)
    return get_a_page("Choix__9__Grenoble")

def get_an_album_page(page=0):
    return get_a_page("Albums__p%d" % page)

def get_a_photo_page(albmId, page=0):
    return get_a_page("Photos__%d_p%d_pa__" % (albmId, page))

def get_a_tag_page(tagId, page=0):
    return get_a_page("Tag__%d__" % (tagId))

print login("kevin", "", 9)
    
print get_an_album_page()
print get_an_album_page(5)

print get_a_photo_page(580)
print get_a_photo_page(580, 2)
    

    
    
#print get_a_tag_page(1)
#print get_a_tag_page(1, 2)