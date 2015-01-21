#! /bin/bash

HOME=$(pwd)

msg() {
    echo $*
}

source ../ArchLinux/PKGBUILD

srcdir=$HOME/src
pkgdir=$HOME/pkg/$pkgname
mkdir -p $srcdir $pkgdir

build

package

pkg_archive=$pkgname.tar.gz
msg "Compressing package $pkg_archive ..."

cd $pkgdir
tar cfz $HOME/$pkg_archive *
msg "Done"
