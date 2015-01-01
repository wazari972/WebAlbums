VERSION ?= 3.5
RELEASE ?= dev

INSTALL_PREFIX := .

PKG_NAME := WebAlbums
PKG_NAMEVERSION += $(PKG_NAME)-$(VERSION)-$(RELEASE)

APPSERVER_DIRNAME = apache-tomee-webprofile
APP_DIRNAME := app-ear

PKG_DIR := ${shell pwd}
HOME_DIR := $(PKG_DIR)/..

ETC_DIR := $(INSTALL_PREFIX)/etc/$(PKG_NAME)
USR_DIR := $(INSTALL_PREFIX)/usr/local/$(PKG_NAME)
TMP_DIR := $(INSTALL_PREFIX)/var/tmp/$(PKG_NAME)
VAR_DIR := $(INSTALL_PREFIX)/var/$(PKG_NAME)
LOG_DIR := $(INSTALL_PREFIX)/var/$(PKG_NAME)/log
APPSERVER_DIR := $(USR_DIR)/$(APPSERVER_DIRNAME)

ALL_DIRS := $(ETC_DIR) $(USR_DIR) $(TMP_DIR) $(VAR_DIR) $(LOG_DIR)

all: prep_config prep_usr_appserver prep_usr_app prep_base_directories

prep_config: prep_base_directories
	cp $(HOME_DIR)/WebAlbums3/conf/conf.xml $(ETC_DIR)
	cp -rv $(APPSERVER_DIR)/conf $(ETC_DIR)/appserver

build_app :
	echo "not yet"

prep_usr_app: prep_usr_appserver prep_base_directories
	cp -r $(HOME_DIR)/WebAlbums3-ea/build $(USR_DIR)/$(APPSERVER_DIR)
	ln -s ../../$(APPSERVER_DIR) $(APPSERVER_DIR)/apps/WebAlbums3-ea

prep_usr_appserver: prep_base_directories
	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME) $(USR_DIR)

	rm -rf $(APPSERVER_DIR)/{conf,logs,work,apps}

	ln -s $(APPSERVER_DIR)/conf $(ETC_DIR)/appserver
	ln -s $(APPSERVER_DIR)/logs $(LOG_DIR)
	ln -s $(APPSERVER_DIR)/work $(TMP_DIR)

	mkdir $(APPSERVER_DIR)/apps

prep_base_directories: clean
	mkdir -p $(ALL_DIRS)

clean:
	rm -rf $(INSTALL_PREFIX)/{etc,usr,var} 
