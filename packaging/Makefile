#####################
### Customization ###
#####################

BUILD_DIRNAME ?= build
WITH_JFS ?= y

VERSION ?= 0
RELEASE ?= -dev

EXEC_PREFIX ?=

##################
###  Env Setup ###
##################

BUILD_PREFIX := ${shell mkdir -p $(BUILD_DIRNAME) && readlink  --canonicalize $(BUILD_DIRNAME)}

PKG_NAME := WebAlbums

APPSERVER_DIRNAME = apache-tomee-webprofile
APP_DIRNAME := app-ear
CONF_FILENAME = conf.xml
DB_CONNECTOR_JAR := RT-DB-mysql-connector-java-5.1.12-bin.jar

PKG_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))
HOME_DIR := ${shell readlink  --canonicalize $(PKG_DIR)/..}

ifneq (,$(findstring 64,$(shell uname -m)))
    LIB_ARCH_SUFFIX :=
else
    LIB_ARCH_SUFFIX :=32
endif

ETC_DIR := $(BUILD_PREFIX)/etc/$(PKG_NAME)
USR_DIR := $(BUILD_PREFIX)/usr/share/$(PKG_NAME)
LIB_DIR := $(BUILD_PREFIX)/usr/lib/$(LIB_ARCH_SUFFIX)
TMP_DIR := $(BUILD_PREFIX)/var/tmp/$(PKG_NAME)
VAR_DIR := $(BUILD_PREFIX)/var/$(PKG_NAME)
LOG_DIR := $(BUILD_PREFIX)/var/log/$(PKG_NAME)

ROOT_PATH := $(VAR_DIR)

APPSERVER_DIR := $(USR_DIR)/$(APPSERVER_DIRNAME)

ANT_EAR_BUILDFILE := $(HOME_DIR)/WebAlbums-ea/build.xml
ANT_WFS_BUILDFILE := $(HOME_DIR)/WebAlbums-FS/WebAlbums-FS/build.xml

###################
### Build rules ###
###################

all: prep_appserver
	@$(MAKE) -f $(firstword $(MAKEFILE_LIST)) conf_root_path conf_webapp conf_jfs

### Example ###

prep_example: prep_root_path
	@echo '# Prepare example'
	mkdir -p $(ROOT_PATH)/ftp/France
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2012/2012-10-12\ Sailing\ Journey $(ROOT_PATH)/ftp/France -r
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2013/2013-03-31\ Antibes $(ROOT_PATH)/ftp/France -r
	@echo

### Root path ###

conf_root_path: prep_root_path
	@echo '# Configure root path'
	mkdir -p $(ETC_DIR) $(ETC_DIR)/appserver
	cp $(HOME_DIR)/WebAlbums/conf/conf-template.xml $(ETC_DIR)/$(CONF_FILENAME)
	$(PKG_DIR)/setup_data_dir.sh --configure $(ROOT_PATH) $(ETC_DIR)/$(CONF_FILENAME) $(TMP_DIR)
	echo $(ETC_DIR)/$(CONF_FILENAME) > $(ETC_DIR)/appserver/config.path
	@echo

prep_root_path:
	@echo '# Prepare root path'
	mkdir -p $(ROOT_PATH)
	$(PKG_DIR)/setup_data_dir.sh --mkdirs $(ROOT_PATH) $(ETC_DIR)/$(CONF_FILENAME) $(TMP_DIR)
	@echo

### WebApp ###

path_build_to_exec:
	sed -i 's#$(BUILD_PREFIX)#$(EXEC_PREFIX)#g' \
		$(ETC_DIR)/appserver/library.path \
		$(ETC_DIR)/appserver/config.path \
		$(ETC_DIR)/$(CONF_FILENAME)

conf_webapp: prep_webapp
	@echo '# Configure webapplication'
	mkdir -p $(APPSERVER_DIR)/apps/
	rm -f $(APPSERVER_DIR)/apps/$(PKG_NAME)-ea
	@$(PKG_DIR)/link.sh $(USR_DIR)/$(APP_DIRNAME) $(APPSERVER_DIR)/apps/$(PKG_NAME)-ea
	@echo

prep_webapp:
	@echo '# Prepare EAR application'
	mkdir -p $(USR_DIR)
	rm -rf $(USR_DIR)/$(APP_DIRNAME)
	cp -r $(HOME_DIR)/WebAlbums-ea/build $(USR_DIR)/$(APP_DIRNAME)
	cp -r $(HOME_DIR)/WebAlbums-FS/WebAlbums-FS/dist/WebAlbums-FS.war $(USR_DIR)/$(APP_DIRNAME) # not sure why ant doesn't copy it itself ...
	@echo

build_webapp: 
	@echo '# Build webapplication'
	@$(MAKE) -j2 -f $(firstword $(MAKEFILE_LIST)) build_vfs_webapp
	@echo

build_ear_webapp: 
	ant -buildfile $(ANT_EAR_BUILDFILE) -quiet dist | \
		grep -v "Trying to override old definition of task"

build_vfs_webapp:
	ant -buildfile $(ANT_WFS_BUILDFILE) -quiet dist | \
		grep -v "Trying to override old definition of task"

clean_webapp:	
	ant -buildfile $(ANT_EAR_BUILDFILE) -quiet

### JnetFS ###

conf_jfs:
	@echo '# Configure JFS'
	mkdir -p $(ETC_DIR)/appserver/
	echo $(LIB_DIR) > $(ETC_DIR)/appserver/library.path
	@echo

prep_jfs:
	@echo '# Prepare JFS'
	mkdir -p $(LIB_DIR)
	cp $(HOME_DIR)/WebAlbums-FS/JnetFS_C/lib/libJnetFS.so $(LIB_DIR)
	@echo

build_jfs: $(HOME_DIR)/WebAlbums-FS/JnetFS_C/lib/libJnetFS.so
	@echo '# Build JFS-lib'
	make -C $(HOME_DIR)/WebAlbums-FS/JnetFS_C
	@echo

### AppServer ###

conf_appserver:
	@echo '# Prepare appserver configuration'
	mkdir -p $(ETC_DIR)

	rm -rf $(ETC_DIR)/appserver
	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME)/conf $(ETC_DIR)/appserver 

	rm $(ETC_DIR)/appserver/tomee.xml -f

	cp $(HOME_DIR)/WebAlbums/conf/appserver-tomee/* $(ETC_DIR)/appserver/ --remove-destination
	@$(PKG_DIR)/link.sh $(APPSERVER_DIR)/lib/logback-test.xml $(ETC_DIR)/appserver/logback-test.xml

	mv $(ETC_DIR)/appserver/tomee-template.xml $(ETC_DIR)/appserver/tomee.xml
	$(PKG_DIR)/setup_db.sh $(ETC_DIR)/appserver/tomee.xml

	@echo

prep_appserver: conf_appserver
	@echo '# Prepare appserver appserver'
	mkdir -p $(USR_DIR) $(LOG_DIR) $(TMP_DIR)

	rm -rf $(USR_DIR)/$(APPSERVER_DIRNAME)
	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME) $(USR_DIR)

	rm -rf $(APPSERVER_DIR)/{logs,work,apps,conf}

	@$(PKG_DIR)/link.sh $(LOG_DIR) $(APPSERVER_DIR)/logs
	@$(PKG_DIR)/link.sh $(TMP_DIR) $(APPSERVER_DIR)/work

	mkdir -p $(APPSERVER_DIR)/webapps/empty
	mkdir $(APPSERVER_DIR)/apps

	rm $(APPSERVER_DIR)/lib/$(DB_CONNECTOR_JAR)
	cp -r $(HOME_DIR)/WebAlbums-libs/$(DB_CONNECTOR_JAR) $(APPSERVER_DIR)/lib
	cp -r $(HOME_DIR)/WebAlbums-Service-Beans/src/java/logback.xml $(APPSERVER_DIR)/lib/logback-test.xml --remove-destination

	@$(PKG_DIR)/link.sh $(ETC_DIR)/appserver $(APPSERVER_DIR)/conf
	@echo

### Cleanup ###

clean:
	@echo '# Cleanup package'
	rm -rf $(BUILD_PREFIX)/{etc,usr,var}
	rmdir $(BUILD_PREFIX) --ignore-fail-on-non-empty
	@echo

clean_all : clean clean_app
	@echo '# Everything cleanup'
