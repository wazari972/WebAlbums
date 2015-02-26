Installation
============

From GIT sources
----------------

    git clone https://github.com/wazari972/WebAlbums.git

    cd WebAlbums
    make -f packaging/Makefile build_ear_webapp
    make -f packaging/Makefile build_vfs_webapp
    make -f packaging/Makefile build_jfs

    cd apache-tomee-webprofile
    # create a database and a user, then
    $EDITOR conf/tomee.xml # configure db access

    # setup users and groups
    $EDITOR conf/users.properties conf/groups.properties

    # if necessary, configure tomcat appserver
    $EDITOR conf/server.xml

    # configure path to webalbums photos
    $EDITOR conf/webalbums.xml

    # create the relevant directories, eg:
    # mkdir $WEBALBUMS/{ftp,miniatures,images}

    # put photos in ftp directory, eg:
    # mkdir $WEBALBUMS/ftp/Familly/2015-01-01\ New\ Year\ Lunch

From Archive
------------

    wget ...
    tar xvf ...
    
From Archlinux
--------------

    wget WebAlbums-Archlinux.tar.gz
    tar xvf WebAlbums-Archlinux.tar.gz
    cd WebAlbums-Archlinux
    makepkg
    
    sudo pacman -U webalbums-all-git-*.-any.pkg.tar.xz
    # or
    sudo pacman -U webalbums-appserver-git-*.-any.pkg.tar.xz
    sudo pacman -U webalbums-webapp-git-*.-any.pkg.tar.xz
    sudo pacman -U webalbums-jfs-git-*.-any.pkg.tar.xz
    
Configuration
=============

