#! /bin/sh

if [[ "$*" == *"--mkdirs"* ]]
then
    DO_MKDIRS=1
    shift
fi
if [[ "$*" == *"--configure"* ]]
then
    DO_CONFIGURE=1
    shift
fi

ROOT_PATH=$1
CONFIG_FILE=$2
TMP=$3

IMAGES=images
MINIS=miniatures
FTP=ftp
BACKUP=backup
PLUGINS=plugins

if [ "$DO_CONFIGURE" == 1 ]
then
    sed -i "s|ROOT_PATH|$ROOT_PATH|g" $CONFIG_FILE
    sed -i "s|IMAGES|$IMAGES|g" $CONFIG_FILE
    sed -i "s|MINIS|$MINIS|g" $CONFIG_FILE
    sed -i "s|TMP|$TMP|g" $CONFIG_FILE
    sed -i "s|FTP|$FTP|g" $CONFIG_FILE
    sed -i "s|BACKUP|$BACKUP|g" $CONFIG_FILE
    sed -i "s|PLUGINS|$PLUGINS|g" $CONFIG_FILE
fi
if [ "$DO_MKDIRS" == 1 ]
then
    mkdir $ROOT_PATH/$IMAGES \
          $ROOT_PATH/$MINIS \
          $ROOT_PATH/$BACKUP \
          $ROOT_PATH/$FTP \
          $ROOT_PATH/$PLUGINS \
          -p
fi
