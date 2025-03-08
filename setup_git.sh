git config --global user.name "yuansx1022"
git config --global user.email "yuansx1022@163.com"
echo "# -yuansx" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/yuansx1022/-yuansx.git
git push -u origin main