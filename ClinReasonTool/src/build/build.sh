dst_tomcat="/home/software/test2/"
mygiturl="https://github.com/Gulpi/ClinicalReasoningTool/archive/master.zipi?access_token=1052c3ad4ed288459460a083476201c604f92f5f"
mytomcat=/usr/local/tomcat7/lib
myclasses=`./classpath.sh ./ClinicalReasoningTool-master/ClinReasonTool/WEB-INF/lib`
myclasses="$myclasses:$mytomcat/servlet-api.jar"
oauth_token="1052c3ad4ed288459460a083476201c604f92f5f"
from_github=true
srcpath=./ClinicalReasoningTool-master/ClinReasonTool/src/java
dst=./ClinicalReasoningTool-master/ClinReasonTool/WEB-INF/classes
curl_cmd="curl -LkSs -H \"Authorization: token 1052c3ad4ed288459460a083476201c604f92f5f\" https://api.github.com/repos/Gulpi/ClinicalReasoningTool/tarball -o master.tar.gz"

if [ "$1" == "" ] ; then
if [ "$from_github" == "false" ] ; then
	rm -rf ClinicalReasoningTool-master
	rm -f ClinicalReasoningTool-master.zip
	cp ../ClinicalReasoningTool-master.zip ./
	unzip ClinicalReasoningTool-master.zip
else
	rm -rf Gulpi-ClinicalReasoning*
	rm -rf master.tar.gz
	eval $curl_cmd
	tar -xzvf master.tar.gz
	mkdir ./ClinicalReasoningTool-master
	cp -rf Gulpi-ClinicalReasoning*/* ./ClinicalReasoningTool-master/
fi
fi
#mkdir $dst 
#cmd="javac -classpath $myclasses -d $dst $(find $srcpath/* | grep '\.java')"
#echo "$cmd"
#eval $cmd

LANG=de_DE@euro;export LANG
JAVA_HOME=/usr/local/jdk1.7; export JAVA_HOME
ANT_OPTS=-Xmx512m;export ANT_OPTS
/usr/local/ant/bin/ant

#cp ./ClinicalReasoningTool-master/ClinReasonTool/src/java/hibernate.properties ./ClinicalReasoningTool-master/ClinReasonTool/WEB-INF/classes/

#cp -r ./ClinicalReasoningTool-master/ClinReasonTool/* $dst_tomcat/webapps/crt/