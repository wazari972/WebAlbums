Presentation
============

WebAlbums is a web-application for managing collections for
pictures. It's my weekend project since more than four years, and
currently holds some 4000 photos, 300 tags, 500 albums and 9 themes.

Photographs are naturally organised into daily albums grouped into
themes (ie, collections). Then pictures' content is described with
tags.  Tags allows to group, and search for, pictures sharing common
aspects. Gpx files can also be attached to albums.


Themes, Albums and Tags Organization
====================================

Albums and themes are the main organisational entites. They refer
directly to the filesystem folder structure.

Themes
------

A *theme* corresponds to a collection of albums, photos and tags. It
can be used for instance to distinguish different living places, or
different people groups (photos with family vs photos with friends).

Albums
------

Album are design to hold the photos from a given day. They are have at least a date and a title, and they are described by the tags of the photos they contain.

Tags
----

Tags allow to group pictures according to common interesting
aspects. They are currently separated into three categories, WHO,
WHERE and WHAT.

 - WHO tags point to persons, and can include a birthdate to indicate
   the person age at shooting time.
 - WHERE tags point to geographic locations and can be displayed on a
   map.
 - WHAT tags should more or less describe everything else!

Carnets
-------

Carnets are text-oriented pages for talking about the pictures. It
allows you to write some 'carnets de voyage' and easily integrate some
pictures.

Right Management
================

As the photo collection can be opened to the Internet, its access is
password protected. Accounts should be created for each individual
user, however picture access right is based on hierchical roles. The
default configuration includes four roles, **all**, **familly**,
**friends** and **public**. The **administrator** privilege can be
granted to thrusted users, which will allow them to edit the
informations. The application manager account should have access to
all pictures, and the admin right !

Albums right
------------

By default, Albums are created with the **all user** visibility, but
it can be changed at anytime.

Photos right
------------

Photos are created with with no right set. This means that they will
be visible only if their album is visible.  If the visibility right is
lowered, the picture will not appear in the album or in the tag
lists. On the other side, a visible photo in an album supposed to be
hidden will make the album visible, but not the other photos.

Features
========

- complete photo management environment (but no file modification)
- way more powerful than directory structure
- possibility to export a standalone static version the pages of a
  Theme, a Carnet or a set of Tags, which can easily be transmitted or
  published on a website.
- geo tags and gpx traces plotted on OpenStreetMaps (open layers)
- easy (for devs) to extend and adapt to your needs
 
