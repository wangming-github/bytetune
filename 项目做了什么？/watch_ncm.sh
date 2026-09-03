#!/usr/bin/env bash

WATCH="/Users/zimai/Music/网易云音乐"
OUT="/Users/zimai/Music/云音乐转换mp3"

mkdir -p "$OUT"

echo "==============================="
echo " NCM Auto Decoder Started"
echo " Watching: $WATCH"
echo " Output  : $OUT"
echo "==============================="

fswatch -0 -e ".*" -i "\\.ncm$" "$WATCH" |
while IFS= read -r -d "" file
do
    echo
    echo "发现新文件:"
    echo "$file"

    # 等下载完成
    sleep 2

    [ -f "$file" ] || continue

    echo "开始解密..."
    ncmdump "$file"

    base="${file%.ncm}"

    for ext in flac mp3; do
        music="$base.$ext"

        if [ -f "$music" ]; then
            echo "移动 -> $music"
            mv "$music" "$OUT/"
        fi
    done

    echo "完成"
done