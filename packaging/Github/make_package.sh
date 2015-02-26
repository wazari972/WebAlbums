#! /bin/bash

BASEDIR=$(pwd)

# <copied from Archlinux makepkg>


# check if messages are to be printed using color
unset ALL_OFF BOLD BLUE GREEN RED YELLOW
# prefer terminal safe colored and bold text when tput is supported
if tput setaf 0 &>/dev/null; then
    ALL_OFF="$(tput sgr0)"
    BOLD="$(tput bold)"
    BLUE="${BOLD}$(tput setaf 4)"
    GREEN="${BOLD}$(tput setaf 2)"
    RED="${BOLD}$(tput setaf 1)"
    YELLOW="${BOLD}$(tput setaf 3)"
else
    ALL_OFF="[0m"
    BOLD="[1m"
    BLUE="${BOLD}[34m"
    GREEN="${BOLD}[32m"
    RED="${BOLD}[31m"
    YELLOW="${BOLD}[33m"
fi

readonly ALL_OFF BOLD BLUE GREEN RED YELLOW


msg() {
    local mesg=$1; shift
    printf "${GREEN}==>${ALL_OFF}${BOLD} ${mesg}${ALL_OFF}
" "$@" >&2
}

warning() {
    local mesg=$1; shift
    printf "${YELLOW}==> $(gettext "WARNING:")${ALL_OFF}${BOLD} ${mesg}${ALL_OFF}
" "$@" >&2
}

error() {
    local mesg=$1; shift
    printf "${RED}==> $(gettext "ERROR:")${ALL_OFF}${BOLD} ${mesg}${ALL_OFF}
" "$@" >&2
}

# </copied from Archlinux makepkg>

source ../ArchLinux/PKGBUILD

srcdir=$BASEDIR/src
mkdir -p $srcdir
cd $srcdir

msg "Preparing and building the application ..."
prepare
build

for pkg in ${pkgname[@]}
do
    pkgdir=$BASEDIR/pkg/$pkg
    pkg_fct=package_$pkg
    pkg_archive=$pkg.tar.gz
    
    if declare -f $pkg_fct &>/dev/null
    then
        msg "Creating package $pkg_archive ..."
        mkdir -p $pkgdir
        cd $srcdir
        eval "$pkg_fct"
        
        msg "Compressing package $pkg_archive ..."
        cd $pkgdir
        tar cfz $BASEDIR/$pkg_archive *
        cd $BASEDIR
        
        msg "Done with $pkg"
    fi
    rm -rf pkgdir
done

rm -rf src pkg



