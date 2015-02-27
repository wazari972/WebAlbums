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

Database access
---------------

To use WebAlbums, you need a mariadb/mysql database and a user that
can access it. Create it with PhpMyAdmin, or with a command like that:

    USER=wazari972
    PASS=ijaheb
    DB=WeAlbums
    cat << EOF | mysql -u root -p # connect as root user
    CREATE USER '$USER'@'localhost' IDENTIFIED BY '$PASS';
    CREATE DATABASE $DB;
    GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON `$DB`.* TO '$USER'@'localhost';
    EOF
    
Then edit `$INSTALL_ROOT/etc/webalbums/appserver/tomee.xml` and update
the resource configuration.

WebAlbums Users
---------------

To use WebAlbums, you need at least one valid user, with
administration rights.

* Define usernames and passwords in `users.properties`
  (username=password)

* Bind users to roles (groups) in `groups.properties` (role=username). WebAlbums roles are:
  * `MANAGER` to give administration rights
  * `VIEWER` to give viewer rights (mandatory!)
  * `Admin`, `Famille`, `Amis`, `Autres` (admin, family, friends and others, respectively) restrict the set of picture a given user can see. `Admin` sees everything, and `Autres` is the most restricted.

WebAlbums Paths and Directories
-------------------------------

WebAlbums searches for its configuration file in `$(cat
$TOMCAT_HOME/config.path)`, that should point to something like
`webalbums.xml`.

Configuration file `webalbums.xml` specifies where WebAlbums should
store and lookup its images:

*  `Configuration/directories/root_path` is the absolute path to the root directory. The directory should contain the subfolders `images`, `miniatures`, `ftp`, ...

    * `images` will contains your photos, don't forget to backup
      it. You may want to access it directly from your $HOME, in that
      case create a symlink to it (`ln -s /var/webalbums/images
      $HOME/wa_images`).
    * You should be allowed to write into `ftp`
      folder, either locally or remotely (hence the name).

`$TOMCAT_HOME/library.path` should point to the directory containing
WebAlbums's JFS library `libJnetFS.so`, if you plan to use WebAlbums
File-System.

Apache Appserver
----------------

WebAlbums relies on Tomcat Appserver (and Tomee EJB/JEE, on top of
it). Everything is configured, but you can customize it by editing
`$INSTALL_ROOT/etc/webalbums/appserver/`.

* In `server.xml`, you can change the default http port (8080), the
  user authentification backend (default based on property files).

* In `logback-test.xml` and `logging.properties` you can change the
  logger verbosity. The former is for Webalbums itself, the later for
  Tomcat.

* In `groups.properties` and `user.properties`, you can give access to
  Tomcat configuration and monitoring webapps. The roles concerned are
  `manager-gui`, `admin-gui` and `tomee-admin`. Move
  `$TOMCAT_HOME/webapps/*` to `$TOMCAT_HOME/apps/` to automatically
  deploy them on server startup.

