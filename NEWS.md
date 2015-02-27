January 2015
------------

* Heavy work on clean packaging and versioning.
  * Archive-based and Archlinux packages are ready

* Change JEE appserver from Glassfish to Tomcat
  * Way simpler for administration, lightweight and fast.
  * Better control of the tool.
  * Embedded server not working yet.
  
* Dependency upgrade: Java 7, Hibernate 4.3, JPA 2.1

* WebAlbums FS
  * Improve efficiency of WFS with path to object caching.
  * Split Album listing year by year.
  * Allow auto-mount at startup.

* WebAlbums web-interface
  * Switch to HTTP/Basic authentication, it remembers login after
    server reboot.
  
* Bug fixes:
  * Fix OpenLayers maps.
  * Fix `Where` tags map display.
  
December 2014
-------------

* WebAlbums FS
  * Add Carnets
  * Add subdirectory Tags into Albums directories
  
April 2014
----------

* WebAlbums web-interface
  * add redditp slideshow support
* Start playing with Python (Jython) direct access to WebAlbums
  engine.
  
January 2014
------------

* Add possibility to download a carnet and all its picture
* Allow WebAlbums bootstrap to start without a GUI
  * `--no-gui` or `-nx`
  
December 2013
-------------

* Bug fix:
  * Avoid NullPointerException if no EXIF data is present
  
September 2013
--------------

* Bug fix (WFS):
  * Force ISO-8859-1 encoding of generated GPX files
  
August 2013
-----------

* WebAlbums FS
  * Support tagging through image drag-and-drop.
* Work on database consistency verification
  * All databases entries point to an existing file
  * All filesystem files point to a valid db enty
* Remove Perf4j performance logging, I don't use them
* Great refactoring of session parameter sharing

July 2013
---------

* Update NetBeans and Hibernate version

* Keep working on photowall GUI

March 2013
----------

* Work on a GUI for generating photowalls
  * plain photos or polaroid-like shots
* Add widget for "years ago" albums: albums shot the same date, years
  before
* Add ability to move an album from one theme to another
* Work on a random galery to display random pictures in fullscreen

January 2013
------------

* Improve Carnets handling
  
2012
----

* Introduce staring feature:
  * Rate photos with stars
  * Select the 'quality' level you want to show
* Add timeline view of album list
* Introduce a Photowall generator, in Python 
* Add chart view of tag tree
* Add support for VISIO mode, an html fullscreen view of albums
* Add heatmap to OpenLayers tag map
* Add systray bootloader for server quick start
* Introduce GPX files into WebAlbums
* Introduce Fuse-base WebAlbums filesystem (summer 2012)
* Introduce OpenLayer maps, instead of Google maps
* Add ability to import/export database to XML
* Work on ability to download WebAlbums for offline view
* Adapt Single Page Interface to HTML5 history

2011
----

* Add birthdate to WHO tags, compute the age when the tag is displayed
* Add appserver security enforcement
* Introduce Carnets: blog-oriented posts focused on pictures
* Switch to Hibernate 4
* Use JQuery for faster image tagging
* Move from Sourceforge SVN to Github GIT (may 2011)


2010
----

* Introduce Single Page Interface, to improve page navigation speed
* Switch to JPA Criteria and Metamodel
* Ability to automatically add white border on photos
* Tree-based tags
* Introduce a great pagination :-)
* Switch to Google Map v3
* Add Random by year widget
* Insert Perf4J logging for performance measurement
* Add a custom backgroup picture !
* First version of bootstrap with embedded glassfish
* Local fullscreen tag view, by copying photos to temp and starting
  Eye of Gnome
* Introduce pluging interface for accessing OS tools
* Introduce Edition, Visite and Normal views
* Massive refactoring. Switch to:
  * Glassfish Appserver and MVC model and XML generation,
  * XSL transformations,
  * XHTML, CSS and Jquery web interface
* Import from SVN Assembla

2009
----

* No clean history, just tarballs ...

2008
----

* No clean history, just tarballs ...
* Oldest tarball (February 2008), application structure already there:
  * Hibernate DAO classes for Albums, Photos, Authors
  * Java-base HTML generation
