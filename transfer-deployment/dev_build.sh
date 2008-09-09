#! /bin/sh

EXPORT=1
INSTALL=1
ASSEMBLY=1
SUBST=1
SCP=1

BASE=/data/transfer/trunk
USER=justin

#Do not change below
START=/home/$USER

if [ $EXPORT = 1 ]; then
	rm -f -r $START/transfer-deployment
	cd $BASE/core
	svn export transfer-deployment $START/transfer-deployment
	svn export transfer/db $START/transfer-deployment/db
fi

if [ $SUBST = 1 ]; then
	cd $START/transfer-deployment
	sed -e "s/CHANGEME_VERSION/1.8-SNAPSHOT/" -i ndnp-dev-lawrencium.py
	sed -e "s/CHANGEME_VERSION/1.8-SNAPSHOT/" -i ndnp-dev-gold.py
	sed -e "s/'qa'/'dev'/g" -e "s/CHANGEME_VERSION/1.8-SNAPSHOT/" -i ndnp-dev-plutonium.py
fi

if [ $INSTALL = 1 ]; then
	cd $BASE/core/transfer-maven-core
	mvn -o clean install
	cd $BASE/ndnp/transfer-maven-ndnp
	mvn -o clean install
fi

if [ $ASSEMBLY = 1 ]; then
	cd $BASE/core/packagemodeler-core
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/core/workflow-processes-core
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/core/transfer-services-core
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/core/transfer-components-core
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/ndnp/packagemodeler-ndnp
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/ndnp/transfer-components-ndnp
	mvn -o -Dmaven.test.skip=true assembly:assembly
	cp target/*.zip $START/transfer-deployment/files
	cd $BASE/ndnp/transfer-ui-ndnp
	cp target/*.war $START/transfer-deployment/files
	cd $BASE/ndnp/workflow-processes-ndnp
	cp src/main/resources/gov/loc/repository/workflow/processdefinitions/ndnp/ndnp1/processdefinition.xml $START/transfer-deployment/files
fi

cd $START

if [ $SCP=1 ]; then
	scp -r transfer-deployment $USER@gold.rdc.lctl.gov:/home/$USER
fi
