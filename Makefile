REVISION := $(shell git describe --always --dirty='-dirty')
BRANCH := $(shell git symbolic-ref --short HEAD)
TAG := $(subst /,-,$(BRANCH))-$(REVISION)

# ================= SERVICE ==================
database:
	docker-compose up -d	
	
buildlocalimage:
	./gradlew jibDockerBuild -PgitHash=${TAG} --stacktrace

buildproductionimage:
	./gradlew jib -PjibImage=eu.gcr.io/grimsborn/argent -PgitHash=${TAG} --stacktrace

rundocker: buildlocalimage
	docker run -t \
	--env-file .env \
	-v ${PWD}/localdockersecrets:/localsecrets \
	--network argent_network -p 8008:8008 \
	$(shell jq -r .image build/jib-image.json)  | node logparse.js
