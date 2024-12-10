chcp 65001
@echo off

git add ./*
git commit -m "update %date:~0,2%%date:~3,4%%date:~8,2%%date:~11,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
git push
pause


:: gbk
:: git commit -m "update %date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"