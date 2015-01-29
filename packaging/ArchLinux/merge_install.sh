#! /bin/bash

#usage: $0 $PATH/TO $INSTALL* > $MERGED_INSTALLS
#usage: $0 $PATH/TO --gen-all

merge_installs() {
    HOME=$1
    shift
    normalize() {
        echo $1 | sed 's/[-.]/_/g'
    }
    echo "#!/bin/bash

# DO NOT EDIT, file generated by 'makepkg PKGBUILD' ($(date))
# this is the merge of $*\n"
    
    for install in $*
    do
        echo "
$(normalize $install)=\$(mktemp)
cat << EOF > \$$(normalize $install)
$(cat $HOME/$install)
EOF
"
    done
    for fct in {pre,post}_{install,upgrade,remove}
    do
        echo "$fct() {"
        ret_test=0
        for install in $*
        do
            echo "  bash -c \"source \$$(normalize $install); type $fct &> /dev/null && $fct \$*\""
            
        done
        echo "  return 0" # need to improve that
        echo "}"
        echo 
    done
}

if [[ "$2" == *"--gen-all"* ]]
then
    TO_MERGE="webalbums-appserver-git.install webalbums-webapp-git.install"
    INTO="webalbums-all-git.gen.install"
    echo "Merge $TO_MERGE into $INTO ($1)"
    merge_installs $1 $TO_MERGE > $1/$INTO
else
    merge_installs $*

fi
