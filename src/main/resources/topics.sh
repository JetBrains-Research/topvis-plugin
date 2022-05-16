#!/bin/bash
requirements=(pip3 python3 git)
for item in ${requirements[*]}
do
    if ! $item --version $>/dev/null; then
        echo "Please install $item"
        exit 1
    fi
done
mkdir temp
cd temp
rm -r *
echo "$2" > repos.txt
git clone https://github.com/JetBrains-Research/topvis/
cd topvis
git checkout tree-visualization
git submodule init
git submodule update
case "$1" in
  sosed)
    ./scripts/run_sosed.sh "../../repos.txt" "../../topics-caches"
    cp sosed/out/topics.json ../../topics.json
    mkdir -p ../../topics-caches/sosed/data
    cp sosed/data/clusters.npy ../../topics-caches/sosed/data/clusters.npy
    cp sosed/data/tokens.txt ../../topics-caches/sosed/data/tokens.txt
    pwd
  ;;
  tfidf)
    ./scripts/run_tfidf.sh "../../repos.txt"
    cp tfidf/out/topics.json ../../topics.json
  ;;
  *)
    echo -n "unknown "
  ;;
esac
cd ../../
rm -rf ./temp