VERSION ?= 3.5
RELEASE ?= dev

INSTALL_PREFIX := ${shell readlink  --canonicalize .}

PKG_NAME := WebAlbums
PKG_NAMEVERSION += $(PKG_NAME)-$(VERSION)-$(RELEASE)

APPSERVER_DIRNAME = apache-tomee-webprofile
APP_DIRNAME := app-ear
CONF_DIR := conf/conf.xml # must agree with WebAlbums3-Service-Beans/src/java/net/wazari/service/engine/ConfigurationBean.java::CONFIG_PATH
DB_CONNECTOR_JAR := RT-DB-mysql-connector-java-5.1.12-bin.jar

PKG_DIR := ${shell pwd}
HOME_DIR := ${shell readlink  --canonicalize $(PKG_DIR)/..}

ETC_DIR := $(INSTALL_PREFIX)/etc/$(PKG_NAME)
USR_DIR := $(INSTALL_PREFIX)/usr/local/$(PKG_NAME)
TMP_DIR := $(INSTALL_PREFIX)/var/tmp/$(PKG_NAME)
VAR_DIR := $(INSTALL_PREFIX)/var/$(PKG_NAME)
LOG_DIR := $(VAR_DIR)/log
ROOT_PATH:= $(VAR_DIR)

APPSERVER_DIR := $(USR_DIR)/$(APPSERVER_DIRNAME)

ALL_DIRS := $(ETC_DIR) $(USR_DIR) $(TMP_DIR) $(VAR_DIR) $(LOG_DIR)

all: prep_config prep_usr_appserver prep_usr_app prep_base_directories prep_example

prep_example: prep_base_directories
	mkdir -p $(ROOT_PATH)/ftp/France
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2012/2012-10-12\ Sailing\ Journey $(ROOT_PATH)/ftp/France -r
	cp /mnt/sda7/kevin/Downloads/WebAlbums-3-release/data/images/France/2013/2013-03-31\ Antibes $(ROOT_PATH)/ftp/France -r

prep_config: prep_base_directories prep_usr_appserver
	@echo '# Prepare configuration'
	rm -rf $(ETC_DIR)/appserver
	mv $(APPSERVER_DIR)/conf $(ETC_DIR)/appserver
	ln -s $(ETC_DIR)/appserver $(APPSERVER_DIR)/conf

	rm $(ETC_DIR)/appserver/tomee.xml -f
	mv $(ETC_DIR)/appserver/tomee.xml.template $(ETC_DIR)/appserver/tomee.xml
	sh setup_db.sh $(ETC_DIR)/appserver/tomee.xml

	cp $(HOME_DIR)/WebAlbums3/conf/conf-example.xml $(ETC_DIR)/conf.xml
	mkdir -p $(ROOT_PATH)
	sh setup_data_dir.sh $(ROOT_PATH) $(ETC_DIR)/conf.xml $(TMP_DIR)
	mkdir -p $(ROOT_PATH)/$(shell dirname $(CONF_DIR)) 
	ln -s $(ETC_DIR)/conf.xml $(ROOT_PATH)/$(CONF_DIR)

	echo $(ROOT_PATH) > $(ETC_DIR)/root.path
	ln -s $(ETC_DIR)/root.path $(APPSERVER_DIR)/conf
	@echo

build_app :
	echo "not yet"

prep_usr_app: prep_usr_appserver prep_base_directories
	@echo '# Prepare EAR application'
	cp -r $(HOME_DIR)/WebAlbums3-ea/build $(USR_DIR)/$(APP_DIRNAME)
	ln -s ../../$(APP_DIRNAME) $(APPSERVER_DIR)/apps/WebAlbums3-ea
	@echo

prep_usr_appserver: prep_base_directories
	@echo '# Prepare J2EE appserver'
	cp -r $(HOME_DIR)/$(APPSERVER_DIRNAME) $(USR_DIR)

	rm -rf $(APPSERVER_DIR)/{logs,work,apps}

	ln -s $(LOG_DIR) $(APPSERVER_DIR)/logs
	ln -s $(TMP_DIR) $(APPSERVER_DIR)/work

	mkdir $(APPSERVER_DIR)/apps
	mkdir -p $(APPSERVER_DIR)/webapps/empty

	rm $(APPSERVER_DIR)/lib/$(DB_CONNECTOR_JAR)
	cp -r $(HOME_DIR)/WebAlbums-libs/$(DB_CONNECTOR_JAR) $(APPSERVER_DIR)/lib
	cp -r $(HOME_DIR)/WebAlbums3-Service-Beans/src/java/logback.xml $(APPSERVER_DIR)/lib/logback-test.xml --remove-destination
	cp $(HOME_DIR)/WebAlbums3/conf/appserver-tomee/* $(APPSERVER_DIR)/conf --remove-destination
	@echo

prep_base_directories: clean
	@echo '# Create base directories'
	mkdir -p $(ALL_DIRS)
	@echo

clean:
	@echo '# Cleanup everything'
	rm -rf $(INSTALL_PREFIX)/{etc,usr,var} 
	@echo
