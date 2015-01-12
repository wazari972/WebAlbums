VERSION ?= 3.5
RELEASE ?= dev

INSTALL_DIRNAME ?= install

INSTALL_PREFIX := ${shell mkdir -p $(INSTALL_DIRNAME) && readlink  --canonicalize $(INSTALL_DIRNAME)}

PKG_NAME := WebAlbums
PKG_NAMEVERSION += $(PKG_NAME)-$(VERSION)-$(RELEASE)

APPSERVER_DIRNAME = apache-tomee-webprofile
APP_DIRNAME := app-ear
CONF_FILENAME = conf.xml
DB_CONNECTOR_JAR := RT-DB-mysql-connector-java-5.1.12-bin.jar

PKG_DIR := ${shell pwd}
HOME_DIR := ${shell readlink  --canonicalize $(PKG_DIR)/..}

ETC_DIR := $(INSTALL_PREFIX)/etc/$(PKG_NAME)
USR_DIR := $(INSTALL_PREFIX)/usr/share/$(PKG_NAME)
TMP_DIR := $(INSTALL_PREFIX)/var/tmp/$(PKG_NAME)
VAR_DIR := $(INSTALL_PREFIX)/var/$(PKG_NAME)
LOG_DIR := $(INSTALL_PREFIX)/var/log/$(PKG_NAME)

ROOT_PATH := $(VAR_DIR)

APPSERVER_DIR := $(USR_DIR)/$(APPSERVER_DIRNAME)

ANT_EAR_BUILDFILE := ../WebAlbums3-ea/build.xml

all: conf_root_path conf_webapp  #prep_example

build_app:
	ant -buildfile $(ANT_EAR_BUILDFILE) -quiet | grep -v "Trying to override old definition of task"

clean_app:
	ant -buildfile $(ANT_EAR_BUILDFILE) -quiet

prep_example: prep_root_path
	mkdir -p $(ROOT_PATH)/ftp/France
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2012/2012-10-12\ Sailing\ Journey $(ROOT_PATH)/ftp/France -r
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2013/2013-03-31\ Antibes $(ROOT_PATH)/ftp/France -r

conf_appserver:
	@echo '# Prepare configuration'
	mkdir -p $(ETC_DIR)
	rm -rf $(ETC_DIR)/appserver

	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME)/conf $(ETC_DIR)/appserver -v

	rm $(ETC_DIR)/appserver/tomee.xml -f

	cp $(HOME_DIR)/WebAlbums3/conf/appserver-tomee/* $(ETC_DIR)/appserver/ --remove-destination

	mv $(ETC_DIR)/appserver/tomee-template.xml $(ETC_DIR)/appserver/tomee.xml
	sh setup_db.sh $(ETC_DIR)/appserver/tomee.xml

	@echo

prep_root_path:
	@echo '# Prepare root path'
	mkdir -p $(ROOT_PATH)
	sh setup_data_dir.sh --mkdirs $(ROOT_PATH) $(ETC_DIR)/$(CONF_FILENAME) $(TMP_DIR)
	@echo

conf_root_path: prep_root_path conf_appserver
	cp $(HOME_DIR)/WebAlbums3/conf/conf-template.xml $(ETC_DIR)/$(CONF_FILENAME)
	sh setup_data_dir.sh --configure $(ROOT_PATH) $(ETC_DIR)/$(CONF_FILENAME) $(TMP_DIR)
	echo $(ETC_DIR)/$(CONF_FILENAME) > $(ETC_DIR)/appserver/config.path

prep_appserver: conf_appserver
	@echo '# Prepare J2EE appserver'
	mkdir -p $(USR_DIR) $(LOG_DIR) $(TMP_DIR)

	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME) $(USR_DIR)

	rm -rf $(APPSERVER_DIR)/{logs,work,apps,conf}

	./link.sh $(LOG_DIR) $(APPSERVER_DIR)/logs
	./link.sh $(TMP_DIR) $(APPSERVER_DIR)/work

	mkdir -p $(APPSERVER_DIR)/webapps/empty
	mkdir $(APPSERVER_DIR)/apps

	rm $(APPSERVER_DIR)/lib/$(DB_CONNECTOR_JAR)
	cp -r $(HOME_DIR)/WebAlbums-libs/$(DB_CONNECTOR_JAR) $(APPSERVER_DIR)/lib
	cp -r $(HOME_DIR)/WebAlbums3-Service-Beans/src/java/logback.xml $(APPSERVER_DIR)/lib/logback-test.xml --remove-destination

	./link.sh $(ETC_DIR)/appserver $(APPSERVER_DIR)/conf
	@echo

prep_webapp:
	@echo '# Prepare EAR application'
	mkdir -p $(USR_DIR)
	cp -r $(HOME_DIR)/WebAlbums3-ea/build $(USR_DIR)/$(APP_DIRNAME)
	@echo

conf_webapp: prep_appserver prep_webapp
	./link.sh $(USR_DIR)/$(APP_DIRNAME) $(APPSERVER_DIR)/apps/$(PKG_NAME)-ea

clean:
	@echo '# Cleanup package'
	rm -rf $(INSTALL_PREFIX)/{etc,usr,var}
	rmdir $(INSTALL_PREFIX) --ignore-fail-on-non-empty
	@echo

clean_all : clean clean_app
	@echo '# Everything cleanup'
